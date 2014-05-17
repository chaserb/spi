/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import com.spi.pipro.client.util.Enum;
   
   public class PhoneType extends Enum
   {
      public static const Work:PhoneType = new PhoneType();
      
      public static const Home:PhoneType = new PhoneType();

      public static const Mobile:PhoneType = new PhoneType();

      public static const Fax:PhoneType = new PhoneType();
      
      /**
       * Retrieve the enum value for the given label, or null if the given
       * label is not part of this enum.
       */ 
      public static function valueOf(label:String):PhoneType
      {
         return PhoneType[label];
      }

      {
         initEnumConstants(PhoneType);
      }
   }
}