/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao;

import java.io.Serializable;
import java.util.List;

import javax.jdo.PersistenceManager;

public interface IDAO<T, ID extends Serializable>
{
   public T findById(ID id);
   
   public List<T> findAll();
   
   public T makePersistent(T entity);
   
   public void deletePersistent(ID id);
   
   public void flush();
   
   public PersistenceManager getPersistenceMgr();
   
   public void setPersistenceMgr(PersistenceManager mgr);
   
   public Class<T> getPersistentClass();
   
   public void startTransaction();
   
   public void commitTransaction();
   
   public void rollbackTransaction();
   
   public void closeSession();
}
