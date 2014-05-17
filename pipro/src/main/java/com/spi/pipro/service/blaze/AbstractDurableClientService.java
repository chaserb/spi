/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.blaze;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.entity.Entity;
import com.spi.pipro.service.IDurableService;
import com.spi.pipro.util.LongUtil;

public class AbstractDurableClientService<T extends Entity> extends AbstractClientService<T>
{
   private Logger _log = Logger.getLogger(AbstractDurableClientService.class.getName());

   public List<T> getAllInactive()
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      List<T> allInactive = getService().getAllInactive();
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete: ");
      return allInactive;
   }

   public void reactivate(List<String> keys)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      List<Long> longKeys = new ArrayList<Long>();
      if (keys != null)
      {
         for (String key : keys)
         {
            longKeys.add(LongUtil.readLong(key));
         }
      }
      getService().reactivate(longKeys);
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
   }
   
   public IDurableService<T, Long> getService()
   {
      return (IDurableService<T, Long>) super.getService();
   }

   public void setService(IDurableService<T, Long> service)
   {
      super.setService(service);
   }
}
