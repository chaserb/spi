/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.company.IServiceCompany;
import com.spi.pipro.persistence.dao.IServiceCompanyAccountDAO;
import com.spi.pipro.persistence.entity.ServiceCompanyAccount;
import com.spi.pipro.service.IServiceCompanyAccountService;

public class ServiceCompanyAccountService extends AbstractDurableService<ServiceCompanyAccount, Long> implements IServiceCompanyAccountService
{
   private static Logger _log = Logger.getLogger(PeopleService.class.getName());

   private Map<String, IServiceCompany> _serviceCompanies;

   @Override
   public Map<String, String> getCompanyNames()
   {
      if (_log.isLoggable(Level.FINE)) _log.fine("enter");
      Map<String, String> names = new HashMap<String, String>();
      for (IServiceCompany serviceCompany : _serviceCompanies.values())
      {
         names.put(serviceCompany.getId(), serviceCompany.getName());
      }
      if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + names);
      return names;
   }

   @Override
   public List<IServiceCompany> getServiceCompanies()
   {
      return (_serviceCompanies == null) ? new ArrayList<IServiceCompany>() : new ArrayList<IServiceCompany>(_serviceCompanies.values());
   }

   @Override
   public void setServiceCompanies(List<IServiceCompany> companies)
   {
      _serviceCompanies = new HashMap<String, IServiceCompany>();
      if (companies != null) 
      {
         for (IServiceCompany company : companies)
         {
            _serviceCompanies.put(company.getId(), company);
         }
      }
   }

   /* -------------------------- Private Utilities -------------------------- */

   protected IServiceCompanyAccountDAO getDAO()
   {
      return getDAOFactory().getServiceCompanyAccountDAO();
   }

   @Override
   protected String getEntityName(boolean singular, boolean upperCase)
   {
      if (upperCase)
      {
         return (singular) ? "Service Company Account" : "Service Company Accounts";
      }
      else
      {
         return (singular) ? "service company account" : "service company accounts";
      }
   }
}
