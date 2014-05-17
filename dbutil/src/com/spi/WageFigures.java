/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.util.Properties;

import com.spi.util.Debug;

public class WageFigures
{
   protected float mBaseWage;
   protected float mPhotoWageFactor;
   protected float mRuralWageFactor;

   public WageFigures(Properties env, InputProvider provider)
   {
      mBaseWage = 2.5f;
      mPhotoWageFactor = 0.5f;
      mRuralWageFactor = 1.5f;
      String baseWageProp = "com.spi." + provider.getName() + ".baseWage";
      String photoWageProp = "com.spi." + provider.getName() + ".photoWageFactor";
      String ruralWageProp = "com.spi." + provider.getName() + ".ruralWageFactor";
      if (env.getProperty(baseWageProp) != null)
      {
         try
         {
            mBaseWage = Float.parseFloat(env.getProperty(baseWageProp));
         }
         catch (Exception e)
         {
            Debug.debugException("Could not read the base wage property: ", e);
         }
      }
      if (env.getProperty(photoWageProp) != null)
      {
         try
         {
            mPhotoWageFactor = Float.parseFloat(env.getProperty(photoWageProp));
         }
         catch (Exception e)
         {
            Debug.debugException("Could not read the photo wage factor property: ", e);
         }
      }
      if (env.getProperty(ruralWageProp) != null)
      {
         try
         {
            mRuralWageFactor = Float.parseFloat(env.getProperty(ruralWageProp));
         }
         catch (Exception e)
         {
            Debug.debugException("Could not read the rural wage factor property: ", e);
         }
      }
   }

   public float getBaseWage()
   {
      return mBaseWage;
   }

   public float getPhotoWageFactor()
   {
      return mPhotoWageFactor;
   }

   public float getRuralWageFactor()
   {
      return mRuralWageFactor;
   }

}
