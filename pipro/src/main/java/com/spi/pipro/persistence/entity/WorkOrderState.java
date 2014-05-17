/*
 * Copyright (c) 2010 Chase Barrett.  All rights reserved.
 */

package com.spi.pipro.persistence.entity;

public enum WorkOrderState
{
   Pending,

   New,

   Dispatched,

   Entered,

   Submitted,

   PendingCancelled,

   Cancelled,

   Reassigned //TODO ???
}
