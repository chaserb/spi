/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao;

public interface IDAOFactory
{
   public IPayStatementDAO getPayStatementDAO();

   public IPersonDAO getPersonDAO();

   public IPropertyDAO getPropertyDAO();

   public IServiceCompanyAccountDAO getServiceCompanyAccountDAO();

   public IWorkOrderDAO getWorkOrderDAO();
}
