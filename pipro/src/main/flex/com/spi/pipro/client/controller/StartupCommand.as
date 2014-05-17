package com.spi.pipro.client.controller
{
   import com.spi.pipro.client.model.BlazePeopleService;
   import com.spi.pipro.client.model.BlazeServiceCompanyAccountService;
   import com.spi.pipro.client.view.ApplicationMediator;
   import com.spi.pipro.client.view.TreeNavigatorMediator;
   
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.puremvc.as3.multicore.interfaces.INotification;
   import org.puremvc.as3.multicore.patterns.command.SimpleCommand;

   public class StartupCommand extends SimpleCommand 
   {
      //--------------------------------------------------------------------------
      //
      //  Class constants
      //
      //--------------------------------------------------------------------------
      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.controller.StartUpCommand");
      
      //--------------------------------------------------------------------------
      //
      //  Overridden methods
      //
      //--------------------------------------------------------------------------
      
      /**
       * When executed this command will create and register all necessary 
       * proxies and mediators and retrieve any dependency data if needed.
       */
      override public function execute(notification:INotification):void 
      {
         var app:pipro = notification.getBody() as pipro;
         
         _log.info("Starting up application...");
         
         // register all proxies in order for dependencies
         facade.registerProxy(new BlazePeopleService());
         facade.registerProxy(new BlazeServiceCompanyAccountService());
         
         // Register all mediators for view preparation
         facade.registerMediator(new ApplicationMediator(app));
         facade.registerMediator(new TreeNavigatorMediator(app.treeNavigator));
      }
   }
}
