/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.view.event
{
   import flash.events.Event;

   /**
    * Event fired by the Manage Account Page
    */ 
   public class AccountEvent extends Event
   {
      /**
       * The user clicked the "Add" button.
       */ 
      public static const ACCOUNT_ADD:String = "accountAdd";
      /**
       * The user clicked the "Delete" button.
       */ 
      public static const ACCOUNT_DELETE:String = "accountDelete";
      /**
       * The user clicked the "Save" button.
       */ 
      public static const ACCOUNT_SAVE:String = "accountSave";
      /**
       * The user clicked the "Cancel" button.
       */ 
      public static const ACCOUNT_CANCEL:String = "accountCancel"; 
      /**
       * The user clicked the "Update Now" button.
       */ 
      public static const ACCOUNT_UPDATE_NOW:String = "accountUpdateNow"; 
      /**
       * The user selected a account in the account list.
       */ 
      public static const ACCOUNT_SELECT:String = "accountSelect"; 
      /**
       * The user changed some property, perhaps incrementally, on a chosen 
       * account object.
       */ 
      public static const ACCOUNT_PROPERTY_CHANGED:String = "accountPropertyChanged";
      /**
       * The user changed some property on a chosen account object, and has moved
       * on to another property.
       */ 
      public static const ACCOUNT_PROPERTY_COMMITTED:String = "accountPropertyCommitted";
      /**
       * The user chose to manage inactive users.
       */ 
      public static const ACCOUNT_MANAGE_INACTIVE:String = "accountManageInactive"; 
      /**
       * The user chose to reactivate inactive users.
       */ 
      public static const ACCOUNT_REACTIVATE:String = "accountReactivate"; 
      
      private var _changedProperty:String;
      
      /**
       * Constructor.
       */ 
      public function AccountEvent(type:String, changedProperty:String = null, bubbles:Boolean = true, cancelable:Boolean = true)
      {
         super(type, bubbles, cancelable);
         _changedProperty = changedProperty;
      }
      
      /**
       * Return the name of the property that's changed, or null if this is not
       * a ACCOUNT_PROPERTY_CHANGED event.
       */ 
      public function get changedProperty():String
      {
         return _changedProperty;
      }
   }
}