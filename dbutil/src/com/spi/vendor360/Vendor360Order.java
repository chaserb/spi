/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.vendor360;

import static com.spi.vendor360.Vendor360Access.BLDG_ID;
import static com.spi.vendor360.Vendor360Access.BROKER_ID;
import static com.spi.vendor360.Vendor360Access.BROKER_NAME;
import static com.spi.vendor360.Vendor360Access.COLOR_ID;
import static com.spi.vendor360.Vendor360Access.CON_ID;
import static com.spi.vendor360.Vendor360Access.FOR_SALE_ID;
import static com.spi.vendor360.Vendor360Access.GARAGE_ID;
import static com.spi.vendor360.Vendor360Access.INSP_TYPE;
import static com.spi.vendor360.Vendor360Access.NEIGH_CON_ID;
import static com.spi.vendor360.Vendor360Access.OCC_STATUS_ID;
import static com.spi.vendor360.Vendor360Access.PREV_OCC_STATUS;
import static com.spi.vendor360.Vendor360Access.PROP_CON_ID;
import static com.spi.vendor360.Vendor360Access.STORIES_ID;
import static com.spi.vendor360.Vendor360Access.TEL1;

import java.util.Date;
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
public class Vendor360Order extends AbstractIncomingOrder
{
   private Vendor360Access mAccess;
   private int mTrackingNumber;
   private String mWorkOrderNum;
   private String mName;
   private String mAcctNum;
   private String mInspectionId;
   private String mPMId;
   private String mInspType;
   private Date mDueDate;
   private Date mDateOrdered;
   private String mMtgName;
   private String mCusNum;
   private String mMtgAdr;
   private String mMtgCity;
   private String mMtgState;
   private int mMtgZip;
   private String mOccStatusId;
   private String mPropConId;
   private String mPrevOccStatus;
   private String mGarageId;
   private String mStoriesId;
   private String mNeighConId;
   private String mConId;
   private String mColorId;
   private String mBldgId;
   private String mForSaleId;
   private String mBrokerId;
   private String mTell1;
   private String mBrokerName;

   protected static Map<String, Map<String, String>> mDebugValues;

