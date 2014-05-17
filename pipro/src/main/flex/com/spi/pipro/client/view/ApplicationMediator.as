package com.spi.pipro.client.view
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.view.component.DispatchNewOrders;
   import com.spi.pipro.client.view.component.EditOrder;
   import com.spi.pipro.client.view.component.EnterCompletedOrders;
   import com.spi.pipro.client.view.component.HomePage;
   import com.spi.pipro.client.view.component.ManageOrderAssignments;
   import com.spi.pipro.client.view.component.ManagePeople;
   import com.spi.pipro.client.view.component.ManageReports;
   import com.spi.pipro.client.view.component.ManageSchedule;
   import com.spi.pipro.client.view.component.ManageServiceCompanies;
   import com.spi.pipro.client.view.component.ReviewCompletedOrders;
   import com.spi.pipro.client.view.component.ViewOrder;
   import com.spi.pipro.client.view.component.ViewProperty;
   import com.spi.pipro.client.view.component.ViewReport;
   import com.spi.pipro.client.view.event.NavigationEvent;
   
   import mx.collections.ArrayCollection;
   import mx.core.UIComponent;
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   /**
    * Mediator for the application component.  The main task of this class is to
    * repond to navigation notifications which update the content portion of the
    * application. 
    */ 
   public class ApplicationMediator extends Mediator
   {
      /**
       * The view component for this mediator, cast as the pipro component. 
       */ 
      [Inject]
      public var view:pipro;      
      /**
       * Logger
       */  
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ApplicationMediator");
      /**
       * The current component occupying the content area.
       */ 
      private var _contentComponent:UIComponent;
      /**
       * The mediator for the current content component.
       */ 
      private var _contentMediator:Mediator;
      /** 
       * Navigation target stack that allows back and forth navigation 
       */ 
      private var _navHistory:ArrayCollection;
      /** 
       * Index of the current navigation target 
       */
      private var _navHistoryCursor:int;
      
      /**
       * Constructor.
       * 
       * @param viewComponent reference to the application view.
       */ 
      public function ApplicationMediator()
      {
         _navHistory = new ArrayCollection();
         _navHistoryCursor = 0;
      }
      
      public function handleNotification(notification:INotification):void
      {
         switch (notification.getName())
         {
            case PIProContext.INFO:
               view.statusText.text = notification.getBody().toString();
               break;
            case PIProContext.WARNING:
               view.statusText.text = notification.getBody().toString();
               break;
            case PIProContext.ERROR:
               view.statusText.text = notification.getBody().toString();
               break;
            default:
               var newTarget:String = notification.getName();
               if (newTarget != currentNavigation)
               {
                  addNavigationStep(newTarget);
                  navigate(newTarget);
               }
         }
      }
      
      /* ----------------------- Robotlegs Framework ------------------------ */ 
      
      public override function onRegister():void
      {
         // Register for application notifications
         addContextListener(PIProContext.INFO, handleNotification);
         addContextListener(PIProContext.WARNING, handleNotification);
         addContextListener(PIProContext.ERROR, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_DISPATCH_NEW_ORDERS, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_EDIT_ORDER, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_ENTER_COMPLETED_ORDERS, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_HOME, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_MANAGE_ORDER_ASSIGNMENTS, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_MANAGE_PEOPLE, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_MANAGE_REPORTS, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_MANAGE_SCHEDULE, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_MANAGE_SERVICE_COMPANIES, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_REVIEW_COMPLETED_ORDERS, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_VIEW_ORDER, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_VIEW_PROPERTY, handleNotification);
         addContextListener(PIProContext.NAV_TARGET_VIEW_REPORT, handleNotification);
         
         // Regsiter for view events
         addViewListener(NavigationEvent.NAVIGATION_BACK, backHandler);
         addViewListener(NavigationEvent.NAVIGATION_FORWARD, forwardHandler);
         
         // Start the navigation stack
         sendNotification(PIProContext.NAV_TARGET_HOME);
      }
      
      public override function onRemove():void
      {
         setContent(null, null);
      }
      
      /* -------------------------- Event Handlers -------------------------- */ 
      
      protected function backHandler(e:NavigationEvent):void
      {
         _log.debug("BACK Navigation");
         
         // Ensure we don't remove the home target from the beginning of the 
         // stack
         if (_navHistoryCursor > 0)
         {
            _navHistoryCursor--;
         }
         
         // Execute
         navigate(currentNavigation);
         updateNavigationView();
      }
      
      protected function forwardHandler(e:NavigationEvent):void
      {
         _log.debug("FORWARD Navigation");
         
         // We only go forward if we've gone back before
         if (_navHistoryCursor < _navHistory.length - 1)
         {
            _navHistoryCursor++;
         }
         
         // Execute
         navigate(currentNavigation);
         updateNavigationView();
      }

      /* ------------------------- Private Functions ------------------------ */
      
      private function addNavigationStep(target:String):void
      {
         _log.debug("ADD Navigation: " + target);
         
         // Clear out any forward steps
         while (_navHistoryCursor < _navHistory.length - 1)
         {
            _navHistory.removeItemAt(_navHistory.length - 1);
         }
         
         // Add the current step to the end of the list
         _navHistory.addItem(target);
         _navHistoryCursor = _navHistory.length - 1;
         
         // Update the UI
         updateNavigationView();
      }
      
      private function updateNavigationView():void
      {
         view.backNav.enabled = _navHistoryCursor > 0;
         view.forwardNav.enabled = _navHistoryCursor < _navHistory.length - 1;
         sendNotification(PIProContext.NAV_TARGET_CHANGED, currentNavigation);
      }
      
      private function get currentNavigation():String
      {
         return (_navHistory.length > _navHistoryCursor) ? _navHistory.getItemAt(_navHistoryCursor) as String : null;
      }
      
      /**
       * Execute the navigation
       */
      private function navigate(target:String):void
      {
         var content:UIComponent; 
         var mediator:Mediator;
         
         switch (target)
         {
            case PIProContext.NAV_TARGET_DISPATCH_NEW_ORDERS:
               content = new DispatchNewOrders();
               mediator = new DispatchNewOrdersMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_EDIT_ORDER:
               content = new EditOrder();
               mediator = new EditOrderMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_ENTER_COMPLETED_ORDERS:
               content = new EnterCompletedOrders();
               mediator = new EnterCompletedOrdersMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_MANAGE_ORDER_ASSIGNMENTS:
               content = new ManageOrderAssignments();
               mediator = new ManageOrderAssignmentsMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_MANAGE_PEOPLE:
               content = new ManagePeople();
               mediator = new ManagePeopleMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_MANAGE_REPORTS:
               content = new ManageReports();
               mediator = new ManageReportsMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_MANAGE_SCHEDULE:
               content = new ManageSchedule();
               mediator = new ManageScheduleMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_MANAGE_SERVICE_COMPANIES:
               content = new ManageServiceCompanies();
               mediator = new ManageServiceCompaniesMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_REVIEW_COMPLETED_ORDERS:
               content = new ReviewCompletedOrders();
               mediator = new ReviewCompletedOrdersMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_VIEW_ORDER:
               content = new ViewOrder();
               mediator = new ViewOrderMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_VIEW_PROPERTY:
               content = new ViewProperty();
               mediator = new ViewPropertyMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_VIEW_REPORT:
               content = new ViewReport();
               mediator = new ViewReportMediator(content);
               setContent(content, mediator);
               break;
            case PIProContext.NAV_TARGET_HOME:
               content = new HomePage();
               mediator = new HomePageMediator(content);
               setContent(content, mediator);
               break;
            default:
         }
      }
      
      /**
       * This method will update the main content panel.
       */ 
      private function setContent(component:UIComponent, mediator:Mediator):void
      {
         // Remove the previous component and mediator
         if (_contentComponent)
         {
            view.removeElement(_contentComponent);
         }
         if (_contentMediator)
         {
            facade.removeMediator(_contentMediator.getMediatorName());
         }
         
         // Update our state
         _contentComponent = component;
         _contentMediator = mediator;
         
         // Hook the new components into the framework
         if (_contentComponent)
         {
            _contentComponent.top = 40;
            _contentComponent.bottom = 40;
            _contentComponent.right = 10;
            _contentComponent.left = 238;

            view.addElement(_contentComponent);
         }
         if (_contentMediator)
         {
            facade.registerMediator(_contentMediator);
         }
      }
   }
}