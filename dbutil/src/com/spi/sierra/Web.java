/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.sierra;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.spi.util.Debug;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class Web extends WebClient
{
   String mInputURL;

   private static final String DEBUG_PREFIX = "Web.";

   static final String LOGIN_VAR_NAME = "login";
   static final String PASSWORD_VAR_NAME = "password";

   /**
    * Web constructor.
    * 
    * @param env client properties identifying the login URL, user name, and
    *        password to the First American website.
    */
   public Web(Properties env, String name)
   {
      super(env, name);
      setInputURL(env.getProperty("com.spi." + name + ".InputURL", getDefaultInputURL()));
   }

   @Override
   public void login() throws IOException
   {
      if (getAuthToken() == null)
      {
         // Set the request parameters
         HttpURLConnection loginConn = getConnection(getLoginURL());
         loginConn.setRequestProperty(REFERER_HEADER, getLoginURL());
         loginConn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
         loginConn.setRequestProperty(CACHE_CONTROL_HEADER, "no-cache");
         loginConn.setDoOutput(true);

         printRequestHeaders(loginConn);

         PrintWriter writer = new PrintWriter(new BufferedOutputStream(loginConn.getOutputStream()));
         writer.write(LOGIN_VAR_NAME);
         writer.write("=");
         writer.write(getInputUser());
         writer.write("&");
         writer.write(PASSWORD_VAR_NAME);
         writer.write("=");
         writer.write(getInputPass());
         writer.flush();
         writer.close();

         printResponseHeaders(loginConn);
         printResponse(loginConn);

         // Read the session cookie
         setAuthToken(loginConn.getHeaderField(SET_COOKIE_HEADER));
      }
   }

   @Override
   public InputStream getInput(String url) throws IOException
   {
      return getInput(url, null);
   }
   
   public InputStream getInput(String url, Map<String, String> postParameters) throws IOException
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): START");

      // Get the authentication cookie from the login page
      login();

      // Set up the connection
      HttpURLConnection conn = getConnection(url);
      conn.setRequestProperty(ACCEPT_LANGUAGE_HEADER, "en-us");
      conn.setRequestProperty(HOST_HEADER, conn.getURL().getHost());
      conn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
      conn.setRequestProperty(COOKIE_HEADER, getAuthToken());
      conn.setRequestProperty(REFERER_HEADER, getInputURL());
      conn.setRequestProperty(CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded");
      conn.setDoOutput(postParameters != null);

      printRequestHeaders(conn);
      
      if (postParameters != null)
      {
         PrintWriter writer = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
         for (Iterator<String> i = postParameters.keySet().iterator(); i.hasNext(); )
         {
            String key = i.next();
            String value = postParameters.get(key);
            writer.write(URLEncoder.encode(key, "utf-8") + "=" + URLEncoder.encode(value, "utf-8"));
            if (i.hasNext())
            {
               writer.write('&');
            }
         }
         writer.flush();
         writer.close();
      }
      
      InputStream inStream = conn.getInputStream();
      printResponseHeaders(conn);

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): END");

      return inStream;
   }

   /**
    * @return the inputURL
    */
   public String getInputURL()
   {
      return mInputURL;
   }

   /**
    * @param inputURL the inputURL to set
    */
   public void setInputURL(String inputURL)
   {
      mInputURL = inputURL;
   }

   @Override
   protected String getDefaultLoginURL()
   {
      return "http://ezinspections.com/inspManager/loginProcess.aspx";
   }

   protected String getDefaultInputURL()
   {
      return "http://ezinspections.com/inspManager/JobListSummary.aspx?s=1&Route=";
   }
}