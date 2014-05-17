/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.Externalizable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Base class for JDO persistent objects in the pipro data store.
 */
public abstract class Base implements Externalizable
{
   /**
    * Return this entity's kind, being the name that identifies its type in the
    * data store.
    * 
    * @return the
    */
   public String getKind()
   {
      return getClass().getSimpleName();
   }

   /**
    * Return a string that represents all of this entity's fields and values.
    */
   public String toString()
   {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
   }
}