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

/**
 * Details associated with an update to a service company account, identifying
 * when the update occurred, and how many orders were received.
 *
 * @author Chase.Barrett
 * @date Jun 11, 2010
 */
@PersistenceCapable
public class UpdateDetails extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;

   @Persistent
   private Date _time;
   
   @Persistent
   private String _status;
   
   @Persistent
   private List<Long> _newOrderIds;
   
   @Persistent
   private List<Long> _cancelledOrderIds;
   
   @Persistent
   private ServiceCompanyAccount _account;

   public Key getKey()
   {
      return _key;
   }

   public void setKey(Key key)
   {
      _key = key;
   }

   /**
    * Return when the last update occurred, automatic or not.
    * 
    * @return when the last update occurred
    */
   public Date getTime()
   {
      return _time;
   }

   /**
    * Set when the last update occurred, automatic or not.
    * 
    * @param lastUpdate when the last update occurred.
    */
   public void setTime(Date time)
   {
      _time = time;
   }

   /**
    * Return a message which summarizes the status of the last update, automatic
    * or not.
    * 
    * @return the last update status, basically, whether it was successful or
    *         not.
    */
   public String getStatus()
   {
      return _status;
   }

   /**
    * Set a message which summarizes the status of the last update, automatic or
    * not.
    * 
    * @param lastUpdateStatus the last update status, basically, whether it was
    *        successful or not.
    */
   public void setStatus(String status)
   {
      _status = status;
   }

   /**
    * Return the number of new orders received in the last update.
    * 
    * @return the number of new orders.
    */
   public List<Long> getNewOrderIds()
   {
      return _newOrderIds;
   }

   /**
    * Set the number of new orders received in the last update.
    * 
    * @return the number of new orders.
    */
   public void setNewOrderIds(List<Long> newOrderIds)
   {
      _newOrderIds = newOrderIds;
   }

   /**
    * @return the cancelledOrderIds
    */
   public List<Long> getCancelledOrderIds()
   {
      return _cancelledOrderIds;
   }

   /**
    * @param cancelledOrderIds the cancelledOrderIds to set
    */
   public void setCancelledOrderIds(List<Long> cancelledOrderIds)
   {
      _cancelledOrderIds = cancelledOrderIds;
   }

   /**
    * Return the account that this update is for.
    * 
    * @return the account
    */
   public ServiceCompanyAccount getAccount()
   {
      return _account;
   }

   /**
    * Set the account that this update is for.
    * 
    * @param account the account to set
    */
   public void setAccount(ServiceCompanyAccount account)
   {
      _account = account;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_time);
      out.writeObject(_status);
      out.writeObject(_newOrderIds);
      out.writeObject(_cancelledOrderIds);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _time = (Date)in.readObject();
      _status = (String)in.readObject();
      _newOrderIds = (List<Long>)in.readObject();
      _cancelledOrderIds = (List<Long>)in.readObject();
   }
}
