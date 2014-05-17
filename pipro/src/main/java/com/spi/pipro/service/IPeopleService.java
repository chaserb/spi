/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service;

import com.spi.pipro.persistence.entity.Person;

public interface IPeopleService extends IDurableService<Person, Long>
{
   /**
    * Retrieve a given person by username.
    * 
    * @param username
    * @return
    */
   public Person getPersonByUsername(String username);
   
   /**
    * Sets or resets the password for the given user. This is a privileged 
    * operation for people with the administer people privilege, which is why
    * you do not have to provide the previous password.  To update your own 
    * password, use the security service.
    * 
    * @param key the key identifying the person to update.
    * @param newPassword the plaintext of the new password.
    */
   public void setPassword(Long key, String newPassword);
}
