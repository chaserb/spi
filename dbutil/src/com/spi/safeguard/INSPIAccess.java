/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.safeguard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import com.spi.AbstractInputProvider;
import com.spi.Order;
import com.spi.util.Debug;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class INSPIAccess extends AbstractInputProvider
{
   private static final String DEBUG_PREFIX = "INSPIAccess.";
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
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

      BufferedReader reader = null;
      try
      {
         try
         {
            // Set up the input stream
            Web web = new Web(getEnvironment(), getName());
            String url = web.getInputURL();
            reader = new BufferedReader(new InputStreamReader(Debug.dumpStream(web.getInput(url))));

            // Read the web page
            StringBuffer webPageBuf = new StringBuffer();
            int bufSize = 512;
            char[] charArry = new char[bufSize];
            for (int numRead = bufSize; numRead == bufSize; numRead = reader.read(charArry))
            {
               webPageBuf.append(charArry, 0, numRead);
            }

            // Find the JSON data table
            int index = webPageBuf.indexOf("var grdOrders_data = ");
            if (index > 0)
            {
               webPageBuf.delete(0, index + 21);
            }
            else
            {
               throw new ParseException("Could not find the beginning of the order table", 0);
            }
            index = webPageBuf.indexOf("try {");
            if (index > 0)
            {
               webPageBuf.delete(index, webPageBuf.length());
               webPageBuf.trimToSize();
            }
            else
            {
               throw new ParseException("Could not find the end of the order table", 0);
            }

            // Parse the data table
            pos.addAll(processTable(new JSONArray(webPageBuf.toString())));
        }
         finally
         {
            if (reader != null) { reader.close(); }
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

   protected List<INSPIOrder> processTable(JSONArray table)
   {
      List<INSPIOrder> orders = new ArrayList<INSPIOrder>();

      Debug.debug(Debug.HIGH, "Number of rows in an INSPI table: " + table.length());

      for (int i = 0; i < table.length(); i++)
      {
         JSONArray row = table.getJSONArray(i);
         INSPIOrder order = new INSPIOrder(this, row.getString(0), new Date());
         order.setInspectionCode(row.getString(6));
         order.setName(row.getString(2));
         order.setAddress1(row.getString(3));
         order.setCity(row.getString(4));
         order.setState("CO");
         order.setZip(row.getString(5));
         try
         {
            order.setInspectionDueDate(DATE_FORMAT.parse(row.getString(8)));
         }
         catch (ParseException e)
         {
            Debug.debugException("Unable to understand the due date: " + row.getString(8), e);
         }
         order.set2535(!StringUtil.isEmpty(row.getString(7)) && row.getString(7).contains("2535"));
         order.setInstructions(row.getString(14));
         orders.add(order);
      }

      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "processTable(): " + table.length() + " rows in the table.");

      return orders;
   }
}
