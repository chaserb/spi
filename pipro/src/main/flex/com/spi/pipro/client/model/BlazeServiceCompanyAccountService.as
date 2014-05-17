/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.PIProContext;
   import com.spi.pipro.client.model.vo.ServiceCompanyAccountVO;
   import com.spi.pipro.client.util.ObjectUtil;
   
   import mx.collections.ArrayCollection;
   import mx.collections.IList;
   import mx.logging.ILogger;
   import mx.logging.Log;
   import mx.rpc.AsyncToken;
   import mx.rpc.events.FaultEvent;
   import mx.rpc.events.ResultEvent;
   import mx.rpc.remoting.RemoteObject;
   
   import spark.components.List;
   
   /**
    * Service which communicates via RPC with the remote blaze 
    * ServiceCompanyAccountClientService.
    */ 
   public class BlazeServiceCompanyAccountService extends AbstractService implements IServiceCompanyAccountService
   {
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.model.ServiceCompanyAccountService");
      
      /**
       * RPC interface to the accounts service
       */ 
      private var _accountsService:RemoteObject;
      
      /**
       * Constructor.
       */ 
      public function BlazeServiceCompanyAccountService()
      {
         super();
         _accountsService = new RemoteObject("serviceCompanyAccountClientService");
         eventMap.mapListener(_accountsService, ResultEvent.RESULT, resultHandler);
         eventMap.mapListener(_accountsService, FaultEvent.FAULT, faultHandler);
      }
      
      /* -------------------------- Public Methods -------------------------- */
      
      public function getAccounts():void
      {
         var token:AsyncToken = _accountsService.getAll();
         token.method = GET_ACCOUNTS_METHOD;
      }
      
      public function getServiceCompanyNames():void
      {
         var token:AsyncToken = _accountsService.getCompanyNames();
         token.method = GET_COMPANY_NAMES_METHOD;
      }
      
      public function getInactiveAccounts():void
      {
         var token:AsyncToken = _accountsService.getAllInactive();
         token.method = GET_INACTIVE_ACCOUNTS_METHOD;
      }
      
      public function getAccountByKey(key:String):void
      {
         var token:AsyncToken = _accountsService.getByKey(key);
         token.method = GET_ACCOUNT_METHOD;
      }
      
      public function addAccount(account:ServiceCompanyAccountVO):void
      {
         var token:AsyncToken = _accountsService.add(account);
         token.method = ADD_ACCOUNT_METHOD;
      }
      
      public function updateAccount(account:ServiceCompanyAccountVO):void
      {
         var token:AsyncToken = _accountsService.update(account);
         token.method = UPDATE_ACCOUNT_METHOD;
      }
      
      public function removeAccount(key:String):void 
      {
         var token:AsyncToken = _accountsService.remove(key);
         token.method = REMOVE_ACCOUNT_METHOD;
      }
      
      public function reactivateAccounts(keys:IList):void 
      {
         var token:AsyncToken = _accountsService.reactivate(keys);
         token.method = REACTIVATE_ACCOUNTS_METHOD;
      }
      
      /* ------------------------ Framework Interface ----------------------- */ 

      public function resultHandler(e:ResultEvent):void
      {
         var method:String = e.token.method;
         var companyNames:Object;
         var accounts:IList;
         var account:ServiceCompanyAccountVO;
         var message:String;
         
         switch (method)
         {
            case GET_COMPANY_NAMES_METHOD:
               companyNames = e.result;
               if (Log.isDebug()) _log.debug("Retrieved " + ((companyNames) ? ObjectUtil.length(companyNames) : 0) + " companyNames"); 
               sendNotification(method, companyNames);
               break;
            case GET_ACCOUNTS_METHOD:
            case GET_INACTIVE_ACCOUNTS_METHOD:
               accounts = e.result as IList;
               if (Log.isDebug()) _log.debug("Retrieved " + ((accounts) ? accounts.length : "0") + " accounts.");  
               sendNotification(method, accounts);
               break;
            case GET_ACCOUNT_METHOD:
               account = e.result as ServiceCompanyAccountVO;
               if (Log.isDebug()) _log.debug("Fetched account: " + account.name + ".");  
               sendNotification(method, account);
               break;
            case ADD_ACCOUNT_METHOD:
               account = e.result as ServiceCompanyAccountVO;
               message = "Account added successfully: " + account.name + ".";
               if (Log.isDebug()) _log.debug(message);   
               sendNotification(method, account);
               sendNotification(PIProContext.INFO, message);
               break;
            case UPDATE_ACCOUNT_METHOD:
               account = e.result as ServiceCompanyAccountVO;
               message = "Account updated successfully: " + account.name + ".";
               if (Log.isDebug()) _log.debug(message);   
               sendNotification(method, account);
               sendNotification(PIProContext.INFO, message);
               break;
            case REMOVE_ACCOUNT_METHOD:
               message = "Account removed successfully.";
               if (Log.isDebug()) _log.debug(message);   
               sendNotification(method);
               sendNotification(PIProContext.INFO, message);
               break;
            case REACTIVATE_ACCOUNTS_METHOD:
               message = "Accounts reactivated successfully.";
               if (Log.isDebug()) _log.debug(message);   
               sendNotification(method);
               sendNotification(PIProContext.INFO, message);
               break;
            default:
         }
      }
   }
}