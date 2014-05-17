/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.appengine.repackaged.com.google.common.util.Base64;

public class HashUtil
{
   /**
    * One way hash the given plaintext using the SHA-1 algorithm.
    * 
    * @param plainText the plaintext you want to hash.
    * @return a base64 encoding of the hashed plaintext.
    */
   public static String hash(String plainText)
   {
      try
      {
         MessageDigest md = MessageDigest.getInstance("SHA");
         byte[] digest = md.digest(plainText.getBytes());
         return Base64.encode(digest);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException("Configuration Error: Could not retrieve the SHA hashing algorithm.");
      }
   }
}
