/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import com.spi.util.Debug;

/**
 * @author Chase Barrett
 */
public class Statistics
{
   private static String DEBUG_PREFIX = "Statistics.";

   private int mNumIOsTotal;
   private int mNumIOsNew;
   private int mNumIOsUpdates;
   private int mNumIOsUnchanged;
   private int mNumInvoicesTotal;
   private boolean mError = false;

   /* ------------------------------ Accessors ----------------------------- */

   public int getNumIOsTotal()
   {
      return mNumIOsTotal;
   }

   public int getNumIOsNew()
   {
      return mNumIOsNew;
   }

   public int getNumIOsUpdates()
   {
      return mNumIOsUpdates;
   }

   public int getNumIOsUnchanged()
   {
      return mNumIOsUnchanged;
   }

   public int getNumInvoicesTotal()
   {
      return mNumInvoicesTotal;
   }

   public boolean getErrorOccurred()
   {
      return mError;
   }

   /* ------------------------------- Mutators ----------------------------- */

   public void setNumIOsTotal(int value)
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setNumIOsTotal(): new value: " + value);
      mNumIOsTotal = value;
   }

   public void setNumIOsNew(int value)
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setNumIOsNew(): new value: " + value);
      mNumIOsNew = value;
   }

   public void setNumIOsUpdates(int value)
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setNumIOsUpdates(): new value: " + value);
      mNumIOsUpdates = value;
   }

   public void setNumIOsUnchanged(int value)
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setNumIOsUnchanged(): new value: " + value);
      mNumIOsUnchanged = value;
   }

   public void setNumInvoicesTotal(int value)
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setNumInvoicesTotal(): new value: " + value);
      mNumInvoicesTotal = value;
   }

   public void setErrorOccurred()
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "setErrorOccurred(): ERROR!");
      mError = true;
   }
}