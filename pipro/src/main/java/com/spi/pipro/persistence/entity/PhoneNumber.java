/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a phone number plus the number's type.
 */
@PersistenceCapable
@EmbeddedOnly
public class PhoneNumber extends Base
{
   @Persistent
   private String _number;
   
   @Persistent
   private PhoneType _type;
   
   public String getNumber()
   {
      return _number;
   }

   public void setNumber(String number)
   {
      _number = number;
   }

   public PhoneType getType()
   {
      return _type;
   }

   public void setType(PhoneType type)
   {
      _type = type;
   }

   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeObject(_number);
      out.writeObject(_type);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      _number = (String)in.readObject();
      _type = (PhoneType)in.readObject();
   }
}
