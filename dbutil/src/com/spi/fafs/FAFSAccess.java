/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fafs;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
public class FAFSAccess extends AbstractInputProvider
{
   private static List<OutputField> cFields;
   private static final String DEBUG_PREFIX = "FAFSAccess.";
   private static final SimpleDateFormat FA_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
   private static final String PAGES_BASE = "https://vendor.fafs.com/pages/";

   private static Map<String, InputFieldHandler> cInputHandlers;
   private static InputFieldHandler cBitBucketHandler = new InputFieldHandler()
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
      }
   };

   private List<String> mInputUrls;
   private Web mWeb;
   private List<String> mIncludedZips;
   private List<String> mExcludedZips;

   public FAFSAccess()
   {
      mInputUrls = new ArrayList<String>();
      mInputUrls.add(PAGES_BASE + "WorkOrderSelection.aspx?type=2&os=4&si=1"); // NEW
      mInputUrls.add(PAGES_BASE + "WorkOrderSelection.aspx?type=2&os=5&si=2"); // ACTIVE
      mInputUrls.add(PAGES_BASE + "WorkOrderSelection.aspx?type=2&os=30&si=3"); // PAST
      // DUE
      mInputUrls.add(PAGES_BASE + "WorkOrderSelection.aspx?type=2&os=9&si=7"); // CANCELLED
      mInputUrls.add(PAGES_BASE + "WorkOrderSelection.aspx?type=2&os=13&si=8"); // COMPLETED
   }

   public String getServiceCompany()
   {
      return "First American";
   }

   public List<Order> getAllOrders()
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): START");
      List<FAFSOrder> orders = new ArrayList<FAFSOrder>();

      for (String url : mInputUrls)
      {
         try
         {
            List<FAFSOrder> harvested = harvestOrders(getWeb().getInput(url), true);
            if (harvested != null) orders.addAll(harvested);
         }
         catch (Exception e)
         {
            getStatistics().setErrorOccurred();
            Debug.debugException("Ooops!", e);
         }
      }
      
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders available: " + orders.size());
      List<FAFSOrder> filteredOrders = filterOrders(orders);
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getAllOrders(): Total number of orders received in last year: " + filteredOrders.size());

      for (FAFSOrder order : filteredOrders)
      {
         try
         {
            harvestOrderDetails(order, getWeb().getInput(order.getDetailsLink()));
         }
         catch (IOException e)
         {
            Debug.debugException("Unable to retrieve details for order: " + order.getTrackingNo(), e);
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
      }
      return cFields;
   }

   public void setEnvironment(Properties props)
   {
      super.setEnvironment(props);

      String includedZips = getEnvironment().getProperty("com.spi." + getName() + ".includeZipCodes");
      if (includedZips != null)
      {
         mIncludedZips = new ArrayList<String>();
         StringTokenizer tok = new StringTokenizer(includedZips, " ,;\t\n");
         while (tok.hasMoreTokens())
            mIncludedZips.add(tok.nextToken().trim());
      }

      String excludedZips = getEnvironment().getProperty("com.spi." + getName() + ".excludeZipCodes");
      if (excludedZips != null)
      {
         mExcludedZips = new ArrayList<String>();
         StringTokenizer tok = new StringTokenizer(excludedZips, " ,;\t\n");
         while (tok.hasMoreTokens())
            mExcludedZips.add(tok.nextToken().trim());
      }
   }

   protected List<FAFSOrder> harvestOrders(InputStream inStream, boolean followLinks) throws Exception
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): START: followLinks: " + followLinks);

      Document doc = getWeb().parseInput(inStream);
      Set<FAFSOrder> orders = null;

      // Find the order table
      NodeList divs = doc.getElementsByTagName("div");
      Node table = null;
      Node navLinks = null;
      for (int i = 0; i < divs.getLength(); i++)
      {
         Node div = divs.item(i);
         String id = div.getAttributes().getNamedItem("id").getNodeValue();
         if (EqualityUtils.equals(id, "Grid"))
         {
            Node text = div.getFirstChild();
            table = text.getNextSibling();
         }
         if (EqualityUtils.equals(id, "Navigation"))
         {
            navLinks = div;
         }
      }

      // Read the orders from the table
      if (table != null)
      {
         orders = new HashSet<FAFSOrder>();
         NodeList rows = table.getChildNodes();
         for (int i = 1; i < rows.getLength(); i++) // skip the first row
         {
            FAFSOrder order = harvestOrder(rows.item(i));
            if (order != null)
            {
               orders.add(order);
            }
         }
      }

      // Retrieve all the URLs for the continuation pages
      if (followLinks && navLinks != null)
      {
         NodeList links = navLinks.getChildNodes();
         for (int i = 1; i < links.getLength(); i++)
         {
            try
            {
               Node anchor = links.item(i);
               String anchorText = anchor.getFirstChild().getNodeValue();
               try
               {
                  if (anchor.getNodeType() == Node.ELEMENT_NODE && EqualityUtils.equalsIgnoreCase("a", anchor.getNodeName()))
                  {
                     // Make sure we are going to an explicit page number
                     Integer.parseInt(anchorText);
                     Node hrefAttr = anchor.getAttributes().getNamedItem("href");
                     if (hrefAttr != null)
                     {
                        String nextPage = hrefAttr.getNodeValue();
                        if (nextPage != null)
                        {
                           inStream = getWeb().getInput(PAGES_BASE + nextPage);
                           List<FAFSOrder> nextOrders = harvestOrders(inStream, false);
                           if (nextOrders != null)
                           {
                              orders.addAll(nextOrders);
                           }
                        }
                     }
                  }
               }
               catch (NumberFormatException e)
               {
                  if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, "FAFSAccess.harvestOrders(): This link is not a page number: " + anchorText);
               }
            }
            catch (Exception e)
            {
               Debug.debugException("FAFSAccess.harvestOrders(): Unable to read one of the pages.  Skipping this page: ", e);
            }
         }
      }

      int numOrders = (orders == null) ? 0 : orders.size();
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrders(): END: " + numOrders);

      return (orders == null) ? new ArrayList<FAFSOrder>() : new ArrayList<FAFSOrder>(orders);
   }

   protected FAFSOrder harvestOrder(Node row)
   {
      NodeList cells = row.getChildNodes();

      // Get the service order ID
      Node idCell = cells.item(4);
      Node detailsAnchor = idCell.getFirstChild();
      Node idText = detailsAnchor.getFirstChild();
      String id = idText.getNodeValue();

      try
      {
         // Get the issue date
         Node issueCell = cells.item(9);
         Node issueText = issueCell.getFirstChild();

         // Create the order
         FAFSOrder order = new FAFSOrder(this, id, FA_DATE_FORMAT.parse(issueText.getNodeValue()));

         // Get the zip
         try
         {
            Node zipCell = cells.item(8);
            Node zipAnchor = zipCell.getFirstChild();
            Node zipText = zipAnchor.getFirstChild();
            String zip = zipText.getNodeValue();
            if (includeZip(zip))
            {
               order.setZip(zip);
            } else
            {
               if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, "FAFSAccess.harvestOrder(): Ignoring zip code: " + zip);
               return null;
            }
         }
         catch (NullPointerException e)
         {
         }

         // Get the due date
         try
         {
            Node windowEndCell = cells.item(25);
            Node windowEndText = windowEndCell.getFirstChild();
            order.setWindowEnd(FA_DATE_FORMAT.parse(windowEndText.getNodeValue()));
         }
         catch (NullPointerException e)
         {
         }

         // Get the address
         try
         {
            Node addressCell = cells.item(5);
            Node addressAnchor = addressCell.getFirstChild();
            Node addressText = addressAnchor.getFirstChild();
            order.setAddress(addressText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get additional address
         try
         {
            Node additionalAddressCell = cells.item(23);
            Node additionalAddressText = additionalAddressCell.getFirstChild();
            if (additionalAddressText != null)
            {
               order.additionalAddress(additionalAddressText.getNodeValue());
            }
         }
         catch (NullPointerException e)
         {
         }

         // Get the city
         try
         {
            Node cityCell = cells.item(6);
            Node cityAnchor = cityCell.getFirstChild();
            Node cityText = cityAnchor.getFirstChild();
            order.setCity(cityText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the state
         try
         {
            Node stateCell = cells.item(7);
            Node stateAnchor = stateCell.getFirstChild();
            Node stateText = stateAnchor.getFirstChild();
            order.setState(stateText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the loan number
         try
         {
            Node loanNoCell = cells.item(20);
            Node loanNoText = loanNoCell.getFirstChild();
            order.setLoanNumber(loanNoText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the mortgager's name
         try
         {
            Node nameCell = cells.item(22);
            Node nameText = nameCell.getFirstChild();
            order.setName(nameText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the type
         try
         {
            Node sTypeCell = cells.item(12);
            Node sTypeText = sTypeCell.getFirstChild();
            order.setSType(sTypeText.getNodeValue());
         }
         catch (NullPointerException e)
         {
         }

         // Get the link to the details page
         try
         {
            Node inputCell = cells.item(0);
            Node inputField = inputCell.getChildNodes().item(1);
            Node idAttribute = inputField.getAttributes().getNamedItem("value");
            String idValue = idAttribute.getNodeValue();
            order.setDetailsLink(PAGES_BASE + "InspectionCompletionForm_rev1.aspx?id=" + idValue);
         }
         catch (NullPointerException e)
         {
         }
         catch (NoSuchElementException e)
         {
         }

         return order;
      }
      catch (Exception e)
      {
         Debug.debugException("WARNING: FAFSAccess.harvestOrder(): Error reading the record for service order ID: " + id + ".  Skipping this order.", e);
      }
      return null;
   }

   protected void harvestOrderDetails(FAFSOrder order, InputStream inStream)
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "harvestOrderDetails(): START");

      Document doc = getWeb().parseInput(inStream);

      NodeList inputTags = doc.getElementsByTagName("input");
      for (int i = 0; i < inputTags.getLength(); i++)
      {
         Node inputTag = inputTags.item(i);
         NamedNodeMap attrs = inputTag.getAttributes();
         if (attrs != null)
         {
            Node idAttrNode = attrs.getNamedItem("id");
            if (idAttrNode != null)
            {
               getInputHandler(idAttrNode.getNodeValue()).applyInputField(idAttrNode.getNodeValue(), inputTag, order);
            }
         }
      }

      // Find the mortgage company (hack)
      try
      {
         NodeList tableCellTags = doc.getElementsByTagName("td");
         Node mortgageCompanyCell = tableCellTags.item(7);
         Node boldTag = mortgageCompanyCell.getFirstChild();
         Node textTag = boldTag.getFirstChild();
         String mortgageCompany = textTag.getNodeValue();
         order.setClientName(mortgageCompany);
      }
      catch (Exception e)
      {
         if (Debug.checkLevel(Debug.LOW)) Debug.debug(Debug.LOW, "Warning: unable to determine the mortgage company for order " + order.getTrackingNo());
      }

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

   protected boolean includeZip(String zip)
   {
      if (mIncludedZips != null) return mIncludedZips.contains(zip);
      if (mExcludedZips != null) return !mExcludedZips.contains(zip);
      return true;
   }

   protected static InputFieldHandler getInputHandler(String id)
   {
      if (cInputHandlers == null)
      {
         cInputHandlers = new HashMap<String, InputFieldHandler>();
         OccupancyHandler occupancy = new OccupancyHandler();
         cInputHandlers.put("D_D1", occupancy);
         cInputHandlers.put("D_D2", occupancy);
         cInputHandlers.put("D_D3", occupancy);
         cInputHandlers.put("D_D4", occupancy);
         cInputHandlers.put("D_D5", occupancy);
         cInputHandlers.put("D_D6", occupancy);
         cInputHandlers.put("D_D7", occupancy);
         cInputHandlers.put("D_D8", occupancy);
         cInputHandlers.put("D_DA1", occupancy);
         cInputHandlers.put("D_DA2", occupancy);
         cInputHandlers.put("D_DA3", occupancy);
         cInputHandlers.put("D_Dtxt1", occupancy);
         cInputHandlers.put("D_Dtxt2", occupancy);
         cInputHandlers.put("D_Dtxt3", occupancy);
         cInputHandlers.put("D_Dtxt4", occupancy);
         cInputHandlers.put("D_Dtxt5", occupancy);
         cInputHandlers.put("D_Dtxt6", occupancy);
         VerifiedByHandler verifiedBy = new VerifiedByHandler();
         cInputHandlers.put("E_E1", verifiedBy);
         cInputHandlers.put("E_E2", verifiedBy);
         cInputHandlers.put("E_E3", verifiedBy);
         cInputHandlers.put("E_E4", verifiedBy);
         cInputHandlers.put("E_E5", verifiedBy);
         NeighborhoodConditionHandler neighborhoodCondition = new NeighborhoodConditionHandler();
         cInputHandlers.put("F_F1", neighborhoodCondition);
         cInputHandlers.put("F_F2", neighborhoodCondition);
         cInputHandlers.put("F_F3", neighborhoodCondition);
         cInputHandlers.put("F_FA1", neighborhoodCondition);
         cInputHandlers.put("F_FA2", neighborhoodCondition);
         ExteriorConditionHandler exteriorCondition = new ExteriorConditionHandler();
         cInputHandlers.put("G_G1", exteriorCondition);
         cInputHandlers.put("G_G2", exteriorCondition);
         cInputHandlers.put("G_G3", exteriorCondition);
         ConstructionHandler construction = new ConstructionHandler();
         cInputHandlers.put("H_H1", construction);
         cInputHandlers.put("H_H2", construction);
         cInputHandlers.put("H_H3", construction);
         cInputHandlers.put("H_H4", construction);
         ForSaleHandler forSale = new ForSaleHandler();
         cInputHandlers.put("I_I1", forSale);
         cInputHandlers.put("I_I2", forSale);
         cInputHandlers.put("I_I5", forSale);
         cInputHandlers.put("I_I6", forSale);
         cInputHandlers.put("I_I7", forSale);
         cInputHandlers.put("I_I8", forSale);
         cInputHandlers.put("I_IA1", forSale);
         cInputHandlers.put("I_IA2", forSale);
         StructureHandler structure = new StructureHandler();
         cInputHandlers.put("J_J1", structure);
         cInputHandlers.put("J_J10", structure);
         cInputHandlers.put("J_J2", structure);
         cInputHandlers.put("J_J3", structure);
         cInputHandlers.put("J_J4", structure);
         cInputHandlers.put("J_J5", structure);
         cInputHandlers.put("J_J6", structure);
         cInputHandlers.put("J_J7", structure);
         cInputHandlers.put("J_J8", structure);
         cInputHandlers.put("J_J9", structure);
         GarageHandler garage = new GarageHandler();
         cInputHandlers.put("M_M1", garage);
         cInputHandlers.put("M_M2", garage);
         cInputHandlers.put("M_MA1", garage);
         cInputHandlers.put("M_MA2", garage);
      }
      InputFieldHandler handler = cInputHandlers.get(id);
      return (handler == null) ? cBitBucketHandler : handler;
   }

   static class OccupancyHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "D_D1"))
            {
               order.setOccupancyStatus("Owner");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D2"))
            {
               order.setOccupancyStatus("New Owner");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D3"))
            {
               order.setOccupancyStatus("Occupant Name Unknown");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D4"))
            {
               order.setOccupancyStatus("Tenant");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D5"))
            {
               order.setOccupancyStatus("Vacant and Locked");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D6"))
            {
               order.setOccupancyStatus("Vacant and Open");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D7"))
            {
               order.setOccupancyStatus("Partial Vacant");
            } else if (EqualityUtils.equalsIgnoreCase(id, "D_D8"))
            {
               order.setOccupancyStatus("No Access");
            }
         }
      }
   }

   static class VerifiedByHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "E_E1"))
            {
               order.setOccupancyVerifiedBy("Direct Contact");
               order.setContactFlag(true);
            } else if (EqualityUtils.equalsIgnoreCase(id, "E_E2"))
            {
               order.setOccupancyVerifiedBy("Visual");
               order.setContactFlag(false);
            } else if (EqualityUtils.equalsIgnoreCase(id, "E_E3"))
            {
               order.setOccupancyVerifiedBy("Neighbor");
               order.setContactFlag(false);
            } else if (EqualityUtils.equalsIgnoreCase(id, "E_E4"))
            {
               order.setOccupancyVerifiedBy("Mailbox/Mailman");
               order.setContactFlag(false);
            } else if (EqualityUtils.equalsIgnoreCase(id, "E_E5"))
            {
               order.setOccupancyVerifiedBy("Other");
               order.setContactFlag(false);
            }
         }
      }
   }

   static class NeighborhoodConditionHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "F_F1"))
            {
               order.setNeighborhoodCond("Stbl");
            } else if (EqualityUtils.equalsIgnoreCase(id, "F_F2"))
            {
               order.setNeighborhoodCond("Declng");
            } else if (EqualityUtils.equalsIgnoreCase(id, "F_F3"))
            {
               order.setNeighborhoodCond("Imprvng");
            } else if (EqualityUtils.equalsIgnoreCase(id, "F_FA1"))
            {
               order.setHighVandalArea(true);
            } else if (EqualityUtils.equalsIgnoreCase(id, "F_FA2"))
            {
               order.setHighVandalArea(false);
            }
         }
      }
   }

   static class ExteriorConditionHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "G_G1"))
            {
               order.setExteriorCondition("Good");
            } else if (EqualityUtils.equalsIgnoreCase(id, "G_G2"))
            {
               order.setExteriorCondition("Fair");
            } else if (EqualityUtils.equalsIgnoreCase(id, "G_G3"))
            {
               order.setExteriorCondition("Poor");
            }
         }
      }
   }

   static class ConstructionHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "H_H1"))
            {
               order.setConstructionType("frm");
            } else if (EqualityUtils.equalsIgnoreCase(id, "H_H2"))
            {
               order.setConstructionType("brck/blck");
            } else if (EqualityUtils.equalsIgnoreCase(id, "H_H3"))
            {
               order.setConstructionType("stcco");
            } else if (EqualityUtils.equalsIgnoreCase(id, "H_H4"))
            {
               order.setConstructionType("other");
            }
         }
      }
   }

   static class StructureHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "J_J1"))
            {
               order.setStructureType("sngl fam");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J2"))
            {
               order.setStructureType("dplx");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J3"))
            {
               order.setStructureType("trplx");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J4"))
            {
               order.setStructureType("frplx");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J5"))
            {
               order.setStructureType("twnhse");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J6"))
            {
               order.setStructureType("cndo");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J7"))
            {
               order.setStructureType("mdulr/manu");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J8"))
            {
               order.setStructureType("vcnt lt");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J9"))
            {
               order.setStructureType("other");
            } else if (EqualityUtils.equalsIgnoreCase(id, "J_J10"))
            {
               order.setStructureType("moble hm");
            }
         }
      }
   }

   static class GarageHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "M_M1"))
            {
               order.setGarage(true);
            } else if (EqualityUtils.equalsIgnoreCase(id, "M_M2"))
            {
               order.setGarage(false);
            } else if (EqualityUtils.equalsIgnoreCase(id, "M_MA1"))
            {
               order.setGarageType("atch");
            } else if (EqualityUtils.equalsIgnoreCase(id, "M_MA2"))
            {
               order.setGarageType("dtch");
            }
         }
      }
   }

   static class ForSaleHandler implements InputFieldHandler
   {
      public void applyInputField(String id, Node inputField, FAFSOrder order)
      {
         Node checked = inputField.getAttributes().getNamedItem("checked");
         if (checked != null && EqualityUtils.equalsIgnoreCase(checked.getNodeValue(), "checked"))
         {
            if (EqualityUtils.equalsIgnoreCase(id, "I_I1"))
            {
               order.setForSale(true);
            } else if (EqualityUtils.equalsIgnoreCase(id, "I_I2"))
            {
               order.setForSale(false);
            } else if (EqualityUtils.equalsIgnoreCase(id, "I_IA1"))
            {
               order.setForSaleBy("By Owner");
            } else if (EqualityUtils.equalsIgnoreCase(id, "I_IA2"))
            {
               order.setForSaleBy("By Broker");
            }
         } else if (EqualityUtils.equalsIgnoreCase(id, "I_I5"))
         {
            Node value = inputField.getAttributes().getNamedItem("value");
            if (value != null && !StringUtil.isEmpty(value.getNodeValue()))
            {
               order.setForSaleBroker(value.getNodeValue());
            }
         } else if (EqualityUtils.equalsIgnoreCase(id, "I_I6"))
         {
            Node value = inputField.getAttributes().getNamedItem("value");
            if (value != null && !StringUtil.isEmpty(value.getNodeValue()))
            {
               order.setForSalePhoneAreaCode(value.getNodeValue());
            }
         } else if (EqualityUtils.equalsIgnoreCase(id, "I_I7"))
         {
            Node value = inputField.getAttributes().getNamedItem("value");
            if (value != null && !StringUtil.isEmpty(value.getNodeValue()))
            {
               order.setForSalePhonePrefix(value.getNodeValue());
            }
         } else if (EqualityUtils.equalsIgnoreCase(id, "I_I8"))
         {
            Node value = inputField.getAttributes().getNamedItem("value");
            if (value != null && !StringUtil.isEmpty(value.getNodeValue()))
            {
               order.setForSalePhoneSuffix(value.getNodeValue());
            }
         }
      }
   }
}