   static
   {
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues = new TreeMap<String, Map<String, String>>();
         mDebugValues.put(INSP_TYPE, new TreeMap<String, String>());
         mDebugValues.put(PREV_OCC_STATUS, new TreeMap<String, String>());
         mDebugValues.put(OCC_STATUS_ID, new TreeMap<String, String>());
         mDebugValues.put(PROP_CON_ID, new TreeMap<String, String>());
         mDebugValues.put(GARAGE_ID, new TreeMap<String, String>());
         mDebugValues.put(STORIES_ID, new TreeMap<String, String>());
         mDebugValues.put(NEIGH_CON_ID, new TreeMap<String, String>());
         mDebugValues.put(CON_ID, new TreeMap<String, String>());
         mDebugValues.put(COLOR_ID, new TreeMap<String, String>());
         mDebugValues.put(BLDG_ID, new TreeMap<String, String>());
         mDebugValues.put(FOR_SALE_ID, new TreeMap<String, String>());
         mDebugValues.put(BROKER_ID, new TreeMap<String, String>());
         mDebugValues.put(TEL1, new TreeMap<String, String>());
         mDebugValues.put(BROKER_NAME, new TreeMap<String, String>());
      }
   }

   public Vendor360Order(Vendor360Access access, String sequenceNumber, Date dueDate)
   {
      super();
      mAccess = access;
      mWorkOrderNum = sequenceNumber;
      mDueDate = dueDate;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mWorkOrderNum.substring(2)) : mTrackingNumber;
   }

   public String getRep()
   {
      return SPIAccess.getInstance().getRepForZip(mMtgZip);
   }

   public String getServiceCompany()
   {
      return mAccess.getServiceCompany();
   }

   public String getCustWONo()
   {
      return mWorkOrderNum;
   }

   public String getLoanNo()
   {
      return mAcctNum;
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
      return mInspType;
   }

   public String getSGInstructions()
   {
      return null;
   }

   public Date getRecdIn()
   {
      return mDateOrdered;
   }

   public Date getDueServ()
   {
      return mDueDate;
   }

   public Date getRepCompDate()
   {
      return null;
   }

   public String getMortgager()
   {
      return mMtgName;
   }

   public String getPropNo()
   {
      String baseString = mMtgAdr;
      if (baseString != null)
      {
         int spaceIndex = baseString.indexOf(' ');
         return (spaceIndex > 0) ? baseString.substring(0, spaceIndex) : null;
      } else
      {
         return null;
      }
   }

   public String getPropertyAddress()
   {
      String baseString = mMtgAdr;
      if (baseString != null)
      {
         int spaceIndex = baseString.indexOf(' ');
         return ((spaceIndex > 0) ? baseString.substring(spaceIndex) : baseString).trim();
      } else
      {
         return null;
      }
   }

   public String getCity()
   {
      return StringUtil.cleanString(mMtgCity);
   }

   public String getSt()
   {
      return StringUtil.cleanString(mMtgState);
   }

   public String getZipCode()
   {
      return Integer.toString(mMtgZip);
   }

   public int getPhotosRecd()
   {
      return -1;
   }

   public String getPhotosYN()
   {
      return null;
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mBldgId))
      {
         buf.append(mBldgId);
      }
      if (!StringUtil.isEmpty(mColorId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(mColorId);
      }
      if (!StringUtil.isEmpty(mConId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(mConId);
      }
      if (!StringUtil.isEmpty(mGarageId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(mGarageId);
      }
      if (!StringUtil.isEmpty(mNeighConId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("NC:");
         buf.append(mNeighConId);
      }
      if (!StringUtil.isEmpty(mPropConId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("PC:");
         buf.append(mPropConId);
      }
      if (!StringUtil.isEmpty(mStoriesId))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(mStoriesId);
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
      return 0;
   }

   public String getOccupancy()
   {
      return StringUtil.isEmpty(mOccStatusId) ? mPrevOccStatus : mOccStatusId;
   }

   public String getPersonalContact()
   {
      return null;
   }

   public String getForSale()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mForSaleId))
      {
         buf.append(mForSaleId);
      }
      if (!StringUtil.isEmpty(mBrokerId))
      {
         if (buf.length() > 0)
         {
            buf.append(", ");
         }
         buf.append(mBrokerId);
      }
      if (!StringUtil.isEmpty(mBrokerName))
      {
         if (buf.length() > 0)
         {
            buf.append(", ");
         }
         buf.append(mBrokerName);
      }
      return StringUtil.cleanString(buf.toString(), 50);
   }

   public String getForSalePhone()
   {
      return mTell1;
   }

   public String getComments()
   {
      return "";
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
      return mCusNum + " - " + mName;
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
      float wage = (getIsRural()) ? spi.getRuralWagesForZip(mMtgZip) : spi.getWagesForZip(mMtgZip);
      wage += getPhotosRecd() * mAccess.getWageFigures().getPhotoWageFactor();
      return (getIsLate()) ? 0 : wage;
   }

   public String getInspectionId()
   {
      return mInspectionId;
   }

   public String getPMId()
   {
      return mPMId;
   }

   /* ------------------------------ Mutators ------------------------------ */

   public void setDateOrdered(Date value)
   {
      mDateOrdered = value;
   }

   public void setTrackingNumber(int value)
   {
      mTrackingNumber = value;
   }

   public void setName(String value)
   {
      mName = cleanString(value);
   }

   public void setAccntNum(String value)
   {
      mAcctNum = cleanString(value);
   }

   public void setInspectionId(String value)
   {
      mInspectionId = cleanString(value);
   }

   public void setPMId(String value)
   {
      mPMId = cleanString(value);
   }

   public void setInspType(String value)
   {
      value = cleanString(value);
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get(INSP_TYPE).put(value, mWorkOrderNum);
      }
      mInspType = (value == null || value.length() == 0) ? "Unknown" : value;
   }

   public void setDueDate(Date value)
   {
      mDueDate = value;
   }

   public void setMtgName(String value)
   {
      mMtgName = cleanString(value);
   }

   public void setCusNum(String value)
   {
      mCusNum = cleanString(value);
   }

   public void setMtgAdr(String value)
   {
      mMtgAdr = cleanString(value);
   }

   public void setMtgCity(String value)
   {
      mMtgCity = cleanString(value);
   }

   public void setMtgState(String value)
   {
      mMtgState = cleanString(value);
   }

   public void setMtgZip(String value)
   {
      mMtgZip = Integer.parseInt(cleanString(value));
   }

   public void setPrevOccStatus(String value)
   {
      value = cleanString(value);
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get(PREV_OCC_STATUS).put(value, mWorkOrderNum);
      }
      mPrevOccStatus = value;
   }

   public void setOccStatusId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(OCC_STATUS_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 1:
         mOccStatusId = "Partially Vacant - Multi Unit";
         break;
      case 2:
         mOccStatusId = "Occupied by Owner";
         break;
      case 3:
         mOccStatusId = "Occupied by Tenant";
         break;
      case 4:
         mOccStatusId = "Occupied by Unknown";
         break;
      case 5:
         mOccStatusId = "Vacant";
         break;
      case 6:
         mOccStatusId = "Bad Address";
         break;
      case 7:
         mOccStatusId = "No Access";
         break;
      case -1:
         mOccStatusId = null;
         break;
      default:
         warnUnknownValue("OccStatusId", numValue);
      }
   }

   public void setPropConId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(PROP_CON_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
      case 5:
          mPropConId = "";
          break;
      case 1:
         mPropConId = "Good";
         break;
      case 2:
         mPropConId = "Fair";
         break;
      case 3:
         mPropConId = "Poor";
         break;
      case 4:
         mPropConId = "Violation";
         break;
      case -1:
         mPropConId = null;
         break;
      default:
         warnUnknownValue("PropConId", numValue);
      }
   }

   public void setGarageId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(GARAGE_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
    	  mGarageId = "";
          break;
      case 1:
         mGarageId = "No Garage";
         break;
      case 2:
         mGarageId = "1 Car Garage";
         break;
      case 3:
         mGarageId = "2 Car Garage";
         break;
      case 4:
         mGarageId = "3 Car Garage";
         break;
      case 5:
         mGarageId = "Other Garage";
         break;
      case 99:
         break;
      case -1:
         mGarageId = null;
         break;
      default:
         warnUnknownValue("GarId", numValue);
      }
   }

   public void setStoriesId(String value)
   {
      value = cleanString(value);
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get(STORIES_ID).put(value, mWorkOrderNum);
      }
      mStoriesId = value;

      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(STORIES_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mStoriesId = "";
          break;
      case 1:
         mStoriesId = "One Story";
         break;
      case 2:
         mStoriesId = "Two Stories";
         break;
      case 3:
         mStoriesId = "Three+ Stories";
         break;
      case 4:
         mStoriesId = "Split Level";
         break;
      case 5:
         mStoriesId = "1.5 Stories";
         break;
      case -1:
         mStoriesId = null;
         break;
      default:
         warnUnknownValue("StoriesId", numValue);
      }

   }

   public void setNeighConId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(NEIGH_CON_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mNeighConId = "";
          break;
      case 1:
         mNeighConId = "Stable";
         break;
      case 2:
         mNeighConId = "Declining";
         break;
      case 3:
         mNeighConId = "Improving";
         break;
      case 4:
         mNeighConId = "High Vandal";
         break;
      case -1:
         mNeighConId = null;
         break;
      default:
         warnUnknownValue("NeighConId", numValue);
      }
   }

   public void setConId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(CON_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mConId = "";
          break;
      case 2:
         mConId = "Stucco";
         break;
      case 3:
         mConId = "Frame";
         break;
      case 4:
         mConId = "Stone";
         break;
      case 5:
         mConId = "Conc Block";
         break;
      case 6:
         mConId = "Brick Frame";
         break;
      case 9:
         mConId = "Brick";
         break;
      case 10:
         mConId = "Alum Frame";
         break;
      case 11:
         mConId = "Vinyl Frame";
         break;
      case 12:
         mConId = "Wood Frame";
         break;
      case -1:
         mConId = null;
         break;
      default:
         warnUnknownValue("ConId", numValue);
      }
   }

   public void setColorId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(COLOR_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mColorId = "";
          break;
      case 1:
         mColorId = "White";
         break;
      case 2:
         mColorId = "Blue";
         break;
      case 3:
         mColorId = "Natural";
         break;
      case 4:
         mColorId = "Beige/Tan";
         break;
      case 5:
         mColorId = "Yellow";
         break;
      case 6:
         mColorId = "Brown";
         break;
      case 7:
         mColorId = "Gray";
         break;
      case 8:
         mColorId = "Green";
         break;
      case 9:
         mColorId = "Red/Pink";
         break;
      case 10:
         mColorId = "Other";
         break;
      case -1:
         mColorId = null;
         break;
      default:
         warnUnknownValue("ColorId", numValue);
      }
   }

   public void setBldgId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(BLDG_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mBldgId = "";
          break;
      case 1:
         mBldgId = "Single";
         break;
      case 6:
         mBldgId = "Mobile Home";
         break;
      case 8:
         mBldgId = "Duplex";
         break;
      case 9:
         mBldgId = "Triplex";
         break;
      case 10:
         mBldgId = "Quadplex";
         break;
      case 11:
         mBldgId = "Condo/Town";
         break;
      case 12:
          mBldgId = "Modular";
          break;
      case 13:
         mBldgId = "Vacant Lot";
         break;
      case 14:
         mBldgId = "Manufactured";
         break;
      case 15:
         mBldgId = "Other";
         break;
      case 16:
         mBldgId = "Non-Residential";
         break;
      case -1:
         mBldgId = null;
         break;
      default:
         warnUnknownValue("BldgId", numValue);
      }
   }

   public void setForSaleId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(FOR_SALE_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 0:
          mForSaleId = "";
          break;
      case 1:
         mForSaleId = "Yes";
         break;
      case 2:
         mForSaleId = "No";
         break;
      case -1:
      case 99:
         mForSaleId = null;
         break;
      default:
         warnUnknownValue("ForSaleID", numValue);
      }
   }

   public void setBrokerId(String value)
   {
      int numValue = cleanStringToInt(value);
      if (Debug.checkLevel(Debug.MED) && value != null)
      {
         mDebugValues.get(BROKER_ID).put(value, mWorkOrderNum);
      }
      switch (numValue)
      {
      case 1:
         mBrokerId = "Owner";
         break;
      case 2:
         mBrokerId = "Real Estate";
         break;
      case 3:
         mBrokerId = "Not Applicable";
         break;
      case 0:
      case -1:
         mBrokerId = null;
         break;
      default:
         warnUnknownValue("BrokerId", numValue);
      }
   }

   public void setTell1(String value)
   {
      value = cleanString(value);
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get(TEL1).put(value, mWorkOrderNum);
      }
      mTell1 = value;
   }

   public void setBrokerName(String value)
   {
      value = cleanString(value);
      if (Debug.checkLevel(Debug.MED))
      {
         mDebugValues.get(BROKER_NAME).put(value, mWorkOrderNum);
      }
      mBrokerName = value;
   }

   /* --------------------------- Discreet Access -------------------------- */

   protected int getZip()
   {
      return mMtgZip;
   }
   
   private void warnUnknownValue(String id, int value)
   {
      Debug.log("Here's a(n) " + id + " I haven't seen before: " + value + ". This is on WO# " + getCustWONo() + ". Please email this message to Chase.");
   }

   private static String cleanString(String value)
   {
      return (value == null) ? "" : value.trim();
   }

   private static int cleanStringToInt(String value)
   {
      try
      {
         return Integer.parseInt(cleanString(value));
      }
      catch (NumberFormatException e)
      {
         return -1;
      }
   }
}
