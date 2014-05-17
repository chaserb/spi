/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.util
{
   public class StringUtil
   {
      public static function leadingZeros(length:int, num:Number): String
      {
         var retString:String = (isNaN(num)) ? "" : num.toString();
         while (retString.length < length)
         {
            retString = '0' + retString;
         }  
         return retString;
      }   
   }
}