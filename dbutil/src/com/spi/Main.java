/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import com.spi.util.Debug;

public class Main
{
   protected static Properties cEnv;
   protected static SPIAccess cAccess;

   public static void main(String[] args) throws Exception
   {
      cEnv = getEnvironment();
      cAccess = SPIAccess.getInstance();
      cAccess.setEnvironment(cEnv);

      // Process each provider
      String providerList = cEnv.getProperty("com.spi.InputProviders");
      if (providerList != null)
      {
         StringTokenizer tok = new StringTokenizer(providerList);
         while (tok.hasMoreTokens())
         {
            String providerName = tok.nextToken();
            InputProvider provider = null;
            try
            {
               Class<?> providerClass = Class.forName(cEnv.getProperty("com.spi." + providerName + ".class"));
               provider = (InputProvider) providerClass.newInstance();
            }
            catch (Exception e)
            {
               Debug.debugException("Could not find the input provider for \"" + providerName + "\":", e);
            }
            if (provider != null)
            {
               provider.setName(providerName);
               provider.setEnvironment(cEnv);
               processProvider(provider);
            }
         }
      }

      Debug.closeLog();
   }

   public static void processProvider(InputProvider provider)
   {
      Debug.log("Reading Inspection Orders From " + provider.getServiceCompany() + " ...");

      List<Order> orders = provider.getAllOrders();
      if (!provider.getStatistics().getErrorOccurred())
      {
         Debug.log("Correlating Work Order Numbers to Tracking Numbers...");
         cAccess.getTrackingNumbers(orders, provider);
      }
      if (!provider.getStatistics().getErrorOccurred())
      {
         Debug.log("Reading Cost and Wage Data From Local Database...");
         cAccess.getCostData(provider);
      }
      if (!provider.getStatistics().getErrorOccurred())
      {
         cAccess.getCostValues(orders);
      }
      trimIgnores(orders, provider);
      if ((!provider.getStatistics().getErrorOccurred()) && (!provider.ignoresNew()))
      {
         Debug.log("Adding New Inspection Orders To Local Database...");
         cAccess.addNewOrders(provider, orders);
      }
      if (!provider.getStatistics().getErrorOccurred())
      {
         Debug.log("Updating Inspection Orders In Local Database...");
         cAccess.updateOrders(provider, orders);
      }
      updateStatistics(provider, orders, provider.getStatistics());
      if (provider.getStatistics().getErrorOccurred() || cAccess.getStatistics().getErrorOccurred())
      {
         Debug.log("");
         Debug.log("ERROR OCCURED. Some or all of the data may not have been accounted for.");
      } else
      {
         Debug.log("");
         Debug.log("Success!!!");
      }
      Debug.log("");
      String plural = (provider.getStatistics().getNumIOsTotal() == 1) ? "" : "s";
      Debug.log("Processed " + provider.getStatistics().getNumIOsTotal() + " Inspection Order" + plural + " (" + provider.getStatistics().getNumIOsNew()
            + " new, " + provider.getStatistics().getNumIOsUpdates() + " updated, " + provider.getStatistics().getNumIOsUnchanged() + " unchanged).");
      plural = (provider.getStatistics().getNumInvoicesTotal() == 1) ? "" : "s";
      Debug.log("Processed " + provider.getStatistics().getNumInvoicesTotal() + " Invoice" + plural + ".");
      Debug.log("");
   }

   private static Properties getEnvironment()
   {
      Properties props = new Properties();
      try
      {
         props.load(new FileInputStream("spi.properties"));
      }
      catch (Exception ex)
      {
         Debug.debugException("Could not load the properties file: ", ex);
      }
      return props;
   }

   private static void trimIgnores(List<Order> orders, InputProvider provider)
   {
      if (provider.ignoresNew() || provider.ignoresOld())
      {
         Order order;
         for (Iterator<Order> i = orders.iterator(); i.hasNext();)
         {
            order = i.next();
            if (provider.ignoresNew() && order.getIsNew())
            {
               i.remove();
            }
            if (provider.ignoresOld() && (!order.getIsNew()))
            {
               i.remove();
            }
         }
      }
   }

   private static void updateStatistics(InputProvider provider, List<Order> orders, Statistics stats)
   {
      int numNew = 0;
      int numUpdated = 0;
      int numUnchanged = 0;
      for (Order order : orders)
      {
         if (order.getRecdIn().compareTo(provider.getEarliestDate()) >= 0)
         {
            if (order.getIsNew())
            {
               numNew++;
            } else if (order.getIsChanged())
            {
               numUpdated++;
            } else
            {
               numUnchanged++;
            }
         }
      }
      stats.setNumIOsNew(numNew);
      stats.setNumIOsUpdates(numUpdated);
      stats.setNumIOsUnchanged(numUnchanged);
      stats.setNumIOsTotal(orders.size());
   }
}
