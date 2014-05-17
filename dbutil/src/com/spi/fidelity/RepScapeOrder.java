/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fidelity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.spi.AbstractIncomingOrder;
import com.spi.CostFigures;
import com.spi.SPIAccess;
import com.spi.util.Debug;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class RepScapeOrder extends AbstractIncomingOrder
{
   protected RepScapeAccess mAccess;
   protected int mTrackingNumber;
   protected String mSequenceNumber;
   protected String mClientName;
   protected String mLoanNumber;
   protected String mInspectionType;
   protected Date mDueDate;
   protected Date mInspectionDate;
   protected String mMortgagorsName;
   protected String mAddress;
   protected String mAdditionalAddress;
   protected String mCity;
   protected String mState;
   protected int mZip;
   protected String mTypeOfConstruction;
   protected String mTypeOfProperty;
   protected String mGarage;
   protected String mOccupancyIs;
   protected String mPropertyIsForSale;
   protected String mBrokerName;
   protected String mBrokerPhone;
   protected String mPhone;
   protected String mComments;
   protected String mBadAddressWhy;
   protected String mBadAddressComments;
   protected String mBadAddressCheckedWith;
   protected String mAccessDeniedWhy;
   protected String mAccessDeniedComments;
   protected String mOutOfAreaWhy;

   protected static Map<String, String> mInspectionTypes;
   protected static Map<Date, Date> mStdRecdDates;
   protected static Map<Date, Date> mRushRecdDates;
   protected static Map<String, Map<String, String>> mDebugValues;

   static
   {
      mInspectionTypes = new HashMap<String, String>();

      mInspectionTypes.put("ACCESS DENIED", "Property Inspection");
      mInspectionTypes.put("BAD ADDRESS ATTEMPT", "Property Inspection");
      mInspectionTypes.put("FANNIE MAE INSPECTION", "Fannie Mae");
      mInspectionTypes.put("INTERIOR/EXTERIOR INSPECTION", "Interior/Exterior Inspection");
      mInspectionTypes.put("OCCUPANCY INSPECTION", "Property Inspection");
      mInspectionTypes.put("OCCUPANCY INSPECTION W/ DOORCARD", "Occupancy Inspection w/Doorcard");
      mInspectionTypes.put("OCCUPANCY INSPECTION W/ DOORCARD & PHOTO", "Occupancy Inspection w/Doorcard");
      mInspectionTypes.put("OCCUPANCY INSPECTION W/ PHOTO", "Property Inspection");
      mInspectionTypes.put("PROPERTY INSPECTION", "Property Inspection");
      mInspectionTypes.put("PROPERTY INSPECTION W/ PHOTO", "Property Inspection");
      mInspectionTypes.put("RUSH INTERIOR/EXTERIOR INSPECTION", "RUSH Interior/Exterior Inspection");
      mInspectionTypes.put("RUSH PROPERTY INSPECTION", "RUSH Property Inspection");
      mInspectionTypes.put("RUSH PROPERTY INSPECTION W/ PHOTO", "RUSH Property Inspection");
      mInspectionTypes.put("SALE DATE INSPECTION", "Sale Date Inspection");

      mStdRecdDates = new HashMap<Date, Date>();
      mRushRecdDates = new HashMap<Date, Date>();

      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues = new TreeMap<String, Map<String, String>>();
         mDebugValues.put("Inspection Type", new TreeMap<String, String>());
         mDebugValues.put("Occupancy Is", new TreeMap<String, String>());
         mDebugValues.put("Type of Property", new TreeMap<String, String>());
         mDebugValues.put("Garage", new TreeMap<String, String>());
         mDebugValues.put("Type of Construction", new TreeMap<String, String>());
         mDebugValues.put("Property Is (For Sale)", new TreeMap<String, String>());
         mDebugValues.put("Broker Name", new TreeMap<String, String>());
         mDebugValues.put("Broker Phone", new TreeMap<String, String>());
         mDebugValues.put("Phone", new TreeMap<String, String>());
         mDebugValues.put("Bad Address (Why?)", new TreeMap<String, String>());
         mDebugValues.put("Bad Address (Comments)", new TreeMap<String, String>());
         mDebugValues.put("Bad Address (Checked With)", new TreeMap<String, String>());
         mDebugValues.put("Access Denied (Why?)", new TreeMap<String, String>());
         mDebugValues.put("Access Denied (Comments)", new TreeMap<String, String>());
         mDebugValues.put("Out Of Area (Why?)", new TreeMap<String, String>());
      }
   }

   public RepScapeOrder(RepScapeAccess access, String sequenceNumber, Date dueDate)
   {
      mAccess = access;
      mSequenceNumber = sequenceNumber;
      mDueDate = dueDate;
      mTrackingNumber = -1;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mSequenceNumber) : mTrackingNumber;
   }

   public String getRep()
   {
      return SPIAccess.getInstance().getRepForZip(mZip);
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
      return StringUtil.cleanString(mLoanNumber);
   }

   public String getSGInspectionCode()
   {
      return null;
   }

   public String getSGInspectionTitle()
   {
      return null;
   }

   public String getReportType()
   {
      String ourType = mInspectionTypes.get(mInspectionType);
      if (ourType == null)
      {
         ourType = (getIsRush()) ? "RUSH Property Inspection" : "Property Inspection";
         Debug.log("WARNING: Got a Fidelity inspection type I don't recognize (" + mInspectionType + ") on inpsection " + mTrackingNumber
               + ".  Assigning a default of " + ourType);
      }
      return ourType;
   }

   public String getSGInstructions()
   {
      return null;
   }

   public Date getRecdIn()
   {
      return getRecdDate(mDueDate, getIsRush());
   }

   public Date getDueServ()
   {
      return mDueDate;
   }

   public Date getRepCompDate()
   {
      return mInspectionDate;
   }

   public String getMortgager()
   {
      return StringUtil.cleanString(mMortgagorsName);
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
      return null;
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mTypeOfProperty))
      {
         buf.append("Prop: ");
         buf.append(mTypeOfProperty);
      }
      if (!StringUtil.isEmpty(mTypeOfConstruction))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("Const: ");
         buf.append(mTypeOfConstruction);
      }
      if (!StringUtil.isEmpty(mGarage))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("Gar: ");
         buf.append(mGarage);
      } else if (buf.length() > 0)
      {
         buf.append(", Gar: None");
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
      return 0;
   }

   public String getOccupancy()
   {
      return mOccupancyIs;
   }

   public String getPersonalContact()
   {
      return null;
   }

   public String getForSale()
   {
      StringBuffer buf = new StringBuffer();
      buf.append(mPropertyIsForSale);
      if (!StringUtil.isEmpty(mBrokerName))
      {
         buf.append(": ");
         buf.append(mBrokerName);
      }
      return StringUtil.cleanString(buf.toString(), 50);
   }

   public String getForSalePhone()
   {
      if (!StringUtil.isEmpty(mPropertyIsForSale))
      {
         return (mPropertyIsForSale.toLowerCase().contains("owner")) ? mPhone : mBrokerPhone;
      }
      return null;
   }

   public String getComments()
   {
      StringBuffer buf = new StringBuffer();
      int lastLen = 0;

      // Add Bad Address Info
      if (!StringUtil.isEmpty(mBadAddressWhy) || !StringUtil.isEmpty(mBadAddressComments) || !StringUtil.isEmpty(mBadAddressCheckedWith))
      {
         buf.append("(BAD ADDRESS:");
         lastLen = buf.length();
         if (!StringUtil.isEmpty(mBadAddressWhy))
         {
            buf.append(mBadAddressWhy);
         }
         if (!StringUtil.isEmpty(mBadAddressComments))
         {
            if (lastLen != buf.length())
            {
               lastLen = buf.length();
               buf.append(", ");
            }
            buf.append(mBadAddressComments);
         }
         if (!StringUtil.isEmpty(mBadAddressCheckedWith))
         {
            if (lastLen != buf.length())
            {
               buf.append(", ");
            }
            buf.append("Checked with ");
            buf.append(mBadAddressCheckedWith);
         }
         buf.append(") ");
      }

      // Access Denied Info
      if (!StringUtil.isEmpty(mAccessDeniedWhy) || !StringUtil.isEmpty(mAccessDeniedComments))
      {
         buf.append("(ACCESS DENIED:");
         lastLen = buf.length();
         if (!StringUtil.isEmpty(mAccessDeniedWhy))
         {
            buf.append(mAccessDeniedWhy);
         }
         if (!StringUtil.isEmpty(mAccessDeniedComments))
         {
            if (lastLen != buf.length())
            {
               buf.append(", ");
            }
            buf.append(mAccessDeniedComments);
         }
         buf.append(") ");
      }

      // Out Of Area Info
      if (!StringUtil.isEmpty(mOutOfAreaWhy))
      {
         buf.append("(OUT OF AREA:");
         buf.append(mAccessDeniedWhy);
         buf.append(") ");
      }

      if (!StringUtil.isEmpty(mComments))
      {
         buf.append(mComments);
      }

      return StringUtil.cleanString(buf.toString(), 255);
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
      return RepScapeAccess.ORDER_DETAILS + getCustWONo();
   }

   public boolean getIsRush()
   {
      return mInspectionType.contains("RUSH");
   }

   /* ------------------------------ Mutators ------------------------------ */

   public void setTrackingNumber(int value)
   {
      mTrackingNumber = value;
   }

   public void setLoanNumber(String value)
   {
      mLoanNumber = value;
   }

   public void setInspectionType(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Inspection Type").put(value, mSequenceNumber);
      }
      mInspectionType = (value == null) ? "" : value.trim().toUpperCase();
   }

   public void setDueDate(Date value)
   {
      mDueDate = value;
   }

   public void setInspectionDate(Date value)
   {
      mInspectionDate = value;
   }

   public void setMortgagorsName(String value)
   {
      mMortgagorsName = value;
   }

   public void setAddress(String value)
   {
      mAddress = value;
   }

   public void additionalAddress(String value)
   {
      mAdditionalAddress = value;
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

   public void setTypeOfConstruction(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Type of Construction").put(value, mSequenceNumber);
      }
      mTypeOfConstruction = value;
   }

   public void setTypeOfProperty(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Type of Property").put(value, mSequenceNumber);
      }
      mTypeOfProperty = value;
   }

   public void setGarage(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Garage").put(value, mSequenceNumber);
      }
      mGarage = value;
   }

   public void setOccupancyIs(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Occupancy Is").put(value, mSequenceNumber);
      }
      mOccupancyIs = value;
   }

   public void setPropertyIsForSale(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Property Is (For Sale)").put(value, mSequenceNumber);
      }
      mPropertyIsForSale = value;
   }

   public void setBrokerName(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Broker Name").put(value, mSequenceNumber);
      }
      mBrokerName = value;
   }

   public void setBrokerPhone(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Broker Phone").put(value, mSequenceNumber);
      }
      mBrokerPhone = value;
   }

   public void setPhone(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Phone").put(value, mSequenceNumber);
      }
      mPhone = value;
   }

   public void setBadAddressWhy(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Bad Address (Why?)").put(value, mSequenceNumber);
      }
      mBadAddressWhy = value;
   }

   public void setBadAddressComments(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Bad Address (Comments)").put(value, mSequenceNumber);
      }
      mBadAddressComments = value;
   }

   public void setBadAddressCheckedWith(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Bad Address (Checked With)").put(value, mSequenceNumber);
      }
      mBadAddressCheckedWith = value;
   }

   public void setAccessDeniedWhy(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Access Denied (Why?)").put(value, mSequenceNumber);
      }
      mAccessDeniedWhy = value;
   }

   public void setAccessDeniedComments(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Access Denied (Comments)").put(value, mSequenceNumber);
      }
      mAccessDeniedComments = value;
   }

   public void setOutOfAreaWhy(String value)
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get("Out Of Area (Why?)").put(value, mSequenceNumber);
      }
      mOutOfAreaWhy = value;
   }

   public void setComments(String value)
   {
      mComments = value;
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

   private String getBasePropertyString()
   {
      String baseString = ((mAdditionalAddress == null) || (mAdditionalAddress.trim().equals(""))) ? mAddress : mAddress + ", " + mAdditionalAddress;
      return baseString.trim();
   }

   private static Date getRecdDate(Date dueDate, boolean rush)
   {
      Map<Date, Date> dateMap = (rush) ? mRushRecdDates : mStdRecdDates;
      Date recdDate = dateMap.get(dueDate);
      if (recdDate == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(dueDate);
         int numDays = (rush) ? 3 : 7;
         while (numDays > 0)
         {
            cal.add(Calendar.DATE, -1);
            switch (cal.get(Calendar.DAY_OF_WEEK))
            {
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
               break;
            default:
               numDays--;
            }
         }
         recdDate = cal.getTime();
         dateMap.put(dueDate, recdDate);
      }
      return recdDate;
   }
}
