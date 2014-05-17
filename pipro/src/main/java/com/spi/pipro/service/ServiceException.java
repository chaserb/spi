/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.service;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException
{
   public ServiceException()
   {
   }

   public ServiceException(String message)
   {
      super(message);
   }

   public ServiceException(Throwable cause)
   {
      super(cause);
   }

   public ServiceException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
