/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.utils.IDataInput;
   import flash.utils.IDataOutput;
   import flash.utils.IExternalizable;

   [Bindable]
   [RemoteClass(alias="com.spi.pipro.persistence.entity.EmailAddress")]
   public class EmailAddressVO implements IExternalizable
   {
      public var email:String;

      public function EmailAddressVO()
      {
      }
      
      public function writeExternal(output:IDataOutput):void
      {
         output.writeObject(email);
      }
      
      public function readExternal(input:IDataInput):void
      {
         email = input.readObject() as String;
      }
   }
}