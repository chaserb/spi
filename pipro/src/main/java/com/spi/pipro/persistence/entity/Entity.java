/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.spi.pipro.util.EqualityUtils;
import com.spi.pipro.util.LongUtil;

/**
 * Base class for persistent entities in the pipro data store.
 */
public abstract class Entity extends Base
{
   /**
    * Return this entity's unique, identifying key.
    * 
    * @return this entity's key.
    */
   public abstract Key getKey();

   /**
    * Set the unique, identifying key for this entity.
    * 
    * @param key this entity's key.
    */
   public abstract void setKey(Key key);

   @Override
   public int hashCode()
   {
      Key key = getKey();
      return (key == null) ? 0 : key.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Entity)
      {
         return EqualityUtils.equals(((Entity)obj).getKey(), getKey());
      }
      return false;
   }
   
   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeObject(Long.toHexString(getKey().getId()));
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      long key = LongUtil.readLong((String)in.readObject());
      setKey((key > 0) ? KeyFactory.createKey(getKind(), key) : null);
   }
}
