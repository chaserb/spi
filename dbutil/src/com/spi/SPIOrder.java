/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.util.Date;

public class SPIOrder implements Order
{
   private String mCity;
   private String mComments;
   private float mCost;
   private String mCustWONo;
   private Date mDueServ;
   private String mForSale;
   private String mForSalePhone;
   private Date mInvoiceDate;
   private String mInvoiceNo;
   private boolean mIsChanged;
   private boolean mIsLate;
   private boolean mIsNew;
   private boolean mIsRural;
   private String mLoanNo;
   private String mMortgager;
   private String mMtgComp;
   private String mOccupancy;
   private String mPersonalContact;
   private String mPhotosYN;
   private String mPropNo;
   private String mPropertyAddress;
   private String mPropertyDescription;
   private Date mRecdIn;
   private String mRep;
   private Date mRepCompDate;
   private String mReportType;
   private String mSGInspectionCode;
   private String mSGInspectionTitle;
   private String mSGInstructions;
   private String mServiceCompany;
   private String mSt;
   private int mTrackingNo;
   private int mValue;
   private float mWages;
   private String mZipCode;

   /**
    * @return the city
    */
   public String getCity()
   {
      return mCity;
   }

   /**
    * @param city the city to set
    */
   public void setCity(String city)
   {
      mCity = city;
   }

   /**
    * @return the comments
    */
   public String getComments()
   {
      return mComments;
   }

   /**
    * @param comments the comments to set
    */
   public void setComments(String comments)
   {
      mComments = comments;
   }

   /**
    * @return the cost
    */
   public float getCost()
   {
      return mCost;
   }

   /**
    * @param cost the cost to set
    */
   public void setCost(float cost)
   {
      mCost = cost;
   }

   /**
    * @return the custWONo
    */
   public String getCustWONo()
   {
      return mCustWONo;
   }

   /**
    * @param custWONo the custWONo to set
    */
   public void setCustWONo(String custWONo)
   {
      mCustWONo = custWONo;
   }

   /**
    * @return the dueServ
    */
   public Date getDueServ()
   {
      return mDueServ;
   }

   /**
    * @param dueServ the dueServ to set
    */
   public void setDueServ(Date dueServ)
   {
      mDueServ = dueServ;
   }

   /**
    * @return the forSale
    */
   public String getForSale()
   {
      return mForSale;
   }

   /**
    * @param forSale the forSale to set
    */
   public void setForSale(String forSale)
   {
      mForSale = forSale;
   }

   /**
    * @return the forSalePhone
    */
   public String getForSalePhone()
   {
      return mForSalePhone;
   }

   /**
    * @param forSalePhone the forSalePhone to set
    */
   public void setForSalePhone(String forSalePhone)
   {
      mForSalePhone = forSalePhone;
   }

   /**
    * @return the invoiceDate
    */
   public Date getInvoiceDate()
   {
      return mInvoiceDate;
   }

   /**
    * @param invoiceDate the invoiceDate to set
    */
   public void setInvoiceDate(Date invoiceDate)
   {
      mInvoiceDate = invoiceDate;
   }

   /**
    * @return the invoiceNo
    */
   public String getInvoiceNo()
   {
      return mInvoiceNo;
   }

   /**
    * @param invoiceNo the invoiceNo to set
    */
   public void setInvoiceNo(String invoiceNo)
   {
      mInvoiceNo = invoiceNo;
   }

   /**
    * @return the isChanged
    */
   public boolean isChanged()
   {
      return mIsChanged;
   }

   /**
    * @param isChanged the isChanged to set
    */
   public void setChanged(boolean isChanged)
   {
      mIsChanged = isChanged;
   }

   /**
    * @return the isLate
    */
   public boolean isLate()
   {
      return mIsLate;
   }

   /**
    * @param isLate the isLate to set
    */
   public void setLate(boolean isLate)
   {
      mIsLate = isLate;
   }

   /**
    * @return the isNew
    */
   public boolean isNew()
   {
      return mIsNew;
   }

   /**
    * @param isNew the isNew to set
    */
   public void setNew(boolean isNew)
   {
      mIsNew = isNew;
   }

   /**
    * @return the isRural
    */
   public boolean isRural()
   {
      return mIsRural;
   }

   /**
    * @param isRural the isRural to set
    */
   public void setRural(boolean isRural)
   {
      mIsRural = isRural;
   }

   /**
    * @return the loanNo
    */
   public String getLoanNo()
   {
      return mLoanNo;
   }

   /**
    * @param loanNo the loanNo to set
    */
   public void setLoanNo(String loanNo)
   {
      mLoanNo = loanNo;
   }

   /**
    * @return the mortgager
    */
   public String getMortgager()
   {
      return mMortgager;
   }

   /**
    * @param mortgager the mortgager to set
    */
   public void setMortgager(String mortgager)
   {
      mMortgager = mortgager;
   }

   /**
    * @return the mtgComp
    */
   public String getMtgComp()
   {
      return mMtgComp;
   }

   /**
    * @param mtgComp the mtgComp to set
    */
   public void setMtgComp(String mtgComp)
   {
      mMtgComp = mtgComp;
   }

   /**
    * @return the occupancy
    */
   public String getOccupancy()
   {
      return mOccupancy;
   }

