/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.safeguard;

import java.util.Date;

import com.spi.AbstractIncomingOrder;
import com.spi.CostFigures;
import com.spi.SPIAccess;
import com.spi.util.StringUtil;

/**
 * @author Chase Barrett
 */
public class INSPIOrder extends AbstractIncomingOrder
{
   private INSPIAccess mAccess;
   private int mTrackingNumber;
   private String mOrderNumber;
   private Date mOrderDate;
   private String mLoanNumber;
   private String mClient;
   private String mName;

   private String mInspectionCode;
   private boolean mRush;
   private String mInstructions;
   private Date mInspectionDueDate;
   private Date mInspectionDate;
   private boolean m2535;
   private String mAddress1;
   private String mAddress2;
   private String mCity;
   private String mState;
   private int mZip;
   private boolean mPhotosReqd;
   private String mConstructionType;
   private String mBuildingType;
   private String mColor1;
   private String mColor2;
   private String mGarageType;
   private int mValue;
   private int mNumDwellings;
   private String mOccupancyStatus;
   private boolean mContactFlag;
   private String mForSaleBy;
   private String mForSaleBroker;
   private String mForSalePhone;
   private int mNeighborhoodCond;
   private int mExteriorCondition;
   private boolean mGrassCut;
   private int mGrassHeight;
   private String mComments;
   private String mInvoiceNumber;
   private Date mInvoiceDate;
   private boolean mBadAddress;
   private String mBadAddressAttempt;
   private String mBadAddressComments;
   private String mBadAddressOther;

   public INSPIOrder(INSPIAccess access, String orderNumber, Date orderDate)
   {
      super();
      mAccess = access;
      mOrderNumber = orderNumber;
      mOrderDate = orderDate;
      mRush = false;
   }

   /* ------------------------------ Accessors ----------------------------- */

   public int getTrackingNo()
   {
      return (mTrackingNumber < 0) ? Integer.parseInt(mOrderNumber) : mTrackingNumber;
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
      return StringUtil.cleanString(mOrderNumber);
   }

   public String getLoanNo()
   {
      String loanNo = StringUtil.cleanString(mLoanNumber);
      if (loanNo == null || loanNo.length() == 0)
      {
         loanNo = getPropNo() + getPropertyAddress() + getCity() + getSt() + getZipCode();
         loanNo = Integer.toString(Math.abs(loanNo.hashCode()));
      }
      return loanNo;
   }

   public String getSGInspectionCode()
   {
      return StringUtil.cleanString(mInspectionCode);
   }

   public String getSGInspectionTitle()
   {
      String code = (mInspectionCode == null) ? "" : mInspectionCode.trim().toUpperCase();
      String title = null;
      if (code.length() > 0)
      {
         if (code.equals("DF1") || code.equals("DF"))
            title = "CONTACT ATTEMPT";
         else if (code.equals("DFI"))
            title = "INITIAL CONTACT ATTMPT";
         else if (code.equals("FI"))
            title = "VERIFY IF OCCPD, VACNT";
         else if (code.equals("FIB"))
            title = "BNKRPTCY DRIVE-BY INSP";
         else if (code.equals("FINC"))
            title = "NO CONTACT INSPECTION";
         else if (code.equals("FINIS") || code.equals("FINT"))
            title = "INTERIOR";
         else if (code.equals("FII"))
            title = "INITIAL FIELD INSPECTN";
         else if (code.equals("FINMA"))
            title = "FANNIE MAE";
         else if (code.equals("FIPSM"))
            title = "POST SALE MONTHLY INSP";
         else if (code.equals("FIR"))
            title = "SALE DATE RUSH";
         else if (code.equals("FIRCKR"))
            title = "VERIFY OCCUPNCY RECHCK";
         else if (code.equals("FIREO"))
            title = "REO PROPERTY";
         else if (code.equals("FISR"))
            title = "STATUS RECHECK";
         else if (code.equals("IL"))
            title = "INSURANCE LOSS";
         else if (code.equals("REINSI"))
            title = "INTERIOR VERIFY DEBRIS";
         else if (code.equals("REOINT"))
            title = "REO INTERIOR";
         else
            title = code;
      }
      if (m2535)
      {
         title = title + " 25/35";
      }
      return title;
   }

