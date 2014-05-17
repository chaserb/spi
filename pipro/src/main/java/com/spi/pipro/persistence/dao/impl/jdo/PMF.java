/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.persistence.dao.impl.jdo;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Class for acquiring the persistence manager factory, ensuring we only
 * initialize it once in a thread safe manager. 
 *
 * @author Chase.Barrett
 * @date May 22, 2010
 */
final class PMF
{
   private static Logger _log = Logger.getLogger(PMF.class.getName());
   private static PersistenceManagerFactory _instance;
   private static ReadWriteLock _lock = new ReentrantReadWriteLock();

   /**
    * Just <i>try</i> to call me!
    */
   private PMF()
   {
   }

   /**
    * Retrieve the persistence manager factory singleton.
    * 
    * @return the factory instance.
    */
   static PersistenceManagerFactory get()
   {
      // Ensure we initialize our factory once, because of the expense of such
      // an operation.
      _lock.readLock().lock();
      if (_instance == null)
      {
         // Must release read lock before attempting a write lock
         _lock.readLock().unlock();
         _lock.writeLock().lock();
         
         // Check the instance again because another thread might have acquired
         // write lock and changed state before we did.
         if (_instance == null)
         {
            if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "Creating the Persistence Manager Factory Singleton");
            _instance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
         }
         
         // Downgrade by acquiring read lock before releasing the write lock
         _lock.readLock().lock();
         _lock.writeLock().unlock();
      }
      _lock.readLock().unlock();
      
      return _instance;
   }
}