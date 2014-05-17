/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.util
{
   import flash.errors.IllegalOperationError;
   import flash.utils.Dictionary;
   import flash.utils.describeType;
   import flash.utils.getQualifiedClassName;  

   /** 
    * An abstract class to emulate Enum type.
    * @credit Peter Molgaard
    * 
    * Example use:
    * public final class CarModelEnum extends Enum {
    * 
    * public static const FORD:CarTypeEnum = new CarTypeEnum(); 
    * public static const FERRARI:CarTypeEnum = new CarTypeEnum(); 
    * public static const YUGO:CarTypeEnum = new CarTypeEnum(); 
    * 
    * // static initializer
    * {  
    * initEnumConstants( CarTypeEnum );  
    * }
    * }
    */  
   public class Enum {
      
      /** 
       * Protects against instantiation after static initializing. 
       */  
      protected static var locks:Dictionary = new Dictionary();  
      
      /** 
       * Enum label.
       */  
      private var _label:String;  
      /** Get the label */
      public function get label():String {  
         return _label;  
      }
      
      /**
       *  Constructor.  Should not be called directly.
       */
      public function Enum() {  
         var className:String = getQualifiedClassName(this);  
         if (locks[className]) {  
            throw new IllegalOperationError("Cannot directly instantiate Enum of : " + className);  
         }  
      }
      
      /** 
       * Function to call for each enum type declared and in static init.
       */  
      protected static function initEnumConstants(clazz:Class):void {  
         
         var className:String = getQualifiedClassName(clazz);  
         var typeXML:XML = describeType(clazz);
         // loop through each of the constants (i.e. Enums) in 'clazz'
         // and create an instance.  Assigns the constant variable name
         // to '_label'.
         for each (var constant:XML in typeXML.constant) {  
            clazz[constant.@name]._label = constant.@name;  
         }
         // lock the Enum subtype so that no new ones will be generated  
         locks[className] = true;  
      }  
   }
}