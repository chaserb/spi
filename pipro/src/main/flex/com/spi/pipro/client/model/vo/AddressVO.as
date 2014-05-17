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
   [RemoteClass(alias="com.spi.pipro.persistence.entity.Address")]
   public class AddressVO implements IExternalizable
   {
      public var address1:String;
      
      public var address2:String;
      
      public var city:String;
      
      public var state:String;
      
      public var zip:int;

      public function AddressVO()
      {
      }
      
      public function writeExternal(output:IDataOutput):void
      {
         output.writeObject(address1);
         output.writeObject(address2);
         output.writeObject(city);
         output.writeObject(state);
         output.writeInt(zip);
      }
      
      public function readExternal(input:IDataInput):void
      {
         address1 = input.readObject();
         address2 = input.readObject();
         city = input.readObject();
         state = input.readObject();
         zip = input.readInt();
      }
   }
}