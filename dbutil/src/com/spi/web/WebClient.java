/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.spi.util.DOMUtil;
import com.spi.util.Debug;

/**
 * Class for connecting to a web server, and for parsing the returning HTML.
 * 
 * @author Chase Barrett
 */
public abstract class WebClient
{
   public static final String ACCEPT_HEADER = "Accept";
   public static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
   public static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
   public static final String CACHE_CONTROL_HEADER = "Cache-Control";
   public static final String CONNECTION_HEADER = "Connection";
   public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
   public static final String CONTENT_LENGTH_HEADER = "Content-Length";
   public static final String CONTENT_TYPE_HEADER = "Content-Type";
   public static final String COOKIE_HEADER = "Cookie";
   public static final String HOST_HEADER = "Host";
   public static final String REFERER_HEADER = "Referer";
   public static final String SET_COOKIE_HEADER = "Set-Cookie";
   public static final String USER_AGENT_HEADER = "User-Agent";

   private String mLoginURL;
   private String mInputUser;
   private String mInputPass;
   private PrintWriter mTidyOut;
   private Properties mEnv;
   private String mName;
   private String mAuthToken;

   public WebClient(Properties env, String name)
   {
      mEnv = env;
      mName = name;
      setInputUser(env.getProperty("com.spi." + name + ".inputUser"));
      setInputPass(env.getProperty("com.spi." + name + ".inputPassword"));
      setLoginURL(env.getProperty("com.spi." + name + ".loginURL", getDefaultLoginURL()));
      HttpURLConnection.setFollowRedirects(false);
   }

   /**
    * Login to the vendor's web site using the current login credentials and
    * login URL.
    * 
    * @return the cookie string returned by the web host.
    * @throws IOException
    */
   public abstract void login() throws IOException;

   /**
    * Get an input stream from the given URL, logging in if necessary.
    * 
    * @param url the url to open.
    * @return an inputstream from the given url.
    * @throws IOException
    */
   public abstract InputStream getInput(String url) throws IOException;

   /**
    * Parse an XML document from the given URL, logging in if necessary.
    * 
    * @param url the url to open.
    * @return an XML document representing the content of the given url.
    * @throws IOException
    */
   public Document parseInput(String url) throws IOException
   {
      return parseInput(getInput(url));
   }

   /**
    * Parse an XML document from the given input stream, logging in if
    * necessary.
    * 
    * @param inStream the input stream to read.
    * @return an XML document representing the content of the given url.
    * @throws IOException
    */
   public Document parseInput(InputStream inStream)
   {
      Tidy tidy = new Tidy();
      tidy.setErrout(getTidyOut());
      Document doc = tidy.parseDOM(inStream, null);
      if (Debug.checkLevel(Debug.HIGH))
      {
         DOMUtil.printDOM(doc, getTidyOut());
      }
      return doc;
   }

   /**
    * @return the loginURL
    */
   public String getLoginURL()
   {
      return mLoginURL;
   }

   /**
    * @param loginURL the loginURL to set
    */
   public void setLoginURL(String loginURL)
   {
      mLoginURL = loginURL;
   }

   /**
    * @return the inputUser
    */
   public String getInputUser()
   {
      return mInputUser;
   }

   /**
    * @param inputUser the inputUser to set
    */
   public void setInputUser(String inputUser)
   {
      mInputUser = inputUser;
   }

   /**
    * @return the inputPass
    */
   public String getInputPass()
   {
      return mInputPass;
   }

   /**
    * @param inputPass the inputPass to set
    */
   public void setInputPass(String inputPass)
   {
      mInputPass = inputPass;
   }
   
   public String getAuthToken()
   {
      return mAuthToken;
   }
   
   public void setAuthToken(String token)
   {
      mAuthToken = token;
   }

