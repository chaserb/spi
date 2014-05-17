/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.blaze;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.entity.ServiceCompanyAccount;
import com.spi.pipro.service.IServiceCompanyAccountService;

public class ServiceCompanyAccountClientService extends AbstractDurableClientService<ServiceCompanyAccount>
{
   private Logger _log = Logger.getLogger(ServiceCompanyAccountClientService.class.getName());

   public Map<String, String> getCompanyNames()
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      Map<String, String> names = getService().getCompanyNames();
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return names;
   }
   
   public IServiceCompanyAccountService getService()
   {
      return (IServiceCompanyAccountService) super.getService();
   }
   
   public void setService(IServiceCompanyAccountService service)
   {
      super.setService(service);
   }
}
