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

@PersistenceCapable
@EmbeddedOnly
public class Address extends Base
{
   @Persistent
   private String _address1;

   @Persistent
   private String _address2;

   @Persistent
   private String _city;

   @Persistent
   private String _state;

   @Persistent
   private int _zip;

   public String getAddress1()
   {
      return _address1;
   }

   public void setAddress1(String address1)
   {
      _address1 = address1;
   }

   public String getAddress2()
   {
      return _address2;
   }

   public void setAddress2(String address2)
   {
      _address2 = address2;
   }

   public String getCity()
   {
      return _city;
   }

   public void setCity(String city)
   {
      _city = city;
   }

   public String getState()
   {
      return _state;
   }

   public void setState(String state)
   {
      _state = state;
   }

   public int getZip()
   {
      return _zip;
   }

   public void setZip(int zip)
   {
      _zip = zip;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeObject(_address1);
      out.writeObject(_address2);
      out.writeObject(_city);
      out.writeObject(_state);
      out.writeInt(_zip);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      _address1 = (String)in.readObject();
      _address2 = (String)in.readObject();
      _city = (String)in.readObject();
      _state = (String)in.readObject();
      _zip = in.readInt();
   }
}