   /**
    * @param occupancy the occupancy to set
    */
   public void setOccupancy(String occupancy)
   {
      mOccupancy = occupancy;
   }

   /**
    * @return the personalContact
    */
   public String getPersonalContact()
   {
      return mPersonalContact;
   }

   /**
    * @param personalContact the personalContact to set
    */
   public void setPersonalContact(String personalContact)
   {
      mPersonalContact = personalContact;
   }

   /**
    * @return the photosYN
    */
   public String getPhotosYN()
   {
      return mPhotosYN;
   }

   /**
    * @param photosYN the photosYN to set
    */
   public void setPhotosYN(String photosYN)
   {
      mPhotosYN = photosYN;
   }

   /**
    * @return the propNo
    */
   public String getPropNo()
   {
      return mPropNo;
   }

   /**
    * @param propNo the propNo to set
    */
   public void setPropNo(String propNo)
   {
      mPropNo = propNo;
   }

   /**
    * @return the propertyAddress
    */
   public String getPropertyAddress()
   {
      return mPropertyAddress;
   }

   /**
    * @param propertyAddress the propertyAddress to set
    */
   public void setPropertyAddress(String propertyAddress)
   {
      mPropertyAddress = propertyAddress;
   }

   /**
    * @return the propertyDescription
    */
   public String getPropertyDescription()
   {
      return mPropertyDescription;
   }

   /**
    * @param propertyDescription the propertyDescription to set
    */
   public void setPropertyDescription(String propertyDescription)
   {
      mPropertyDescription = propertyDescription;
   }

   /**
    * @return the recdIn
    */
   public Date getRecdIn()
   {
      return mRecdIn;
   }

   /**
    * @param recdIn the recdIn to set
    */
   public void setRecdIn(Date recdIn)
   {
      mRecdIn = recdIn;
   }

   /**
    * @return the rep
    */
   public String getRep()
   {
      return mRep;
   }

   /**
    * @param rep the rep to set
    */
   public void setRep(String rep)
   {
      mRep = rep;
   }

   /**
    * @return the repCompDate
    */
   public Date getRepCompDate()
   {
      return mRepCompDate;
   }

   /**
    * @param repCompDate the repCompDate to set
    */
   public void setRepCompDate(Date repCompDate)
   {
      mRepCompDate = repCompDate;
   }

   /**
    * @return the reportType
    */
   public String getReportType()
   {
      return mReportType;
   }

   /**
    * @param reportType the reportType to set
    */
   public void setReportType(String reportType)
   {
      mReportType = reportType;
   }

   /**
    * @return the sGInspectionCode
    */
   public String getSGInspectionCode()
   {
      return mSGInspectionCode;
   }

   /**
    * @param inspectionCode the sGInspectionCode to set
    */
   public void setSGInspectionCode(String inspectionCode)
   {
      mSGInspectionCode = inspectionCode;
   }

   /**
    * @return the sGInspectionTitle
    */
   public String getSGInspectionTitle()
   {
      return mSGInspectionTitle;
   }

   /**
    * @param inspectionTitle the sGInspectionTitle to set
    */
   public void setSGInspectionTitle(String inspectionTitle)
   {
      mSGInspectionTitle = inspectionTitle;
   }

   /**
    * @return the sGInstructions
    */
   public String getSGInstructions()
   {
      return mSGInstructions;
   }

   /**
    * @param instructions the sGInstructions to set
    */
   public void setSGInstructions(String instructions)
   {
      mSGInstructions = instructions;
   }

   /**
    * @return the serviceCompany
    */
   public String getServiceCompany()
   {
      return mServiceCompany;
   }

   /**
    * @param serviceCompany the serviceCompany to set
    */
   public void setServiceCompany(String serviceCompany)
   {
      mServiceCompany = serviceCompany;
   }

   /**
    * @return the st
    */
   public String getSt()
   {
      return mSt;
   }

   /**
    * @param st the st to set
    */
   public void setSt(String st)
   {
      mSt = st;
   }

   /**
    * @return the trackingNo
    */
   public int getTrackingNo()
   {
      return mTrackingNo;
   }

   /**
    * @param trackingNo the trackingNo to set
    */
   public void setTrackingNo(int trackingNo)
   {
      mTrackingNo = trackingNo;
   }

   /**
    * @return the value
    */
   public int getValue()
   {
      return mValue;
   }

   /**
    * @param value the value to set
    */
   public void setValue(int value)
   {
      mValue = value;
   }

   /**
    * @return the wages
    */
   public float getWages()
   {
      return mWages;
   }

   /**
    * @param wages the wages to set
    */
   public void setWages(float wages)
   {
      mWages = wages;
   }

   /**
    * @return the zipCode
    */
   public String getZipCode()
   {
      return mZipCode;
   }

   /**
    * @param zipCode the zipCode to set
    */
   public void setZipCode(String zipCode)
   {
      mZipCode = zipCode;
   }

   public boolean getIsChanged()
   {
      return false;
   }

   public boolean getIsLate()
   {
      return false;
   }

   public boolean getIsNew()
   {
      return false;
   }

   public boolean getIsRural()
   {
      return false;
   }

   public void setIsChanged(boolean value)
   {
   }

   public void setIsLate(String isLate)
   {
   }

   public void setIsNew(boolean value)
   {
   }

   public void setTrackingNumber(int value)
   {
   }

}
