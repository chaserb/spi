/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.dao.IDAO;
import com.spi.pipro.persistence.dao.IPersonDAO;
import com.spi.pipro.persistence.entity.Person;
import com.spi.pipro.service.IPeopleService;
import com.spi.pipro.util.EqualityUtils;

public class PeopleService extends AbstractDurableService<Person, Long> implements IPeopleService
{
   private static Logger _log = Logger.getLogger(PeopleService.class.getName());

   @Override
   public Person getPersonByUsername(String username)
   {
      IPersonDAO dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, retrieving: " + username);
         Person person = dao.findByUsername(username);
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + person);
         return person;
      }
      catch (Throwable e)
      {
         handleError(e, "fetch the person by username.");
         return null;
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public void setPassword(Long key, String newPassword)
   {
      IPersonDAO dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, setting password for: " + key);
         dao.startTransaction();
         Person person = dao.findById(key);
         person.setPlainTextPassword(newPassword);
         dao.makePersistent(person);
         dao.commitTransaction();
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + person);
      }
      catch (Throwable e)
      {
         dao.rollbackTransaction();
         handleError(e, "set the person's password.");
      }
      finally
      {
         dao.closeSession();
      }
   }

   /* -------------------------- Private Utilities -------------------------- */

   protected IPersonDAO getDAO()
   {
      return getDAOFactory().getPersonDAO();
   }

   @Override
   protected void preprocessUpdate(Person person, IDAO<Person, Long> dao)
   {
      IPersonDAO personDao = (IPersonDAO)dao;
      
      // Ensure the user name is unique, since GAE does not support the
      // @Unique annotation
      Person refPerson = personDao.findByUsername(person.getUserName());
      if (refPerson != null && !EqualityUtils.equals(person.getKey(), refPerson.getKey()))
      {
         throw new IllegalArgumentException("Another user already has the username: " + person.getUserName());
      }

      // See if we need to update the password
      refPerson = (person.getKey() == null) ? null : dao.findById(person.getKey().getId());
      if (refPerson == null || !EqualityUtils.equals(refPerson.getPassword(), person.getPassword()))
      {
         // Either we are adding a new person, or an existing person is
         // updating their password in some way. Need to hash the given
         // password.
         person.setPlainTextPassword(person.getPassword());
      }
   }

   @Override
   protected String getEntityName(boolean singular, boolean upperCase)
   {
      if (upperCase)
      {
         return (singular) ? "Person" : "People";
      }
      else
      {
         return (singular) ? "person" : "people";
      }
   }
}
