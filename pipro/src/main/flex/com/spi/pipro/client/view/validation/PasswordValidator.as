/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.view.validation
{
   import flash.errors.IllegalOperationError;
   
   import mx.validators.ValidationResult;
   import mx.validators.Validator;
   
   public class PasswordValidator extends Validator
   {
      private var _confirmSource:Object;
      private var _confirmProperty:String;
      
      public function PasswordValidator()
      {
         super();
      }
      
      protected override function doValidation(value:Object):Array
      {
         var result:Array = super.doValidation(value);
         
         if (result.length > 0)
         {
            return result;
         }
         
         if (confirmSource && confirmProperty)
         {
            if (source[property] != confirmSource[confirmProperty])
            {
               result.push(new ValidationResult(true, null, "notMatched", "The Password and Password Confirmation do not match"));
            }
         }
         else
         {
            throw new ArgumentError("The confirmSource or the confirmProperty properties have not been initialized");
         }
         
         return result;
      }
      
      public function get confirmSource():Object
      {
         return _confirmSource;
      }
      
      public function set confirmSource(source:Object):void
      {
         _confirmSource = source;
      }
      
      public function get confirmProperty():String
      {
         return _confirmProperty;
      }
      
      public function set confirmProperty(property:String):void
      {
         _confirmProperty = property;
      }
   }
}