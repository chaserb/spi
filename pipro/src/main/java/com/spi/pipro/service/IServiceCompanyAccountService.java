/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service;

import java.util.List;
import java.util.Map;

import com.spi.pipro.company.IServiceCompany;
import com.spi.pipro.persistence.entity.ServiceCompanyAccount;

public interface IServiceCompanyAccountService extends IDurableService<ServiceCompanyAccount, Long>
{
   /**
    * Return the map of company IDs to company names, representing the companies
    * with which this service is equipped to interface.
    * 
    * @return the list of display-able company names.
    */
   public Map<String, String> getCompanyNames();

   /**
    * Getter for the list of service companies with which this company may
    * interact.
    * 
    * @return the list of service companies.
    */
   public List<IServiceCompany> getServiceCompanies();

   /**
    * Setter for the list of service companies with which this company may
    * interact.
    * 
    * @param companies the list of service companies.
    */
   public void setServiceCompanies(List<IServiceCompany> companies);
}
