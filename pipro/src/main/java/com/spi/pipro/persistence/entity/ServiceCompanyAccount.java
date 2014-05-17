/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * A class representing an individual account with a particular service company.
 * 
 * @author Chase.Barrett
 * @date Jun 3, 2010
 */
@PersistenceCapable
public class ServiceCompanyAccount extends Entity
{
   @PrimaryKey
   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
   private Key _key;

   @Persistent
   private String _name;

   @Persistent
   private String _companyId;

   @NotPersistent
   private String _companyName;

   @Persistent
   private boolean _updatesEnabled;

   @Persistent
   private UpdatePeriod _updatePeriod;

   @Persistent
   private int _updateHour;

   @Persistent
   private int _updateMinute;

   @Persistent
   private boolean _updateInProgress;

   @Persistent
   private String _userName;

   @Persistent
   private String _password;

   @Persistent
   private Date _nextUpdate;

   @Persistent(mappedBy = "_account")
   @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "_time desc"))
   private List<UpdateDetails> _updates;

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

   /**
    * Returns the name of this account, which will distinguish it from other
    * accounts, perhaps with the same service company.
    * 
    * @return the name of this service company account.
    */
   public String getName()
   {
      return _name;
   }

   /**
    * Sets the name of this account, which will distinguish it from other
    * accounts, perhaps with the same service company.
    * 
    * = * @param name the name of this service company account.
    */
   public void setName(String name)
   {
      _name = name;
   }

   /**
    * Return the ID of the service company for this account. Service company
    * names may change, but the ID will remain the same.
    * 
    * @return the ID for the company for this account.
    */
   public String getCompanyId()
   {
      return _companyId;
   }

   /**
    * Set the ID of the service company for this account. Service company names
    * may change, but the ID will remain the same.
    * 
    * @return the ID for the company for this account.
    */
   public void setCompanyId(String companyId)
   {
      _companyId = companyId;
   }

   /**
    * Get the displayable name of the service company for this account.
    * 
    * @return the name of the service company for this account.
    */
   public String getCompanyName()
   {
      return _companyName;
   }

   /**
    * Set the displayable name of the service company for this account.
    * 
    * @param companyName the name of the service company for this account.
    */
   public void setCompanyName(String companyName)
   {
      this._companyName = companyName;
   }

   /**
    * Return whether or not periodic, automatic updates are enabled for this
    * account.
    * 
    * @return the periodic update enabled flag.
    */
   public boolean isUpdatesEnabled()
   {
      return _updatesEnabled;
   }

   /**
    * Sets whether or not periodic, automatic updates are enabled for this
    * account.
    * 
    * @param enabled the periodic update enabled flag.
    */
   public void setUpdatesEnabled(boolean enabled)
   {
      _updatesEnabled = enabled;
   }

   /**
    * @return the updatePeriod
    */
   public UpdatePeriod getUpdatePeriod()
   {
      return _updatePeriod;
   }

   /**
    * @param updatePeriod the updatePeriod to set
    */
   public void setUpdatePeriod(UpdatePeriod updatePeriod)
   {
      _updatePeriod = updatePeriod;
   }

   /**
    * @return the updateHour
    */
   public int getUpdateHour()
   {
      return _updateHour;
   }

   /**
    * @param updateHour the updateHour to set
    */
   public void setUpdateHour(int updateHour)
   {
      _updateHour = updateHour;
   }

   /**
    * @return the updateMinute
    */
   public int getUpdateMinute()
   {
      return _updateMinute;
   }

   /**
    * @param updateMinute the updateMinute to set
    */
   public void setUpdateMinute(int updateMinute)
   {
      _updateMinute = updateMinute;
   }

   /**
    * @return the updateInProgress
    */
   public boolean isUpdateInProgress()
   {
      return _updateInProgress;
   }

   /**
    * @param updateInProgress the updateInProgress to set
    */
   public void setUpdateInProgress(boolean updateInProgress)
   {
      _updateInProgress = updateInProgress;
   }

   /**
    * Returns the user name to use when performing an update.
    * 
    * @return the account user name.
    */
   public String getUserName()
   {
      return _userName;
   }

   /**
    * Sets the user name to use when performing an update.
    * 
    * @param userName the account user name.
    */
   public void setUserName(String userName)
   {
      _userName = userName;
   }

   /**
    * Returns the password to use when performing an update.
    * 
    * @return the account password.
    */
   public String getPassword()
   {
      return _password;
   }

   /**
    * Sets the user name to use when performing an update.
    * 
    * @param password the account password.
    */
   public void setPassword(String password)
   {
      _password = password;
   }

   /**
    * Return when the next automatic update will occur
    * 
    * @return when the next automatic update will occur.
    */
   public Date getNextUpdate()
   {
      return _nextUpdate;
   }

   /**
    * Set when the next automatic update will occur
    * 
    * @param when the next automatic update will occur.
    */
   public void setNextUpdate(Date nextUpdate)
   {
      _nextUpdate = nextUpdate;
   }

   /**
    * Get the most recent update for this account
    * 
    * @return the lastUpdate
    */
   public UpdateDetails getLastUpdate()
   {
      return (_updates != null && _updates.size() > 0) ? _updates.get(0) : null;
   }

   /**
    * Set the most recent update for this account, replacing the element already
    * existing in that position.
    * 
    * @param lastUpdate the lastUpdate to set.  If the lastUpdate is null, or if
    *        the updates list does not yet have at least one update, this method
    *        is ignored.
    */
   public void setLastUpdate(UpdateDetails lastUpdate)
   {
      if (lastUpdate != null && _updates != null && _updates.size() > 0)
      {
         _updates.set(0, lastUpdate);
      }
   }
   
   /**
    * Add an update to the update list, making now the most recent (last) 
    * update.
    * 
    * @param update
    */
   public void addUpdate(UpdateDetails update)
   {
      if (_updates == null)
      {
         _updates = new ArrayList<UpdateDetails>();
      }
      _updates.add(0, update);
   }

   /**
    * Get all of the service order updates for this account, sorted in
    * chronological order with the most recent update at element 0.
    * 
    * @return the updates
    */
   public List<UpdateDetails> getUpdates()
   {
      return _updates;
   }

   /**
    * Set all of the service order updates for this account, sorted in
    * chronological order with the most recent update at element 0.
    * 
    * @param updates the priorUpdates to set
    */
   public void setUpdates(List<UpdateDetails> updates)
   {
      _updates = updates;
   }

   /**
    * Returns whether or not this account is active. An inactive account is
    * essentially deleted, but because of incoming entity references, cannot
    * actually be deleted from the data source.
    * 
    * @return whether or not this account is active.
    */
   public boolean isActive()
   {
      return _active;
   }

   /**
    * Sets whether or not this account is active. An inactive account is
    * essentially deleted, but because of incoming entity references, cannot
    * actually be deleted from the data source.
    * 
    * @param active whether or not this account is active.
    */
   public void setActive(boolean active)
   {
      _active = active;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(_name);
      out.writeObject(_companyId);
      out.writeObject(_companyName);
      out.writeBoolean(_updatesEnabled);
      out.writeObject(_updatePeriod);
      out.writeInt(_updateHour);
      out.writeInt(_updateMinute);
      out.writeBoolean(_updateInProgress);
      out.writeObject(_userName);
      out.writeObject(_password);
      out.writeObject(_nextUpdate);
      out.writeObject(getLastUpdate());
      out.writeBoolean(_active);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      _name = (String)in.readObject();
      _companyId = (String)in.readObject();
      _companyName = (String)in.readObject();
      _updatesEnabled = in.readBoolean();
      _updatePeriod = (UpdatePeriod)in.readObject();
      _updateHour = in.readInt();
      _updateMinute = in.readInt();
      _updateInProgress = in.readBoolean();
      _userName = (String)in.readObject();
      _password = (String)in.readObject();
      _nextUpdate = (Date)in.readObject();
      setLastUpdate((UpdateDetails)in.readObject()); 
      _active = in.readBoolean();
   }
}