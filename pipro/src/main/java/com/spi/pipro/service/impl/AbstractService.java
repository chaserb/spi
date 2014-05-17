/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.dao.IDAO;
import com.spi.pipro.persistence.dao.IDAOFactory;
import com.spi.pipro.persistence.entity.Entity;
import com.spi.pipro.service.IService;
import com.spi.pipro.service.ServiceException;

public abstract class AbstractService<T extends Entity, ID extends Serializable> implements IService<T, ID>
{
   private static Logger _log = Logger.getLogger(AbstractService.class.getName());

   private IDAOFactory _factory;

   @Override
   public List<T> getAll()
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter");
         List<T> entities = dao.findAll();
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + ((entities == null) ? null : Integer.toString(entities.size())));
         return entities;
      }
      catch (Throwable e)
      {
         handleError(e, "fetch the list of " + getEntityName(false, false) + ".");
         return null;
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public T getByKey(ID key)
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, retrieving: " + key);
         T entity = dao.findById(key);
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + entity);
         return entity;
      }
      catch (Throwable e)
      {
         handleError(e, "fetch the " + getEntityName(true, false) + " by unique identifier.");
         return null;
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public T add(T entity)
   {
      if (_log.isLoggable(Level.FINE)) _log.fine("enter, adding: " + entity);
      entity.setKey(null);
      T retPerson = update(entity);
      if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + retPerson);
      return retPerson;
   }

   @Override
   public T update(T entity)
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, updating: " + entity);
         dao.startTransaction();

         preprocessUpdate(entity, dao);

         T retPerson = dao.makePersistent(entity);
         dao.commitTransaction();
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: " + retPerson);
         return retPerson;
      }
      catch (Throwable e)
      {
         dao.rollbackTransaction();
         handleError(e, "update the " + getEntityName(true, false) + ".");
         return null;
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public void remove(ID key)
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, removing: " + key);
         dao.startTransaction();
         dao.deletePersistent(key);
         dao.commitTransaction();
         if (_log.isLoggable(Level.FINE)) _log.fine("complete");
      }
      catch (Throwable e)
      {
         dao.rollbackTransaction();
         handleError(e, "remove the " + getEntityName(true, false) + ".");
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public IDAOFactory getDAOFactory()
   {
      return _factory;
   }

   @Override
   public void setDAOFactory(IDAOFactory factory)
   {
      _factory = factory;
   }

   protected abstract IDAO<T, ID> getDAO();

   /**
    * Retrieve a descriptive name for the entities returned by this service.
    * 
    * @param singular if true, get the singular name of the entity, otherwise,
    *        get the plural.
    * @param upperCase if true, get the name with a leading capital letter, as
    *        if it will be used in the beginning of a sentence. Otherwise,
    *        return the lower case version.
    * @return
    */
   protected abstract String getEntityName(boolean singular, boolean upperCase);

   /**
    * Process the entity before persisting in the data source. This super method
    * does nothing, and is intended as a hook for subclasses to perform entity
    * specific business logic and validation before updating.
    * 
    * @param entity
    * @param dao
    */
   protected void preprocessUpdate(T entity, IDAO<T, ID> dao)
   {
   }

   protected void handleError(Throwable e, String operation) throws ServiceException
   {
      String message = "The " + getEntityName(false, false) + " service was unable to " + operation;
      _log.log(Level.WARNING, message, e);
      throw new ServiceException(message, e);
   }
}
