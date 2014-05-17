/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service.blaze;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.spi.pipro.persistence.entity.Person;
import com.spi.pipro.service.IPeopleService;
import com.spi.pipro.util.LongUtil;

public class PeopleClientService extends AbstractDurableClientService<Person>
{
   private Logger _log = Logger.getLogger(PeopleClientService.class.getName());
   
   public Person getPersonByUsername(String username)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      Person person = getService().getPersonByUsername(username); 
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
      return person;
   }
   
   public void setPassword(String key, String newPassword)
   {
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "enter");
      getService().setPassword(LongUtil.readLong(key), newPassword);
      if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "complete");
   }
   
   public IPeopleService getService()
   {
      return (IPeopleService) super.getService();
   }

   public void setService(IPeopleService service)
   {
      super.setService(service);
   }
}
