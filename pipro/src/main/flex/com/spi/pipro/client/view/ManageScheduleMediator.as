package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.ManageSchedule;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class ManageScheduleMediator extends Mediator
   {
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ManageScheduleMediator");
      
      public function ManageScheduleMediator(viewComponent:Object=null)
      {
      }
      
      public override function onRegister():void
      {
         super.onRegister();
      }
      
      public override function onRemove():void
      {
         setViewComponent(null);
      }
      
      /**
       * Retrieve the view component, cast as the pipro app.
       */
      protected function get view():ManageSchedule
      {
         return viewComponent as ManageSchedule;
      }
   }
}