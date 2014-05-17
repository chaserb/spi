/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Property extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;
   
   @Persistent
   private Address _address;
   
   @Persistent
   private String _mortgagerName;
   
   @Persistent
   private String _loanNumber;
   
   @Persistent
   private String _loanType;
   
   @Persistent
   private String _client;

   public Key getKey()
   {
      return _key;
   }

   public void setKey(Key key)
   {
      _key = key;
   }

   /**
    * @return the address
    */
   public Address getAddress()
   {
      return _address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(Address address)
   {
      _address = address;
   }

   /**
    * @return the mortgagerName
    */
   public String getMortgagerName()
   {
      return _mortgagerName;
   }

   /**
    * @param mortgagerName the mortgagerName to set
    */
   public void setMortgagerName(String mortgagerName)
   {
      _mortgagerName = mortgagerName;
   }

   /**
    * @return the loanNumber
    */
   public String getLoanNumber()
   {
      return _loanNumber;
   }

   /**
    * @param loanNumber the loanNumber to set
    */
   public void setLoanNumber(String loanNumber)
   {
      _loanNumber = loanNumber;
   }

   /**
    * @return the loanType
    */
   public String getLoanType()
   {
      return _loanType;
   }

   /**
    * @param loanType the loanType to set
    */
   public void setLoanType(String loanType)
   {
      _loanType = loanType;
   }

   /**
    * @return the client
    */
   public String getClient()
   {
      return _client;
   }

   /**
    * @param client the client to set
    */
   public void setClient(String client)
   {
      _client = client;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_address);
      out.writeObject(_mortgagerName);
      out.writeObject(_loanNumber);
      out.writeObject(_loanType);
      out.writeObject(_client);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _address = (Address)in.readObject();
      _mortgagerName = (String)in.readObject();
      _loanNumber = (String)in.readObject();
      _loanType = (String)in.readObject();
      _client = (String)in.readObject();
   }
}