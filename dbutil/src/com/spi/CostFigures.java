/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.util.HashMap;
import java.util.Map;

public class CostFigures
{
   protected static float cBaseCost;
   protected static float cContactCostFactor;
   protected static float cPhotoCostFactor;
   protected static Map<String, Float> cCostMatrix;

   /* ---------------------------- Constructors ---------------------------- */

   static
   {
      cBaseCost = 5.0f;
      cContactCostFactor = 0.0f;
      cPhotoCostFactor = 1.5f;
      cCostMatrix = new HashMap<String, Float>();
   }

   public static float getBaseCost()
   {
      return cBaseCost;
   }

   public static float getPersonalContactCost()
   {
      return cContactCostFactor;
   }

   public static float getPhotoCost()
   {
      return cPhotoCostFactor;
   }

   public static Map<String, Float> getCostMatrix()
   {
      return cCostMatrix;
   }

   public static void setPersonalContactCost(float value)
   {
      cContactCostFactor = value;
   }

   public static void setPhotoCost(float value)
   {
      cPhotoCostFactor = value;
   }

   public static void setCostMatrix(Map<String, Float> value)
   {
      cCostMatrix = new HashMap<String, Float>(value);
   }
}