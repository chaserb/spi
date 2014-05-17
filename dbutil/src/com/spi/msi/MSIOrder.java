/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.msi;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.spi.AbstractIncomingOrder;
import com.spi.CostFigures;
import com.spi.SPIAccess;
import com.spi.util.Debug;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class MSIOrder extends AbstractIncomingOrder
{
   private MSIAccess mAccess;
   private int mTrackingNumber;
   private String mSequenceNumber;
   private String mClientName;
   private Date mAssigned;
   private String mAddress;
   private String mCity;
   private String mState;
   private int mZip;
   private String mInstructions;
   private String mMortgagor;
   private Date mInspected;
   private String mOccupancyStatus;
   private String mOccupancyVerifiedBy;
   private String mEMV;
   private String mForSaleBy;
   private String mConstructionType;
   private String mBuildingType;
   private String mStories;
   private String mColorOfDwelling;
   private String mRoofType;
   private String mPropertyCondition;
   private String mNeighborhoodCondition;

   private static Map<Date, Date> mStdDueDates;
   private static Map<Date, Date> mRushDueDates;
   protected static Map<String, Map<String, String>> mDebugValues;

   static
   {
      mStdDueDates = new HashMap<Date, Date>();
      mRushDueDates = new HashMap<Date, Date>();

      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues = new TreeMap<String, Map<String, String>>();
         mDebugValues.put("Instructions", new TreeMap<String, String>());
         mDebugValues.put("Occupancy Status", new TreeMap<String, String>());
         mDebugValues.put("Occupancy Verified By", new TreeMap<String, String>());
         mDebugValues.put("Building Type", new TreeMap<String, String>());
         mDebugValues.put("Stories", new TreeMap<String, String>());
         mDebugValues.put("Color of Dwelling", new TreeMap<String, String>());
         mDebugValues.put("Roof Type", new TreeMap<String, String>());
         mDebugValues.put("Property Condition", new TreeMap<String, String>());
         mDebugValues.put("Construction Type", new TreeMap<String, String>());
         mDebugValues.put("Neighborhood Condition", new TreeMap<String, String>());
         mDebugValues.put("For Sale By", new TreeMap<String, String>());
         mDebugValues.put("EMV", new TreeMap<String, String>());
      }
   }

   public MSIOrder(MSIAccess access, String sequenceNumber, Date assignedDate)
   {
      super();
      mAccess = access;
      mSequenceNumber = sequenceNumber;
      mAssigned = assignedDate;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mSequenceNumber) : mTrackingNumber;
   }

   public String getServiceCompany()
   {
      return mAccess.getServiceCompany();
   }

   public String getCustWONo()
   {
      return StringUtil.cleanString(mSequenceNumber);
   }

   public String getLoanNo()
   {
      return null;
   }

   public String getReportType()
   {
      String reportType = null;
      StringTokenizer tok = new StringTokenizer(mInstructions, "/");
      boolean isRush = false;
      if (tok.hasMoreTokens())
      {
         reportType = tok.nextToken().trim();
         if (reportType.contains("RUSH") && tok.hasMoreTokens())
         {
            isRush = true;
            reportType = tok.nextToken().trim();
         }
         if (isRush)
         {
            reportType = "RUSH " + reportType;
         }
      }
      return reportType;
   }

   public Date getRecdIn()
   {
      return mAssigned;
   }

   public Date getDueServ()
   {
      return getDueDate(mAssigned, getIsRush());
   }

   public Date getRepCompDate()
   {
      return mInspected;
   }

   public String getMortgager()
   {
      return StringUtil.cleanString(mMortgagor);
   }

   public String getPropNo()
   {
      String baseString = getBasePropertyString();
      int spaceIndex = baseString.indexOf(' ');
      return (spaceIndex > 0) ? baseString.substring(0, spaceIndex) : null;
   }

   public String getPropertyAddress()
   {
      String baseString = getBasePropertyString();
      int spaceIndex = baseString.indexOf(' ');
      return ((spaceIndex > 0) ? baseString.substring(spaceIndex) : baseString).trim();
   }

   public String getCity()
   {
      return StringUtil.cleanString(mCity);
   }

   public String getSt()
   {
      return StringUtil.cleanString(mState);
   }

   public String getZipCode()
   {
      return Integer.toString(mZip);
   }

   public String getPhotosYN()
   {
      return (mInstructions.contains("PHOTO")) ? "Yes" : "No";
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mBuildingType))
      {
         buf.append(mBuildingType);
      }
      if (!StringUtil.isEmpty(mConstructionType))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append(mConstructionType);
      }
      if (!StringUtil.isEmpty(mStories))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append(mStories);
      }
      if (!StringUtil.isEmpty(mColorOfDwelling))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append(mColorOfDwelling);
      }
      if (!StringUtil.isEmpty(mRoofType))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append(mRoofType);
      }
      if (!StringUtil.isEmpty(mPropertyCondition))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append("P:");
         buf.append(mPropertyCondition);
      }
      if (!StringUtil.isEmpty(mNeighborhoodCondition))
      {
         if (buf.length() > 0) buf.append(",");
         buf.append("N:");
         buf.append(mNeighborhoodCondition);
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
      if (mEMV != null)
      {
         if (mEMV.startsWith("0"))
         {
            return 0;
         }
         int i = mEMV.indexOf(',');
         if (i >= 0)
         {
            return Integer.valueOf(mEMV.substring(0, i));
         }
      }
      return 0;
   }

   public String getOccupancy()
   {
      return mOccupancyStatus;
   }

   public String getPersonalContact()
   {
      return mOccupancyVerifiedBy;
   }

   public String getForSale()
   {
      return mForSaleBy;
   }

   public String getForSalePhone()
   {
      return null;
   }

   public String getComments()
   {
      return null;
   }

   public String getInvoiceNo()
   {
      return null;
   }

   public Date getInvoiceDate()
   {
      return null;
   }

   public String getMtgComp()
   {
      return StringUtil.cleanString(mClientName);
   }

   public float getCost()
   {
      float cost = CostFigures.getBaseCost();
      Float costFromMatrix = (Float) CostFigures.getCostMatrix().get(getReportType());
      if (costFromMatrix != null)
      {
         cost = costFromMatrix.floatValue();
      }
      return cost;
   }

   public float getWages()
   {
      SPIAccess spi = SPIAccess.getInstance();
      float wage = (getIsRural()) ? spi.getRuralWagesForZip(mZip) : spi.getWagesForZip(mZip);
      return (getIsLate()) ? 0 : wage;
   }

   public String getDetailsLink()
   {
      return MSIAccess.ORDER_DETAILS + getCustWONo();
   }

   public boolean getIsRush()
   {
      return mInstructions.contains("RUSH");  
   }

   /* ------------------------------ Mutators ------------------------------ */

   public void setTrackingNumber(int value)
   {
      mTrackingNumber = value;
   }

   public void setInstructions(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Instructions").put(value, mSequenceNumber);
      }
      mInstructions = (value == null) ? "" : value.trim().toUpperCase();;
   }

   public void setAssigned(Date value)
   {
      mAssigned = value;
   }

   public void setInspected(Date value)
   {
      mInspected = value;
   }

   public void setMortgagor(String value)
   {
      mMortgagor = value;
   }

   public void setAddress(String value)
   {
      mAddress = value;
   }

   public void setCity(String value)
   {
      mCity = value;
   }

   public void setState(String value)
   {
      mState = value;
   }

   public void setZip(String value)
   {
      mZip = Integer.parseInt(StringUtil.cleanString(value));
   }
   
   public void setEMV(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("EMV").put(value, mSequenceNumber);
      }
      mEMV = value;
   }

   public void setConstructionType(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Construction Type").put(value, mSequenceNumber);
      }
      mConstructionType = value;
   }

   public void setBuildingType(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Building Type").put(value, mSequenceNumber);
      }
      mBuildingType = value;
   }

   public void setStories(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Stories").put(value, mSequenceNumber);
      }
      mStories = value;
   }
   
   public void setColorOfDwelling(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Color of Dwelling").put(value, mSequenceNumber);
      }
      mColorOfDwelling = value;
   }
   
   public void setRoofType(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Roof Type").put(value, mSequenceNumber);
      }
      mRoofType = value;
   }
   
   public void setPropertyCondition(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Property Condition").put(value, mSequenceNumber);
      }
      mPropertyCondition = value;
   }
   
   public void setNeighborhoodCondition(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Neighborhood Condition").put(value, mSequenceNumber);
      }
      mNeighborhoodCondition = value;
   }

   public void setOccupancyStatus(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Occupancy Status").put(value, mSequenceNumber);
      }
      mOccupancyStatus = value;
   }

   public void setOccupancyVerifiedBy(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Occupancy Verified By").put(value, mSequenceNumber);
      }
      mOccupancyVerifiedBy = value;
   }

   public void setForSaleBy(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("For Sale By").put(value, mSequenceNumber);
      }
      mForSaleBy = value;
   }

   public void setClientName(String value)
   {
      mClientName = value;
   }

   /* --------------------------- Discreet Access -------------------------- */

   protected int getZip()
   {
      return mZip;
   }
   
   String getBasePropertyString()
   {
      String baseString = mAddress;
      return baseString.trim();
   }

   private static Date getDueDate(Date assignedDate, boolean rush)
   {
      Map<Date, Date> dateMap = (rush) ? mRushDueDates : mStdDueDates;
      Date dueDate = dateMap.get(assignedDate);
      if (dueDate == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(assignedDate);
         cal.add(Calendar.DATE, (rush) ? 3 : 7);
         dueDate = cal.getTime();
         dateMap.put(assignedDate, dueDate);
      }
      return dueDate;
   }
}
