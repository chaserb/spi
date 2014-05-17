/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.event
{
   import flash.events.Event;
   
   public class ServiceCompanyAccountEvent extends Event
   {
      /**
       * Service method for retrieving all the active accounts.
       */ 
      public static const GET_ACCOUNTS_METHOD:String = "getAccountsMethod";
      /**
       * Service method for retrieving the names of all the available service
       * companies.
       */ 
      public static const GET_COMPANY_NAMES_METHOD:String = "getCompanyNamesMethod";
      /**
       * Service method for retrieving all the inactive accounts.
       */ 
      public static const GET_INACTIVE_ACCOUNTS_METHOD:String = "getInactiveAccountsMethod";
      /**
       * Service method for retrieving an individual account.
       */ 
      public static const GET_ACCOUNT_METHOD:String = "getAccountMethod";
      /**
       * Service method for adding an account.
       */ 
      public static const ADD_ACCOUNT_METHOD:String = "addAccountMethod";
      /**
       * Service method for updating an account.
       */ 
      public static const UPDATE_ACCOUNT_METHOD:String = "updateAccountMethod";
      /**
       * Service method for removing an account.
       */ 
      public static const REMOVE_ACCOUNT_METHOD:String = "removeAccountMethod";
      /**
       * Service method for reactivating an account.
       */ 
      public static const REACTIVATE_ACCOUNTS_METHOD:String = "reactivateAccountsMethod";
      
      public function ServiceCompanyAccountEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
      {
         super(type, bubbles, cancelable);
      }
   }
}