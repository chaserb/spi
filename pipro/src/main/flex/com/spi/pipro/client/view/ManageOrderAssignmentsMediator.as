package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.ManageOrderAssignments;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class ManageOrderAssignmentsMediator extends Mediator
   {
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ManageOrderAssignmentsMediator");
      
      public function ManageOrderAssignmentsMediator()
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
      protected function get view():ManageOrderAssignments
      {
         return viewComponent as ManageOrderAssignments;
      }
   }
}