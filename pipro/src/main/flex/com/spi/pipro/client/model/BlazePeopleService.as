/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.event.PeopleServiceEvent;
   import com.spi.pipro.client.model.vo.PersonVO;
   
   import mx.collections.IList;
   import mx.logging.ILogger;
   import mx.logging.Log;
   import mx.rpc.AsyncToken;
   import mx.rpc.events.FaultEvent;
   import mx.rpc.events.ResultEvent;
   import mx.rpc.remoting.RemoteObject;
   
   /**
    * Proxy which communicates via RPC with the remote blaze PeopleService.
    */ 
   public class BlazePeopleService extends AbstractService implements IPeopleService
   {
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.model.PeopleService");
      
      /**
       * RPC interface to the people service
       */ 
      private var _peopleService:RemoteObject;
      
      [Inject]
      public var model:IPeopleModel;
      
      /**
       * Constructor.
       */ 
      public function BlazePeopleService()
      {
         super();
         _peopleService = new RemoteObject("peopleClientService");
         eventMap.mapListener(_peopleService, ResultEvent.RESULT, resultHandler);
         eventMap.mapListener(_peopleService, FaultEvent.FAULT, faultHandler);
      }
      
      /* -------------------------- Public Methods -------------------------- */
      
      public function getPeople():void
      {
         var token:AsyncToken = _peopleService.getAll();
         token.method = GET_PEOPLE_METHOD;
      }
      
      public function getInactivePeople():void
      {
         var token:AsyncToken = _peopleService.getAllInactive();
         token.method = GET_INACTIVE_PEOPLE_METHOD;
      }
      
      public function getPersonByKey(key:String):void
      {
         var token:AsyncToken = _peopleService.getByKey(key);
         token.method = GET_PERSON_METHOD;
      }
      
      public function getPersonByUsername(username:String):void
      {
         var token:AsyncToken = _peopleService.getPersonByUsername(username);
         token.method = GET_PERSON_METHOD;
      }
      
      public function addPerson(person:PersonVO):void
      {
         var token:AsyncToken = _peopleService.add(person);
         token.method = ADD_PERSON_METHOD;
      }
      
      public function updatePerson(person:PersonVO):void
      {
         var token:AsyncToken = _peopleService.update(person);
         token.method = UPDATE_PERSON_METHOD;
      }
      
      public function removePerson(key:String):void 
      {
         var token:AsyncToken = _peopleService.remove(key);
         token.method = REMOVE_PERSON_METHOD;
      }
      
      public function reactivatePeople(keys:IList):void 
      {
         var token:AsyncToken = _peopleService.reactivate(keys);
         token.method = REACTIVATE_PEOPLE_METHOD;
      }
      
      public function setPassword(key:String, newPassword:String):void
      {
         var token:AsyncToken = _peopleService.setPassword(key, newPassword);
         token.method = SET_PASSWORD_METHOD;
      }
      
      /* ------------------------ Framework Interface ----------------------- */ 

      public function resultHandler(e:ResultEvent):void
      {
         var method:String = e.token.method;
         var people:IList;
         var person:PersonVO;
         var message:String;
         
         switch (method)
         {
            case GET_PEOPLE_METHOD:
            case GET_INACTIVE_PEOPLE_METHOD:
               people = e.result as IList;
               if (Log.isDebug()) _log.debug("Retrieved " + ((people) ? people.length : "0") + " people.");
               model.
               dispatch(new PeopleServiceEvent(method, people));
               break;
            case GET_PERSON_METHOD:
               person = e.result as PersonVO;
               if (Log.isDebug()) _log.debug("Fetched person: " + person.userName + ".");  
               dispatch(new PeopleServiceEvent(method, person));
               break;
            case ADD_PERSON_METHOD:
               person = e.result as PersonVO;
               message = "Person added successfully: " + person.userName + ".";
               if (Log.isDebug()) _log.debug(message);   
               dispatch(new PeopleServiceEvent(method, person));
               sendNotification(PIProContext.INFO, message);
               break;
            case UPDATE_PERSON_METHOD:
               person = e.result as PersonVO;
               message = "Person updated successfully: " + person.userName + ".";
               if (Log.isDebug()) _log.debug(message);   
               dispatch(new PeopleServiceEvent(method, person));
               sendNotification(PIProContext.INFO, message);
               break;
            case REMOVE_PERSON_METHOD:
               message = "Person removed successfully.";
               if (Log.isDebug()) _log.debug(message);   
               dispatch(new PeopleServiceEvent(method));
               sendNotification(PIProContext.INFO, message);
               break;
            case REACTIVATE_PEOPLE_METHOD:
               message = "People reactivated successfully.";
               if (Log.isDebug()) _log.debug(message);   
               dispatch(new PeopleServiceEvent(method);
               sendNotification(PIProContext.INFO, message);
               break;
            case SET_PASSWORD_METHOD:
               if (Log.isDebug()) _log.debug("Password updated successfully.");   
               dispatch(new PeopleServiceEvent(method);
               break;
            default:
         }
      }
   }
}