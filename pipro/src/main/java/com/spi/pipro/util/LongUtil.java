/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.util;

public class LongUtil
{
   /**
    * Reads a long primitive from the given byte array, starting with bytes[0]
    * (MSB) and ending with bytes[7] (LSB).
    * 
    * @param bytes the byte array
    * @return the long primitive
    * @throws ArrayIndexOutOfBoundsException if the given array is less than 8
    *         bytes in length.
    */
   public static long readLong(byte[] bytes)
   {
      long longPrimitive = bytes[0] << 7 | bytes[1] << 6 | bytes[2] << 5 | bytes[3] << 4 | bytes[4] << 3 | bytes[5] << 2 | bytes[6] << 1 | bytes[7];
      return longPrimitive;
   }

   /**
    * Reads a long primitive from the given string, assuming the string is a
    * hexadecimal representation of a long integer.
    * 
    * @param hexadecimal the string representation of the long integer
    * @return the long integer
    * @throws NumberFormatException if hexadecimal is null or has a length of
    *         zero.
    */
   public static long readLong(String hexadecimal)
   {
      return Long.parseLong(hexadecimal, 16);
   }
}
