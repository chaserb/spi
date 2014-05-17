package com.spi.pipro.client.view
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.view.component.TreeNavigator;
   import com.spi.pipro.client.view.event.NavigationEvent;
   
   import mx.collections.XMLListCollection;
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   /**
    * Mediator for the tree navigation component. 
    */  
   public class TreeNavigatorMediator extends Mediator
   {
      /**
       * An easy hook to retrieving this mediator by name.
       * 
       * @default "TreeNavigatorMediator"
       */
      public static const NAME:String = "TreeNavigatorMediator";
      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.TreeNavigatorMediator");

      /**
       * Model for the navigation tree
       */ 
      private var _model:XMLList;
      
      /**
       * Constructor.
       * 
       * @param viewComponent the navigator component view that this mediator
       *        is mediating.
       */
      public function TreeNavigatorMediator(viewComponent:TreeNavigator)
      {
         super(NAME, viewComponent);
         _model = generateModel();
         viewComponent.dataProvider = _model;
         viewComponent.openItems = _model;
      }
      
      /* ------------------------ Pure MVC Framework ------------------------ */ 
      
      /**
       * Register for all the navigation events that the navigation tree will 
       * send.
       */  
      public override function onRegister():void
      {
         super.onRegister();
         view.addEventListener(NavigationEvent.NAVIGATION_TARGET_SELECTED, navigationHandler);
      }
      
      /**
       * Un-register for all the navigation events that the navigation tree will 
       * send.
       */  
      public override function onRemove():void
      {
         super.onRemove();
         view.removeEventListener(NavigationEvent.NAVIGATION_TARGET_SELECTED, navigationHandler);
         setViewComponent(null);
      }
      
      public override function listNotificationInterests():Array
      {
         return [PIProContext.NAV_TARGET_CHANGED];
      }
      
      public override function handleNotification(notification:INotification):void
      {
         if (notification.getName() == PIProContext.NAV_TARGET_CHANGED)
         {
            var target:Object = notification.getBody();
            _log.debug("Navigation Changed: " + target);
            
            // Find the leaf nodes in the model whose id attribute matches the
            // navigation target name.  Only match those nodes which are 
            // unique.
            var matches:XMLList = _model..leaf.(@id == target);
            view.selectedItem = (matches.length() == 1) ? matches[0] : null;
         }
      }
      
      /* -------------------------- Event Handlers -------------------------- */ 
      
      protected function navigationHandler(event:NavigationEvent):void
      {
         if (view.selectedItem && view.selectedItem.hasOwnProperty("@id"))
         {
            sendNotification(view.selectedItem["@id"]);
         }
      }
      
      /* ------------------------- Private Functions ------------------------ */ 
      
      /**
       * Cast the viewComponent to its actual type.
       * 
       * @return The viewComponent cast to TreeNavigator
       */
      private function get view():TreeNavigator 
      {
         return viewComponent as TreeNavigator;
      }
      
      private function generateModel():XMLList
      {
         var ret:XML = 
            <root label="Navigation Links">
               <leaf label="Home" id={PIProContext.NAV_TARGET_HOME} icon="home"/>
               <branch label="Orders" icon="orders">
                  <leaf label="Dispatch New Orders" id={PIProContext.NAV_TARGET_DISPATCH_NEW_ORDERS} icon="orders"/>
                  <leaf label="Enter Completed Orders" id={PIProContext.NAV_TARGET_ENTER_COMPLETED_ORDERS} icon="orders"/>
                  <leaf label="Review Completed Orders" id={PIProContext.NAV_TARGET_REVIEW_COMPLETED_ORDERS} icon="orders"/>
               </branch>
               <branch label="Reports" icon="reports">
                  <leaf label="Order Volume by Month" id={PIProContext.NAV_TARGET_VIEW_REPORT} icon="reports"/>
                  <leaf label="Order Volume by Zip Code" id={PIProContext.NAV_TARGET_VIEW_REPORT} icon="reports"/>
                  <leaf label="Manage Reports" id={PIProContext.NAV_TARGET_MANAGE_REPORTS} icon="reports"/>
               </branch>
               <branch label="Configuration" icon="manage">
                  <leaf label="Manage People" id={PIProContext.NAV_TARGET_MANAGE_PEOPLE} icon="manage"/>
                  <leaf label="Manage Scheduled Tasks" id={PIProContext.NAV_TARGET_MANAGE_SCHEDULE} icon="manage"/>
                  <leaf label="Manage Service Companies" id={PIProContext.NAV_TARGET_MANAGE_SERVICE_COMPANIES} icon="manage"/>
                  <leaf label="Manage Order Assignments" id={PIProContext.NAV_TARGET_MANAGE_ORDER_ASSIGNMENTS} icon="manage"/>
               </branch>
            </root>;
            
         return ret.children();
      }
   }
}