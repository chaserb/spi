/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class PayStatement extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;
   
   @Persistent
   private Person _payee;
   
   @Persistent
   private Date _payPeriodStart;
   
   @Persistent
   private Date _payPeriodEnd;
   
   @Persistent
   private Date _statementDate;
   
   @Persistent
   private List<WorkOrder> _workOrders;

   @Persistent
   private float _grossPay;

   @Override
   public Key getKey()
   {
      return _key;
   }

   @Override
   public void setKey(Key key)
   {
      _key = key;
   }

   /**
    * The key of the pay statement's payee.
    * 
    * @return the key of the payee
    */
   public Person getPayee()
   {
      return _payee;
   }

   /**
    * Set the key of the pay statement's payee.
    * 
    * @param payee the key of the payee.
    */
   public void setPayee(Person payee)
   {
      _payee = payee;
   }

   /**
    * @return the payPeriodStart
    */
   public Date getPayPeriodStart()
   {
      return _payPeriodStart;
   }

   /**
    * @param payPeriodStart the payPeriodStart to set
    */
   public void setPayPeriodStart(Date payPeriodStart)
   {
      _payPeriodStart = payPeriodStart;
   }

   /**
    * @return the payPeriodEnd
    */
   public Date getPayPeriodEnd()
   {
      return _payPeriodEnd;
   }

   /**
    * @param payPeriodEnd the payPeriodEnd to set
    */
   public void setPayPeriodEnd(Date payPeriodEnd)
   {
      _payPeriodEnd = payPeriodEnd;
   }

   /**
    * @return the statementDate
    */
   public Date getStatementDate()
   {
      return _statementDate;
   }

   /**
    * @param statementDate the statementDate to set
    */
   public void setStatementDate(Date statementDate)
   {
      _statementDate = statementDate;
   }

   /**
    * @return the workOrders
    */
   public List<WorkOrder> getWorkOrders()
   {
      return _workOrders;
   }

   /**
    * @param workOrders the workOrders to set
    */
   public void setWorkOrders(List<WorkOrder> workOrders)
   {
      _workOrders = workOrders;
   }

   /**
    * @return the grossPay
    */
   public float getGrossPay()
   {
      return _grossPay;
   }

   /**
    * @param grossPay the grossPay to set
    */
   public void setGrossPay(float grossPay)
   {
      _grossPay = grossPay;
   }
   
   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_payee);
      out.writeObject(_payPeriodStart);
      out.writeObject(_payPeriodEnd);
      out.writeObject(_statementDate);
      out.writeObject(_workOrders);
      out.writeFloat(_grossPay);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _payee = (Person)in.readObject();
      _payPeriodStart = (Date)in.readObject();
      _payPeriodEnd = (Date)in.readObject();
      _statementDate = (Date)in.readObject();
      _workOrders = (List<WorkOrder>)in.readObject();
      _grossPay = in.readFloat();
   }
}