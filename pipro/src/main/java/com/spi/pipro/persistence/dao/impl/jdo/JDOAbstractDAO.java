/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao.impl.jdo;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.spi.pipro.persistence.dao.IDAO;

public abstract class JDOAbstractDAO <T> implements IDAO<T, Long>
{
   private static Logger _log = Logger.getLogger(JDOAbstractDAO.class.getName());
   
   private Class<T> _persistentClass;
   private PersistenceManager _persistenceManager;
   private Transaction _transaction;
   
   @SuppressWarnings("unchecked")
   public JDOAbstractDAO() 
   {
      _persistentClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
   }
   
   public T findById(Long id)
   {
      Key key = KeyFactory.createKey(getPersistentClass().getSimpleName(), id);
      return (T)getPersistenceMgr().getObjectById(getPersistentClass(), key);
   }
   
   public List<T> findAll()
   {
      PersistenceManager pm = getPersistenceMgr();
      pm.getFetchPlan().addGroup("detail");
      List<T> all = new ArrayList<T>();
      Extent<T> extent = pm.getExtent(_persistentClass);
      for (T entity : extent)
      {
         all.add(entity);
      }
      return all;
   }
   
   public T makePersistent(T entity)
   {
      return getPersistenceMgr().makePersistent(entity);
   }
   
   public void deletePersistent(Long id)
   {
      PersistenceManager pm = getPersistenceMgr(); 
      Key key = KeyFactory.createKey(getPersistentClass().getSimpleName(), id);
      T entity = pm.getObjectById(getPersistentClass(), key);
      pm.deletePersistent(entity);
   }
   
   public void flush()
   {
      
   }
   
   public PersistenceManager getPersistenceMgr()
   {
      if (_persistenceManager == null)
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("Retrieving the persistence manager");
         _persistenceManager = PMF.get().getPersistenceManager();
      }
      return _persistenceManager;
   }
   
   public void setPersistenceMgr(PersistenceManager mgr)
   {
      if (_log.isLoggable(Level.FINE)) _log.fine("Received persistence manager: " + mgr);
      _persistenceManager = mgr;
   }
   
   public Class<T> getPersistentClass()
   {
      return _persistentClass;
   }

   @Override
   public void startTransaction()
   {
      _transaction = getPersistenceMgr().currentTransaction();
      _transaction.begin();
   }

   @Override
   public void commitTransaction()
   {
      if (_transaction != null)
      {
         _transaction.commit();
      }
   }

   @Override
   public void rollbackTransaction()
   {
      if (_transaction != null)
      {
         _transaction.rollback();
      }
   }

   @Override
   public void closeSession()
   {
      getPersistenceMgr().close();
   }
}
