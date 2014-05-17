/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fafs;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.spi.util.Debug;
import com.spi.web.Cookie;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class Web extends WebClient
{
   private static final String DEBUG_PREFIX = "Web.";

   /**
    * Web constructor.
    * 
    * @param env client properties identifying the login URL, user name, and
    *        password to the First American website.
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
         // Set the request parameters
         HttpURLConnection loginConn = getConnection(getLoginURL());
         loginConn.setRequestProperty(REFERER_HEADER, getLoginURL());
         loginConn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
         loginConn.addRequestProperty(CACHE_CONTROL_HEADER, "no-cache");
         loginConn.setDoOutput(true);

         printRequestHeaders(loginConn);

         PrintWriter writer = new PrintWriter(new BufferedOutputStream(loginConn.getOutputStream()));
         writer.write("__VIEWSTATE=");
         writer.write("&");
         writer.write("txtUserName=" + getInputUser());
         writer.write("&");
         writer.write("txtPassword=" + getInputPass());
         writer.write("&");
         writer.write("btn_login=Login");
         writer.close();

         printResponseHeaders(loginConn);
         printResponse(loginConn);

         // Read the session cookie
         List<String> cookieStrings = loginConn.getHeaderFields().get(SET_COOKIE_HEADER);
         StringBuffer cookieStringBuf = new StringBuffer();
         if (cookieStrings != null)
         {
            for (Iterator<String> i = cookieStrings.iterator(); i.hasNext();)
            {
               Cookie cookie = new Cookie(i.next());
               cookieStringBuf.append(cookie.getName());
               cookieStringBuf.append("=");
               cookieStringBuf.append(cookie.getValue());
               if (i.hasNext())
               {
                  cookieStringBuf.append("; ");
               }
            }
         }
         setAuthToken(cookieStringBuf.toString());
      }
   }

   @Override
   public InputStream getInput(String url) throws IOException
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

      // Get the connection's input stream
      printRequestHeaders(conn);
      InputStream inStream = conn.getInputStream();
      printResponseHeaders(conn);

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): END");

      return inStream;
   }

   @Override
   protected String getDefaultLoginURL()
   {
      return "https://vendor.fafs.com/pages/login.aspx";
   }
}