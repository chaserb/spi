/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.company.lps;

import java.util.List;

import com.spi.pipro.company.IServiceCompany;
import com.spi.pipro.company.ServiceCompanyID;
import com.spi.pipro.persistence.entity.WorkOrder;

public class LPSServiceCompany implements IServiceCompany
{
   private String _name = "LPS";
   
   @Override
   public String getId()
   {
      return ServiceCompanyID.LPS.toString();
   }

   @Override
   public String getName()
   {
      return _name;
   }

   @Override
   public void setName(String name)
   {
      _name = name;
   }

   @Override
   public List<WorkOrder> getNewOrders(List<String> existingSeqNos)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<WorkOrder> getNewCancelledOrders(List<String> existingCancelledSeqNos)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void submitWorkOrder(WorkOrder order)
   {
      // TODO Auto-generated method stub

   }
}
