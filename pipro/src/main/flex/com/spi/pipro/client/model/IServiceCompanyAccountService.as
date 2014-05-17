/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model
{
   import com.spi.pipro.client.model.vo.ServiceCompanyAccountVO;
   
   import mx.collections.IList;

   public interface IServiceCompanyAccountService
   {
      function getAccounts():void;
      
      function getServiceCompanyNames():void;
      
      function getInactiveAccounts():void;
      
      function getAccountByKey(key:String):void;
      
      function addAccount(account:ServiceCompanyAccountVO):void;
      
      function updateAccount(account:ServiceCompanyAccountVO):void;
      
      function removeAccount(key:String):void;
      
      function reactivateAccounts(keys:IList):void;  
   }
}