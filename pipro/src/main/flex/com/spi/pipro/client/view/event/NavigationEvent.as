package com.spi.pipro.client.view.event
{
   import flash.events.Event;
   
   /**
    * Event fired by the navigation view.
    */  
   public class NavigationEvent extends Event
   {
      /**
       * Indication from the navigation component that the user has selected a 
       * navigation target.
       */ 
      public static const NAVIGATION_TARGET_SELECTED:String = "navigationTargetSelected";
      
      /**
       * Indication from the navigation component that the user has selected to
       * go forward in navigation history.
       */ 
      public static const NAVIGATION_FORWARD:String = "navigationForward";

      /**
       * Indication from the navigation component that the user has selected to
       * go back in navigation history.
       */ 
      public static const NAVIGATION_BACK:String = "navigationBack";

      /**
       * Constructor.
       */
      public function NavigationEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = true) 
      {
         super(type, bubbles, cancelable);
      }
   }
}