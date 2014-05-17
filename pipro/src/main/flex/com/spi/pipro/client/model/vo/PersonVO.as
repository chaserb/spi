/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.utils.IDataInput;
   import flash.utils.IDataOutput;
   import flash.utils.IExternalizable;
   
   import mx.collections.ArrayCollection;
   import mx.collections.IList;

   [Bindable]
   [RemoteClass(alias="com.spi.pipro.persistence.entity.Person")]
   public class PersonVO extends EntityVO
   {
      public var _userName:String;
      
      public var password:String;
      
      public var firstName:String;
      
      public var lastName:String;
      
      private var _address:AddressVO;
      
      private var _email:EmailAddressVO;
      
      private var _phoneNumbers:IList; // of PhoneNumberVO
      
      public var active:Boolean = true;
      
      public function get userName():String
      {
         return (active) ? _userName : _userName + " (inactive)";
      }
      
      public function set userName(userName:String):void
      {
         _userName = userName;
      }
      
      public function get address():AddressVO
      {
         if (!_address)
         {
            _address = new AddressVO;
         }
         return _address;
      }
      
      public function set address(addressvo:AddressVO):void
      {
         _address = addressvo;
      }
      
      public function get email():EmailAddressVO
      {
         if (!_email)
         {
            _email = new EmailAddressVO;
         }
         return _email;
      }
      
      public function set email(emailvo:EmailAddressVO):void
      {
         _email = emailvo;
      }
      
      public function get phoneNumbers():IList
      {
         if (!_phoneNumbers)
         {
            _phoneNumbers = new ArrayCollection();
         }
         return _phoneNumbers;
      }
      
      public function set phoneNumbers(numbers:IList):void
      {
         _phoneNumbers = numbers;
      }

      public override function writeExternal(output:IDataOutput):void
      {
         super.writeExternal(output);
         output.writeObject(_userName);
         output.writeObject(password);
         output.writeObject(firstName);
         output.writeObject(lastName);
         output.writeObject(_address);
         output.writeObject(_email);
         output.writeObject(_phoneNumbers);
         output.writeBoolean(active);
      }
      
      public override function readExternal(input:IDataInput):void
      {
         super.readExternal(input);
         _userName = input.readObject();
         password = input.readObject();
         firstName = input.readObject();
         lastName = input.readObject();
         _address = input.readObject();
         _email = input.readObject();
         _phoneNumbers = input.readObject();
         active = input.readBoolean();
      }
   }
}