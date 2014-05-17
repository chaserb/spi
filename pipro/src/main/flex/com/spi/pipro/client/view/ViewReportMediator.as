package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.ViewReport;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class ViewReportMediator extends Mediator
   {
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ViewReportMediator");
      
      public function ViewReportMediator()
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
      protected function get view():ViewReport
      {
         return viewComponent as ViewReport;
      }
   }
}