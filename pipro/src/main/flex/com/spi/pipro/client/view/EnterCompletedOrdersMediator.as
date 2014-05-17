package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.EnterCompletedOrders;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class EnterCompletedOrdersMediator extends Mediator
   {
      /**
       * The view component for this mediator, cast as the EnterCompletedOrders
       * component. 
       */ 
      [Inject]
      public var view:EnterCompletedOrders;      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.EnterCompletedOrdersMediator");
      
      public function EnterCompletedOrdersMediator()
      {
      }
      
      public override function onRegister():void
      {
      }
   }
}