/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.vendor360;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spi.AbstractInputProvider;
import com.spi.Database;
import com.spi.Order;
import com.spi.util.Debug;
import com.spi.util.EqualityUtils;

/**
 * @author Chase Barrett
 */
public class Vendor360Access extends AbstractInputProvider
{
   private static List<OutputField> cFields;
   private static final String DEBUG_PREFIX = "Vendor360Access.";

   static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
   static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

   static final String INSPECTIONS_WIP = "InspectionsWIP";
   static final String INSPECTIONS_OVERDUE = "OverdueInspections";
   static final String INSPECTIONS_DUE_TODAY = "InspectionsDueToday";
   static final String TABLE = "Table";
   static final String WORK_ORDER_NUM = "work_order_num";
   static final String INSPECTION_ID = "inspection_id";
   static final String PM_ID = "pm_id";
   static final String INSP_TYPE = "insp_type";
   static final String DATE_ORDERED = "date_ordered";
   static final String INSP_LAST_DT = "insp_last_dt";
   static final String BETWEEN_DT = "between_dt";
   static final String ACCNT_NUM = "accnt_num";
   static final String NAME = "name";
   static final String CUS_NUM = "cus_num";
   static final String PREV_OCC_STATUS = "prev_occ_status";
   static final String MTG_NAME = "mtg_name";
   static final String MTG_ADR = "mtg_adr";
   static final String MTG_ADR1 = "mtg_adr1";
   static final String MTG_CITY = "mtg_city";
   static final String MTG_STATE = "mtg_state";
   static final String MTG_ZIP = "mtg_zip";

   // Property Data Items
   static final String OCC_STATUS_ID = "OccStatusID";
   static final String PROP_CON_ID = "PropConID";
   static final String GARAGE_ID = "GarageID";
   static final String STORIES_ID = "StoriesID";
   static final String NEIGH_CON_ID = "NeighConID";
   static final String CON_ID = "ConID";
   static final String COLOR_ID = "ColorID";
   static final String BLDG_ID = "BldgID";
   static final String FOR_SALE_ID = "ForSaleID";
   static final String BROKER_INFO = "BrokerInfo";
   static final String BROKER_ID = "BrokerID";
   static final String TEL1 = "Tel1";
   static final String BROKER_NAME = "BrokerName";

   private Web mWeb;

   public String getServiceCompany()
   {
      return "Mortgage Contracting Services";
   }

