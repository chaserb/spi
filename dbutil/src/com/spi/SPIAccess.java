/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.spi.InputProvider.OutputField;
import com.spi.util.Debug;

/**
 * @author Chase Barrett
 */
public class SPIAccess
{
   private static Statistics mStats = new Statistics();
   private static final String DEBUG_PREFIX = "SPIAccess.";
   private String mUrl;
   private String mUser;
   private String mPass;
   private Properties mEnvProps;
   private Map<Integer, ZipCodeRecord> mZipCodeRecords;
   private static SPIAccess cInstance;

   private SPIAccess()
   {
   }

   public static SPIAccess getInstance()
   {
      if (cInstance == null)
      {
         synchronized (SPIAccess.class)
         {
            if (cInstance == null)
            {
               cInstance = new SPIAccess();
            }
         }
      }
      return cInstance;
   }

   public int getMaxTrackingNumber()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getMaxTrackingNumber(): START");
      Connection conn = null;
      Statement st = null;
      ResultSet rs = null;
      int baseNum = Integer.getInteger("com.spi.SPIAccess.baseTrackingNumber", 800000).intValue();
      int trackingNum = baseNum;
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         st = conn.createStatement();
         rs = st.executeQuery("select max([tracking sheets].[tracking no]) as [maxtrackingno] from [tracking sheets]");
         if (rs.next())
         {
            trackingNum = rs.getInt("maxtrackingno");
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error retrieving the last tracking num: ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }
      if (baseNum > trackingNum) trackingNum = baseNum;
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getMaxTrackingNumber(): END: " + trackingNum);
      return trackingNum;
   }

   public void getTrackingNumbers(List<Order> orders, InputProvider provider)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getTrackingNumbers(): START");
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      int trackingNum;
      int currentTrackingNo = provider.getMinTrackingNumber();
      Date recdInDate, highestDate;
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         ps = conn.prepareStatement("select [tracking no], [recd in] from [tracking sheets] where [cust wo no] = ?");
         for (Order order : orders)
         {
            highestDate = null;
            ps.setString(1, order.getCustWONo());
            rs = ps.executeQuery();
            while (rs.next())
            {
               trackingNum = rs.getInt("tracking no");
               recdInDate = rs.getDate("recd in");
               if ((highestDate == null) || (recdInDate.getTime() > highestDate.getTime()))
               {
                  order.setTrackingNumber(trackingNum);
                  order.setIsNew(false);
                  highestDate = recdInDate;
               } else
               {
                  Debug.debug(Debug.OFF, "Here's a tracking number I ignored: " + trackingNum + ".  It had a duplicate work order number: "
                        + order.getCustWONo());
               }
            }
            Database.closeResultSet(rs);
            if (order.getIsNew())
            {
               if (provider.generatesTrackingNumber())
               {
                  currentTrackingNo++;
                  order.setTrackingNumber(currentTrackingNo);
               }
               Debug.log("\tFound: " + order.getTrackingNo(), false);
            }
            if (Debug.checkLevel(Debug.HIGH))
               Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getTrackingNumbers(): correlated tracking no: " + order.getTrackingNo() + " to work order no: "
                     + order.getCustWONo());
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error retrieving PO Numbers from the local database: ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(ps);
         Database.closeConnection(conn);
      }
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getTrackingNumbers(): END");
   }

   public void getCostData(InputProvider provider)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getCostData(): START");
      Connection conn = null;
      Statement st = null;
      ResultSet rs = null;
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         st = conn.createStatement();
         rs = st.executeQuery("select [photo cost], [personal contact cost] from [service companys] where [service company] = '" + provider.getServiceCompany()
               + "'");
         if (rs.next())
         {
            CostFigures.setPhotoCost(rs.getFloat("photo cost"));
            CostFigures.setPersonalContactCost(rs.getFloat("personal contact cost"));
         } else
         {
            CostFigures.setPhotoCost(1.5f);
            CostFigures.setPersonalContactCost(0.0f);
         }
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         st = conn.createStatement();
         rs = st.executeQuery("select [report type], [cost] from [cost matrix] where [service company] = '" + provider.getServiceCompany() + "'");
         Map<String, Float> hash = new HashMap<String, Float>();
         while (rs.next())
         {
            hash.put(rs.getString("report type"), new Float(rs.getFloat("cost")));
         }
         CostFigures.setCostMatrix(hash);
      }
      catch (Exception e)
      {
         Debug.debugException("Error retrieving the last tracking num: ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getCostData(): END");
   }

   public void getCostValues(List<Order> orders)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getCostValues(): START");

      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Determining the range of order tracking numbers");
      int cur = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
      for (Order order : orders)
      {
         cur = order.getTrackingNo();
         max = Math.max(cur, max);
         min = Math.min(cur, min);
      }
      if (Debug.checkLevel(Debug.HIGH))
      {
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Done: " + min + " - " + max);
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Retrieving cost values for the range of orders");
      }
      Order order;
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      Map<Integer, String> otLateMap = new HashMap<Integer, String>();
      Map<Integer, Integer> qtyRecdMap = new HashMap<Integer, Integer>();
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         ps = conn
               .prepareStatement("select [tracking no], [recd ot late], [qty photos rec] from [tracking sheets] where [tracking no] >= ? and [tracking no] <= ?");
         ps.setInt(1, min);
         ps.setInt(2, max);
         rs = ps.executeQuery();
         while (rs.next())
         {
            int trackingNo = rs.getInt("tracking no");
            otLateMap.put(trackingNo, rs.getString("recd ot late"));
            qtyRecdMap.put(trackingNo, rs.getInt("qty photos rec"));
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error retrieving rural and photo values from the local database: ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(ps);
         Database.closeConnection(conn);
      }
      if (Debug.checkLevel(Debug.HIGH))
      {
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Retrieved " + otLateMap.size() + " ontime/late values from the database");
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Retrieved " + qtyRecdMap.size() + " qty photos recd values from the database");
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Updating order objects with cost values");
      }
      int otLateUpdated = 0;
      for (Iterator<Order> i = orders.iterator(); i.hasNext();)
      {
         order = i.next();
         String otLate = otLateMap.get(order.getTrackingNo());
         if (otLate != null)
         {
            order.setIsLate(otLate);
            otLateUpdated++;
         } else if (Debug.checkLevel(Debug.HIGH))
         {
            Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): No ot/late data for order " + order.getTrackingNo() + ", " + order.getRecdIn());
         }
      }
      if (Debug.checkLevel(Debug.HIGH))
      {
         Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getCostValues(): Updated " + otLateUpdated + " orders with ontime/late data");
      }
      if (Debug.checkLevel(Debug.MED))
      {
         Debug.debug(Debug.MED, DEBUG_PREFIX + "getCostValues(): END");
      }
   }

   public void addNewOrders(InputProvider provider, List<Order> orders)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "addNewOrders(): START");
      java.sql.Date today = new java.sql.Date(new Date().getTime());
      Order order = null;
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         ps = conn.prepareStatement("insert into [tracking sheets] ([tracking no], [cust wo no], [service company], [added dt]) values (?, ?, ?, ?)");
         for (Iterator<Order> i = orders.iterator(); i.hasNext();)
         {
            order = i.next();
            if (order.getIsNew() && (order.getRecdIn().compareTo(provider.getEarliestDate()) >= 0))
            {
               try
               {
                  if (Debug.checkLevel(Debug.HIGH))
                     Debug.debug(Debug.HIGH, DEBUG_PREFIX + "addNewOrders(): adding wo number: " + order.getCustWONo() + ", tracking no: "
                           + order.getTrackingNo());
                  ps.setInt(1, order.getTrackingNo());
                  ps.setString(2, order.getCustWONo());
                  ps.setString(3, order.getServiceCompany());
                  ps.setDate(4, today);
                  ps.executeUpdate();
                  Debug.log("\tAdded: " + order.getTrackingNo(), false);
               }
               catch (Exception e)
               {
                  Debug.debugException("Unable to add a record to the database: [Tracking No: " + order.getTrackingNo() + ", Cust WO No: "
                        + order.getCustWONo() + "] because of a(n) ", e);
                  getStatistics().setErrorOccurred();
               }
            }
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error adding records to the local database for order: [" 
               + ((order == null) ? "null" : 
                  "Tracking No: " + order.getTrackingNo() + ", Cust WO No: " + order.getCustWONo()) + "] because of a(n) ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(ps);
         Database.closeConnection(conn);
      }
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "addNewOrders(): END");
   }

   public void updateOrders(InputProvider provider, List<Order> orders)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "updateOrders(): START");
      Order order = null;
      HashSet<String> invoices = new HashSet<String>();
      Connection conn = null;
      PreparedStatement selectPs = null;
      ResultSet selectRs = null;
      StringBuffer selectSql = new StringBuffer();
      selectSql.append("select ");
      for (Iterator<OutputField> i = provider.getSupportedFields().iterator(); i.hasNext();)
      {
         OutputField field = i.next();
         field.addSelectClause(selectSql);
         if (i.hasNext())
         {
            selectSql.append(", ");
         }
      }
      selectSql.append(" from [tracking sheets] where [tracking no] = ?");
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         selectPs = conn.prepareStatement(selectSql.toString());
         for (Iterator<Order> i = orders.iterator(); i.hasNext();)
         {
            order = i.next();
            if (order.getRecdIn().compareTo(provider.getEarliestDate()) >= 0)
            {
               try
               {
                  if (Debug.checkLevel(Debug.HIGH))
                     Debug.debug(Debug.HIGH, DEBUG_PREFIX + "updateOrders(): updating tracking number: " + order.getTrackingNo());

                  // See if an update is necessary
                  List<OutputField> fieldsToUpdate = new ArrayList<OutputField>();
                  selectPs.setInt(1, order.getTrackingNo());
                  selectRs = selectPs.executeQuery();
                  if (selectRs.next())
                  {
                     for (int idx = 1; idx <= provider.getSupportedFields().size(); idx++)
                     {
                        OutputField field = provider.getSupportedFields().get(idx - 1);
                        if (field.requiresUpdate(order, idx, selectRs))
                        {
                           fieldsToUpdate.add(field);
                        }
                     }
                  }

                  // If necessary, apply the update
                  if (!fieldsToUpdate.isEmpty())
                  {
                     updateOrder(order, fieldsToUpdate, conn);
                  }
                  if (provider.getSupportedFields().contains(OutputField.INVOICE_NO) && order.getInvoiceNo() != null)
                  {
                     Debug.log("\t\tAdded to Invoice: " + order.getInvoiceNo(), false);
                     invoices.add(order.getInvoiceNo());
                  }
               }
               catch (Exception e)
               {
                  Debug.debugException("Unable to update a record to the database: [Tracking No: " + order.getTrackingNo() + ", Cust WO No: "
                        + order.getCustWONo() + "] because of a(n) ", e);
                  getStatistics().setErrorOccurred();
               }
            }
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error updating a order in the local database for order [" 
               + ((order == null) ? "null" : 
                  "Tracking No: " + order.getTrackingNo() + ", Cust WO No: " + order.getCustWONo()) + "] because of a(n) ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(selectRs);
         Database.closeStatement(selectPs);
         Database.closeConnection(conn);
      }
      getStatistics().setNumInvoicesTotal(invoices.size());
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "updateOrders(): END");
   }

   public Statistics getStatistics()
   {
      if (mStats == null)
      {
         mStats = new Statistics();
      }
      return mStats;
   }

   public void setEnvironment(Properties props)
   {
      mEnvProps = props;
   }

   public String getRepForZip(int zip)
   {
      return getZipCodeRecord(zip).getRep();
   }

   public boolean getRuralForZip(int zip)
   {
      return getZipCodeRecord(zip).isRural();
   }

   public float getWagesForZip(int zip)
   {
      return getZipCodeRecord(zip).getWages();
   }

   public float getRuralWagesForZip(int zip)
   {
      return getZipCodeRecord(zip).getWagesRural();
   }

   public String getStateForZip(int zip)
   {
      return getZipCodeRecord(zip).getState();
   }

   protected String getUrl()
   {
      if (mUrl == null)
      {
         mUrl = getEnvironment().getProperty("com.spi.Database.outputURL");
      }
      return mUrl;
   }

   protected String getUser()
   {
      if (mUser == null)
      {
         mUser = getEnvironment().getProperty("com.spi.Database.outputUser");
      }
      return mUser;
   }

   protected String getPassword()
   {
      if (mPass == null)
      {
         mPass = getEnvironment().getProperty("com.spi.Database.outputPassword");
      }
      return mPass;
   }

   public Properties getEnvironment()
   {
      if (mEnvProps == null)
      {
         mEnvProps = new Properties();
      }
      return mEnvProps;
   }

   protected void updateOrder(Order order, List<OutputField> updateFields, Connection conn) throws Exception
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "updateOrder(): START: updating tracking number: " + order.getTrackingNo());

      if (updateFields == null || updateFields.isEmpty())
      {
         return;
      }
      PreparedStatement updatePs = null;
      StringBuffer updateSql = new StringBuffer();
      updateSql.append("update [tracking sheets] set ");
      for (Iterator<OutputField> i = updateFields.iterator(); i.hasNext();)
      {
         OutputField field = i.next();
         field.addUpdateClause(updateSql);
         if (i.hasNext())
         {
            updateSql.append(", ");
         }
      }
      updateSql.append(" where [tracking no] = ?");
      try
      {
         updatePs = conn.prepareStatement(updateSql.toString());
         order.setIsChanged(true);
         for (int idx = 1; idx <= updateFields.size(); idx++)
         {
            OutputField field = updateFields.get(idx - 1);
            field.applyUpdateParameter(order, idx, updatePs);
         }
         updatePs.setInt(updateFields.size() + 1, order.getTrackingNo());
         updatePs.executeUpdate();
         Debug.log("\tUpdated: " + order.getTrackingNo(), false);
      }
      finally
      {
         Database.closeStatement(updatePs);
      }
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "updateOrder(): END");
   }

   protected ZipCodeRecord getZipCodeRecord(int zip)
   {
      if (mZipCodeRecords == null)
      {
         mZipCodeRecords = new HashMap<Integer, ZipCodeRecord>();

         if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getZipCodeRecords(): START");
         Connection conn = null;
         Statement st = null;
         ResultSet rs = null;
         try
         {
            conn = Database.getConnection(getUrl(), getUser(), getPassword());
            st = conn.createStatement();
            rs = st.executeQuery("select [zip code], [rep], [rural], [wages], [wages_rural], [st] from [zip codes]");
            while (rs.next())
            {
               ZipCodeRecord record = new ZipCodeRecord();
               record.setZip(rs.getInt("zip code"));
               record.setRep(rs.getString("rep"));
               record.setRural((rs.getString("rural").equalsIgnoreCase("yes")));
               record.setWages(rs.getFloat("wages"));
               record.setWagesRural(rs.getFloat("wages_rural"));
               record.setState(rs.getString("st"));
               mZipCodeRecords.put(record.getZip(), record);
            }
         }
         catch (Exception e)
         {
            Debug.debugException("Error retrieving zip code data: ", e);
            getStatistics().setErrorOccurred();
         }
         finally
         {
            Database.closeResultSet(rs);
            Database.closeStatement(st);
            Database.closeConnection(conn);
         }
         if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getZipCodeRecords(): END");
      }
      ZipCodeRecord record = mZipCodeRecords.get(zip);
      if (record == null)
      {
         record = new ZipCodeRecord();
      }
      return record;
   }

   public Collection<SPIOrder> getUnassignedOrders()
   {
      Collection<SPIOrder> orders = new ArrayList<SPIOrder>();
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getUnassignedOrders(): START");
      Connection conn = null;
      Statement st = null;
      ResultSet rs = null;
      try
      {
         conn = Database.getConnection(getUrl(), getUser(), getPassword());
         st = conn.createStatement();
         StringBuffer sql = new StringBuffer();
         sql.append("select ");
         for (OutputField field : OutputField.values())
         {
            field.addSelectClause(sql);
            sql.append(", ");
         }
         sql.setLength(sql.length() - 2);
         sql.append(" from [tracking sheets] where [tracking sheets].rep is null");
         rs = st.executeQuery(sql.toString());
         while (rs.next())
         {
            SPIOrder order = new SPIOrder();
            for (OutputField field : OutputField.values())
            {
               field.getSelectedParameter(order, rs);
            }
            orders.add(order);
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Error retrieving zip code data: ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getUnassignedOrders(): END");
      return orders;
   }

   class ZipCodeRecord
   {
      private int mZip = 80910;
      private String mRep = "Lindsey";
      private boolean mRural = false;
      private float mWages = 2.5f;
      private float mWagesRural = 4.0f;
      private String mState = "CO";

      public String getRep()
      {
         return mRep;
      }

      public void setRep(String rep)
      {
         mRep = rep;
      }

      public boolean isRural()
      {
         return mRural;
      }

      public void setRural(boolean rural)
      {
         mRural = rural;
      }

      public String getState()
      {
         return mState;
      }

      public void setState(String state)
      {
         mState = state;
      }

      public float getWages()
      {
         return mWages;
      }

      public void setWages(float wages)
      {
         mWages = wages;
      }

      public float getWagesRural()
      {
         return mWagesRural;
      }

      public void setWagesRural(float wagesRural)
      {
         mWagesRural = wagesRural;
      }

      public int getZip()
      {
         return mZip;
      }

      public void setZip(int zip)
      {
         mZip = zip;
      }
   }
}