   public String getReportType()
   {
      String code = (mInspectionCode == null) ? "" : mInspectionCode.trim().toUpperCase();
      String type = "Occupancy Verification";
      if (code.length() > 0)
      {
         if (code.equals("DF1"))
            type = "Delinquent";
         else if (code.equals("DFI"))
            type = "Delinquent";
         else if (code.equals("FI"))
            type = "Occupancy Verification";
         else if (code.equals("FIB"))
            type = "Occupancy Verification";
         else if (code.equals("FII"))
            type = "Occupancy Verification";
         else if (code.equals("FINMA"))
            type = "Occupancy Verification";
         else if (code.equals("FIPSM"))
            type = "Occupancy Verification";
         else if (code.equals("FIREO"))
            type = "Occupancy Verification";
         else if (code.equals("FISR"))
            type = "Occupancy Verification";
         else if (code.equals("FIREO")) type = "Occupancy Verification";
         if (mRush) type = (type == null) ? "Rush" : type + " Rush";
      }
      return type;
   }

   public String getSGInstructions()
   {
      return StringUtil.cleanString(mInstructions, 255);
   }

   public Date getRecdIn()
   {
      return mOrderDate;
   }

   public Date getDueServ()
   {
      return mInspectionDueDate;
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
      return (mPhotosReqd) ? "Yes" : "No";
   }

