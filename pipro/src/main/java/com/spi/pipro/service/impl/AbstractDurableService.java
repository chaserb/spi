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
import com.spi.pipro.persistence.entity.Entity;
import com.spi.pipro.service.IDurableService;

public abstract class AbstractDurableService<T extends Entity, ID extends Serializable> extends AbstractService<T, ID> implements IDurableService<T, ID>
{
   private static Logger _log = Logger.getLogger(AbstractDurableService.class.getName());

   @Override
   public List<T> getAllInactive()
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter");
         // TODO
         if (_log.isLoggable(Level.FINE)) _log.fine("complete: ");
         return null;
      }
      catch (Throwable e)
      {
         handleError(e, "fetch the list of inactive people.");
         return null;
      }
      finally
      {
         dao.closeSession();
      }
   }

   @Override
   public void reactivate(List<ID> keys)
   {
      IDAO<T, ID> dao = getDAO();
      try
      {
         if (_log.isLoggable(Level.FINE)) _log.fine("enter, reactivating: " + keys);
         // TODO
         if (_log.isLoggable(Level.FINE)) _log.fine("complete");
      } 
      catch (Throwable e)
      {
         handleError(e, "reactivate the person.");
      }
      finally
      {
         dao.closeSession();
      }
   }
}
