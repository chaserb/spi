/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.utils.IExternalizable;
   
   [Bindable]
   [RemoteClass(alias="com.spi.pipro.persistence.entity.PhoneNumber")]
   public class PhoneNumberVO implements IExternalizable
   {
      public function PhoneNumberVO()
      {
         public var number:String;
         
         public var type:PhoneType;
         
         public function writeExternal(output:IDataOutput):void
         {
            output.writeObject(number);
            output.writeObject(type.label);
         }
         
         public function readExternal(input:IDataInput):void
         {
            number = input.readObject() as String;
            type = PhoneType.valueOf(input.readObject());
         }
      }
   }
}