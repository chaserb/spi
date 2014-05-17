/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.spi.SPIAccess;

/**
 * @author Chase Barrett
 */
public class Debug
{
   public static final String DEBUG_LEVEL_KEY = "com.spi.util.Debug.debugLevel";
   public static final String LOG_PATH_KEY = "com.spi.util.Debug.logPath";
   public static final int OFF = 0;
   public static final int LOW = 1;
   public static final int MED = 2;
   public static final int HIGH = 3;
   public static final SimpleDateFormat DEBUG_FORMAT = new SimpleDateFormat("[HH:mm:ss.SSS] ");
   public static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

   private static int cDebugLevel = OFF;
   private static PrintStream cOutStream = null;
   private static PrintStream cLogStream = null;
   private static Properties cEnv;

   /* ---------------------------- Constructors ---------------------------- */

   static
   {
      try
      {
         cDebugLevel = Integer.parseInt(getEnvironment().getProperty(DEBUG_LEVEL_KEY));
      }
      catch (Exception e)
      {
      }
      if (cDebugLevel < OFF) cDebugLevel = OFF;
      if (cDebugLevel > HIGH) cDebugLevel = HIGH;
      cOutStream = System.out;
      try
      {
         File logFile = null;
         if (getEnvironment().getProperty(LOG_PATH_KEY) != null)
         {
            File logPath = new File(getEnvironment().getProperty(LOG_PATH_KEY));
            if (!logPath.exists())
            {
               if (logPath.createNewFile()) logFile = logPath;
            } else if (logPath.isFile())
            {
               logFile = logPath;
            } else if (logPath.isDirectory())
            {
               logFile = new File(logPath, "inspi" + LOG_FORMAT.format(new Date()) + ".log");
            }
         }
         if (logFile != null)
         {
            cLogStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile.getAbsolutePath(), false)));
         }
      }
      catch (Exception e)
      {
         debugException("Couldn't initialize the log file", e);
         cLogStream = null;
      }
   }

   /* ------------------------------ Accessors ----------------------------- */

   public static void log(String msg)
   {
      log(msg, true);
   }

   public static void log(String msg, boolean includeOutStream)
   {
      synchronized (cOutStream)
      {
         if (includeOutStream)
         {
            cOutStream.print(msg);
            cOutStream.println();
            cOutStream.flush();
         }
         if (cLogStream != null)
         {
            cLogStream.print(msg);
            cLogStream.println();
            cLogStream.flush();
         }
      }
   }

   public static void debug(int level, String msg)
   {
      if (checkLevel(level))
      {
         synchronized (cOutStream)
         {
            cOutStream.print(DEBUG_FORMAT.format(new Date()));
            cOutStream.print(msg);
            cOutStream.println();
            cOutStream.flush();
            if (cLogStream != null)
            {
               cLogStream.print(DEBUG_FORMAT.format(new Date()));
               cLogStream.print(msg);
               cLogStream.println();
               cLogStream.flush();
            }
         }
      }
   }

   public static void debugException(String msg)
   {
      debugException(msg, null);
   }

   public static void debugException(String msg, Throwable t)
   {
      synchronized (cOutStream)
      {
         cOutStream.print(DEBUG_FORMAT.format(new Date()));
         cOutStream.println(msg);
         if (t != null)
         {
            t.printStackTrace(cOutStream);
         }
         cOutStream.flush();
         if (cLogStream != null)
         {
            cLogStream.print(DEBUG_FORMAT.format(new Date()));
            cLogStream.println(msg);
            if (t != null) t.printStackTrace(cLogStream);
            cLogStream.flush();
         }
      }
   }

   public static void debugException(int level, String msg, Throwable t)
   {
      if (checkLevel(level))
      {
         debugException(msg, t);
      }
   }

   public static void closeLog()
   {
      if (cLogStream != null)
      {
         cLogStream.flush();
         cLogStream.close();
         cLogStream = null;
      }
   }

   public static void setEnvironment(Properties env)
   {
      cEnv = env;
   }

   public static Properties getEnvironment()
   {
      if (cEnv == null)
      {
         cEnv = SPIAccess.getInstance().getEnvironment();
      }
      return cEnv;
   }

   public static int getLevel()
   {
      return cDebugLevel;
   }

   public static InputStream dumpStream(InputStream is) throws IOException
   {
      if (Debug.checkLevel(Debug.MED))
      {
         int datum = 0;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         while ((datum = is.read()) >= 0)
         {
            baos.write(datum);
         }
         is = new ByteArrayInputStream(baos.toByteArray());
         InputStreamReader reader = new InputStreamReader(is);
         char[] buf = new char[1024];
         int numRead;
         while ((numRead = reader.read(buf)) >= 0)
         {
            if (numRead < 1024)
            {
               StringBuffer stringBuf = new StringBuffer();
               stringBuf.append(buf);
               stringBuf.setLength(numRead);
               cOutStream.print(stringBuf.toString());
               if (Debug.checkLevel(Debug.HIGH))
               {
                  cLogStream.print(stringBuf.toString());
               }
            } else
            {
               cOutStream.print(buf);
               if (Debug.checkLevel(Debug.HIGH))
               {
                  cLogStream.print(buf);
               }
            }
         }
         is.reset();
      }
      return is;
   }

   /* ------------------------------ Discrete ------------------------------ */

   public static boolean checkLevel(int level)
   {
      return ((cDebugLevel != OFF) && (level <= cDebugLevel));
   }
}