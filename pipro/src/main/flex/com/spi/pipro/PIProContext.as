package com.spi.pipro
{
   import com.spi.pipro.client.controller.StartupCommand;
   import com.spi.pipro.client.view.ApplicationMediator;
   import com.spi.pipro.client.view.DispatchNewOrdersMediator;
   import com.spi.pipro.client.view.EditOrderMediator;
   import com.spi.pipro.client.view.HomePageMediator;
   import com.spi.pipro.client.view.ManageOrderAssignmentsMediator;
   import com.spi.pipro.client.view.ManagePeopleMediator;
   import com.spi.pipro.client.view.ManageReportsMediator;
   import com.spi.pipro.client.view.ManageScheduleMediator;
   import com.spi.pipro.client.view.ManageServiceCompaniesMediator;
   import com.spi.pipro.client.view.ReviewCompletedOrdersMediator;
   import com.spi.pipro.client.view.TreeNavigatorMediator;
   import com.spi.pipro.client.view.ViewOrderMediator;
   import com.spi.pipro.client.view.ViewPropertyMediator;
   import com.spi.pipro.client.view.ViewReportMediator;
   import com.spi.pipro.client.view.component.DispatchNewOrders;
   import com.spi.pipro.client.view.component.EditOrder;
   import com.spi.pipro.client.view.component.HomePage;
   import com.spi.pipro.client.view.component.ManageOrderAssignments;
   import com.spi.pipro.client.view.component.ManagePeople;
   import com.spi.pipro.client.view.component.ManageReports;
   import com.spi.pipro.client.view.component.ManageSchedule;
   import com.spi.pipro.client.view.component.ManageServiceCompanies;
   import com.spi.pipro.client.view.component.ReviewCompletedOrders;
   import com.spi.pipro.client.view.component.TreeNavigator;
   import com.spi.pipro.client.view.component.ViewOrder;
   import com.spi.pipro.client.view.component.ViewProperty;
   import com.spi.pipro.client.view.component.ViewReport;
   
   import mx.logging.Log;
   import mx.logging.targets.TraceTarget;
   
   import org.robotlegs.mvcs.Context;
   
   public class PIProContext extends Context
   {
      
      /* --------------------- Application Notifications -------------------- */
      
      /**
       * The notification name used when starting up the application.
       * 
       * @default "startup"
       */
      public static const STARTUP:String = "startup";
      
      // Status messages
      
      /**
       * Update the status bar with an informational message.  The body of the 
       * notificaiton will the the message string.
       */ 
      public static const INFO:String = "info";
      
      /**
       * Update the status bar with a warning message.  The body of the 
       * notificaiton will the the message string.
       */ 
      public static const WARNING:String = "warning";
      
      /**
       * Update the status bar with an error message.  The body of the 
       * notificaiton will the the message string.
       */ 
      public static const ERROR:String = "error";
      
      // Navigation notifications
      
      /**
       * Indication that the content panel's content has changed.  The body will
       * be one of the NAV_TARGET_XXX constants below.
       */ 
      public static const NAV_TARGET_CHANGED:String = "navTargetChanged";
      
      /**
       *  Bring the home page in a read only viewer
       */
      public static const NAV_TARGET_HOME:String = "navTargetHome";
      
      /**
       *  Bring up an order in a read only viewer
       */
      public static const NAV_TARGET_VIEW_ORDER:String = "navTargetViewOrder";
      
      /** 
       * Bring up an order in an editor
       */
      public static const NAV_TARGET_EDIT_ORDER:String = "navTargetEditOrder";
      
      /** 
       * Bring up a property in a read only viewer
       */
      public static const NAV_TARGET_VIEW_PROPERTY:String = "navTargetViewProperty";
      
      /** 
       * Bring up a UI which presents new orders, allowing them to be dispatched
       * to sub contractors.
       */
      public static const NAV_TARGET_DISPATCH_NEW_ORDERS:String = "navTargetDispatchNewOrders";
      
      /** 
       * Bring up a UI which presents my assigned orders, allowing them to be 
       * completed before submitting them for quality review.
       */
      public static const NAV_TARGET_ENTER_COMPLETED_ORDERS:String = "navTargetEnterCompletedOrders";
      
      /** 
       * Bring up a UI which presents completed orders, allowing them to be 
       * quality checked before submitting them to the service company.
       */
      public static const NAV_TARGET_REVIEW_COMPLETED_ORDERS:String = "navTargetReviewCompletedOrders";
      
      /** 
       * View the output of a named report.
       */
      public static const NAV_TARGET_VIEW_REPORT:String = "navTargetViewReport";
      
      /** 
       * View the output of a named report.
       */
      public static const NAV_TARGET_MANAGE_REPORTS:String = "navTargetManageReports";
      
      /** 
       * Bring up a UI which allows an administrator to view, add, update, 
       * and delete service companies
       */ 
      public static const NAV_TARGET_MANAGE_SERVICE_COMPANIES:String = "navTargetManageServiceCompanies";
      
      /** 
       * Bring up a UI which allows an administrator to view, add, update, 
       * and delete service companies
       */ 
      public static const NAV_TARGET_MANAGE_ORDER_ASSIGNMENTS:String = "navTargetManageOrderAssignments";
      
      /** 
       * Bring up a UI which allows an administrator to view, add, update, 
       * and delete service companies.
       */ 
      public static const NAV_TARGET_MANAGE_PEOPLE:String = "navTargetManagePeople";
      
      /** 
       * Bring up a UI which allows an administrator to view and manage 
       * scheduled tasks.
       */  
      public static const NAV_TARGET_MANAGE_SCHEDULE:String = "navTargetManageSchedule";
      
      //--------------------------------------------------------------------------
      //
      //  Class methods
      //
      //--------------------------------------------------------------------------
      
      /**
       * Returns an instance of the ApplicationFacade by the key specified. If no 
       * instance was found by that key one is created and maintained in a map so 
       * its retrievable at a later point by that key. The key is typically a UID 
       * or some other unique value such as a date/time string.
       * 
       * @param key The unique key for creating the multiton instance.
       * 
       * @return The multiton ApplicationFacade instance
       */
//      public static function getInstance(key:String):PIProContext 
//      {
//         // in the Standard version of the framework you would just inerhit an 
//         // "instance" variable (Singleton) whereas the MultiCore version 
//         // has an "instanceMap" array (Multiton) so it can manage multiple 
//         // instances of the Facade this way the application and the modules 
//         // loaded, all each using PureMVC have their own "Core" without 
//         // overwriting one another
//         if (instanceMap[key] == null) 
//         {
//            instanceMap[key] = new PIProContext(key);
//         }
//         
//         return instanceMap[key] as PIProContext;
//      }
      
      //--------------------------------------------------------------------------
      //
      //  Constructor
      //
      //--------------------------------------------------------------------------
      
      /**
       * Constructor.
       * 
       * @param key The key for creating a unique core in PureMVC. The key is 
       * typically a UID or some other unique value such as a date/time string.
       */
      public function PIProContext(contextView:pipro)
      {
         super(contextView);
      }
      
      /**
       * The view hierarchy has been built, so start the application.
       * 
       * @param app The application view instance.
       */
      override public function startup():void 
      {
         Log.addTarget(new TraceTarget());
         
         // Set up the mediator map
         mediatorMap.mapView(pipro, ApplicationMediator);
         mediatorMap.mapView(DispatchNewOrders, DispatchNewOrdersMediator);
         mediatorMap.mapView(EditOrder, EditOrderMediator);
         mediatorMap.mapView(HomePage, HomePageMediator);
         mediatorMap.mapView(ManageOrderAssignments, ManageOrderAssignmentsMediator);
         mediatorMap.mapView(ManagePeople, ManagePeopleMediator);
         mediatorMap.mapView(ManageReports, ManageReportsMediator);
         mediatorMap.mapView(ManageSchedule, ManageScheduleMediator);
         mediatorMap.mapView(ManageServiceCompanies, ManageServiceCompaniesMediator);
         mediatorMap.mapView(ReviewCompletedOrders, ReviewCompletedOrdersMediator);
         mediatorMap.mapView(TreeNavigator, TreeNavigatorMediator);
         mediatorMap.mapView(ViewOrder, ViewOrderMediator);
         mediatorMap.mapView(ViewProperty, ViewPropertyMediator);
         mediatorMap.mapView(ViewReport, ViewReportMediator);
         
         // Kick off the notifications
//         sendNotification(STARTUP, app);
      }
      
      //--------------------------------------------------------------------------
      //
      //  Overridden methods
      //
      //--------------------------------------------------------------------------
      
      /**
       * Register Commands with the Controller.
       */
//      override protected function initializeController():void 
//      {
//         super.initializeController();
//
//         // Register all our commands
//         registerCommand(STARTUP, StartupCommand);
//      }
   }
}