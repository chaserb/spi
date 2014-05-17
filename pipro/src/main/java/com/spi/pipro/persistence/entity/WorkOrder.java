/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WorkOrder extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;
   
   @Persistent
   private WorkOrderState _currentState = WorkOrderState.New;
   
   @Persistent
   private Property _property;
   
   @Persistent
   private ServiceCompanyAccount _serviceCompany;
   
   @Persistent
   private String _clientSequenceNumber;
   
   @Persistent
   private String _instructions;
   
   @Persistent
   private Date _dateWindowStart;
   
   @Persistent
   private Date _dateReceived;
   
   @Persistent
   private Date _dateDispatched;
   
   @Persistent
   private Date _dateCompleted;
   
   @Persistent
   private Date _dateSubmitted;
   
   @Persistent
   private Date _dateDue;
   
   @Persistent
   private String _comments;

   public Key getKey()
   {
      return _key;
   }

   public void setKey(Key key)
   {
      _key = key;
   }

   /**
    * @return the currentState
    */
   public WorkOrderState getCurrentState()
   {
      return _currentState;
   }

   /**
    * @param currentState the currentState to set
    */
   public void setCurrentState(WorkOrderState currentState)
   {
      _currentState = currentState;
   }

   /**
    * @return the property
    */
   public Property getProperty()
   {
      return _property;
   }

   /**
    * @param property the property to set
    */
   public void setProperty(Property property)
   {
      _property = property;
   }

   /**
    * @return the serviceCompany
    */
   public ServiceCompanyAccount getServiceCompany()
   {
      return _serviceCompany;
   }

   /**
    * @param serviceCompany the serviceCompany to set
    */
   public void setServiceCompany(ServiceCompanyAccount serviceCompany)
   {
      _serviceCompany = serviceCompany;
   }

   /**
    * @return the clientSequenceNumber
    */
   public String getClientSequenceNumber()
   {
      return _clientSequenceNumber;
   }

   /**
    * @param clientSequenceNumber the clientSequenceNumber to set
    */
   public void setClientSequenceNumber(String clientSequenceNumber)
   {
      _clientSequenceNumber = clientSequenceNumber;
   }

   /**
    * @return the instructions
    */
   public String getInstructions()
   {
      return _instructions;
   }

   /**
    * @param instructions the instructions to set
    */
   public void setInstructions(String instructions)
   {
      _instructions = instructions;
   }

   /**
    * @return the dateWindowStart
    */
   public Date getDateWindowStart()
   {
      return _dateWindowStart;
   }

   /**
    * @param dateWindowStart the dateWindowStart to set
    */
   public void setDateWindowStart(Date dateWindowStart)
   {
      _dateWindowStart = dateWindowStart; 
   }

   /**
    * @return the dateReceived
    */
   public Date getDateReceived()
   {
      return _dateReceived;
   }

   /**
    * @param dateReceived the dateReceived to set
    */
   public void setDateReceived(Date dateReceived)
   {
      _dateReceived = dateReceived;
   }

   /**
    * @return the dateDispatched
    */
   public Date getDateDispatched()
   {
      return _dateDispatched;
   }

   /**
    * @param dateDispatched the dateDispatched to set
    */
   public void setDateDispatched(Date dateDispatched)
   {
      _dateDispatched = dateDispatched;
   }

   /**
    * @return the dateCompleted
    */
   public Date getDateCompleted()
   {
      return _dateCompleted;
   }

   /**
    * @param dateCompleted the dateCompleted to set
    */
   public void setDateCompleted(Date dateCompleted)
   {
      _dateCompleted = dateCompleted;
   }

   /**
    * @return the dateSubmitted
    */
   public Date getDateSubmitted()
   {
      return _dateSubmitted;
   }

   /**
    * @param dateSubmitted the dateSubmitted to set
    */
   public void setDateSubmitted(Date dateSubmitted)
   {
      _dateSubmitted = dateSubmitted;
   }

   /**
    * @return the dateDue
    */
   public Date getDateDue()
   {
      return _dateDue;
   }

   /**
    * @param dateDue the dateDue to set
    */
   public void setDateDue(Date dateDue)
   {
      _dateDue = dateDue;
   }

   /**
    * @return the comments
    */
   public String getComments()
   {
      return _comments;
   }

   /**
    * @param comments the comments to set
    */
   public void setComments(String comments)
   {
      _comments = comments;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_currentState.toString());
      out.writeObject(_property);
      out.writeObject(_serviceCompany);
      out.writeObject(_clientSequenceNumber);
      out.writeObject(_instructions);
      out.writeObject(_dateWindowStart);
      out.writeObject(_dateReceived);
      out.writeObject(_dateDispatched);
      out.writeObject(_dateCompleted);
      out.writeObject(_dateSubmitted);
      out.writeObject(_dateDue);
      out.writeObject(_comments);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _currentState = WorkOrderState.valueOf((String)in.readObject());
      _property = (Property)in.readObject();
      _serviceCompany = (ServiceCompanyAccount)in.readObject();
      _clientSequenceNumber = (String)in.readObject();
      _instructions = (String)in.readObject();
      _dateWindowStart = (Date)in.readObject();
      _dateReceived = (Date)in.readObject();
      _dateDispatched = (Date)in.readObject();
      _dateCompleted = (Date)in.readObject();
      _dateSubmitted = (Date)in.readObject();
      _dateDue = (Date)in.readObject();
      _comments = (String)in.readObject();
   }
}