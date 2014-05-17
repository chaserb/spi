/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.view.component
{
   import com.spi.pipro.client.util.StringUtil;
   
   import flash.events.Event;
   
   import spark.components.TextInput;
   
   [DefaultProperty("number")]
   
   public class TextInputNumber extends TextInput
   {
      private var _number:Number;
      private var _minChars:int;
      
      /**
       * Constructs a numerical text input field
       * 
       * @param minChars the 
       */ 
      public function TextInputNumber(minChars:int=-1)
      {
         super();
         addEventListener("change", changeHandler);
      }
      
      public function get minChars():int
      {
         return _minChars;
      }
      
      public function set minChars(minChars:int):void
      {
         _minChars = minChars;
      }
      
      private function changeHandler(event:Event):void
      {
         _number = Number(super.text);
         dispatchEvent(new Event("numberChanged"));
      }
      
      public function set number(value:Number):void
      {
         _number = value;
         if (isNaN(_number))
         {
            super.text = "";
         }
         else if (minChars > 0)
         {
            super.text = StringUtil.leadingZeros(minChars, _number);     
         }
         else
         {
            super.text = _number.toString(); 
         }
         dispatchEvent(new Event("numberChanged"));
      }
      
      [Bindable("numberChanged")]
      public function get number():Number
      {
         return _number;
      }
   }
}