   public List<Order> getAllOrders()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): START");
      ArrayList<Vendor360Order> orders = new ArrayList<Vendor360Order>();

      List<Vendor360Order> harvested = null;
      try
      {
         harvested = harvestOrders();
         if (harvested != null) orders.addAll(harvested);
      }
      catch (Exception e)
      {
         getStatistics().setErrorOccurred();
         Debug.debugException("Ooops!", e);
      }

      if (Debug.checkLevel(Debug.MED))
      {
         Map<String, Map<String, String>> debug = Vendor360Order.mDebugValues;
         for (String field : debug.keySet())
         {
            Debug.debug(Debug.MED, "Found these values for \"" + field + "\"");
            for (String value : debug.get(field).keySet())
            {
               Debug.debug(Debug.MED, "    " + value + "\t(eg: order #" + debug.get(field).get(value) + ")");
            }
         }
      }

      getStatistics().setNumIOsTotal(orders.size());
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): END: " + orders.size());
      return new ArrayList<Order>(orders);
   }

   public List<OutputField> getSupportedFields()
   {
      if (cFields == null)
      {
         cFields = new ArrayList<OutputField>(20);
         cFields.add(OutputField.CITY);
         cFields.add(OutputField.DUE_SERV);
         cFields.add(OutputField.LOAN_NO);
         cFields.add(OutputField.FOR_SALE);
         cFields.add(OutputField.FOR_SALE_PHONE);
         cFields.add(OutputField.MORTGAGER);
         cFields.add(OutputField.MTG_COMP);
         cFields.add(OutputField.OCCUPANCY);
         cFields.add(OutputField.PROPERTY_ADDRESS);
         cFields.add(OutputField.PROPERTY_DESCRIPTION);
         cFields.add(OutputField.PROP_NO);
         cFields.add(OutputField.RECD_IN);
         cFields.add(OutputField.REPORT_TYPE);
         cFields.add(OutputField.ST);
         cFields.add(OutputField.ZIP_CODE);
      }
      return cFields;
   }

   protected List<Vendor360Order> harvestOrders() throws Exception
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): START");

      Web web = getWeb();
      web.login();

      // Get the orders we completed on previous passes. We're looking for
      // orders we have entered, and so therefore won't be in the WIP request
      // in the next block of code. We capture their entered values one last
      // time, and then remove them from the list of previous WIP orders at
      // the end of this method.
      Set<Vendor360Order> orders = getPreviousWIPOrders(true);
      Set<Vendor360Order> enteredOrders = new HashSet<Vendor360Order>(orders);

      // Get the orders that are in progress
      Map<String, String> vars = new HashMap<String, String>();
      vars.put(Web.SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.WIP_METHOD);
      InputStream is = web.getInput(web.getLoginURL(), web.getWorkInProgressRequest(), vars);
      processInput(is, orders, enteredOrders, web, INSPECTIONS_WIP, true);
      is.close();

      // Get the orders that are overdue
      vars.put(Web.SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.OVERDUE_METHOD);
      is = web.getInput(web.getLoginURL(), web.getOverdueInspectionsRequest(), vars);
      processInput(is, orders, enteredOrders, web, INSPECTIONS_OVERDUE, true);
      is.close();

      // Get the orders that are due today
      vars.put(Web.SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.DUE_TODAY_METHOD);
      is = web.getInput(web.getLoginURL(), web.getInspectionsDueTodayRequest(), vars);
      processInput(is, orders, enteredOrders, web, INSPECTIONS_DUE_TODAY, true);
      is.close();

      // Get completed orders if called for
      if (Boolean.parseBoolean(getEnvironment().getProperty("com.spi.vendor360.retrieveCompletedOrders")))
      {
         vars.put(Web.SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.EXECUTE_USER_SP_METHOD);
         is = web.getInput(web.getLoginURL(), web.getEnteredForDateRangeRequest(getEarliestDate()), vars);
         processInput(is, orders, enteredOrders, web, TABLE, false);
         is.close();
      }

      // Get the details on each order
      for (Vendor360Order order : orders)
      {
         harvestOrderDetails(order);
      }

      // Update the list of previous WIP requests
      updatePreviousWIPOrders(orders, enteredOrders);

      int numOrders = (orders == null) ? 0 : orders.size();
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): END: " + numOrders);

      return (orders == null) ? new ArrayList<Vendor360Order>() : new ArrayList<Vendor360Order>(orders);
   }
   
   protected void processInput(InputStream is, Set<Vendor360Order> orders, Set<Vendor360Order> enteredOrders, Web web, String listTag, boolean WIP) throws Exception
   {
      is = Debug.dumpStream(is);
      Document doc = web.parseInput(is);
      NodeList inspections = doc.getElementsByTagName(listTag);
      Map<String, String> vars = new HashMap<String, String>();
      for (int i = 0; i < inspections.getLength(); i++)
      {
         vars.clear();
         NodeList children = inspections.item(i).getChildNodes();
         for (int j = 0; j < children.getLength(); j++)
         {
            Node child = children.item(j);
            vars.put(child.getNodeName(), child.getTextContent());
         }
         Vendor360Order order = harvestOrder(vars);
         orders.remove(order);
         orders.add(order);
         
         if (WIP)
         {
            // If the order is in the WIP response, then it hasn't been
            // entered yet
            enteredOrders.remove(order);
         }
      }
   }

   protected Vendor360Order harvestOrder(Map<String, String> map) throws Exception
   {
      Vendor360Order order = null;

      String workOrderNum = map.get(WORK_ORDER_NUM);
      String dueDate = map.get(INSP_LAST_DT);
      if (dueDate == null)
      {
         dueDate = map.get(BETWEEN_DT);
         if (dueDate != null)
         {
            dueDate = dueDate.substring(13);
         }
      }

      if (workOrderNum != null && dueDate != null)
      {
         try
         {
            order = new Vendor360Order(this, workOrderNum, parseDate(dueDate));
            order.setInspectionId(map.get(INSPECTION_ID));
            order.setPMId(map.get(PM_ID));
            order.setInspType(map.get(INSP_TYPE));
            order.setName(map.get(NAME));
            order.setCusNum(map.get(CUS_NUM));
            order.setAccntNum(map.get(ACCNT_NUM));
            order.setPrevOccStatus(map.get(PREV_OCC_STATUS));
            order.setDateOrdered(parseDate(map.get(DATE_ORDERED)));
            order.setMtgName(map.get(MTG_NAME));
            if (map.get(MTG_ADR) != null)
            {
               order.setMtgAdr(map.get(MTG_ADR));
            }
            else
            {
               order.setMtgAdr(map.get(MTG_ADR1));
            }
            order.setMtgCity(map.get(MTG_CITY));
            order.setMtgState(map.get(MTG_STATE));
            order.setMtgZip(map.get(MTG_ZIP));
         }
         catch (Exception e)
         {
            Debug.debugException("WARNING: Vendor360Access.harvestOrder(): Error reading the record for service order ID: " + workOrderNum
                  + ".  Skipping this order.", e);
         }
      }
      return order;
   }

   protected void harvestOrderDetails(Vendor360Order order) throws Exception
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): START");
      
      String pmId = order.getPMId();
      String inspectionId = order.getInspectionId();
      
      if (pmId != null && pmId.length() > 0 && inspectionId != null && inspectionId.length() > 0)
      {
         Web web = getWeb();
         Map<String, String> vars = new HashMap<String, String>();
         vars.put(Web.SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.GET_LAST_INSP_RESULTS_METHOD);
         InputStream is = web.getInput(web.getLoginURL(), web.getInspectionRequest(order), vars);
   //      is = Debug.dumpStream(is);
         Document doc = getWeb().parseInput(is);
   
         // Get the property data
         order.setOccStatusId(getFirstText(doc.getElementsByTagName(OCC_STATUS_ID)));
         order.setPropConId(getFirstText(doc.getElementsByTagName(PROP_CON_ID)));
         order.setGarageId(getFirstText(doc.getElementsByTagName(GARAGE_ID)));
         order.setStoriesId(getFirstText(doc.getElementsByTagName(STORIES_ID)));
         order.setNeighConId(getFirstText(doc.getElementsByTagName(NEIGH_CON_ID)));
         order.setConId(getFirstText(doc.getElementsByTagName(CON_ID)));
         order.setColorId(getFirstText(doc.getElementsByTagName(COLOR_ID)));
         order.setBldgId(getFirstText(doc.getElementsByTagName(BLDG_ID)));
         order.setForSaleId(getFirstText(doc.getElementsByTagName(FOR_SALE_ID)));
   
         // Get the broker info
         NodeList brokerNodes = doc.getElementsByTagName(BROKER_INFO);
         if (brokerNodes.getLength() > 0)
         {
            Node node = brokerNodes.item(0);
            NodeList brokerChildren = node.getChildNodes();
            for (int i = 0; i < brokerChildren.getLength(); i++)
            {
               Node brokerChild = brokerChildren.item(i);
               String text = brokerChild.getTextContent();
               if (EqualityUtils.equals(brokerChild.getNodeName(), BROKER_ID))
               {
                  order.setBrokerId(text);
               } else if (EqualityUtils.equals(brokerChild.getNodeName(), TEL1))
               {
                  order.setTell1(text);
               } else if (EqualityUtils.equals(brokerChild.getNodeName(), BROKER_NAME))
               {
                  order.setBrokerName(text);
               }
            }
         }
      }
   }

   protected Web getWeb()
   {
      if (mWeb == null)
      {
         mWeb = new Web(getEnvironment(), getName());
      }
      return mWeb;
   }

   static Date parseDate(String dateString) throws Exception
   {
      if (dateString.length() == 10)
      {
         return DATE_FORMAT.parse(dateString);
      } else
      {
         String noColon = dateString.substring(0, 22) + dateString.substring(23);
         return DATE_TIME_FORMAT.parse(noColon);
      }
   }

   static String formatDate(Date date) throws Exception
   {
      String noColon = DATE_TIME_FORMAT.format(date);
      return noColon.substring(0, 22) + ":" + noColon.substring(22);
   }

   private String getFirstText(NodeList nodes)
   {
      if (nodes.getLength() > 0)
      {
         return nodes.item(0).getTextContent();
      }
      return null;
   }

   private Set<Vendor360Order> getPreviousWIPOrders(boolean tryAgain)
   {
      Set<Vendor360Order> previous = new HashSet<Vendor360Order>();
      String url = getEnvironment().getProperty("com.spi.Database.outputURL");
      String user = getEnvironment().getProperty("com.spi.Database.outputUser");
      String pass = getEnvironment().getProperty("com.spi.Database.outputPassword");
      Connection conn = null;
      Statement st = null;
      ResultSet rs = null;

      try
      {
         conn = Database.getConnection(url, user, pass);
         st = conn.createStatement();
         rs = st.executeQuery("select * from [Vendor360 WIP]");

         while (rs.next())
         {
            String pmId = rs.getString(PM_ID);
            String inspectionId = rs.getString(INSPECTION_ID);
            String woNum = rs.getString(WORK_ORDER_NUM);
            Date dueDate = rs.getDate(INSP_LAST_DT);
            Date recdInDate = rs.getDate(DATE_ORDERED);
            Vendor360Order order = new Vendor360Order(this, woNum, dueDate);
            order.setPMId(pmId);
            order.setInspectionId(inspectionId);
            order.setDateOrdered(recdInDate);
            previous.add(order);
         }
      }
      catch (Exception e)
      {
         if (tryAgain)
         {
            createWIPTable();
            return getPreviousWIPOrders(false);
         }
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }

      return previous;
   }

   private void updatePreviousWIPOrders(Set<Vendor360Order> orders, Set<Vendor360Order> enteredOrders)
   {
      String url = getEnvironment().getProperty("com.spi.Database.outputURL");
      String user = getEnvironment().getProperty("com.spi.Database.outputUser");
      String pass = getEnvironment().getProperty("com.spi.Database.outputPassword");
      Connection conn = null;
      PreparedStatement ps = null;
      Statement st = null;

      try
      {
         conn = Database.getConnection(url, user, pass);
         st = conn.createStatement();
         st.execute("delete from [Vendor360 WIP]");

         ps = conn.prepareStatement("insert into [Vendor360 WIP] ([" + PM_ID + "], [" + INSPECTION_ID + "], [" + WORK_ORDER_NUM + "], [" + INSP_LAST_DT
               + "], [" + DATE_ORDERED + "]) values (?, ?, ?, ?, ?)");
         for (Vendor360Order order : orders)
         {
            if (!enteredOrders.contains(order))
            {
               try
               {
                  ps.setString(1, order.getPMId());
                  ps.setString(2, order.getInspectionId());
                  ps.setString(3, order.getCustWONo());
                  ps.setDate(4, new java.sql.Date(order.getDueServ().getTime()));
                  ps.setDate(5, new java.sql.Date(order.getRecdIn().getTime()));
                  ps.executeUpdate();
               }
               catch (Exception e)
               {
                  Debug.debugException("Unable to add a record to the database: [Tracking No: " + order.getTrackingNo() + ", Cust WO No: "
                        + order.getCustWONo() + "] because of a(n) " + e);
                  getStatistics().setErrorOccurred();
               }
            }
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Unable to clear the vendor 360 previous WIP table because of a(n) " + e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeStatement(ps);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }
   }

   private void createWIPTable()
   {
      String url = getEnvironment().getProperty("com.spi.Database.outputURL");
      String user = getEnvironment().getProperty("com.spi.Database.outputUser");
      String pass = getEnvironment().getProperty("com.spi.Database.outputPassword");
      Connection conn = null;
      Statement st = null;
      ResultSet rs = null;

      StringBuffer sql = new StringBuffer();
      sql.append("create table [Vendor360 WIP] (");
      sql.append(PM_ID + " varchar(50), ");
      sql.append(INSPECTION_ID + " varchar(50), ");
      sql.append(WORK_ORDER_NUM + " varchar(50), ");
      sql.append(INSP_LAST_DT + " date, ");
      sql.append(DATE_ORDERED + " date");
      sql.append(");");

      try
      {
         conn = Database.getConnection(url, user, pass);
         st = conn.createStatement();
         st.execute(sql.toString());
      }
      catch (Exception e)
      {
         Debug.debugException("Error creating the Vendor360 previous ", e);
         getStatistics().setErrorOccurred();
      }
      finally
      {
         Database.closeResultSet(rs);
         Database.closeStatement(st);
         Database.closeConnection(conn);
      }
   }
}