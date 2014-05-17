/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service;

import java.io.Serializable;
import java.util.List;

import com.spi.pipro.persistence.entity.Entity;

/**
 * Interface for services whose entities may not always be deleted when
 * requested, due to references from other entities. 
 * 
 * @author Chase.Barrett
 * @date Jun 3, 2010
 */
public interface IDurableService<T extends Entity, ID extends Serializable> extends IService<T, ID>
{
   /**
    * Retrieves all the inactive entities served up by this service.
    * 
    * @return a collection of active entities.
    */
   public List<T> getAllInactive();

   /**
    * Reactivate inactive entities.
    * 
    * @param key
    */
   public void reactivate(List<ID> keys);
}
