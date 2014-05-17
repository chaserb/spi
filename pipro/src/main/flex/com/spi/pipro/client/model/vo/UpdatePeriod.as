/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import com.spi.pipro.client.util.Enum;
   
   public class UpdatePeriod extends Enum
   {
      public static const Hourly:UpdatePeriod = new UpdatePeriod();

      public static const Daily:UpdatePeriod = new UpdatePeriod();
      
      /**
       * Retrieve the enum value for the given label, or null if the given
       * label is not part of this enum.
       */ 
      public static function valueOf(label:String):UpdatePeriod
      {
         return UpdatePeriod[label];
      }

      {
         initEnumConstants(UpdatePeriod);
      }
   }
}