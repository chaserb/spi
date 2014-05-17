/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fafs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spi.AbstractIncomingOrder;
import com.spi.CostFigures;
import com.spi.SPIAccess;
import com.spi.util.Debug;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class FAFSOrder extends AbstractIncomingOrder
{
   private FAFSAccess mAccess;
   private int mTrackingNumber;
   private String mPONumber;
   private String mLoanNumber;
   private String mSType;
   private Date mIssueDate;
   private Date mWindowEnd;
   private Date mInspectionDate;
   private String mName;
   private String mAddress;
   private String mAdditionalAddress;
   private String mCity;
   private String mState;
   private int mZip;
   private String mConstructionType;
   private String mStructureType;
   private boolean mGarage;
   private String mGarageType;
   private String mOccupancyStatus;
   private String mOccupancyVerifiedBy;
   private boolean mContactFlag;
   private boolean mForSale;
   private String mForSaleBy;
   private String mForSaleBroker;
   private String mForSalePhoneAreaCode;
   private String mForSalePhonePrefix;
   private String mForSalePhoneSuffix;
   private String mNeighborhoodCond;
   private boolean mHighVandal;
   private String mExteriorCondition;
   private String mComments;
   private String mClientName;
   private String mDetailsLink;

   private static Map<String, String> mInspectionTypes;

   static
   {
      mInspectionTypes = new HashMap<String, String>();
      mInspectionTypes.put("BK", "Bankruptcy");
      mInspectionTypes.put("DQ", "Delinquent");
      mInspectionTypes.put("DQ: NO CONTACT", "Delinquent");
      mInspectionTypes.put("DQ: ONE TIME", "Delinquent");
      mInspectionTypes.put("FC", "Foreclosure");
      mInspectionTypes.put("FC: BANKRUPTCY", "Bankruptcy");
      mInspectionTypes.put("FC: INT INSP", "Foreclosure");
      mInspectionTypes.put("SP", "Property Inspection");
      mInspectionTypes.put("SP: FANNIE MAE", "Property Inspection");
      mInspectionTypes.put("SP: ONE TIME", "Delinquent");
      mInspectionTypes.put("10", "Property Inspection");
      mInspectionTypes.put("BANKRUPTCY INSPECTION", "Bankruptcy");
      mInspectionTypes.put("BORROWER INTERVIEW", "Delinquent");
      mInspectionTypes.put("FORECLOSURE INSPECTION", "Foreclosure");
      mInspectionTypes.put("OCCUPANCY DETERMINATION", "Property Inspection");
      mInspectionTypes.put("PROPERTY INSPECTION - NO CONTACT", "Property Inspection");
   }

   public FAFSOrder(FAFSAccess access, String PONumber, Date issueDate)
   {
      super();
      mAccess = access;
      mPONumber = PONumber;
      mIssueDate = issueDate;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mPONumber) : mTrackingNumber;
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
      return StringUtil.cleanString(mPONumber);
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
      String theirType = (mSType == null) ? "" : mSType.trim().toUpperCase();
      String ourType = "Property Inspection";
      if (mInspectionTypes.containsKey(theirType))
      {
         ourType = mInspectionTypes.get(theirType);
      } else
      {
         Debug.log("WARNING: Got a First American inspection type I don't recognize (" + theirType + ") on inpsection " + mTrackingNumber
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
      return mIssueDate;
   }

   public Date getDueServ()
   {
      return mWindowEnd;
   }

   public Date getRepCompDate()
   {
      return mInspectionDate;
   }

   public String getMortgager()
   {
      return StringUtil.cleanString(mName);
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

   public boolean getIsRural()
   {
      return SPIAccess.getInstance().getRuralForZip(mZip);
   }

   public String getPhotosYN()
   {
      return null;
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mStructureType))
      {
         buf.append(mStructureType);
      }
      if (!StringUtil.isEmpty(mConstructionType))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(mConstructionType);
      }
      if ((mGarage) && (buf.length() > 0))
      {
         buf.append(", ");
         buf.append(StringUtil.isEmpty(mGarageType) ? "garage" : mGarageType + " garage");
      } else if (buf.length() > 0)
      {
         buf.append(", ");
         buf.append("no garage");
      }
      if (!StringUtil.isEmpty(mNeighborhoodCond))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("NC:");
         buf.append(mNeighborhoodCond);
         if (mHighVandal) buf.append(" hi vandal");
      }
      if (!StringUtil.isEmpty(mExteriorCondition))
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("EC:");
         buf.append(mExteriorCondition);
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
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
      StringBuffer buf = new StringBuffer();
      if (mForSale)
      {
         buf.append(StringUtil.isEmpty(mForSaleBy) ? "For Sale" : mForSaleBy);
         if (!StringUtil.isEmpty(mForSaleBroker))
         {
            buf.append(": ");
            buf.append(mForSaleBroker);
         }
      }
      return StringUtil.cleanString(buf.toString(), 50);
   }

   public String getForSalePhone()
   {
      if (!StringUtil.isEmpty(mForSalePhoneAreaCode) && !StringUtil.isEmpty(mForSalePhonePrefix) && !StringUtil.isEmpty(mForSalePhoneSuffix))
      {
         StringBuffer buf = new StringBuffer();
         buf.append("(");
         buf.append(mForSalePhoneAreaCode);
         buf.append(") ");
         buf.append(mForSalePhonePrefix);
         buf.append("-");
         buf.append(mForSalePhoneSuffix);
         return buf.toString();
      }
      return null;
   }

   public String getComments()
   {
      return StringUtil.cleanString(mComments, 255);
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
      if (mContactFlag)
      {
         cost += CostFigures.getPersonalContactCost();
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
      return mDetailsLink;
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

   public void setSType(String value)
   {
      mSType = value;
   }

   public void setIssueDate(Date value)
   {
      mIssueDate = value;
   }

   public void setWindowEnd(Date value)
   {
      mWindowEnd = value;
   }

   public void setInspectionDate(Date value)
   {
      mInspectionDate = value;
   }

   public void setName(String value)
   {
      mName = value;
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

   public void setConstructionType(String value)
   {
      mConstructionType = value;
   }

   public void setStructureType(String value)
   {
      mStructureType = value;
   }

   public void setGarage(boolean value)
   {
      mGarage = value;
   }

   public void setGarageType(String value)
   {
      mGarageType = value;
   }

   public void setOccupancyStatus(String value)
   {
      mOccupancyStatus = value;
   }

   public void setOccupancyVerifiedBy(String value)
   {
      mOccupancyVerifiedBy = value;
   }

   public void setContactFlag(boolean value)
   {
      mContactFlag = value;
   }

   public void setForSale(boolean value)
   {
      mForSale = value;
   }

   public void setForSaleBy(String value)
   {
      mForSaleBy = value;
   }

   public void setForSaleBroker(String value)
   {
      mForSaleBroker = value;
   }

   public void setForSalePhoneAreaCode(String value)
   {
      mForSalePhoneAreaCode = value;
   }

   public void setForSalePhonePrefix(String value)
   {
      mForSalePhonePrefix = value;
   }

   public void setForSalePhoneSuffix(String value)
   {
      mForSalePhoneSuffix = value;
   }

   public void setNeighborhoodCond(String value)
   {
      mNeighborhoodCond = value;
   }

   public void setHighVandalArea(boolean hiVandal)
   {
      mHighVandal = hiVandal;
   }

   public void setExteriorCondition(String value)
   {
      mExteriorCondition = value;
   }

   public void setComments(String value)
   {
      mComments = value;
   }

   public void setClientName(String value)
   {
      mClientName = value;
   }

   public void setDetailsLink(String url)
   {
      mDetailsLink = url;
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
}
