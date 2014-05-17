/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.safeguard;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.spi.util.Debug;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class Web extends WebClient
{
   String mPreInputURL;
   String mInputURL;

   private static final String DEBUG_PREFIX = "Web.";
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

   static final String USER_VAR_NAME = "txtUsername";
   static final String PASS_VAR_NAME = "txtPassword";
   static final String HIDDEN_RUN_PARAMETERS = "hidden_run_parameters";
   static final String ORIENTATION = "ORIENTATION";
   static final String P_BEGINDATE = "P_BEGINDATE";
   static final String P_ENDDATE = "P_ENDDATE";

   /**
    * Web constructor.
    * 
    * @param env client properties identifying the login URL, user name, and
    *        password to the First American website.
    */
   public Web(Properties env, String name)
   {
      super(env, name);
      setPreInputURL(env.getProperty("com.spi." + name + ".preInputURL", getDefaultPreInputURL()));
      setInputURL(env.getProperty("com.spi." + name + ".inputURL", getDefaultInputURL()));
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
         writer.write(USER_VAR_NAME);
         writer.write("=");
         writer.write(getInputUser());
         writer.write("&");
         writer.write(PASS_VAR_NAME);
         writer.write("=");
         writer.write(getInputPass());
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
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): START");

      // Get the authentication cookie from the login page
      login();

      // Set up the connection
      HttpURLConnection conn = getConnection(getPreInputURL());
      conn.setRequestProperty(ACCEPT_LANGUAGE_HEADER, "en-us");
      conn.setRequestProperty(HOST_HEADER, conn.getURL().getHost());
      conn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
      conn.setRequestProperty(COOKIE_HEADER, getAuthToken());

      // Get the next set of cookies connection's input stream
      printRequestHeaders(conn);
      printResponseHeaders(conn);
      setAuthToken(getAuthToken() + "; " + conn.getHeaderField(SET_COOKIE_HEADER));

      conn = getConnection(url);
      conn.setRequestProperty(ACCEPT_LANGUAGE_HEADER, "en-us");
      conn.setRequestProperty(HOST_HEADER, conn.getURL().getHost());
      conn.setRequestProperty(CONNECTION_HEADER, "Keep-Alive");
      conn.setRequestProperty(COOKIE_HEADER, getAuthToken());
      conn.setDoOutput(true);

      printRequestHeaders(conn);
      PrintWriter writer = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
      writer.write(HIDDEN_RUN_PARAMETERS);
      writer.write("=");
      writer.write(URLEncoder.encode(getPreInputURL().split("\\?")[1], Charset.defaultCharset().name()));
      writer.write("&");
      writer.write(ORIENTATION);
      writer.write("=");
      writer.write("Portrait");
      writer.write("&");
      writer.write(P_BEGINDATE);
      writer.write("=");
      writer.write("01/01/2000");
      writer.write("&");
      writer.write(P_ENDDATE);
      writer.write("=");
      writer.write(DATE_FORMAT.format(new Date()));
      writer.close();

      InputStream inStream = conn.getInputStream();
      printResponseHeaders(conn);

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): END");

      return inStream;
   }

   /**
    * @return the inputURL
    */
   public String getPreInputURL()
   {
      return mPreInputURL;
   }

   /**
    * @param inputURL the inputURL to set
    */
   public void setPreInputURL(String inputURL)
   {
      mPreInputURL = inputURL;
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
      return "https://inspi2.safeguardproperties.com/inspi2/login.php";
   }

   protected String getDefaultPreInputURL()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("https://inspi2.safeguardproperties.com/inspi2/reports/rwservlet?inspi2rpt+report=spi_inspi2_open_order_assignment_master_user_input.rdf+destype=cache+desformat=html+paramform=yes+inspector=");
      buf.append(getInputUser());
      buf.append("+authid=");
      buf.append(getInputUser());
      buf.append('/');
      buf.append(getInputPass());
      return buf.toString();
   }

   protected String getDefaultInputURL()
   {
      return "http://pappsrv1.safeguardproperties.com:7777/reports/rwservlet?";
   }
}