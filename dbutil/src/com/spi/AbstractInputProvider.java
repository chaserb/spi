/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.spi.util.Debug;

/**
 * Common implementations for all input providers
 * 
 * @author Chase Barrett
 */
public abstract class AbstractInputProvider implements InputProvider
{
   public static final long ONE_YEAR_MS = 1000l * 60l * 60l * 24l * 365l;
   public static final Date ONE_YEAR_AGO = new Date(System.currentTimeMillis() - ONE_YEAR_MS);
   
   private String mName;
   private Properties mEnvProps;
   private Statistics mStats;
   private WageFigures mWages;

   public String getName()
   {
      return mName;
   }

   public void setName(String name)
   {
      mName = name;
   }

   public boolean generatesTrackingNumber()
   {
      return Boolean.parseBoolean(getEnvironment().getProperty("com.spi." + getName() + ".generatesTrackingNum"));
   }

   public Date getEarliestDate()
   {
      try
      {
         return DATE_FORMAT.parse(getEnvironment().getProperty("com.spi." + getName() + ".earliestDate"));
      }
      catch (Exception e)
      {
         Calendar cal = Calendar.getInstance();
         cal.set(2007, 12, 3);
         return cal.getTime();
      }
   }

   public int getMaxTrackingNumber()
   {
      try
      {
         return Integer.parseInt(getEnvironment().getProperty("com.spi." + getName() + ".trackingNum.max"));
      }
      catch (NumberFormatException e)
      {
         return Integer.MAX_VALUE;
      }
   }

   public int getMinTrackingNumber()
   {
      try
      {
         return Integer.parseInt(getEnvironment().getProperty("com.spi." + getName() + ".trackingNum.min"));
      }
      catch (NumberFormatException e)
      {
         return 0;
      }
   }

   public Statistics getStatistics()
   {
      if (mStats == null)
      {
         mStats = new Statistics();
      }
      return mStats;
   }

   public boolean ignoresNew()
   {
      return Boolean.parseBoolean(getEnvironment().getProperty("com.spi." + getName() + ".ignoreNew"));
   }

   public boolean ignoresOld()
   {
      return Boolean.parseBoolean(getEnvironment().getProperty("com.spi." + getName() + ".ignoreOld"));
   }

   public void setEnvironment(Properties props)
   {
      mEnvProps = props;
      if (ignoresNew())
      {
         Debug.log("The " + getName() + " provider will NOT Add New Orders To The Local Database.");
      }
      if (ignoresOld())
      {
         Debug.log("The " + getName() + " provider will NOT Update Existing Orders In The Local Database.");
      }
   }

   public WageFigures getWageFigures()
   {
      if (mWages == null)
      {
         mWages = new WageFigures(getEnvironment(), this);
      }
      return mWages;
   }

   protected Properties getEnvironment()
   {
      return (mEnvProps == null) ? new Properties() : mEnvProps;
   }

   /**
    * Filter out any orders that have been received in over one year ago.
    * 
    * @param <T> the order subclass
    * @param orders the list of all orders known
    * @return the subset of orders that have been received in the last year.
    */
   @SuppressWarnings("unchecked")
   protected <T extends Order> List<T> filterOrders(List<T> orders)
   {
      List<T> retOrders = new ArrayList<T>();
      if (retOrders != null)
      {
         for (Order order : orders)
         {
            if (order.getRecdIn() != null && ONE_YEAR_AGO.compareTo(order.getRecdIn()) < 0)
            {
               retOrders.add((T)order);
            }
         }
      }
      return retOrders;
   }
}
