/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao.impl.jdo;

import com.spi.pipro.persistence.dao.IDAOFactory;
import com.spi.pipro.persistence.dao.IPayStatementDAO;
import com.spi.pipro.persistence.dao.IPersonDAO;
import com.spi.pipro.persistence.dao.IPropertyDAO;
import com.spi.pipro.persistence.dao.IServiceCompanyAccountDAO;
import com.spi.pipro.persistence.dao.IWorkOrderDAO;

public class JDODAOFactory implements IDAOFactory
{
   @Override
   public IPayStatementDAO getPayStatementDAO()
   {
      return new JDOPayStatementDAO();
   }

   @Override
   public IPersonDAO getPersonDAO()
   {
      return new JDOPersonDAO();
   }

   @Override
   public IPropertyDAO getPropertyDAO()
   {
      return new JDOPropertyDAO();
   }

   @Override
   public IServiceCompanyAccountDAO getServiceCompanyAccountDAO()
   {
      return new JDOServiceCompanyDAO();
   }

   @Override
   public IWorkOrderDAO getWorkOrderDAO()
   {
      return new JDOWorkOrderDAO();
   }
}
