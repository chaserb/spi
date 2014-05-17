/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.company;

import java.util.List;

import com.spi.pipro.persistence.entity.WorkOrder;

public interface IServiceCompany
{
   public String getId();
   
   public String getName();
   
   public void setName(String name);
   
   public List<WorkOrder> getNewOrders(List<String> existingSeqNos);
   
   public List<WorkOrder> getNewCancelledOrders(List<String> existingCancelledSeqNos);
   
   public void submitWorkOrder(WorkOrder order);
}
