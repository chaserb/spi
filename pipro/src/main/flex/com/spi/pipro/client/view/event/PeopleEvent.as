/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.view.event
{
   import flash.events.Event;

   /**
    * Event fired by the Manage People Page
    */ 
   public class PeopleEvent extends Event
   {
      /**
       * The user clicked the "Add" button.
       */ 
      public static const PERSON_ADD:String = "personAdd";
      /**
       * The user clicked the "Delete" button.
       */ 
      public static const PERSON_DELETE:String = "personDelete";
      /**
       * The user clicked the "Save" button.
       */ 
      public static const PERSON_SAVE:String = "personSave";
      /**
       * The user clicked the "Cancel" button.
       */ 
      public static const PERSON_CANCEL:String = "personCancel"; 
      /**
       * The user selected a person in the person list.
       */ 
      public static const PERSON_SELECT:String = "personSelect"; 
      /**
       * The user changed some property, perhaps incrementally, on a chosen 
       * person object.
       */ 
      public static const PERSON_PROPERTY_CHANGED:String = "personPropertyChanged";
      /**
       * The user changed some property on a chosen person object, and has moved
       * on to another property.
       */ 
      public static const PERSON_PROPERTY_COMMITTED:String = "personPropertyCommitted";
      /**
       * The user chose to manage inactive users.
       */ 
      public static const PERSON_MANAGE_INACTIVE:String = "personManageInactive"; 
      /**
       * The user chose to reactivate inactive users.
       */ 
      public static const PERSON_REACTIVATE:String = "personReactivate"; 
      
      private var _changedProperty:String;
      
      /**
       * Constructor.
       */ 
      public function PeopleEvent(type:String, changedProperty:String = null, bubbles:Boolean = true, cancelable:Boolean = true)
      {
         super(type, bubbles, cancelable);
         _changedProperty = changedProperty;
      }
      
      /**
       * Return the name of the property that's changed, or null if this is not
       * a PERSON_PROPERTY_CHANGED event.
       */ 
      public function get changedProperty():String
      {
         return _changedProperty;
      }
   }
}