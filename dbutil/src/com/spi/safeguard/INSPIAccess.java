/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.safeguard;

import java.io.BufferedInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spi.AbstractInputProvider;
import com.spi.Order;
import com.spi.util.Debug;

/**
 * @author Chase Barrett
 */
public class INSPIAccess extends AbstractInputProvider
{
   private static final String DEBUG_PREFIX = "INSPIAccess.";
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy");
   private static List<OutputField> cFields;

   public String getServiceCompany()
   {
      return "Safeguard";
   }

   public List<Order> getAllOrders()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): START");

      // Set up the list of orders
      List<Order> pos = new ArrayList<Order>();

      try
      {
         // Set up the input stream
         Web web = new Web(getEnvironment(), getName());
         BufferedInputStream buf = new BufferedInputStream(Debug.dumpStream(web.getInput(web.getInputURL())));

         // Set up the document
         Document doc = web.parseInput(buf);

         // Find the tables that represent each page
         NodeList tables = doc.getElementsByTagName("table");
         for (int i = 0; i < tables.getLength(); i++)
         {
            pos.addAll(processTable(tables.item(i)));
         }
      }
      catch (Exception e)
      {
         getStatistics().setErrorOccurred();
         Debug.debugException("Ooops!", e);
      }
      
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders available: " + pos.size());
      List<Order> filteredOrders = filterOrders(pos);
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders received in last year: " + filteredOrders.size());

      getStatistics().setNumIOsTotal(filteredOrders.size());

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): END: " + filteredOrders.size());
      return filteredOrders;
   }

   public List<OutputField> getSupportedFields()
   {
      if (cFields == null)
      {
         cFields = new ArrayList<OutputField>(20);
         cFields.add(OutputField.LOAN_NO);
         cFields.add(OutputField.REP);
         cFields.add(OutputField.SG_INSPECTION_CODE);
         cFields.add(OutputField.SG_INSPECTION_TITLE);
         cFields.add(OutputField.SG_INSTRUCTIONS);
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
         cFields.add(OutputField.PHOTOS_Y_N);
         cFields.add(OutputField.COST);
         cFields.add(OutputField.WAGES);
      }
      return cFields;
   }

   protected List<INSPIOrder> processTable(Node table)
   {
      // How to interpret the rows in the table. Each table comprises one
      // page, with four or five orders on the page.
      //
      // Rows 1 - 10: Header for the page, including the date the report
      // is published, and the date range for the report.
      //
      // Next 1 or 2 Rows: Spacer rows, sometimes with assignment dates
      //
      // Next 14 Rows: The order itself. THE KEY TO ALL THIS IS THAT THE
      // FIRST ROW IN THE ORDER HAS **17** CELL CHILDREN.
      //
      // Next 2 to 4 Rows: Spacer rows, sometimes with assignment dates

      List<INSPIOrder> orders = new ArrayList<INSPIOrder>();

      NodeList rows = table.getChildNodes();
      Debug.debug(Debug.HIGH, "Number of rows in an INSPI table: " + rows.getLength());

      for (int i = 0; i < rows.getLength(); i++)
      {
         Node row = rows.item(i);
         NodeList cells = row.getChildNodes();
         Debug.debug(Debug.HIGH, "Number of cells in an INSPI table row: " + cells.getLength());
         if (cells.getLength() == 17)
         {
            // We have the beginning of a 14 row block that describes an
            // order.

            // Order date and order number are in the second row, or the
            // third row, depending on where the spacer lands :)
            row = rows.item(i + 2);
            if (row.getChildNodes().getLength() != 11)
            {
               row = rows.item(i + 3);
               if (row.getChildNodes().getLength() != 11)
               {
                  continue;
               }
            }
            INSPIOrder order = createOrder(row);

            // Get the client and loan number
            row = rows.item(i);
            getClientAndWorkCode(row, order);

            // Get the mortgager's name
            row = rows.item(i + 4);
            getName(row, order);

            // Get the address, city, state, and zip
            row = rows.item(i + 7);
            getAddress(row, order);

            // Get the due date
            row = rows.item(i + 9);
            getDueDate(row, order);

            // Get photos reqd
            row = rows.item(i + 10);
            try
            {
               getPhotoYN(row, 3, order);
            }
            catch (NullPointerException e)
            {
               row = rows.item(i + 11);
               getPhotoYN(row, 5, order);
            }

            // Get instructions
            row = rows.item(i + 13);
            getInstructions(row, order);

            orders.add(order);

            i += 13;
         }
      }

      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "processTable(): " + rows.getLength() + " rows in the table.");

      return orders;
   }

   protected INSPIOrder createOrder(Node row)
   {
      NodeList cells = row.getChildNodes();
      String orderDateStr = cells.item(5).getFirstChild().getFirstChild().getNodeValue();
      Date orderDate = new Date();
      try
      {
         orderDate = DATE_FORMAT.parse(orderDateStr);
      }
      catch (ParseException e)
      {
         Debug.debugException("Unexpected problem reading the order date:", e);
      }
      String orderNumStr = cells.item(9).getFirstChild().getFirstChild().getNodeValue();
      return new INSPIOrder(this, orderNumStr, orderDate);
   }

   protected void getClientAndWorkCode(Node row, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String client = cells.item(3).getFirstChild().getFirstChild().getNodeValue();
      order.setClient(client);
      String workCode = cells.item(11).getFirstChild().getFirstChild().getNodeValue();
      order.setInspectionCode(workCode);
   }

   protected void getName(Node row, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String name = cells.item(1).getFirstChild().getFirstChild().getNodeValue();
      order.setName(name);
   }

   protected void getAddress(Node row, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String address1 = cells.item(1).getFirstChild().getFirstChild().getNodeValue();
      order.setAddress1(address1);
      String address2 = cells.item(3).getFirstChild().getFirstChild().getNodeValue();
      order.setAddress2(address2);
      String city = cells.item(5).getFirstChild().getFirstChild().getNodeValue();
      order.setCity(city);
      String state = cells.item(7).getFirstChild().getFirstChild().getNodeValue();
      order.setState(state);
      String zip = cells.item(9).getFirstChild().getFirstChild().getNodeValue();
      order.setZip(zip);
   }

   protected void getDueDate(Node row, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String due = cells.item(3).getFirstChild().getFirstChild().getNodeValue();
      try
      {
         Date dueDate = DATE_FORMAT.parse(due);
         order.setInspectionDueDate(dueDate);
      }
      catch (ParseException e)
      {
         Debug.debugException("Unable to understand the due date: " + due, e);
      }
      String start2535 = cells.item(7).getFirstChild().getFirstChild().getNodeValue();
      order.set2535(!start2535.equals("N/A"));
   }

   protected void getPhotoYN(Node row, int cellIndex, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String photoReqd = cells.item(cellIndex).getFirstChild().getFirstChild().getNodeValue();
      order.setPhotosReqd("Y".equalsIgnoreCase(photoReqd));
   }

   protected void getInstructions(Node row, INSPIOrder order)
   {
      NodeList cells = row.getChildNodes();
      String instructions = cells.item(1).getFirstChild().getFirstChild().getNodeValue();
      order.setInstructions(instructions);
   }
}
