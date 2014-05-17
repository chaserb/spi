/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.blaze;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.entity.Entity;
import com.spi.pipro.service.IService;
import com.spi.pipro.util.LongUtil;

public class AbstractClientService<T extends Entity>
{
   private Logger _log = Logger.getLogger(AbstractClientService.class.getName());
   private IService<T, Long> _service;

   public List<T> getAll()
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      List<T> all = getService().getAll();
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return all;
   }

   public T getByKey(String key)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      T entity = getService().getByKey(LongUtil.readLong(key));
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return entity;
   }

   public T add(T entity)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      T retEntity = (T)getService().add(entity);
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return retEntity;
   }

   public T update(T entity)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      T retEntity = getService().update(entity);
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return retEntity;
   }

   public void remove(String key)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      getService().remove(LongUtil.readLong(key));
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
   }

   /* ------------------------- Dependency Accessors ------------------------ */

   public IService<T, Long> getService()
   {
      return _service;
   }

   public void setService(IService<T, Long> service)
   {
      _service = service;
   }
}