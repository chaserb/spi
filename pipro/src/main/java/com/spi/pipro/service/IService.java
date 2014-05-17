/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service;

import java.io.Serializable;
import java.util.List;

import com.spi.pipro.persistence.dao.IDAOFactory;
import com.spi.pipro.persistence.entity.Entity;

public interface IService<T extends Entity, ID extends Serializable>
{
   /**
    * Retrieves all the entities served up by this service.
    * 
    * @return a collection of active entities.
    */
   public List<T> getAll();

   /**
    * Retrieve a given entity.
    * 
    * @param key the entity's identifier.
    * @return 
    */
   public T getByKey(ID key);
   
   /**
    * Add a new entity.
    * 
    * @param entity
    */
   public T add(T entity);

   /**
    * Update an existing entity. 
    * 
    * @param entity
    */
   public T update(T entity);
   
   /**
    * Remove an existing entity.
    *  
    * @param key
    */
   public void remove(ID key);

   /**
    * Get the data access object factory this service will use to retrieve DAO
    * instances.
    * 
    * @return
    */
   public IDAOFactory getDAOFactory();

   /**
    * Set the data access object factory this service will use to retrieve DAO
    * instances.
    * 
    * @param factory
    */
   public void setDAOFactory(IDAOFactory factory);
}
