/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao.impl.jdo;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;

import com.spi.pipro.persistence.dao.IPersonDAO;
import com.spi.pipro.persistence.entity.Person;

public class JDOPersonDAO extends JDOAbstractDAO<Person> implements IPersonDAO
{
   private static Logger _log = Logger.getLogger(JDOPersonDAO.class.getName());
   
   @SuppressWarnings("unchecked")
   @Override
   public Person findByUsername(String username)
   {
      Query query = getPersistenceMgr().newQuery(getPersistentClass());
      query.setFilter("_userName == userNameParam");
      query.declareParameters("String userNameParam");
      try {
         List<Person> results = (List<Person>) query.execute(username);
         switch (results.size())
         {
            case 0:
               return null;
            case 1:
               return results.get(0);
            default:
               _log.severe("There are " + results.size() + " Person records with the following username: " + username);
               return results.get(0);
         }
     } finally {
         query.closeAll();
     }      
   }
}
