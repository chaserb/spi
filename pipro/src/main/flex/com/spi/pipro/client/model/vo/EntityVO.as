/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.utils.ByteArray;
   import flash.utils.IDataInput;
   import flash.utils.IDataOutput;
   import flash.utils.IExternalizable;

   public class EntityVO extends AbstractVO implements IExternalizable
   {
      /**
       * Key of an entity that has not yet been persisted.
       */ 
      public static const NEW_KEY:String = "0";
      
      private var _key:String = NEW_KEY;
      
      public function get key():String
      {
         return _key;
      }
      
      public function writeExternal(output:IDataOutput):void
      {
         // WARN: Destination is a java long primitive
         output.writeObject(_key);
      }
      
      public function readExternal(input:IDataInput):void
      {
         // WARN: Source is a java long primitive.
         _key = input.readObject();
      }
   }
}