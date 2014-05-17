/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.view
{
   import com.spi.pipro.PIProContext;
   
   import mx.events.ValidationResultEvent;
   import mx.validators.ValidationResult;
   import mx.validators.Validator;
   
   import org.robotlegs.mvcs.Mediator;
   
   /**
    * Mediator with support for validating the view component's input fields.
    */ 
   public class ValidatingMediator extends Mediator
   {
      /**
       * Map of validation source IDs to validators
       */  
      private var _validators:Object;
      
      public function ValidatingMediator()
      {
         // Setup the validator lookup
         _validators = new Object();
      }
      
      /**
       * Clean up in response to being removed from the framework.
       */  
      public override function onRemove():void
      {
         super.onRemove();
         
         _validators = null;
      }
         
      /**
       * Bind a validator to the ID of a field on the view component that should
       * be validated with the given validator.
       * 
       * @param sourceId the ID string of the input field that needs validating.
       *        This ID should identify a child of this mediator's viewComponent
       * @param validator the validator that will validate the given field.
       */
      protected function registerValidator(sourceId:String, validator:Validator):void
      {
         _validators[sourceId] = validator;
      }
      
      /**
       * Retrieve the validator that has been bound to the given field ID.
       * 
       * @param sourceid the ID string of the input field of interest.
       *        This ID should identify a child of this mediator's viewComponent
       * @return the validator for the input field with the given ID.
       */ 
      protected function getValidator(sourceId:String):Validator
      {
         return _validators[sourceId];
      }
      
      /**
       * Cycle through all the validators that have been registered.
       * 
       * @return true if all the validators are valid, false otherwise.  If any
       *         validator fails to validate, this method will fire a WARNING 
       *         notification.
       */ 
      protected function validateAll():Boolean
      {
         var valid:Boolean = true;
         
         for (var field:String in _validators)
         {
            valid = validateField(field);
            if (!valid)
            {
               break;
            }
         }
         
         return valid;
      }
      
      /**
       * Validate the input field with the given ID.
       * 
       * @param sourceid the ID string of the input field of interest.
       *        This ID should identify a child of this mediator's viewComponent
       * @return true if the input field is valid, false otherwise.  If the 
       *         field fails to validate, this method will fire a WARNING
       *         notification to alert the user.
       */ 
      protected function validateField(sourceId:String):Boolean
      {
         var valid:Boolean = true;
         
         // Get and initialize the validator
         var validator:Validator = getValidator(sourceId);
         
         // Validate the input
         if (validator)
         {
            validator.source = viewComponent[sourceId];
            var resultEvent:ValidationResultEvent = validator.validate();
            if (resultEvent && resultEvent.results && resultEvent.results.length > 0)
            {
               var result:ValidationResult = resultEvent.results[0];
               if (result.isError)
               {
                  sendNotification(PIProContext.WARNING, "Please correct the form before submitting: " + result.errorMessage);
                  valid = false;
               }
            }
            
            // Clear the validator
            validator.source = null;
         }
         
         return valid;
      }
   }
}