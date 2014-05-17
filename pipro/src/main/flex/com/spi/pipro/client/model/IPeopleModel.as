/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.client.model.vo.PersonVO;

   public interface IPeopleModel
   {
      function getPeople():Vector.<PersonVO>;
      function getInactivePeople():Vector.<PersonVO>;
      function getPersonByKey(key:String):PersonVO;
      function getPersonByUsername(username:String):PersonVO;
      function addPerson(person:PersonVO):void;
      function updatePerson(person:PersonVO):void;
      function removePerson(key:String):void;
   }
}