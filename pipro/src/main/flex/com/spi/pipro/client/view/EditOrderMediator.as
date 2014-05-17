package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.EditOrder;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class EditOrderMediator extends Mediator
   {
      /**
       * The view component for this mediator, cast as the EditOrder
       * component. 
       */ 
      [Inject]
      public var view:EditOrder;      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.EditOrderMediator");
      
      public function EditOrderMediator()
      {
      }
      
      public override function onRegister():void
      {
      }
   }
}