   protected HttpURLConnection getConnection(String url) throws IOException
   {
      URL urlObj = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, "WebClient.getConnection(): Connect timeout: " + conn.getConnectTimeout());
      conn.setConnectTimeout(60000);
      return conn;
   }

   protected void printRequestHeaders(HttpURLConnection conn)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         Debug.debug(Debug.MED, "************ Request Headers: " + conn.getURL().getFile());
         Map<String, List<String>> requestMap = conn.getRequestProperties();
         for (String key : requestMap.keySet())
         {
            List<String> val = requestMap.get(key);
            Debug.debug(Debug.MED, key + "=" + val);
         }
         Debug.debug(Debug.MED, "");
      }
   }

   protected void printResponseHeaders(HttpURLConnection conn)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         Debug.debug(Debug.MED, "************ Response Headers:");
         boolean done = false;
         for (int n = 0; !done;)
         {
            String headerKey = conn.getHeaderFieldKey(n);
            String headerVal = conn.getHeaderField(n);
            if (headerKey == null && headerVal == null)
            {
               done = true;
            } else
            {
               Debug.debug(Debug.MED, headerKey + "=" + headerVal);
            }
            n++;
         }
         Debug.debug(Debug.MED, "");
      }
   }

   public void printResponse(InputStream inStream) throws IOException
   {
      if (Debug.checkLevel(Debug.HIGH))
      {
         Debug.debug(Debug.HIGH, "************ Response:");
         BufferedInputStream buf = new BufferedInputStream(inStream);
         StringBuffer response = new StringBuffer();
         for (int curChar = buf.read(); curChar >= 0; curChar = buf.read())
         {
            response.append((char) curChar);
         }
         Debug.debug(Debug.HIGH, response.toString());
         Debug.debug(Debug.HIGH, "");
      }
   }

   protected void printResponse(HttpURLConnection conn) throws IOException
   {
      printResponse(conn.getInputStream());
   }

   public PrintWriter getTidyOut()
   {
      if (mTidyOut == null)
      {
         if (Boolean.parseBoolean(getEnvironment().getProperty("com.spi." + mName + ".generateTidyWarnings")))
         {
            mTidyOut = new PrintWriter(System.err);
            String tidyFileName = getEnvironment().getProperty("com.spi." + mName + ".tidyFileLog");
            if (tidyFileName != null && tidyFileName.trim().length() > 0)
            {
               try
               {
                  final OutputStream fos = new BufferedOutputStream(new FileOutputStream(tidyFileName));
                  OutputStream os = new OutputStream()
                  {
                     public void write(int arg0) throws IOException
                     {
                        fos.write(arg0);
                        System.err.write(arg0);
                     }

                     @Override
                     public void close() throws IOException
                     {
                        fos.close();
                     }

                     @Override
                     public void flush() throws IOException
                     {
                        fos.flush();
                     }

                     @Override
                     public void write(byte[] b, int off, int len) throws IOException
                     {
                        fos.write(b, off, len);
                        System.err.write(b, off, len);
                     }

                     @Override
                     public void write(byte[] b) throws IOException
                     {
                        fos.write(b);
                        System.err.write(b);
                     }
                  };
                  mTidyOut = new PrintWriter(os);
               }
               catch (IOException e)
               {
                  Debug.debugException("Could not open the tidy writer file, all tidy out put will go to system error", e);
               }
            }
         } 
         else
         {
            OutputStream os = new OutputStream()
            {
               public void write(int arg0)
               { /* bit bucket */
               }
            };
            mTidyOut = new PrintWriter(os);
         }
      }
      return mTidyOut;
   }

   protected Properties getEnvironment()
   {
      return mEnv;
   }

   protected void appendCookies(StringBuffer buf, HttpURLConnection conn)
   {
      List<String> cookieStrings = conn.getHeaderFields().get(SET_COOKIE_HEADER);
      if (cookieStrings != null && cookieStrings.size() > 0)
      {
         Map<String, String> cookies = parseCookies(buf.toString());
         for (int i = cookieStrings.size() - 1; i >= 0; i--)
         {
            Cookie cookie = new Cookie(cookieStrings.get(i));
            cookies.put(cookie.getName(), cookie.getValue());
         }
         buf.setLength(0);
         assembleCookies(cookies, buf);
      }
   }

   protected Map<String, String> parseCookies(String cookieString)
   {
      Map<String, String> cookies = new TreeMap<String, String>();
      if (cookieString != null && cookieString.length() > 0)
      {
         StringTokenizer tok = new StringTokenizer(cookieString, "=;");
         while (tok.hasMoreTokens())
         {
            String key = tok.nextToken().trim();
            if (tok.hasMoreTokens())
            {
               String value = tok.nextToken().trim();
               cookies.put(key, value);
            }
         }
      }
      return cookies;
   }

   protected void assembleCookies(Map<String, String> cookies, StringBuffer buf)
   {
      for (Iterator<String> names = cookies.keySet().iterator(); names.hasNext();)
      {
         String name = names.next();
         String value = cookies.get(name);
         if (value != null && value.trim().length() > 0)
         {
            if (buf.length() > 0)
            {
               buf.append("; ");
            }
            buf.append(name);
            buf.append("=");
            buf.append(value);
         }
      }
   }

   abstract protected String getDefaultLoginURL();
}