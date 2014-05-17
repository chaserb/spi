/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.sierra;

import java.util.Date;

import com.spi.AbstractIncomingOrder;
import com.spi.SPIAccess;
import com.spi.util.Debug;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class SierraOrder extends AbstractIncomingOrder
{
   private SierraAccess mAccess;
   private int mTrackingNumber;
   private String mWorkOrderNumber;
   private String mSysId;
   private Date mStartDate;
   private String mAccountNumber; // MCS will have this
   private String mPropertyId; // Fidelity/Corelogic will have this 
   private String mClient;
   private String mMortgagor;

   private String mServiceType;
//   private boolean mRush;
   private Date mRepDueDate;
   private Date mCompleteDate;
   private String mAddress;
   private String mCity;
   private String mState;
   private String mZip;
//   private boolean mPhotosReqd;
   private String mConstructionType;
   private String mBuildingType;
   private String mColor;
   private String mGarageType;
//   private int mValue;
   private String mOccupancy;
   private String mOccupancyVerifiedBy;
   private String mForSale;
   private String mSignInYard;
   private String mRealEstateCompany;
   private String mRealtorPhoneNumber;
   private String mNeighborhoodCondition;
   private String mPropertyCondition;
   private String mStories;
//   private boolean mGrassCut;
//   private int mGrassHeight;
//   private String mComments;
//   private String mInvoiceNumber;
//   private Date mInvoiceDate;
//   private boolean mBadAddress;
//   private String mBadAddressAttempt;
//   private String mBadAddressComments;
//   private String mBadAddressOther;

   public SierraOrder(SierraAccess access, String workOrderNumber)
   {
      super();
      mAccess = access;
      mWorkOrderNumber = workOrderNumber;
//      mRush = false;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mWorkOrderNumber.replaceAll("[^\\d]", "")) : mTrackingNumber;
   }

   public String getRep()
   {
      try
      {
         return SPIAccess.getInstance().getRepForZip(Integer.parseInt(mZip));
      }
      catch (Exception e)
      {
         Debug.debugException("Unable to turn a zip code into a number (" + mZip + "): ", e);
         return "Lindsey";
      }
   }

   public String getServiceCompany()
   {
      return mAccess.getServiceCompany();
   }

   public String getCustWONo()
   {
      return StringUtil.cleanString(mWorkOrderNumber);
   }

   public String getLoanNo()
   {
      return (mAccountNumber != null) ? StringUtil.cleanString(mAccountNumber) : StringUtil.cleanString(mPropertyId);
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
      return mServiceType;
   }

   public String getSGInstructions()
   {
      return null;
   }

   public Date getRecdIn()
   {
      if (mStartDate == null)
      {
         Date date = new Date();
         if (mRepDueDate != null && date.after(mRepDueDate))
         {
            date = mRepDueDate;
         }
         mStartDate = date;
      }
      return mStartDate;
   }

   public Date getDueServ()
   {
      return mRepDueDate;
   }

   public Date getRepCompDate()
   {
      return mCompleteDate;
   }

   public String getMortgager()
   {
      return StringUtil.cleanString(mMortgagor);
   }

   public String getPropNo()
   {
      String baseString = mAddress;
      int spaceIndex = baseString.indexOf(' ');
      return (spaceIndex > 0) ? baseString.substring(0, spaceIndex) : null;
   }

   public String getPropertyAddress()
   {
      String baseString = mAddress;
      int spaceIndex = baseString.indexOf(' ');
      return ((spaceIndex > 0) ? baseString.substring(spaceIndex) : baseString).trim();
   }

   public String getCity()
   {
      return mCity;
   }

   public String getSt()
   {
      return mState;
   }

   public String getZipCode()
   {
      return mZip;
   }

   public boolean getIsRural()
   {
      return SPIAccess.getInstance().getRuralForZip(Integer.parseInt(mZip));
   }

   public String getPhotosYN()
   {
      return null;//(mPhotosReqd) ? "Yes" : "No";
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if (!StringUtil.isEmpty(mBuildingType) && !mBuildingType.equals("|"))
      {
         buf.append("BT:");
         if (mBuildingType.contains("|S|"))
         {
            buf.append("Single");
         }
         else if (mBuildingType.contains("|CT|"))
         {
            buf.append("Condo/Town");
         }
         else if (mBuildingType.contains("|D|")) // Guessing here
         {
            buf.append("Duplex");
         }
         else if (mBuildingType.contains("|T|")) // Guessing here
         {
            buf.append("Triplex");
         }
         else if (mBuildingType.contains("|Q|"))
         {
            buf.append("Quadplex");
         }
         else if (mBuildingType.contains("|OT|"))
         {
            buf.append("Other");
         }
         else
         {
            buf.append(mBuildingType);
         }
      }
      if (!StringUtil.isEmpty(mConstructionType) && !mConstructionType.equals("|"))
      {
         StringUtil.prependComma(buf);
         buf.append("CT:");
         if (mConstructionType.contains("|BR|"))
         {
            buf.append("Brick");
         }
         else if (mConstructionType.contains("|F|"))
         {
            buf.append("Frame");
         }
         else if (mConstructionType.contains("|SO|"))
         {
            buf.append("Stucco");
         }
         else if (mConstructionType.contains("|ST|"))
         {
            buf.append("Stone");
         }
         else if (mConstructionType.contains("|OT|"))
         {
            buf.append("Other");
         }
         else
         {
            buf.append(mConstructionType);
         }
      }
      if (!StringUtil.isEmpty(mStories) && !mStories.equals("|"))
      {
         buf.append("ST:");
         if (mStories.contains("|O|"))
         {
            buf.append("One");
         }
         else if (mStories.contains("|TW|"))
         {
            buf.append("Two");
         }
         else if (mStories.contains("|TH|")) // Guessing here
         {
            buf.append("Three");
         }
         else if (mStories.contains("|F|")) // Guessing here
         {
            buf.append("Four");
         }
         else
         {
            buf.append(mStories);
         }
      }
      if (!StringUtil.isEmpty(mColor) && !mColor.equals("|"))
      {
         StringUtil.prependComma(buf);
         buf.append("COL:");
         if (mColor.contains("|BL|"))
         {
            buf.append("Blue");
         }
         else if (mColor.contains("|BT|"))
         {
            buf.append("Beige/Tan");
         }
         else if (mColor.contains("|BR|"))
         {
            buf.append("Brown");
         }
         else if (mColor.contains("|GN|"))
         {
            buf.append("Green");
         }
         else if (mColor.contains("|GY|"))
         {
            buf.append("Gray");
         }
         else if (mColor.contains("|N|"))
         {
            buf.append("Natural");
         }
         else if (mColor.contains("|RP|"))
         {
            buf.append("Red/Pink");
         }
         else if (mColor.contains("|W|"))
         {
            buf.append("White");
         }
         else if (mColor.contains("|Y|"))
         {
            buf.append("Yellow");
         }
         else
         {
            buf.append(mColor);
         }
      }
      if (!StringUtil.isEmpty(mGarageType) && !mGarageType.equals("|"))
      {
         StringUtil.prependComma(buf);
         buf.append("GAR:");
         if (mGarageType.contains("|0|"))
         {
            buf.append("1 Car");
         }
         else if (mGarageType.contains("|1|"))
         {
            buf.append("2 Car");
         }
         else if (mGarageType.contains("|2|"))
         {
            buf.append("3 Car");
         }
         else if (mGarageType.contains("|4|"))
         {
            buf.append("None");
         }
         else if (mGarageType.contains("|A|"))
         {
            buf.append("Attached");
         }
         else if (mGarageType.contains("|D|"))
         {
            buf.append("Detached");
         }
         else if (mGarageType.contains("|N|"))
         {
            buf.append("No");
         }
         else
         {
            buf.append(mGarageType);
         }
      }
      if (!StringUtil.isEmpty(mNeighborhoodCondition) && !mNeighborhoodCondition.equals("|"))
      {
         StringUtil.prependComma(buf);
         buf.append("NC:");
         if (mNeighborhoodCondition.contains("|S|"))
         {
            buf.append("Stable");
         }
         else if (mNeighborhoodCondition.contains("|U|"))
         {
            buf.append("Unknown");
         }
         else if (mNeighborhoodCondition.contains("|I|")) // Guessing here
         {
            buf.append("Improving");
         }
         else if (mNeighborhoodCondition.contains("|D|")) // Guessing here
         {
            buf.append("Declining");
         }
         else if (mNeighborhoodCondition.contains("|HV|")) // Guessing here
         {
            buf.append("High Vandal");
         }
         else
         {
            buf.append(mNeighborhoodCondition);
         }
      }
      if (!StringUtil.isEmpty(mPropertyCondition)&& !mPropertyCondition.equals("|"))
      {
         StringUtil.prependComma(buf);
         buf.append("PC:");
         if (mPropertyCondition.contains("|G|"))
         {
            buf.append("Good");
         }
         else if (mPropertyCondition.contains("|F|"))
         {
            buf.append("Fair");
         }
         else if (mPropertyCondition.contains("|P|")) // Guessing here
         {
            buf.append("Poor");
         }
         else if (mPropertyCondition.contains("|V|")) // Guessing here
         {
            buf.append("Violation");
         }
         else if (mPropertyCondition.contains("|OT|"))
         {
            buf.append("Other");
         }
         else
         {
            buf.append(mPropertyCondition);
         }
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
      return 0;//mValue * 1000;
   }

   public String getOccupancy()
   {
      if (mOccupancy != null)
      {
         if (mOccupancy.contains("|B|"))
         {
            return "Bad Address";
         }
         else if (mOccupancy.contains("|N|"))
         {
            return "No Access";
         }
         else if (mOccupancy.contains("|O|"))
         {
            return "Occupied By Owner";
         }
         else if (mOccupancy.contains("|T|"))
         {
            return "Occupied By Tenant";
         }
         else if (mOccupancy.contains("|U|"))
         {
            return "Occupied By Unknown";
         }
         else if (mOccupancy.contains("|V|"))
         {
            return "Vacant";
         }
      }
      return mOccupancy;
   }

   public String getPersonalContact()
   {
      StringBuffer retValue = new StringBuffer();
      if (mOccupancyVerifiedBy != null)
      {
         if (mOccupancyVerifiedBy.contains("|V|"))
         {
            retValue.append("Visual");
         }
         if (mOccupancyVerifiedBy.contains("|C|"))
         {
            StringUtil.prependComma(retValue);
            retValue.append("Contact");
         }
         if (mOccupancyVerifiedBy.contains("|O|"))
         {
            StringUtil.prependComma(retValue);
            retValue.append("Other");
         }
         if (mOccupancyVerifiedBy.contains("|N|"))
         {
            StringUtil.prependComma(retValue);
            retValue.append("Neighbor");
         }
      }
      if (retValue.length() == 0)
      {
         retValue.append(mOccupancyVerifiedBy);
      }
      return retValue.toString();
   }

   public String getForSale()
   {
      if (mForSale != null && mForSale.contains("|Y|"))
      {
         if (mForSale.contains("|R|"))
         {
            if ("Yes".equals(mSignInYard))
            {
               return mRealEstateCompany + ", Sign";
            }
         }
      }
      return "Not for Sale";
   }

   public String getForSalePhone()
   {
      return (mForSale != null && mForSale.contains("|P|")) ? mRealtorPhoneNumber : null;
   }

   public String getComments()
   {
//      StringBuffer buf = new StringBuffer();
//      if (mBadAddress)
//      {
//         buf.append("BAD ADDR: ");
//         if ((mBadAddressAttempt != null) && (mBadAddressAttempt.trim().length() > 0))
//         {
//            buf.append("(");
//            mBadAddressAttempt = mBadAddressAttempt.trim();
//            for (int i = 0; i < mBadAddressAttempt.length(); i++)
//            {
//               if (mBadAddressAttempt.charAt(i) == 'A') buf.append("Post Ofc");
//               if (mBadAddressAttempt.charAt(i) == 'B') buf.append("Tax Ass'r");
//               if (mBadAddressAttempt.charAt(i) == 'C') buf.append("Fire Dept");
//               if (mBadAddressAttempt.charAt(i) == 'D') buf.append("Police Dept");
//               if (mBadAddressAttempt.charAt(i) == 'E') buf.append("Nghbrs");
//               if (mBadAddressAttempt.charAt(i) == 'F') buf.append(mBadAddressOther);
//               buf.append(", ");
//            }
//            buf.append(mBadAddressComments);
//            buf.append(") ");
//         }
//      }
//      buf.append(mComments);
      return null;//StringUtil.cleanString(buf.toString(), 255);
   }

   public String getInvoiceNo()
   {
      return null;//mInvoiceNumber;
   }

   public Date getInvoiceDate()
   {
      return null;//mInvoiceDate;
   }

   public String getMtgComp()
   {
      return StringUtil.cleanString(mClient);
   }

   public float getCost()
   {
//      float cost = CostFigures.getBaseCost();
//      Float costFromMatrix = (Float) CostFigures.getCostMatrix().get(getReportType());
//      if (costFromMatrix != null)
//      {
//         cost = costFromMatrix.floatValue();
//      }
//      if (getPhotosRecd() > 0)
//      {
//         cost += CostFigures.getPhotoCost();
//      }
//      if (mContactFlag)
//      {
//         cost += CostFigures.getPersonalContactCost();
//      }
      return 0;//cost;
   }

   public float getWages()
   {
//      SPIAccess spi = SPIAccess.getInstance();
//      float wage = (getIsRural()) ? spi.getRuralWagesForZip(mZip) : spi.getWagesForZip(mZip);
//      wage += getPhotosRecd() * mAccess.getWageFigures().getPhotoWageFactor();
      return 0;//(getIsLate()) ? 0 : wage;
   }
   
   public String getSysId()
   {
      return mSysId;
   }
   
   public String getDetailsLink()
   {
      return "http://ezinspections.com/inspManager/jobDetails.aspx?Id=" + getSysId();
   }

   /* ------------------------------ Mutators ------------------------------ */

   public void setTrackingNumber(int value)
   {
      mTrackingNumber = value;
   }

   public void setWorkOrderNumber(String value)
   {
      mWorkOrderNumber = value;
   }
   
   public void setSysId(String id)
   {
      mSysId = id;
   }
   
   public void setAccountNumber(String value)
   {
      mAccountNumber = value;
   }
   
   public void setPropertyId(String value)
   {
      mPropertyId = value;
   }

   public void setServiceType(String value)
   {
      mServiceType = value;
   }

//   public void setRush(boolean value)
//   {
//      mRush = value;
//   }

   public void setStartDate(Date value)
   {
      mStartDate = value;
   }

   public void setRepDueDate(Date value)
   {
      mRepDueDate = value;
   }

   public void setCompleteDate(Date value)
   {
      mCompleteDate = value;
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
      mZip = value;
   }

//   public void setPhotosReqd(boolean value)
//   {
//      mPhotosReqd = value;
//   }

   public void setConstructionType(String value)
   {
      mConstructionType = value;
   }

   public void setBuildingType(String value)
   {
      mBuildingType = value;
   }

   public void setColor(String value)
   {
      mColor = value;
   }

   public void setGarageType(String value)
   {
      mGarageType = value;
   }

//   public void setValue(int value)
//   {
//      mValue = value;
//   }

   public void setOccupancy(String value)
   {
      mOccupancy = value;
   }
   
   public void setOccupancyVerifiedBy(String value)
   {
      mOccupancyVerifiedBy = value;
   }
   
   public void setForSale(String value)
   {
      mForSale = value;
   }
   
   public void setSignInYard(String value)
   {
      mSignInYard = value;
   }

   public void setRealEstateCompany(String value)
   {
      mRealEstateCompany = value;
   }

   public void setRealtorPhoneNumber(String value)
   {
      mRealtorPhoneNumber = value;
   }

   public void setNeighborhoodCondition(String value)
   {
      mNeighborhoodCondition = value;
   }

   public void setPropertyCondition(String value)
   {
      mPropertyCondition = value;
   }
   
   public void setStories(String value)
   {
      mStories = value;
   }

//   public void setGrassCut(boolean value)
//   {
//      mGrassCut = value;
//   }
//
//   public void setGrassHeight(int value)
//   {
//      mGrassHeight = value;
//   }
//
//   public void setComments(String value)
//   {
//      mComments = value;
//   }
//
//   public void setInvoiceNumber(String value)
//   {
//      mInvoiceNumber = value;
//   }
//
//   public void setInvoiceDate(Date value)
//   {
//      mInvoiceDate = value;
//   }
//
   public void setClient(String value)
   {
      mClient = value;
   }
//
//   public void setBadAddress(boolean value)
//   {
//      mBadAddress = value;
//   }
//
//   public void setBadAddressAttempt(String value)
//   {
//      mBadAddressAttempt = value;
//   }
//
//   public void setBadAddressComments(String value)
//   {
//      mBadAddressComments = value;
//   }
//
//   public void setBadAddressOther(String value)
//   {
//      mBadAddressOther = value;
//   }
}
