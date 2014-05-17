/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import com.google.appengine.api.datastore.Key;
import com.spi.pipro.util.HashUtil;

@PersistenceCapable
@FetchGroup(name = "detail", members = { 
      @Persistent(name = "_address"),
      @Persistent(name = "_email"),
      @Persistent(name = "_phoneNumbers")}) 
public class Person extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;

   @Persistent
   @Unique
   private String _userName;
   
   @Persistent
   private String _password;

   @Persistent
   private String _firstName;

   @Persistent
   private String _lastName;

   @Persistent
   @Embedded
   private Address _address;

   @Persistent
   @Embedded
   private EmailAddress _email;
   
   @Persistent
   private List<PhoneNumber> _phoneNumbers;

   @Persistent
   private boolean _active = true;

   public Key getKey()
   {
      return _key;
   }

   public void setKey(Key key)
   {
      _key = key;
   }

   public String getUserName()
   {
      return _userName;
   }

   public void setUserName(String userName)
   {
      _userName = userName;
   }

   public String getPassword()
   {
      return _password;
   }

   public void setPassword(String password)
   {
      _password = password;
   }

   public void setPlainTextPassword(String plainTextPassword)
   {
      _password = (plainTextPassword == null) ? null : HashUtil.hash(plainTextPassword);
   }

   public String getFirstName()
   {
      return _firstName;
   }

   public void setFirstName(String firstName)
   {
      _firstName = firstName;
   }

   public String getLastName()
   {
      return _lastName;
   }

   public void setLastName(String lastName)
   {
      _lastName = lastName;
   }

   public Address getAddress()
   {
      return _address;
   }

   public void setAddress(Address address)
   {
      if (address == null)
      {
         _address = null;
      }
      else if (address instanceof Address)
      {
         _address = (Address)address;
      }
      else
      {
         _address = new Address();
         _address.setAddress1(address.getAddress1());
         _address.setAddress2(address.getAddress2());
         _address.setCity(address.getCity());
         _address.setState(address.getState());
         _address.setZip(address.getZip());
      }
   }

   public EmailAddress getEmail()
   {
      return _email;
   }

   /**
    * Set the email.
    */
   public void setEmail(EmailAddress email)
   {
      if (email == null)
      {
         _email = null;
      }
      else if (email instanceof EmailAddress)
      {
         _email = (EmailAddress)email;
      }
      else
      {
         _email = new EmailAddress();
         _email.setEmail(email.getEmail());
      }
   }

   public List<PhoneNumber> getPhoneNumbers()
   {
      return _phoneNumbers;
   }

   public void setPhoneNumbers(List<PhoneNumber> numbers)
   {
      _phoneNumbers = numbers;
   }

   public boolean isActive()
   {
      return _active;
   }

   public void setActive(boolean active)
   {
      _active = active;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_userName);
      out.writeObject(_password);
      out.writeObject(_firstName);
      out.writeObject(_lastName);
      out.writeObject(_address);
      out.writeObject(_email);
      out.writeObject(_phoneNumbers);
      out.writeBoolean(_active);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _userName = (String)in.readObject();
      _password = (String)in.readObject();
      _firstName = (String)in.readObject();
      _lastName = (String)in.readObject();
      _address = (Address)in.readObject();
      _email = (EmailAddress)in.readObject();
      _phoneNumbers = (List<PhoneNumber>)in.readObject();
      _active = in.readBoolean();
   }
}
