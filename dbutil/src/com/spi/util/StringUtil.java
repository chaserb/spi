/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.util;

public class StringUtil
{
   public static String cleanString(String dirty)
   {
      return cleanString(dirty, -1);
   }

   public static String cleanString(String dirty, int maxLength)
   {
      if (dirty == null)
      {
         return null;
      }
      dirty = dirty.replace('\u00a0', ' ').trim();
      if (isEmpty(dirty))
      {
         return null;
      } else if (dirty.trim().equals("null"))
      {
         return null;
      } else if ((maxLength > 0) && (dirty.length() > maxLength))
      {
         return dirty.substring(0, maxLength);
      } else
      {
         return dirty;
      }
   }

   public static boolean isEmpty(String string)
   {
      return (string == null || string.trim().length() == 0);
   }
   
   public static void prependComma(StringBuffer buf)
   {
      if (buf != null && buf.length() > 0) buf.append(", ");
   }
}
