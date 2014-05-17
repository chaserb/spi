/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

/**
 * @author Chase.Barrett
 *
 */
public abstract class AbstractIncomingOrder implements Order
{
   private boolean mIsLate;
   private boolean mNew;
   private boolean mChanged;

   public AbstractIncomingOrder()
   {
      mNew = true;
      setTrackingNumber(-1);
   }

   public String getRep()
   {
      return SPIAccess.getInstance().getRepForZip(getZip());
   }

   public boolean getIsRural()
   {
      return SPIAccess.getInstance().getRuralForZip(getZip());
   }

   public String getSGInspectionCode()
   {
      return null;
   }

   public String getSGInspectionTitle()
   {
      return null;
   }

   public String getSGInstructions()
   {
      return null;
   }

   public boolean getIsLate()
   {
      return mIsLate;
   }

   public boolean getIsNew()
   {
      return mNew;
   }

   public boolean getIsChanged()
   {
      return mChanged;
   }

   public void setIsLate(String isLate)
   {
      mIsLate = (isLate == null) ? false : (isLate.trim().toLowerCase().startsWith("n"));
   }

   public void setIsNew(boolean value)
   {
      mNew = value;
   }

   public void setIsChanged(boolean value)
   {
      mChanged = value;
   }
   
   protected int getZip()
   {
      return Integer.parseInt(getZipCode());
   }

   public boolean equals(Object that)
   {
      if (that instanceof Order)
      {
         return ((Order) that).getTrackingNo() == getTrackingNo();
      }
      return false;
   }

   public int hashCode()
   {
      return getTrackingNo();
   }
}
