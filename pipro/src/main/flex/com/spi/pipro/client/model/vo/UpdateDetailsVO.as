/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.utils.IDataInput;
   import flash.utils.IDataOutput;
   
   import mx.collections.ArrayCollection;

   [Bindable]
   [RemoteClass(alias="com.spi.pipro.persistence.entity.UpdateDetails")]
   public class UpdateDetailsVO extends EntityVO
   {
      public var time:Date;
      
      public var status:String;

      public var newOrderIds:ArrayCollection;
      
      public var cancelledOrderIds:ArrayCollection;
      
      public var account:ServiceCompanyAccountVO;
      
      public override function writeExternal(output:IDataOutput):void
      {
         super.writeExternal(output);
         output.writeObject(time);
         output.writeObject(status);
         output.writeObject(newOrderIds);
         output.writeObject(cancelledOrderIds);
      }
      
      public override function readExternal(input:IDataInput):void
      {
         super.readExternal(input);
         time = input.readObject() as Date;
         status = input.readObject();
         newOrderIds = input.readObject();
         cancelledOrderIds = input.readObject();
      }
   }
}