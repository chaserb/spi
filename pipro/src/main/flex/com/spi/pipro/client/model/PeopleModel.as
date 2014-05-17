/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import org.robotlegs.mvcs.Actor;
   
   public class PeopleModel extends Actor implements IPeopleModel
   {
      public function PeopleModel()
      {
         super();
      }

      public function getPeople():Vector.<PersonVO>
      {
         return null;
      }
      
      public function getInactivePeople():Vector.<PersonVO>
      {
         return null;
      }
      
      public function getPersonByKey(key:String):PersonVO
      {
         return null;
      }
      
      public function getPersonByUsername(username:String):PersonVO
      {
         return null;
      }
      
      public function addPerson(person:PersonVO):void
      {
         
      }
      
      public function updatePerson(person:PersonVO):void
      {
         
      }
      
      public function removePerson(key:String):void
      {
         
      }
   }
}