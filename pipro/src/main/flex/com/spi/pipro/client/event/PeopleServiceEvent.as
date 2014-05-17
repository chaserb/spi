/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.event
{
   import flash.events.Event;
   
   public class PeopleServiceEvent extends Event
   {
      /**
       * Service method for retrieving all the active people.
       */ 
      public static const GET_PEOPLE_METHOD:String = "getPeopleMethod";
      /**
       * Service method for retrieving all the inactive people.
       */ 
      public static const GET_INACTIVE_PEOPLE_METHOD:String = "getInactivePeopleMethod";
      /**
       * Service method for retrieving an individual person, whether by key or
       * by username.
       */ 
      public static const GET_PERSON_METHOD:String = "getPersonMethod";
      /**
       * Service method for adding a person.
       */ 
      public static const ADD_PERSON_METHOD:String = "addPersonMethod";
      /**
       * Service method for updating a person.
       */ 
      public static const UPDATE_PERSON_METHOD:String = "updatePersonMethod";
      /**
       * Service method for removing a person.
       */ 
      public static const REMOVE_PERSON_METHOD:String = "removePersonMethod";
      /**
       * Service method for reactivating a person.
       */ 
      public static const REACTIVATE_PEOPLE_METHOD:String = "reactivatePeopleMethod";
      /**
       * Service method for setting a person's password.
       */ 
      public static const SET_PASSWORD_METHOD:String = "setPasswordMethod";
      
      public function PeopleServiceEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
      {
         super(type, bubbles, cancelable);
      }
   }
}