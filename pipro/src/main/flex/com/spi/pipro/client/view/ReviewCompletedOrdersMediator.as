package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.ReviewCompletedOrders;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class ReviewCompletedOrdersMediator extends Mediator
   {
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ReviewCompletedOrdersMediator");
      
      public function ReviewCompletedOrdersMediator()
      {
      }
      
      public override function onRegister():void
      {
      }
      
      public override function onRemove():void
      {
         setViewComponent(null);
      }
      
      /**
       * Retrieve the view component, cast as the pipro app.
       */
      protected function get view():ReviewCompletedOrders
      {
         return viewComponent as ReviewCompletedOrders;
      }
   }
}