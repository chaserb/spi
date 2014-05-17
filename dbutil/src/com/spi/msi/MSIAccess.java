/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.msi;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spi.AbstractInputProvider;
import com.spi.Order;
import com.spi.util.Debug;
import com.spi.util.EqualityUtils;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class MSIAccess extends AbstractInputProvider
{
   private static List<OutputField> cFields;
   private static Map<String, FieldHandler> cFieldHandlers;
   static final String DEBUG_PREFIX = "MSIWebAccess.";
   static final SimpleDateFormat MSI_DATE_FORMAT = new SimpleDateFormat("M/d/y h:m:s a");
   static final SimpleDateFormat MSI_SHORT_DATE_FORMAT = new SimpleDateFormat("M/d/y");
   static final String PAGES_BASE = "https://enterprise.msionline.com";
   static final String OUTSTANDING_INSPECTIONS = PAGES_BASE + "/default.aspx?ti=0&tab=150";
   static final String COMPLETED_INSPECTIONS = PAGES_BASE + "/default.aspx?ti=0&tab=157";
   static final String ORDER_DETAILS = PAGES_BASE + "/default.aspx?ti=-1&tab=163&woid=";

   private Web mWeb;

   public String getServiceCompany()
   {
      return "MSI";
   }

   public List<Order> getAllOrders()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): START");
      List<MSIOrder> orders = new ArrayList<MSIOrder>();

      try
      {
         orders.addAll(harvestOrders(true, OUTSTANDING_INSPECTIONS, false));
         orders.addAll(harvestOrders(false, COMPLETED_INSPECTIONS, true));
      }
      catch (Exception e)
      {
         getStatistics().setErrorOccurred();
         Debug.debugException("Ooops!", e);
      }
      
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders available: " + orders.size());
      List<MSIOrder> filteredOrders = filterOrders(orders);
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders received in last year: " + filteredOrders.size());

      for (MSIOrder order : filteredOrders)
      {
         if (order.getRecdIn().compareTo(getEarliestDate()) >= 0)
         {
            try
            {
               InputStream is = getWeb().getInput(order.getDetailsLink());
               harvestOrderDetails(order, is);
            }
            catch (Exception e)
            {
               Debug.debugException("Unable to retrieve details for order: " + order.getTrackingNo(), e);
            }
         }
      }

      if (Debug.checkLevel(Debug.MED))
      {
         Map<String, Map<String, String>> debug = MSIOrder.mDebugValues;
         for (String field : debug.keySet())
         {
            Debug.debug(Debug.MED, "Found these values for \"" + field + "\"");
            for (String value : debug.get(field).keySet())
            {
               Debug.debug(Debug.MED, "    " + value + "\t(eg: order #" + debug.get(field).get(value) + ")");
            }
         }
      }

      getStatistics().setNumIOsTotal(filteredOrders.size());
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): END: " + filteredOrders.size());
      return new ArrayList<Order>(filteredOrders);
   }

   public List<OutputField> getSupportedFields()
   {
      if (cFields == null)
      {
         cFields = new ArrayList<OutputField>(20);
         cFields.add(OutputField.REP);
         cFields.add(OutputField.REPORT_TYPE);
         cFields.add(OutputField.RECD_IN);
         cFields.add(OutputField.DUE_SERV);
         cFields.add(OutputField.MORTGAGER);
         cFields.add(OutputField.MTG_COMP);
         cFields.add(OutputField.PROP_NO);
         cFields.add(OutputField.IS_RURAL);
         cFields.add(OutputField.PROPERTY_ADDRESS);
         cFields.add(OutputField.CITY);
         cFields.add(OutputField.ST);
         cFields.add(OutputField.ZIP_CODE);
         cFields.add(OutputField.OCCUPANCY);
         cFields.add(OutputField.PERSONAL_CONTACT);
         cFields.add(OutputField.PROPERTY_DESCRIPTION);
         cFields.add(OutputField.FOR_SALE);
         // cFields.add(OutputField.FOR_SALE_PHONE);
         cFields.add(OutputField.COST);
         cFields.add(OutputField.WAGES);
         // cFields.add(OutputField.COMMENTS);
         cFields.add(OutputField.PHOTOS_Y_N);
         cFields.add(OutputField.REP_COMP_DATE);
         cFields.add(OutputField.VALUE);
      }
      return cFields;
   }
   
   public Map<String, FieldHandler> getFieldHandlers()
   {
      if (cFieldHandlers == null)
      {
         cFieldHandlers = new TreeMap<String, FieldHandler>();
         cFieldHandlers.put("ctl00_cp_ctl00_Assigned", new SpanHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               try
               {
                  if (!StringUtil.isEmpty(value))
                  {
                     order.setAssigned(MSI_SHORT_DATE_FORMAT.parse(value));
                  }
               }
               catch (Exception e)
               {
                  Debug.debugException("Could not parse the assigned date for order: " + order.getCustWONo(), e);
               }
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl00_Instructions", new SpanHandler() 
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setInstructions(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl00_txtMortgagor", new SpanHandler() 
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setMortgagor(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl00_txtCompleteDate", new SpanHandler() 
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               try
               {
                  if (!StringUtil.isEmpty(value))
                  {
                     order.setInspected(MSI_SHORT_DATE_FORMAT.parse(value));
                  }
               }
               catch (Exception e)
               {
                  Debug.debugException("Could not parse the inspeciton date for order: " + order.getCustWONo(), e);
               }
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlOccupancyTypeId", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setOccupancyStatus(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlEMV", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setEMV(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlOccupancyVerifiedByTypeId", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setOccupancyVerifiedBy(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlPropertyForSaleByType", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setForSaleBy(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlPropertyType", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setBuildingType(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlConstructionType", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setConstructionType(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlStories", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setStories(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlPrimaryColor", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setColorOfDwelling(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlRoofType", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setRoofType(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlPropertyCondition", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setPropertyCondition(value);
            }
         });
         cFieldHandlers.put("ctl00_cp_ctl01_ddlNbrHdCondition", new SelectHandler()
         {
            @Override
            protected void applyValue(String value, MSIOrder order)
            {
               order.setNeighborhoodCondition(value);
            }
         });
      }
      return cFieldHandlers;
   }

   protected List<MSIOrder> harvestOrders(boolean followLinks, String url, boolean completed) throws Exception
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): START: followLinks: " + followLinks);

      InputStream inStream;
      Document doc;

      inStream = new BufferedInputStream(getWeb().getInput(url));
      if (Debug.checkLevel(Debug.HIGH)) inStream = Debug.dumpStream(inStream);
      // inStream = isolateHtml(inStream);
      doc = getWeb().parseInput(inStream);
      Set<MSIOrder> orders = null;

      // Find the order table
      NodeList tables = doc.getElementsByTagName("table");
      Node table = null;
      for (int i = 0; i < tables.getLength(); i++)
      {
         table = tables.item(i);
         Node id = table.getAttributes().getNamedItem("id");
         if (id != null && "ctl00_cp_ctl00_gvWorkOrders".equals(id.getNodeValue()))
         {
            break;
         }
      }

      // Read the orders from the table
      NodeList pageCells = null;
      if (table != null)
      {
         orders = new HashSet<MSIOrder>();
         NodeList rows = table.getChildNodes();
         for (int i = 1; i < rows.getLength(); i++) // skip the first header row
         {
            MSIOrder order = harvestOrder(rows.item(i), completed);
            if (order != null)
            {
               orders.add(order);
            } 
            else
            {
               Node row = rows.item(i);
               Node cell = row.getFirstChild();
               Node pageTable = cell.getFirstChild();
               if (pageTable != null)
               {
                  Node pageRow = pageTable.getFirstChild();
                  if (pageRow != null)
                  {
                     pageCells = pageRow.getChildNodes();
                  }
               }
            }
         }
      }

      // Retrieve all the URLs for the continuation pages
      if (followLinks && pageCells != null)
      {
         for (int i = 1; i < pageCells.getLength(); i++)
         {
            try
            {
               Node pageCell = pageCells.item(i);
               Node pageCellChild = pageCell.getFirstChild();
               try
               {
                  if (pageCellChild.getNodeType() == Node.ELEMENT_NODE && EqualityUtils.equalsIgnoreCase("a", pageCellChild.getNodeName()))
                  {
                     // Make sure we are going to an explicit page number
                     String urlParams = "&__EVENTTARGET=ctl00$cp$ctl00$gvWorkOrders&__EVENTARGUMENT=Page$" + (i + 1);
                     List<MSIOrder> nextOrders = harvestOrders(false, url + urlParams, completed);
                     if (nextOrders != null)
                     {
                        orders.addAll(nextOrders);
                     }
                  }
               }
               catch (NumberFormatException e)
               {
                  if (Debug.checkLevel(Debug.MED))
                     Debug.debug(Debug.MED, "MSIAccess.harvestOrders(): This link is not a page number: " + e.getMessage());
               }
            }
            catch (Exception e)
            {
               Debug.debugException("MSIAccess.harvestOrders(): Unable to read one of the pages.  Skipping this page: ", e);
            }
         }
      }

      int numOrders = (orders == null) ? 0 : orders.size();
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): END: " + numOrders);

      return (orders == null) ? new ArrayList<MSIOrder>() : new ArrayList<MSIOrder>(orders);
   }

   protected MSIOrder harvestOrder(Node row, boolean completed)
   {
      MSIOrder order = null;

      NodeList cells = row.getChildNodes();

      // Get the service order ID
      Node seqCell = cells.item(0);
      if (seqCell == null) return order;
      Node seqAnchor = seqCell.getFirstChild();
      if (seqAnchor == null) return order;
      Node seqText = seqAnchor.getFirstChild();
      if (seqText == null) return order;
      String seq = seqText.getNodeValue();

      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, "harvestOrder(): " + seq);

      try
      {
         // Get the due date
         Node assignedCell = cells.item(completed ? 9 : 13);
         if (assignedCell == null) return order;
         Node assignedText = assignedCell.getFirstChild();
         Date assignedDate = completed ? MSI_DATE_FORMAT.parse(assignedText.getNodeValue()) : MSI_SHORT_DATE_FORMAT.parse(assignedText.getNodeValue());

         // Create the order
         order = new MSIOrder(this, seq, assignedDate);

         // Get the address
         try
         {
            Node addressCell = cells.item(completed ? 1 : 2);
            Node addressText = addressCell.getFirstChild();
            order.setAddress(addressText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the city
         try
         {
            Node cityCell = cells.item(completed ? 2 : 5);
            Node cityText = cityCell.getFirstChild();
            order.setCity(cityText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the state
         try
         {
            Node stateCell = cells.item(completed ? 4 : 7);
            Node stateText = stateCell.getFirstChild();
            order.setState(stateText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the zip
         try
         {
            Node zipCell = cells.item(completed ? 5 : 8);
            Node zipText = zipCell.getFirstChild();
            order.setZip(zipText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the Mortgage Company
         try
         {
            Node clientCell = cells.item(completed ? 7 : 10);
            Node clientText = clientCell.getFirstChild();
            order.setClientName(clientText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         return order;
      }
      catch (Exception e)
      {
         Debug.debugException("WARNING: MSIAccess.harvestOrder(): Error reading the record for service order ID: " + seq + ".  Skipping this order.",
                     e);
      }
      return order;
   }

   protected void harvestOrderDetails(MSIOrder order, InputStream inStream)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): START");

      Document doc = getWeb().parseInput(inStream);

      handleNodeList(doc.getElementsByTagName("span"), order);
      handleNodeList(doc.getElementsByTagName("select"), order);
      
      Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): END");
   }

   protected Web getWeb()
   {
      if (mWeb == null)
      {
         mWeb = new Web(getEnvironment(), getName());
      }
      return mWeb;
   }
   
   protected void handleNodeList(NodeList nodes, MSIOrder order)
   {
      try
      {
         for (int i=0; i < nodes.getLength(); i++)
         {
            Node node = nodes.item(i);
            Node idAttr = node.getAttributes().getNamedItem("id");
            if (idAttr != null)
            {
               String id = idAttr.getNodeValue();
               FieldHandler handler = getFieldHandlers().get(id);
               if (handler != null)
               {
                  handler.handle(node, order);
               }
            }
         }
      }
      catch (Exception e)
      {
         Debug.debugException(DEBUG_PREFIX + "harvestOrderDetails(): Could not read for #" + order.getTrackingNo());
      }
   }

   interface FieldHandler
   {
      public void handle(Node node, MSIOrder order);
   }
   
   abstract static class SpanHandler implements FieldHandler
   {
      public void handle(Node node, MSIOrder order)
      {
         if (node != null)
         {
            Node textNode = node.getFirstChild();
            if (textNode != null)
            {
               applyValue(textNode.getNodeValue(), order);
            }
         }
      }
      
      protected abstract void applyValue(String value, MSIOrder order);
   }
   
   abstract static class SelectHandler implements FieldHandler
   {
      public void handle(Node node, MSIOrder order)
      {
         if (node != null)
         {
            NodeList options = node.getChildNodes();
            for (int i = 0; i < options.getLength(); i++)
            {
               Node option = options.item(i);
               if (option.getAttributes().getNamedItem("selected") != null)
               {
                  Node selectedOption = option.getFirstChild();
                  if (selectedOption != null)
                  {
                     String selectedText = selectedOption.getNodeValue();
                     if (!StringUtil.isEmpty(selectedText))
                     {
                        int firstSpace = selectedText.indexOf(' ');
                        if (firstSpace > 0)
                        {
                           applyValue(selectedText.substring(firstSpace).trim(), order);
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
      
      protected abstract void applyValue(String value, MSIOrder order);
   }
}