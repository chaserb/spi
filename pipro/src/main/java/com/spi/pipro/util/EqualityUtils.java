/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.util;

/**
 * Collection of utility methods for checking the equality of two objects
 * without having to worry if one or both of the objects is null.
 * 
 * @author Chase Barrett
 */
public class EqualityUtils
{
   /**
    * Tests two objects for equality in a null-safe manner
    * 
    * @param o1 the first object under consideration
    * @param o2 the second object under consideration
    * @return true if both objects are null, or if neither are null and o1
    *         equals o2. Otherwise return false.
    */
   public static boolean equals(Object o1, Object o2)
   {
      return (o1 == o2) ? true : (o1 == null || o2 == null) ? false : o1.equals(o2);
   }

   /**
    * Tests two strings for equality in a null-safe manner
    * 
    * @param s1 the first string under consideration
    * @param s2 the second string under consideration
    * @return true if both strings are null, or if neither are null and s1
    *         equals s2 in a case insensitive manner. Otherwise return false.
    */
   public static boolean equalsIgnoreCase(String s1, String s2)
   {
      return (s1 == s2) ? true : (s1 == null || s2 == null) ? false : s1.equalsIgnoreCase(s2);
   }
}
