/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.client.model.vo.PersonVO;
   
   import mx.collections.IList;

   public interface IPeopleService
   {
      function getPeople():void;
      
      function getInactivePeople():void;
      
      function getPersonByKey(key:String):void;
      
      function getPersonByUsername(username:String):void;
      
      function addPerson(person:PersonVO):void;
      
      function updatePerson(person:PersonVO):void;
      
      function removePerson(key:String):void; 
      
      function reactivatePeople(keys:IList):void; 
      
      function setPassword(key:String, newPassword:String):void;
   }
}