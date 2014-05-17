/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.util
{
   public class ObjectUtil
   {
      /**
       * Calculates the number of enumerable properties on the given object.
       * 
       * @param object the object of interest
       * @return the number of enumerable properties on the object.
       */ 
      public static function length(object:Object):int
      {
         var i:int = 0;
         for(var x:String in object) 
         { 
            if(typeof (object[x]) != "function") 
            {
               i++; 
            }
         }
         return i;
      }
   }
}