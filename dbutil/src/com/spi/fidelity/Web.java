/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fidelity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spi.util.Debug;
import com.spi.util.EqualityUtils;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class Web extends WebClient
{
   public static final String VAR_EVENTARGUMENT = "__EVENTARGUMENT";
   public static final String VAR_EVENTTARGET = "__EVENTTARGET";
   public static final String VAR_EVENTVALIDATION = "__EVENTVALIDATION";
   public static final String VAR_LASTFOCUS = "__LASTFOCUS";
   public static final String VAR_VIEWSTATE = "__VIEWSTATE";

   private Map<String, String> mFormVars = new HashMap<String, String>();

   private static final String DEBUG_PREFIX = "Web.";

   /**
    * Web constructor.
    * 
    * @param env client properties identifying the login URL, user name, and
    *        password to the Fidelity website.
    */
   public Web(Properties env, String name)
   {
      super(env, name);
   }

   @Override
   public void login() throws IOException
   {
      if (getAuthToken() == null)
      {
         setAuthToken("");

         URL loginUrl = new URL(getLoginURL());

         // Read and parse the login page to grab the session cookie and to
         // read hidden input tags in the page's body
         HttpURLConnection loginConn = getConnection(getLoginURL());
         InputStream is = getInput(loginConn, null, null);
         StringBuffer cookieStringBuf = new StringBuffer();
         appendCookies(cookieStringBuf, loginConn);
         parseInput(is);

         // Set the request parameters to retrieve the authorization ID
         loginConn = getConnection(getLoginURL());
         loginConn.setRequestProperty(ACCEPT_HEADER, "*/*");
         loginConn.setRequestProperty(ACCEPT_LANGUAGE_HEADER, "en-us");
         loginConn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
         loginConn.setRequestProperty(COOKIE_HEADER, cookieStringBuf.toString());
         loginConn.setRequestProperty(HOST_HEADER, loginUrl.getHost());
         loginConn.setRequestProperty(USER_AGENT_HEADER, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
         loginConn.setDoOutput(true);

         printRequestHeaders(loginConn);

         // POST our credentials
         PrintWriter writer = new PrintWriter(new BufferedOutputStream(loginConn.getOutputStream()));
         writer.write(VAR_EVENTTARGET + "=");
         writer.write("&");
         writer.write(VAR_EVENTARGUMENT + "=");
         writer.write("&");
         writer.write(VAR_LASTFOCUS + "=");
         writer.write("&");
         writer.write(VAR_VIEWSTATE + "=" + URLEncoder.encode(getInputVar(VAR_VIEWSTATE), "utf-8"));
         writer.write("&");
         writer.write("ctl00%24content_main%24UserName=" + getInputUser());
         writer.write("&");
         writer.write("ctl00%24content_main%24Password=" + getInputPass());
         writer.write("&");
         writer.write("ctl00%24content_main%24login_button=Submit");
         writer.write("&");
         writer.write(VAR_EVENTVALIDATION + "=" + URLEncoder.encode(getInputVar(VAR_EVENTVALIDATION), "utf-8"));
         writer.close();

         printResponseHeaders(loginConn);

         // Read the authorization ID cookie
         appendCookies(cookieStringBuf, loginConn);

         setAuthToken(cookieStringBuf.toString());
      }
   }

   /**
    * Retrieve the value of the hidden input tag with the given name, as
    * determined with the last call to {@link #parseInput(InputStream)}
    * 
    * @param varName
    * @return
    */
   public String getInputVar(String varName)
   {
      return mFormVars.get(varName);
   }

   @Override
   public InputStream getInput(String url) throws IOException
   {
      return getInput(url, null);
   }

   public InputStream getInput(String url, Map<String, String> formVars) throws IOException
   {
      return getInput(url, formVars, null);
   }

   public InputStream getInput(String url, Map<String, String> formVars, Map<String, String> reqHeaderVars) throws IOException
   {
      return getInput(getConnection(url), formVars, reqHeaderVars);
   }

   public InputStream getInput(HttpURLConnection conn, Map<String, String> formVars, Map<String, String> reqHeaderVars) throws IOException
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): START");

      // Ensure we have the authentication cookie from the login page
      login();

      // Set up the connection
      conn.setRequestProperty(ACCEPT_ENCODING_HEADER, "gzip");
      conn.setRequestProperty(ACCEPT_HEADER, "*/*");
      conn.setRequestProperty(ACCEPT_LANGUAGE_HEADER, "en-us");
      conn.setRequestProperty(HOST_HEADER, conn.getURL().getHost());
      conn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
      conn.setRequestProperty(USER_AGENT_HEADER, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
      if (getAuthToken() != null && getAuthToken().length() > 0)
      {
         conn.setRequestProperty(COOKIE_HEADER, getAuthToken());
      }
      if (reqHeaderVars != null)
      {
         for (String propName : reqHeaderVars.keySet())
         {
            conn.setRequestProperty(propName, reqHeaderVars.get(propName));
         }
      }

      // Formulate the variables to post if set, and determine the content
      // length of the variable text
      StringBuffer buf = new StringBuffer();
      if (formVars != null && !formVars.isEmpty())
      {
         for (String varName : formVars.keySet())
         {
            String varValue = mFormVars.get(varName);
            if (varValue == null)
            {
               varValue = formVars.get(varName);
            }
            if (buf.length() > 0)
            {
               buf.append('&');
            }
            buf.append(varName);
            buf.append('=');
            buf.append(varValue == null ? "" : URLEncoder.encode(varValue, "utf-8"));
         }
         if (buf.length() > 0)
         {
            conn.setRequestProperty(CONTENT_LENGTH_HEADER, Integer.toString(buf.length()));
         }
      }

      printRequestHeaders(conn);

      // If we have variables to post, do it
      if (buf.length() > 0)
      {
         conn.setRequestProperty(CONTENT_LENGTH_HEADER, Integer.toString(buf.length()));
         if (Debug.checkLevel(Debug.MED))
         {
            Debug.debug(Debug.MED, "************ Post Data:");
            Debug.debug(Debug.MED, buf.toString());
            Debug.debug(Debug.MED, "");
         }
         conn.setDoOutput(true);
         PrintWriter writer = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
         writer.write(buf.toString());
         writer.flush();
         writer.close();
      }

      // Get the connection's input stream
      InputStream inStream = new BufferedInputStream(conn.getInputStream());
      String encoding = conn.getHeaderField(CONTENT_ENCODING_HEADER);
      if (EqualityUtils.equals(encoding, "gzip"))
      {
         inStream = new GZIPInputStream(inStream);
      }
      printResponseHeaders(conn);

      // Check for a redirect
      if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
      {
         String redirectUrl = conn.getHeaderField("Location");
         if (redirectUrl != null && redirectUrl.trim().length() != 0)
         {
            if (redirectUrl.contains("/login.aspx?"))
            {
               setAuthToken(null);
               login();
               return getInput(getConnection(conn.getURL().toExternalForm()), formVars, reqHeaderVars);
            } else
            {
               redirectUrl = redirectUrl.replaceFirst("Wizard[0-9]+", "Wizard6");
               return getInput(getConnection(redirectUrl), formVars, reqHeaderVars);
            }
         }
      }

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): END");

      return inStream;
   }

   @Override
   public Document parseInput(InputStream inStream)
   {
      Document doc = super.parseInput(inStream);
      getInputVars(doc);
      return doc;
   }

   @Override
   protected String getDefaultLoginURL()
   {
      return RepScapeAccess.PAGES_BASE + "login.aspx";
   }

   protected void getInputVars(Document doc)
   {
      NodeList inputTags = doc.getElementsByTagName("input");
      for (int i = 0; i < inputTags.getLength(); i++)
      {
         Node inputTag = inputTags.item(i);
         NamedNodeMap attrs = inputTag.getAttributes();
         if (attrs != null)
         {
            Node nameAttrNode = attrs.getNamedItem("name");
            if (nameAttrNode != null)
            {
               Node valueAttrNode = attrs.getNamedItem("value");
               if (valueAttrNode != null)
               {
                  mFormVars.put(nameAttrNode.getNodeValue(), valueAttrNode.getNodeValue());
               }
            }
         }
      }
   }
}