   public String getPropertyDescription()
   {
      StringBuffer buf = new StringBuffer();
      if ((mBuildingType != null) && (mBuildingType.trim().length() > 0))
      {
         if (mBuildingType.indexOf('A') >= 0)
            buf.append("1 st");
         else if (mBuildingType.indexOf('B') >= 0)
            buf.append("2 st");
         else if (mBuildingType.indexOf('C') >= 0)
            buf.append("3 st");
         else if (mBuildingType.indexOf('D') >= 0)
            buf.append("splt lvl");
         else if (mBuildingType.indexOf('E') >= 0)
            buf.append("cndo/twnhse");
         else if (mBuildingType.indexOf('F') >= 0)
            buf.append("mbl hm");
         else if (mBuildingType.indexOf('G') >= 0)
            buf.append("vcnt lnd");
         else if (mBuildingType.indexOf('H') >= 0)
            buf.append("bnglw");
         else if (mBuildingType.indexOf('I') >= 0)
            buf.append("rnch");
         else if (mBuildingType.indexOf('J') >= 0)
            buf.append("row");
         else if (mBuildingType.indexOf('Z') >= 0) buf.append("other");
      }
      if ((mConstructionType != null) && (mConstructionType.trim().length() > 0))
      {
         if (buf.length() > 0) buf.append(", ");
         if (mConstructionType.indexOf('A') >= 0)
            buf.append("frm");
         else if (mConstructionType.indexOf('B') >= 0)
            buf.append("brck");
         else if (mConstructionType.indexOf('C') >= 0)
            buf.append("stcco");
         else if (mConstructionType.indexOf('D') >= 0)
            buf.append("almnm");
         else if (mConstructionType.indexOf('E') >= 0)
            buf.append("vnyl");
         else if (mConstructionType.indexOf('F') >= 0)
            buf.append("cncrt");
         else if (mConstructionType.indexOf('G') >= 0)
            buf.append("frm/brck");
         else if (mConstructionType.indexOf('Z') >= 0) buf.append("other");
      }
      if ((mColor1 != null) && (mColor1.trim().length() > 0))
      {
         if (buf.length() > 0) buf.append(", ");
         if (mColor1.indexOf('A') >= 0)
            buf.append("wht");
         else if (mColor1.indexOf('B') >= 0)
            buf.append("brn");
         else if (mColor1.indexOf('C') >= 0)
            buf.append("beig");
         else if (mColor1.indexOf('D') >= 0)
            buf.append("rd");
         else if (mColor1.indexOf('E') >= 0)
            buf.append("pnk");
         else if (mColor1.indexOf('F') >= 0)
            buf.append("blu");
         else if (mColor1.indexOf('G') >= 0)
            buf.append("gry");
         else if (mColor1.indexOf('H') >= 0)
            buf.append("peach");
         else if (mColor1.indexOf('I') >= 0)
            buf.append("yllw");
         else if (mColor1.indexOf('J') >= 0)
            buf.append("cream");
         else if (mColor1.indexOf('K') >= 0)
            buf.append("grn");
         else if (mColor1.indexOf('Z') >= 0) buf.append("other");
      }
      if ((mColor2 != null) && (mColor2.trim().length() > 0))
      {
         if (mColor1 != null)
            buf.append("/");
         else if (buf.length() > 0) buf.append(", ");
         if (mColor2.indexOf('A') >= 0)
            buf.append("wht");
         else if (mColor2.indexOf('B') >= 0)
            buf.append("brn");
         else if (mColor2.indexOf('C') >= 0)
            buf.append("beig");
         else if (mColor2.indexOf('D') >= 0)
            buf.append("rd");
         else if (mColor2.indexOf('E') >= 0)
            buf.append("pnk");
         else if (mColor2.indexOf('F') >= 0)
            buf.append("blu");
         else if (mColor2.indexOf('G') >= 0)
            buf.append("gry");
         else if (mColor2.indexOf('H') >= 0)
            buf.append("peach");
         else if (mColor2.indexOf('I') >= 0)
            buf.append("yllw");
         else if (mColor2.indexOf('J') >= 0)
            buf.append("cream");
         else if (mColor2.indexOf('K') >= 0)
            buf.append("grn");
         else if (mColor2.indexOf('Z') >= 0) buf.append("other");
      }
      if ((mGarageType != null) && (mGarageType.trim().length() > 0))
      {
         if (buf.length() > 0) buf.append(", ");
         if (mGarageType.indexOf('A') >= 0)
            buf.append("1cr atch");
         else if (mGarageType.indexOf('B') >= 0)
            buf.append("2cr atch");
         else if (mGarageType.indexOf('C') >= 0)
            buf.append("1cr dtch");
         else if (mGarageType.indexOf('D') >= 0)
            buf.append("2cr dtch");
         else if (mGarageType.indexOf('E') >= 0)
            buf.append("crprt");
         else if (mGarageType.indexOf('F') >= 0)
            buf.append("3cr atch");
         else if (mGarageType.indexOf('G') >= 0)
            buf.append("3cr dtch");
         else if (mGarageType.indexOf('X') >= 0)
            buf.append("no garge");
         else if (mGarageType.indexOf('Z') >= 0) buf.append("other");
      }
      if (mNumDwellings > 0)
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(Integer.toString(mNumDwellings));
         buf.append((mNumDwellings > 1) ? " dwlls" : " dwll");
      }
      String neighborhoodCondition = null;
      switch (mNeighborhoodCond)
      {
      case 1:
         neighborhoodCondition = "Stbl";
         break;
      case 2:
         neighborhoodCondition = "Declng";
         break;
      case 3:
         neighborhoodCondition = "Imprvng";
      default:
      }
      if (neighborhoodCondition != null)
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("NC:");
         buf.append(neighborhoodCondition);
      }
      String exteriorCondition = null;
      switch (mExteriorCondition)
      {
      case 1:
         exteriorCondition = "Good";
         break;
      case 2:
         exteriorCondition = "Fair";
         break;
      case 3:
         exteriorCondition = "Poor";
      default:
      }
      if (exteriorCondition != null)
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("EC:");
         buf.append(exteriorCondition);
      }
      if (mGrassCut)
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("Grass Needs Cut: " + mGrassHeight + "\"");
      }
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public int getValue()
   {
      return mValue * 1000;
   }

   public String getOccupancy()
   {
      String code = (mOccupancyStatus == null) ? "" : mOccupancyStatus.trim().toUpperCase();
      String status = null;
      if (code.length() > 0)
      {
         if (code.equals("A"))
            status = "Mortgager";
         else if (code.equals("B"))
            status = "Tenant";
         else if (code.equals("C"))
            status = "Occ Unknown";
         else if (code.equals("D"))
            status = "Vacant";
         else if (code.equals("E"))
            status = "Partially Vacant";
         else if (code.equals("U")) status = (mBadAddress) ? "Unknown - Bad Address" : "Unable to Verify";
      }
      return status;
   }

   public String getPersonalContact()
   {
      return (mContactFlag) ? "Yes" : "No";
   }

   public String getForSale()
   {
      String forSaleBy = (mForSaleBy == null) ? "" : mForSaleBy.trim().toUpperCase();
      if (forSaleBy.equals("A"))
      {
         return (mForSaleBroker);
      } else if (forSaleBy.equals("B"))
      {
         return ("By Owner");
      } else
      {
         return null;
      }
   }

   public String getForSalePhone()
   {
      return mForSalePhone;
   }

   public String getComments()
   {
      StringBuffer buf = new StringBuffer();
      if (mBadAddress)
      {
         buf.append("BAD ADDR: ");
         if ((mBadAddressAttempt != null) && (mBadAddressAttempt.trim().length() > 0))
         {
            buf.append("(");
            mBadAddressAttempt = mBadAddressAttempt.trim();
            for (int i = 0; i < mBadAddressAttempt.length(); i++)
            {
               if (mBadAddressAttempt.charAt(i) == 'A') buf.append("Post Ofc");
               if (mBadAddressAttempt.charAt(i) == 'B') buf.append("Tax Ass'r");
               if (mBadAddressAttempt.charAt(i) == 'C') buf.append("Fire Dept");
               if (mBadAddressAttempt.charAt(i) == 'D') buf.append("Police Dept");
               if (mBadAddressAttempt.charAt(i) == 'E') buf.append("Nghbrs");
               if (mBadAddressAttempt.charAt(i) == 'F') buf.append(mBadAddressOther);
               buf.append(", ");
            }
            buf.append(mBadAddressComments);
            buf.append(") ");
         }
      }
      buf.append(mComments);
      return StringUtil.cleanString(buf.toString(), 255);
   }

   public String getInvoiceNo()
   {
      return mInvoiceNumber;
   }

   public Date getInvoiceDate()
   {
      return mInvoiceDate;
   }

   public String getMtgComp()
   {
      return StringUtil.cleanString(mClient);
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

   /* ------------------------------ Mutators ------------------------------ */

   public void setTrackingNumber(int value)
   {
      mTrackingNumber = value;
   }

   public void setOrderNumber(String value)
   {
      mOrderNumber = value;
   }

   public void setLoanNumber(String value)
   {
      mLoanNumber = value;
   }

   public void setInspectionCode(String value)
   {
      mInspectionCode = value;
   }

   public void setRush(boolean value)
   {
      mRush = value;
   }

   public void setInstructions(String value)
   {
      mInstructions = value;
   }

   public void setOrderDate(Date value)
   {
      mOrderDate = value;
   }

   public void setInspectionDueDate(Date value)
   {
      mInspectionDueDate = value;
   }

   public void setInspectionDate(Date value)
   {
      mInspectionDate = value;
   }
   
   public void set2535(boolean value)
   {
      m2535 = value;
   }

   public void setName(String value)
   {
      mName = value;
   }

   public void setAddress1(String value)
   {
      mAddress1 = StringUtil.cleanString(value);
   }

   public void setAddress2(String value)
   {
      mAddress2 = StringUtil.cleanString(value);
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

   public void setPhotosReqd(boolean value)
   {
      mPhotosReqd = value;
   }

   public void setConstructionType(String value)
   {
      mConstructionType = value;
   }

   public void setBuildingType(String value)
   {
      mBuildingType = value;
   }

   public void setColor1(String value)
   {
      mColor1 = value;
   }

   public void setColor2(String value)
   {
      mColor2 = value;
   }

   public void setGarageType(String value)
   {
      mGarageType = value;
   }

   public void setValue(int value)
   {
      mValue = value;
   }

   public void setNumDwellings(int value)
   {
      mNumDwellings = value;
   }

   public void setOccupancyStatus(String value)
   {
      mOccupancyStatus = value;
   }

   public void setContactFlag(boolean value)
   {
      mContactFlag = value;
   }

   public void setForSaleBy(String value)
   {
      mForSaleBy = value;
   }

   public void setForSaleBroker(String value)
   {
      mForSaleBroker = value;
   }

   public void setForSalePhone(String value)
   {
      mForSalePhone = value;
   }

   public void setNeighborhoodCond(int value)
   {
      mNeighborhoodCond = value;
   }

   public void setExteriorCondition(int value)
   {
      mExteriorCondition = value;
   }

   public void setGrassCut(boolean value)
   {
      mGrassCut = value;
   }

   public void setGrassHeight(int value)
   {
      mGrassHeight = value;
   }

   public void setComments(String value)
   {
      mComments = value;
   }

   public void setInvoiceNumber(String value)
   {
      mInvoiceNumber = value;
   }

   public void setInvoiceDate(Date value)
   {
      mInvoiceDate = value;
   }

   public void setClient(String value)
   {
      mClient = value;
   }

   public void setBadAddress(boolean value)
   {
      mBadAddress = value;
   }

   public void setBadAddressAttempt(String value)
   {
      mBadAddressAttempt = value;
   }

   public void setBadAddressComments(String value)
   {
      mBadAddressComments = value;
   }

   public void setBadAddressOther(String value)
   {
      mBadAddressOther = value;
   }

   /* --------------------------- Discreet Access -------------------------- */

   protected int getZip()
   {
      return mZip;
   }

   private String getBasePropertyString()
   {
      String baseString = ((mAddress2 == null) || (mAddress2.trim().equals(""))) ? mAddress1 : mAddress1 + ", " + mAddress2;
      return baseString.trim();
   }

}
