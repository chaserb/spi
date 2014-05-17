/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.web;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Cookie
{
   static final String DOMAIN_PROP = "domain";
   static final String NAME_PROP = "name";
   static final String PATH_PROP = "path";
   static final String SECURE_PROP = "secure";
   static final String VALUE_PROP = "value";

   private Map<String, Object> mKeyValuePairs;

   public Cookie(String cookieString)
   {
      parseString(cookieString);
   }

   public String getName()
   {
      return (String) getKeyValuePairs().get(NAME_PROP);
   }

   public String getValue()
   {
      return (String) getKeyValuePairs().get(VALUE_PROP);
   }

   public String getPath()
   {
      return (String) getKeyValuePairs().get(PATH_PROP);
   }

   public String getDomain()
   {
      return (String) getKeyValuePairs().get(DOMAIN_PROP);
   }

   public boolean isSecure()
   {
      Boolean secure = (Boolean) getKeyValuePairs().get(SECURE_PROP);
      return (secure == null) ? false : secure.booleanValue();
   }

   void parseString(String cookieString)
   {
      StringTokenizer tokenizer = new StringTokenizer(cookieString, ";");
      String token;
      String key;
      Object value;
      int equalsIndex;
      while (tokenizer.hasMoreTokens())
      {
         token = tokenizer.nextToken();
         equalsIndex = token.indexOf('=');
         if (equalsIndex > 0)
         {
            key = token.substring(0, equalsIndex);
            value = token.substring(equalsIndex + 1, token.length());
            if (getKeyValuePairs().isEmpty())
            {
               getKeyValuePairs().put(NAME_PROP, key);
               key = VALUE_PROP;
            }
         } else
         {
            key = token;
            value = Boolean.TRUE;
         }
         getKeyValuePairs().put(key.toLowerCase(), value);
      }
   }

   Map<String, Object> getKeyValuePairs()
   {
      if (mKeyValuePairs == null)
      {
         mKeyValuePairs = new HashMap<String, Object>();
      }
      return mKeyValuePairs;
   }
}