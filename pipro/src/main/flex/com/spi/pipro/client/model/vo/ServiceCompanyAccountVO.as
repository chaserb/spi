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
   [RemoteClass(alias="com.spi.pipro.persistence.entity.ServiceCompanyAccount")]
   public class ServiceCompanyAccountVO extends EntityVO
   {
      public var _name:String;
      
      public var companyId:String;

      public var companyName:String;
      
      public var updatesEnabled:Boolean = true;
      
      public var updatePeriod:UpdatePeriod;
      
      public var updateHour:int;
      
      public var updateMinute:int;
      
      public var updateInProgress:Boolean = false;
      
      public var userName:String;
      
      public var password:String;
      
      public var nextUpdate:Date;
      
      public var lastUpdate:UpdateDetailsVO;
      
      public var active:Boolean = true;
      
      public function get name():String
      {
         return (active) ? _name : _name + " (inactive)";   
      }
      
      public function set name(name:String):void
      {
         _name = name;
      }
      
      /**
       * Time when the last update ran
       * 
       * @return if there has been an update, return the time of the most recent
       * update.  Otherwise, return null.
       */ 
      public function get lastUpdateTime():Date
      {
         return (lastUpdate) ? lastUpdate.time : null;
      }

      /**
       * Number of new orders received in the last update. 
       *
       * @return if there has been an update, return the number of new orders
       * from that update.  Otherwise, return 0.
       */
      public function get lastNewOrders():Number
      {
         return (lastUpdate && lastUpdate.newOrderIds) ? lastUpdate.newOrderIds.length : 0;
      }
      
      /**
       * Number of cancelled orders received in the last update. 
       *
       * @return if there has been an update, return the number of cancelled 
       * orders from that update.  Otherwise, return 0.
       */
      public function get lastCancelledOrders():Number
      {
         return (lastUpdate && lastUpdate.cancelledOrderIds) ? lastUpdate.cancelledOrderIds.length : 0;
      }
      
      /**
       * Status of the last update. 
       *
       * @return if there has been an update, return the status of the last
       * update.  Otherwise, return null.
       */
      public function get lastStatus():String
      {
         return (lastUpdate) ? lastUpdate.status : null;
      }

      public override function writeExternal(out:IDataOutput):void
      {
         super.writeExternal(out);
         out.writeObject(name);
         out.writeObject(companyId);
         out.writeObject(companyName);
         out.writeBoolean(updatesEnabled);
         out.writeObject((updatePeriod) ? updatePeriod.label : null);
         out.writeInt(updateHour);
         out.writeInt(updateMinute);
         out.writeBoolean(updateInProgress);
         out.writeObject(userName);
         out.writeObject(password);
         out.writeObject(nextUpdate);
         out.writeObject(lastUpdate);
         out.writeBoolean(active);
      }
      
      public override function readExternal(input:IDataInput):void
      {
         super.readExternal(input);
         name = input.readObject();
         companyId = input.readObject();
         companyName = input.readObject();
         updatesEnabled = input.readBoolean();
         updatePeriod = UpdatePeriod.valueOf(input.readObject());
         updateHour = input.readInt();
         updateMinute = input.readInt();
         updateInProgress = input.readBoolean();
         userName = input.readObject();
         password = input.readObject();
         nextUpdate = input.readObject() as Date;
         lastUpdate = input.readObject() as UpdateDetailsVO;
         if (lastUpdate)
         {
            lastUpdate.account = this;
         }
         active = input.readBoolean();
      }
   }
}