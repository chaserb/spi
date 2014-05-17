/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fidelity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spi.AbstractInputProvider;
import com.spi.Order;
import com.spi.util.Debug;
import com.spi.util.EqualityUtils;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class RepScapeAccess extends AbstractInputProvider
{
   private static List<OutputField> cFields;
   static final String DEBUG_PREFIX = "RepScapeAccess.";
   static final SimpleDateFormat FIS_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
   static final String PAGES_BASE = "https://repscape.lpsfs.com/";
   static final String MY_INSPECTIONS = PAGES_BASE + "User/Inspector/MyInspections/MyInspections.aspx?SiloId=6&VendorId=";
   static final String MESSAGE_CENTER = PAGES_BASE + "User/MessageCenter.aspx?SiloId=6";
   static final String ORDER_DETAILS = PAGES_BASE + "User/Inspector/Inspection/Select.aspx?SiloId=6&InspectionId=";

   private Web mWeb;

   public String getServiceCompany()
   {
      return "Fidelity";
   }

   public List<Order> getAllOrders()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): START");
      ArrayList<RepScapeOrder> orders = new ArrayList<RepScapeOrder>();

      try
      {
         List<RepScapeOrder> harvested = harvestOrders("1", true);
         if (harvested != null) orders.addAll(harvested);
      }
      catch (Exception e)
      {
         getStatistics().setErrorOccurred();
         Debug.debugException("Ooops!", e);
      }
      
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders available: " + orders.size());
      List<RepScapeOrder> filteredOrders = filterOrders(orders);
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders received in last year: " + filteredOrders.size());
      
      for (RepScapeOrder order : filteredOrders)
      {
         if (order.getRecdIn().compareTo(getEarliestDate()) >= 0)
         {
            try
            {
               Map<String, String> reqHeaderVars = new HashMap<String, String>();
               reqHeaderVars.put(WebClient.REFERER_HEADER, MY_INSPECTIONS);
               InputStream is = getWeb().getInput(order.getDetailsLink(), null, reqHeaderVars);
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
         Map<String, Map<String, String>> debug = RepScapeOrder.mDebugValues;
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
         cFields.add(OutputField.LOAN_NO);
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
         cFields.add(OutputField.FOR_SALE_PHONE);
         cFields.add(OutputField.COST);
         cFields.add(OutputField.WAGES);
         cFields.add(OutputField.COMMENTS);
      }
      return cFields;
   }

   protected List<RepScapeOrder> harvestOrders(String pageNo, boolean followLinks) throws Exception
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): START: followLinks: " + followLinks);

      InputStream inStream;
      Document doc;
      if (followLinks)
      {
         // Read the "My Inspections" page on the first pass through and
         // harvest its hidden input tags
         inStream = new BufferedInputStream(getWeb().getInput(MY_INSPECTIONS));
         doc = getWeb().parseInput(inStream);
      }

      // Now post the hidden input variables back to the server
      Map<String, String> headerVars = new HashMap<String, String>();
      headerVars.put("Referer", MY_INSPECTIONS);
      headerVars.put("Content-Type", "application/x-www-form-urlencoded");
      headerVars.put("Cache-Control", "no-cache");
      Map<String, String> postVars = getMyInspectionsVariables();
      postVars.put("__CALLBACKPARAM", "InspectionId|0|" + pageNo + "|0");
      inStream = new BufferedInputStream(getWeb().getInput(MY_INSPECTIONS, postVars, headerVars));
      inStream = Debug.dumpStream(inStream);
      inStream = isolateHtml(inStream);
      doc = getWeb().parseInput(inStream);
      Set<RepScapeOrder> orders = null;

      // Find the order table
      NodeList tables = doc.getElementsByTagName("table");
      Node table = null;
      NodeList cellChildren = null;
      for (int i = 0; i < tables.getLength(); i++)
      {
         table = tables.item(i);
      }

      // Read the orders from the table
      if (table != null)
      {
         orders = new HashSet<RepScapeOrder>();
         NodeList rows = table.getChildNodes();
         for (int i = 2; i < rows.getLength(); i++) // skip the first header
         // row and the second
         // select all row
         {
            RepScapeOrder order = harvestOrder(rows.item(i));
            if (order != null)
            {
               orders.add(order);
            } else
            {
               Node row = rows.item(i);
               Node cell = row.getFirstChild();
               String text = cell.getFirstChild().getNodeValue();
               if (text != null && text.startsWith("Go to Page:"))
               {
                  cellChildren = cell.getChildNodes();
               }
            }
         }
      }

      // Retrieve all the URLs for the continuation pages
      if (followLinks && cellChildren != null)
      {
         for (int i = 1; i < cellChildren.getLength(); i++)
         {
            try
            {
               Node child = cellChildren.item(i);
               try
               {
                  if (child.getNodeType() == Node.ELEMENT_NODE && EqualityUtils.equalsIgnoreCase("a", child.getNodeName()))
                  {
                     // Make sure we are going to an explicit page number
                     String pageNum = child.getFirstChild().getNodeValue();
                     Integer.parseInt(pageNum);
                     List<RepScapeOrder> nextOrders = harvestOrders(pageNum, false);
                     if (nextOrders != null)
                     {
                        orders.addAll(nextOrders);
                     }
                  }
               }
               catch (NumberFormatException e)
               {
                  if (Debug.checkLevel(Debug.MED))
                     Debug.debug(Debug.MED, "RepScapeAccess.harvestOrders(): This link is not a page number: " + e.getMessage());
               }
            }
            catch (Exception e)
            {
               Debug.debugException("RepScapeAccess.harvestOrders(): Unable to read one of the pages.  Skipping this page: ", e);
            }
         }
      }

      int numOrders = (orders == null) ? 0 : orders.size();
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): END: " + numOrders);

      return (orders == null) ? new ArrayList<RepScapeOrder>() : new ArrayList<RepScapeOrder>(orders);
   }

   protected RepScapeOrder harvestOrder(Node row)
   {
      RepScapeOrder order = null;

      NodeList cells = row.getChildNodes();

      // Get the service order ID
      Node seqCell = cells.item(1);
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
         Node dueCell = cells.item(3);
         if (dueCell == null) return order;
         Node dueText = dueCell.getFirstChild();

         // Create the order
         order = new RepScapeOrder(this, seq, FIS_DATE_FORMAT.parse(dueText.getNodeValue()));

         // Get the loan number
         try
         {
            Node loanNoCell = cells.item(2);
            Node loanNoText = loanNoCell.getFirstChild();
            order.setLoanNumber(loanNoText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the address
         try
         {
            Node addressCell = cells.item(4);
            Node addressText = addressCell.getFirstChild();
            order.setAddress(addressText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the city
         try
         {
            Node cityCell = cells.item(5);
            Node cityText = cityCell.getFirstChild();
            order.setCity(cityText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the state
         try
         {
            Node stateCell = cells.item(6);
            Node stateText = stateCell.getFirstChild();
            order.setState(stateText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the zip
         try
         {
            Node zipCell = cells.item(7);
            Node zipText = zipCell.getFirstChild();
            order.setZip(zipText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the type
         try
         {
            Node inspectionTypeCell = cells.item(8);
            Node inspectionTypeText = inspectionTypeCell.getFirstChild();
            order.setInspectionType(inspectionTypeText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         return order;
      }
      catch (Exception e)
      {
         Debug
               .debugException("WARNING: RepScapeAccess.harvestOrder(): Error reading the record for service order ID: " + seq + ".  Skipping this order.",
                     e);
      }
      return order;
   }

   protected void harvestOrderDetails(RepScapeOrder order, InputStream inStream)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): START");

      Document doc = getWeb().parseInput(inStream);

      // Get a handle on all the data tables on the page
      NodeList tableTags = doc.getElementsByTagName("table");
      List<Node> tableNodes = new ArrayList<Node>();
      try
      {
         for (int i = 0; i < tableTags.getLength(); i++)
         {
            NamedNodeMap attrs = tableTags.item(i).getAttributes();
            Node classAttr = attrs.getNamedItem("class");
            if (classAttr != null)
            {
               String attrVal = classAttr.getNodeValue();
               if (attrVal.equals("data"))
               {
                  tableNodes.add(tableTags.item(i));
               }
            }
         }
      }
      catch (Exception e)
      {
         Debug.debugException("Could not find data tables for #" + order.getTrackingNo());
      }

      cClientNameHandler.handle(tableNodes.get(0), order);
      cMortgagersNameHandler.handle(tableNodes.get(0), order);
      cTypeOfPropertyHandler.handle(tableNodes.get(2), order);
      cGarageHandler.handle(tableNodes.get(2), order);
      cTypeOfConstructionHandler.handle(tableNodes.get(2), order);
      cOccupancyIsHandler.handle(tableNodes.get(4), order);
      cPropertyIsForSaleHandler.handle(tableNodes.get(5), order);
      cBrokerNameHandler.handle(tableNodes.get(5), order);
      cBrokerPhoneHandler.handle(tableNodes.get(5), order);
      cPhoneHandler.handle(tableNodes.get(5), order);
      cBadAddressWhyHandler.handle(tableNodes.get(1), order);
      cBadAddressCommentsHandler.handle(tableNodes.get(1), order);
      cBadAddressCheckedWithHandler.handle(tableNodes.get(1), order);
      cAccessDeniedWhyHandler.handle(tableNodes.get(1), order);
      cAccessDeniedCommentsHandler.handle(tableNodes.get(1), order);
      cOutOfAreaWhyHandler.handle(tableNodes.get(1), order);

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): END");
   }

   protected Web getWeb()
   {
      if (mWeb == null)
      {
         mWeb = new Web(getEnvironment(), getName());
      }
      return mWeb;
   }

   protected InputStream isolateHtml(InputStream is) throws IOException
   {
      // Consume the input stream until we hit the delimiter (^~^)
      int val = 0;
      while (val >= 0)
      {
         if ((val = is.read()) == '^')
         {
            if ((val = is.read()) == '~')
            {
               if ((val = is.read()) == '^')
               {
                  break;
               }
            }
         }
      }
      return is;
   }

   protected Map<String, String> getMyInspectionsVariables()
   {
      Map<String, String> vars = new HashMap<String, String>();
      vars.put("__EVENTTARGET", "");
      vars.put("__EVENTARGUMENT", "");
      vars.put("__VIEWSTATE", "");
      vars.put("ctl00$content_main$PrintList", "");
      vars.put("ctl00$content_main$SortColumn", "InspectionId");
      vars.put("ctl00$content_main$SortDirection", "0");
      vars.put("ctl00$content_main$ClientRefNum", "");
      vars.put("ctl00$content_main$InspectionId", "");
      vars.put("ctl00$content_main$ReceiveDateRange$DateFrom$DateTextBox", "");
      vars.put("ctl00$content_main$ReceiveDateRange$DateTo$DateTextBox", "");
      vars.put("ctl00$content_main$DueBackDateRange$DateFrom$DateTextBox", "");
      vars.put("ctl00$content_main$DueBackDateRange$DateTo$DateTextBox", "");
      vars.put("ctl00$content_main$Address1", "");
      vars.put("ctl00$content_main$Address2", "");
      vars.put("ctl00$content_main$City", "");
      vars.put("ctl00$content_main$ZipCode", "");
      vars.put("ctl00$content_main$MortgagorFirstName", "");
      vars.put("ctl00$content_main$MortgagorLastName", "");
      vars.put("ctl00$content_main$ZipRange1", "");
      vars.put("ctl00$content_main$ZipRange2", "");
      vars.put("__CALLBACKID", "__Page");
      vars.put("__CALLBACKPARAM", "InspectionId|0|1|0");
      vars.put("__EVENTVALIDATION", "");
      return vars;
   }

   interface FieldHandler
   {
      public void handle(Node tableNode, RepScapeOrder order);
   }

   static abstract class StrongCellHandler implements FieldHandler
   {
      public void handle(Node tableNode, RepScapeOrder order)
      {
         try
         {
            Node row = tableNode.getChildNodes().item(getRowNumber());
            Node cell = row.getChildNodes().item(getCellNumber());
            Node strong = cell.getFirstChild();
            Node text = strong.getFirstChild();
            if (text != null)
            {
               setValueOnOrder(order, text.getNodeValue());
            }
         }
         catch (Exception e)
         {
            Debug.debugException("Could not determine " + getFieldName() + " for #" + order.getTrackingNo());
         }
      }

      abstract int getRowNumber();

      int getCellNumber()
      {
         return 1;
      }

      abstract String getFieldName();

      abstract void setValueOnOrder(RepScapeOrder order, String value);
   }

   static abstract class DoubleSpanCellHandler extends StrongCellHandler
   {
      public void handle(Node tableNode, RepScapeOrder order)
      {
         try
         {
            Node row = tableNode.getChildNodes().item(getRowNumber());
            Node cell = row.getChildNodes().item(getCellNumber());
            Node span1 = cell.getFirstChild();
            Node span2 = span1.getFirstChild();
            Node text = span2.getFirstChild();
            if (text != null)
            {
               setValueOnOrder(order, text.getNodeValue());
            }
         }
         catch (Exception e)
         {
            Debug.debugException("Could not determine " + getFieldName() + " for #" + order.getTrackingNo());
         }
      }
   }

   static abstract class DoubleSpanSecondColumnCellHandler extends DoubleSpanCellHandler
   {
      int getCellNumber()
      {
         return 3;
      }
   }

   static StrongCellHandler cClientNameHandler = new StrongCellHandler()
   {
      int getRowNumber()
      {
         return 0;
      }

      String getFieldName()
      {
         return "Client Name";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setClientName(value);
      }
   };

   static StrongCellHandler cMortgagersNameHandler = new StrongCellHandler()
   {
      int getRowNumber()
      {
         return 1;
      }

      int getCellNumber()
      {
         return 3;
      }

      String getFieldName()
      {
         return "Mortgagor's Name";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setMortgagorsName(value);
      }
   };

   static DoubleSpanCellHandler cTypeOfPropertyHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 1;
      }

      String getFieldName()
      {
         return "Type of Property";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setTypeOfProperty(value);
      }
   };

   static DoubleSpanCellHandler cGarageHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 3;
      }

      String getFieldName()
      {
         return "Garage";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setGarage(value);
      }
   };

   static DoubleSpanCellHandler cTypeOfConstructionHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 4;
      }

      String getFieldName()
      {
         return "Type of Construction";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setTypeOfConstruction(value);
      }
   };

   static DoubleSpanCellHandler cOccupancyIsHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 1;
      }

      String getFieldName()
      {
         return "Occupancy Is";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setOccupancyIs(value);
      }
   };

   static DoubleSpanCellHandler cPropertyIsForSaleHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 9;
      }

      String getFieldName()
      {
         return "Property Is (For Sale)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setPropertyIsForSale(value);
      }
   };

   static DoubleSpanCellHandler cBrokerNameHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 10;
      }

      String getFieldName()
      {
         return "Broker Name";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setBrokerName(value);
      }
   };

   static DoubleSpanCellHandler cBrokerPhoneHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 11;
      }

      String getFieldName()
      {
         return "Broker Phone";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setBrokerPhone(value);
      }
   };

   static DoubleSpanCellHandler cPhoneHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 4;
      }

      String getFieldName()
      {
         return "Phone";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setPhone(value);
      }
   };

   static DoubleSpanCellHandler cBadAddressWhyHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 3;
      }

      String getFieldName()
      {
         return "Bad Address (Why?)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setBadAddressWhy(value);
      }
   };

   static DoubleSpanCellHandler cBadAddressCommentsHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 4;
      }

      String getFieldName()
      {
         return "Bad Address (Comments)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setBadAddressComments(value);
      }
   };

   static DoubleSpanCellHandler cBadAddressCheckedWithHandler = new DoubleSpanCellHandler()
   {
      int getRowNumber()
      {
         return 5;
      }

      String getFieldName()
      {
         return "Bad Address (Checked With)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setBadAddressCheckedWith(value);
      }
   };

   static DoubleSpanSecondColumnCellHandler cAccessDeniedWhyHandler = new DoubleSpanSecondColumnCellHandler()
   {
      int getRowNumber()
      {
         return 3;
      }

      String getFieldName()
      {
         return "Access Denied (Why?)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setAccessDeniedWhy(value);
      }
   };

   static DoubleSpanSecondColumnCellHandler cAccessDeniedCommentsHandler = new DoubleSpanSecondColumnCellHandler()
   {
      int getRowNumber()
      {
         return 4;
      }

      String getFieldName()
      {
         return "Access Denied (Comments)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setAccessDeniedComments(value);
      }
   };

   static DoubleSpanSecondColumnCellHandler cOutOfAreaWhyHandler = new DoubleSpanSecondColumnCellHandler()
   {
      int getRowNumber()
      {
         return 6;
      }

      int getCellNumber()
      {
         return 2;
      }

      String getFieldName()
      {
         return "Out Of Area (Why?)";
      }

      void setValueOnOrder(RepScapeOrder order, String value)
      {
         order.setOutOfAreaWhy(value);
      }
   };
}