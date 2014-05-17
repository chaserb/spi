package com.spi.pipro.client.view
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.model.AbstractService;
   import com.spi.pipro.client.model.BlazePeopleService;
   import com.spi.pipro.client.model.vo.EntityVO;
   import com.spi.pipro.client.model.vo.PersonVO;
   import com.spi.pipro.client.view.component.ManagePeople;
   import com.spi.pipro.client.view.event.PeopleEvent;
   import com.spi.pipro.client.view.validation.PasswordValidator;
   
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
   
   import org.puremvc.as3.multicore.interfaces.INotification;
   
   public class ManagePeopleMediator extends ValidatingMediator
   {
      /**
       * View state where the only available component is the people list, where
       * the expectation is that the user will select a person in the list.
       */
      public static const SELECT_PERSON_STATE:String = "selectPerson";
      /**
       * View state where the available components are the people list and the
       * person editor.
       */
      public static const EDIT_PERSON_STATE:String = "editPerson";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const FIRST_NAME_INPUT:String = "firstNameInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const LAST_NAME_INPUT:String = "lastNameInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const USER_NAME_INPUT:String = "userNameInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const PASSWORD_INPUT:String = "passwordInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const PASSWORD_CONFIRM_INPUT:String = "passwordConfirmInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const EMAIL_INPUT:String = "emailInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const ADDRESS1_INPUT:String = "address1Input";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const ADDRESS2_INPUT:String = "address2Input";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const CITY_INPUT:String = "cityInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const STATE_INPUT:String = "stateInput";
      
      /**
       * ID of the view's first name input field
       */ 
      public static const ZIP_INPUT:String = "zipInput";
      
      /**
       * The token we use to determine if the user attempted to replace the 
       * password.
       */ 
      public static const PASSWORD_FACADE:String = "GffyfCB6Il2p14";
      
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.ManagePeopleMediator");
         
      /**
       * Our gateway to the people service.
       */ 
      private var _peopleProxy:BlazePeopleService;
      
      /**
       * Backup copy of the current person, used for dirty checking.
       */ 
      private var _referencePerson:PersonVO;

      /**
       * Constructor.
       */ 
      public function ManagePeopleMediator()
      {
         // First name validation
         var firstNameVal:StringValidator = new StringValidator();
         firstNameVal.property="text";
         registerValidator(FIRST_NAME_INPUT, firstNameVal);

         // Last name validation
         var lastNameVal:StringValidator = new StringValidator();
         lastNameVal.property="text";
         registerValidator(LAST_NAME_INPUT, lastNameVal);

         // User name validation
         var userNameVal:StringValidator = new StringValidator();
         userNameVal.property="text";
         registerValidator(USER_NAME_INPUT, userNameVal);

         // Password validation
         var passwordVal:PasswordValidator = new PasswordValidator();
         passwordVal.property="text";
         passwordVal.confirmProperty="text";
         passwordVal.required = false;
         registerValidator(PASSWORD_INPUT, passwordVal);
         registerValidator(PASSWORD_CONFIRM_INPUT, passwordVal);

         // Email validation
         var emailVal:EmailValidator = new EmailValidator();
         emailVal.property="text";
         registerValidator(EMAIL_INPUT, emailVal);

         // State validation
         var stateVal:StringValidator = new StringValidator();
         stateVal.property = "text";
         stateVal.minLength = 2;
         stateVal.maxLength = 2;
         stateVal.required = false;
         registerValidator(STATE_INPUT, stateVal);

         // Zip code validation
         var zipCodeVal:ZipCodeValidator = new ZipCodeValidator();
         zipCodeVal.property = "text";
         zipCodeVal.domain = ZipCodeValidatorDomainType.US_ONLY;
         zipCodeVal.wrongLengthError = "Zip Code must be 5 digits";
         zipCodeVal.required = false;
         registerValidator(ZIP_INPUT, zipCodeVal);
      }
      
      /* ------------------------ Pure MVC Framework ------------------------ */ 
      
      /**
       * Register for all the navigation events that the navigation tree will 
       * send.
       */  
      public override function onRegister():void
      {
         super.onRegister();
         
         _peopleProxy = facade.retrieveProxy(BlazePeopleService.NAME) as BlazePeopleService;
         
         view.addEventListener(PeopleEvent.PERSON_ADD, addHandler);
         view.addEventListener(PeopleEvent.PERSON_DELETE, deleteHandler);
         view.addEventListener(PeopleEvent.PERSON_SAVE, saveHandler);
         view.addEventListener(PeopleEvent.PERSON_CANCEL, cancelHandler);
         view.addEventListener(PeopleEvent.PERSON_SELECT, selectHandler);
         view.addEventListener(PeopleEvent.PERSON_PROPERTY_CHANGED, propertyChangedHandler);
         view.addEventListener(PeopleEvent.PERSON_PROPERTY_COMMITTED, propertyCommittedHandler);
         
         // Get the list of people, and set the view state for waiting
         waitForResponse();
         _peopleProxy.getPeople();
      }
      
      /**
       * Un-register for all the navigation events that the navigation tree will 
       * send.
       */  
      public override function onRemove():void
      {
         super.onRemove();
         
         view.removeEventListener(PeopleEvent.PERSON_ADD, addHandler);
         view.removeEventListener(PeopleEvent.PERSON_DELETE, deleteHandler);
         view.removeEventListener(PeopleEvent.PERSON_SAVE, saveHandler);
         view.removeEventListener(PeopleEvent.PERSON_CANCEL, cancelHandler);
         view.removeEventListener(PeopleEvent.PERSON_SELECT, selectHandler);
         view.removeEventListener(PeopleEvent.PERSON_PROPERTY_CHANGED, propertyChangedHandler);
         view.removeEventListener(PeopleEvent.PERSON_PROPERTY_COMMITTED, propertyCommittedHandler);

         // Dereference our dependencies
         setViewComponent(null);
         _peopleProxy = null;
         _referencePerson = null;
      }
      
//      public override function listNotificationInterests():Array
//      {
//         return [
//            PeopleProxy.GET_PEOPLE_METHOD,
//            PeopleProxy.GET_INACTIVE_PEOPLE_METHOD,
//            PeopleProxy.ADD_PERSON_METHOD,
//            PeopleProxy.UPDATE_PERSON_METHOD,
//            PeopleProxy.REMOVE_PERSON_METHOD,
//            PeopleProxy.REACTIVATE_PEOPLE_METHOD,
//            AbstractProxy.REQUEST_FAILED
//         ];
//      }
      
//      public override function handleNotification(notification:INotification):void
//      {
//         
//         // Respond to each of the possible PeopleProxy notifications listed
//         // in listNotificationInterests() above.
//         
//         switch (notification.getName())
//         {
//            case PeopleProxy.GET_PEOPLE_METHOD:
//               // Update the people model
//               view.people = notification.getBody() as ArrayCollection;
//               
//               // Reset the view
//               receiveResponse();
//               
//               break;
//            case PeopleProxy.ADD_PERSON_METHOD:
//            case PeopleProxy.UPDATE_PERSON_METHOD:
//               // Update the person model
//               _referencePerson = notification.getBody() as PersonVO;
//               view.person = _referencePerson.clone() as PersonVO;
//               
//               // Fetch the people model
//               _peopleProxy.getPeople();
//               
//               break;
//            case PeopleProxy.REMOVE_PERSON_METHOD:
//               // Update the person model
//               _referencePerson = null;
//               view.person = null;
//               
//               // Fetch the people model
//               _peopleProxy.getPeople();
//               
//               // Reset the view
//               view.currentState = SELECT_PERSON_STATE;
//               view.deleteButton.enabled = false;
//
//               break;
//            case AbstractProxy.REQUEST_FAILED:
//               // Reset the view
//               receiveResponse();
//         }
//      }
      
      /* -------------------------- Event Handlers -------------------------- */ 
      
      // Respond to each of the possible events triggered by our view, listed
      // in onRegister() above.
      
      protected function addHandler(event:PeopleEvent):void
      {
         // Switch to the edit state
         view.currentState = EDIT_PERSON_STATE;

         // Update the person model
         _referencePerson = new PersonVO();
         view.person = new PersonVO();
         
         // Reset the view
         view.peopleList.selectedIndex = -1;
         view.deleteButton.enabled = false;
         view.saveButton.enabled = false;
         view.passwordConfirmInput.enabled = false;
      }
      
      protected function deleteHandler(event:PeopleEvent):void
      {
         // Get ready to wait for the response
         waitForResponse();
         
         // Send the delete request
         _peopleProxy.removePerson(_referencePerson.key);
      }
      
      protected function saveHandler(event:PeopleEvent):void
      {
         if (validateAll())
         {
            // Get ready to wait for the response
            waitForResponse();
            
            if (view.passwordInput.text != PASSWORD_FACADE)
            {
               view.person.password = view.passwordInput.text;
            }
            
            // Send the update request
            if (_referencePerson.key != EntityVO.NEW_KEY)
            {
               _peopleProxy.updatePerson(view.person);
            }
            else
            {
               _peopleProxy.addPerson(view.person);
            }
         }
      }
      
      protected function cancelHandler(event:PeopleEvent):void
      {
         // Switch to the select state
         view.currentState = SELECT_PERSON_STATE;
         
         // Update the person model
         _referencePerson = null;
         view.person = null;
         
         // Reset the view
         view.peopleList.selectedIndex = -1;
         view.deleteButton.enabled = false;
         view.passwordConfirmInput.enabled = false;
      }
      
      protected function selectHandler(event:PeopleEvent):void
      {
         // Switch to the edit state
         view.currentState = EDIT_PERSON_STATE;

         // Update the person model
         _referencePerson = view.peopleList.selectedItem as PersonVO;
         view.person = _referencePerson.clone() as PersonVO;
         
         // Reset the view
         view.saveButton.enabled = false;
         view.deleteButton.enabled = true;
         view.passwordConfirmInput.enabled = false;
      }
      
      protected function propertyChangedHandler(event:PeopleEvent):void
      {
         view.saveButton.enabled = true;
         
         // Update the username field
         if ((! _referencePerson.userName) &&
             (event.changedProperty == FIRST_NAME_INPUT || event.changedProperty == LAST_NAME_INPUT))
         {
            var text:String = view.firstNameInput.text.toLowerCase();
            if (view.lastNameInput.text)
            {
               text += "." + view.lastNameInput.text.toLowerCase(); 
            } 
            view.userNameInput.text = StringUtil.restrict(text, view.userNameInput.restrict);
         }
         
         // Enable the password confirm
         if (event.changedProperty == PASSWORD_INPUT)
         {
            view.passwordConfirmInput.enabled = true;
         }
      }
      
      protected function propertyCommittedHandler(event:PeopleEvent):void
      {
         view.saveButton.enabled = validateField(event.changedProperty);
      }
      
      /* ------------------------- Private Functions ------------------------ */ 
      
      /**
       * Retrieve the view component, cast as the ManagePeople view.
       */
      private function get view():ManagePeople
      {
         return viewComponent as ManagePeople;
      }
      
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
      
      protected override function validateField(field:String):Boolean
      {
         var valid:Boolean;
         
         // Get and initialize the validator
         var validator:Validator = getValidator(field);
         
         if (validator is PasswordValidator)
         {
            (validator as PasswordValidator).confirmSource = view[PASSWORD_CONFIRM_INPUT];
            valid = super.validateField(PASSWORD_INPUT);
            (validator as PasswordValidator).confirmSource = null;
         }
         else
         {
            valid = super.validateField(field);
         }
         
         return valid;
      }
   }
}