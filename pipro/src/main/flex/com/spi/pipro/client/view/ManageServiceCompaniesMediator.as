package com.spi.pipro.client.view
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.model.AbstractService;
   import com.spi.pipro.client.model.BlazeServiceCompanyAccountService;
   import com.spi.pipro.client.model.vo.EntityVO;
   import com.spi.pipro.client.model.vo.ServiceCompanyAccountVO;
   import com.spi.pipro.client.view.component.ManageServiceCompanies;
   import com.spi.pipro.client.view.event.AccountEvent;
   
   import flash.utils.Dictionary;
   
   import mx.collections.ArrayCollection;
   import mx.events.ValidationResultEvent;
   import mx.logging.ILogger;
   import mx.logging.Log;
   import mx.managers.CursorManager;
   import mx.utils.StringUtil;
   import mx.validators.EmailValidator;
   import mx.validators.StringValidator;
   import mx.validators.ValidationResult;
   import mx.validators.Validator;
   import mx.validators.ZipCodeValidator;
   import mx.validators.ZipCodeValidatorDomainType;
   
   public class ManageServiceCompaniesMediator extends ValidatingMediator
   {
      /**
       * View state where the only available component is the accounts list, where
       * the expectation is that the user will select a account in the list.
       */
      public static const SELECT_ACCOUNT_STATE:String = "selectAccount";
      /**
       * View state where the available components are the accounts list and the
       * account editor.
       */
      public static const EDIT_ACCOUNT_STATE:String = "editAccount";
      
      /**
       * ID of the view's account name input field
       */ 
      public static const NAME_INPUT:String = "nameInput";
      
      /**
       * ID of the view's last name input field
       */ 
      public static const COMPANY_NAME_INPUT:String = "companyNameInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const USER_NAME_INPUT:String = "userNameInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const PASSWORD_INPUT:String = "passwordInput";
      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ManageServiceCompaniesMediator");
      
      /**
       * This mediator's view
       */
      [Inject]
      public var view:ManageServiceCompanies;
      
      /**
       * Our gateway to the accounts service.
       */ 
      private var _accountProxy:BlazeServiceCompanyAccountService;
      
      /**
       * Backup copy of the current account, used for dirty checking.
       */ 
      private var _referenceAccount:ServiceCompanyAccountVO;

      /**
       * Constructor.
       */ 
      public function ManageServiceCompaniesMediator()
      {
         // First name validation
         var nameVal:StringValidator = new StringValidator();
         nameVal.property="text";
         registerValidator(NAME_INPUT, nameVal);
         
         // Last name validation
         var companyNameVal:StringValidator = new StringValidator();
         companyNameVal.property="text";
         registerValidator(COMPANY_NAME_INPUT, companyNameVal);
         
         // User name validation
         var userNameVal:StringValidator = new StringValidator();
         userNameVal.property="text";
         registerValidator(USER_NAME_INPUT, userNameVal);
         
         // Password validation
         var passwordVal:StringValidator = new StringValidator();
         passwordVal.property="text";
         registerValidator(PASSWORD_INPUT, passwordVal);
      }
      
      /* ------------------------ Pure MVC Framework ------------------------ */ 
      
      /**
       * Hook up with the proxy, and register for all the events that the view 
       * will send.
       */  
      public override function onRegister():void
      {
         super.onRegister();
         
         _accountProxy = facade.retrieveProxy(BlazeServiceCompanyAccountService.NAME) as BlazeServiceCompanyAccountService;
         
         view.addEventListener(AccountEvent.ACCOUNT_ADD, addHandler);
         view.addEventListener(AccountEvent.ACCOUNT_DELETE, deleteHandler);
         view.addEventListener(AccountEvent.ACCOUNT_SAVE, saveHandler);
         view.addEventListener(AccountEvent.ACCOUNT_CANCEL, cancelHandler);
         view.addEventListener(AccountEvent.ACCOUNT_SELECT, selectHandler);
         view.addEventListener(AccountEvent.ACCOUNT_PROPERTY_CHANGED, propertyChangedHandler);
         view.addEventListener(AccountEvent.ACCOUNT_PROPERTY_COMMITTED, propertyCommittedHandler);
         
         // Get the list of accounts, and set the view state for waiting
         waitForResponse();
         _accountProxy.getServiceCompanyNames();
         _accountProxy.getAccounts();
      }
      
      /**
       * Clear out references, and un-register for all the events that the view 
       * will send.
       */  
      public override function onRemove():void
      {
         super.onRemove();
         
         view.removeEventListener(AccountEvent.ACCOUNT_ADD, addHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_DELETE, deleteHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_SAVE, saveHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_CANCEL, cancelHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_SELECT, selectHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_PROPERTY_CHANGED, propertyChangedHandler);
         view.removeEventListener(AccountEvent.ACCOUNT_PROPERTY_COMMITTED, propertyCommittedHandler);
         
         // Dereference our dependencies
         setViewComponent(null);
         _accountProxy = null;
         _referenceAccount = null;
      }
      
      public override function listNotificationInterests():Array
      {
         return [
            BlazeServiceCompanyAccountService.GET_ACCOUNTS_METHOD,
            BlazeServiceCompanyAccountService.GET_INACTIVE_ACCOUNTS_METHOD,
            BlazeServiceCompanyAccountService.ADD_ACCOUNT_METHOD,
            BlazeServiceCompanyAccountService.UPDATE_ACCOUNT_METHOD,
            BlazeServiceCompanyAccountService.REMOVE_ACCOUNT_METHOD,
            BlazeServiceCompanyAccountService.REACTIVATE_ACCOUNTS_METHOD,
            AbstractService.REQUEST_FAILED
         ];
      }
      
      public override function handleNotification(notification:INotification):void
      {
         
         // Respond to each of the possible ServiceCompanyAccountProxy notifications listed
         // in listNotificationInterests() above.
         
         switch (notification.getName())
         {
            case BlazeServiceCompanyAccountService.GET_ACCOUNTS_METHOD:
               // Update the accounts model
               view.accounts = notification.getBody() as ArrayCollection;
               
               // Reset the view
               receiveResponse();
               
               break;
            case BlazeServiceCompanyAccountService.ADD_ACCOUNT_METHOD:
            case BlazeServiceCompanyAccountService.UPDATE_ACCOUNT_METHOD:
               // Update the account model
               _referenceAccount = notification.getBody() as ServiceCompanyAccountVO;
               view.account = _referenceAccount.clone() as ServiceCompanyAccountVO;
               
               // Fetch the accounts model
               _accountProxy.getAccounts();
               
               break;
            case BlazeServiceCompanyAccountService.REMOVE_ACCOUNT_METHOD:
               // Update the account model
               _referenceAccount = null;
               view.account = null;
               
               // Fetch the accounts model
               _accountProxy.getAccounts();
               
               // Reset the view
               view.currentState = SELECT_ACCOUNT_STATE;
               view.deleteButton.enabled = false;
               
               break;
            case AbstractService.REQUEST_FAILED:
               // Reset the view
               receiveResponse();
         }
      }
      
      /* -------------------------- Event Handlers -------------------------- */ 
      
      // Respond to each of the possible events triggered by our view, listed
      // in onRegister() above.
      
      protected function addHandler(event:AccountEvent):void
      {
         // Switch to the edit state
         view.currentState = EDIT_ACCOUNT_STATE;
         
         // Update the account model
         _referenceAccount = new ServiceCompanyAccountVO();
         view.account = new ServiceCompanyAccountVO();
         
         // Reset the view
         view.accountsList.selectedIndex = -1;
         view.deleteButton.enabled = false;
         view.saveButton.enabled = false;
      }
      
      protected function deleteHandler(event:AccountEvent):void
      {
         // Get ready to wait for the response
         waitForResponse();
         
         // Send the delete request
         _accountProxy.removeAccount(_referenceAccount.key);
      }
      
      protected function saveHandler(event:AccountEvent):void
      {
         if (validateAll())
         {
            // Get ready to wait for the response
            waitForResponse();
            
            // Send the update request
            if (_referenceAccount.key != EntityVO.NEW_KEY)
            {
               _accountProxy.updateAccount(view.account);
            }
            else
            {
               _accountProxy.addAccount(view.account);
            }
         }
      }
      
      protected function cancelHandler(event:AccountEvent):void
      {
         // Switch to the select state
         view.currentState = SELECT_ACCOUNT_STATE;
         
         // Update the account model
         _referenceAccount = null;
         view.account = null;
         
         // Reset the view
         view.accountsList.selectedIndex = -1;
         view.deleteButton.enabled = false;
      }
      
      protected function selectHandler(event:AccountEvent):void
      {
         // Switch to the edit state
         view.currentState = EDIT_ACCOUNT_STATE;
         
         // Update the account model
         _referenceAccount = view.accountsList.selectedItem as ServiceCompanyAccountVO;
         view.account = _referenceAccount.clone() as ServiceCompanyAccountVO;
         
         // Reset the view
         view.saveButton.enabled = false;
         view.deleteButton.enabled = true;
      }
      
      protected function propertyChangedHandler(event:AccountEvent):void
      {
         view.saveButton.enabled = true;
      }
      
      protected function propertyCommittedHandler(event:AccountEvent):void
      {
         view.saveButton.enabled = validateField(event.changedProperty);
      }
      
      /* ------------------------- Private Functions ------------------------ */ 
      
      private function waitForResponse():void
      {
         CursorManager.setBusyCursor();
         view.enabled = false;
      }
      
      private function receiveResponse():void
      {
         CursorManager.removeBusyCursor();
         view.enabled = true;
      }
   }
}