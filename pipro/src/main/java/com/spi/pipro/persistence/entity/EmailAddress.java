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
public class EmailAddress extends Base
{
   @Persistent
   private String _email;
   
   public String getEmail()
   {
      return _email;
   }

   public void setEmail(String email)
   {
      _email = email;
   }

   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeObject(_email);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      _email = (String)in.readObject();
   }
}