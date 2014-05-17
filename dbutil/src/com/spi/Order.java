/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.util.Date;

/**
 * @author Chase Barrett
 */
public interface Order
{
   public int getTrackingNo();

   public String getRep();

   public String getCustWONo();

   public String getServiceCompany();

   public String getLoanNo();

   public String getSGInspectionCode();

   public String getSGInspectionTitle();

   public String getReportType();

   public String getSGInstructions();

   public Date getRecdIn();

   public Date getDueServ();

   public Date getRepCompDate();

   public String getMortgager();

   public String getPropNo();

   public String getPropertyAddress();

   public String getCity();

   public String getSt();

   public String getZipCode();

   public boolean getIsRural();

   public boolean getIsLate();

   public String getPhotosYN();

   public String getPropertyDescription();

   public int getValue();

   public String getOccupancy();

   public String getPersonalContact();

   public String getForSale();

   public String getForSalePhone();

   public String getComments();

   public String getInvoiceNo();

   public Date getInvoiceDate();

   public String getMtgComp();

   public boolean getIsNew();

   public boolean getIsChanged();

   public float getCost();

   public float getWages();

   public void setIsLate(String isLate);

   public void setTrackingNumber(int value);

   public void setIsNew(boolean value);

   public void setIsChanged(boolean value);
}
