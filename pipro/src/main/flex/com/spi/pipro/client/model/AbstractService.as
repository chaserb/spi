/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.PIProContext;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   import mx.rpc.events.FaultEvent;
   import mx.rpc.remoting.RemoteObject;
   
   import org.robotlegs.mvcs.Actor;
   
   public class AbstractService extends Actor
   {
      /**
       * Indication that the last request failed.
       */ 
      public static const REQUEST_FAILED:String = "requestFailed";
      
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.model.AbstractService");
      
      /* -------------------------- Public Methods -------------------------- */

      /**
       * Constructor 
       */ 
      public function AbstractService()
      {
         super();
      }
      
      /* ---------------- Protected Interface for Subclasses ---------------- */
      
      public function faultHandler(e:FaultEvent):void
      {
         switch (e.fault.name)
         {
            case "Error":
               dispatch(PIProContext.ERROR, e.fault.faultString); 
               dispatch(REQUEST_FAILED);
               break;
            default:
               dispatch(PIProContext.WARNING, e.fault.faultString); 
               dispatch(REQUEST_FAILED);
               break;
         }
      }
   }
}