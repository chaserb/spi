package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.DispatchNewOrders;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class DispatchNewOrdersMediator extends Mediator
   {
      /**
       * The view component for this mediator, cast as the DispatchNewOrders
       * component. 
       */ 
      [Inject]
      public var view:DispatchNewOrders;      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.DispatchNewOrdersMediator");
      
      public function DispatchNewOrdersMediator()
      {
      }
      
      public override function onRegister():void
      {
      }
   }
}