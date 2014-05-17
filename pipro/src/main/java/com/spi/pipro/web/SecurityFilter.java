/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.pipro.web;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserServiceFactory;

public class SecurityFilter implements Filter
{
   private static Logger _log = Logger.getLogger(SecurityFilter.class.getName());
   
   @Override
   public void init(FilterConfig config) throws ServletException
   {
   }

   @Override
   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
   {
      if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse)
      {
         HttpServletRequest hreq = (HttpServletRequest)req;
         HttpServletResponse hresp = (HttpServletResponse)resp;
         Principal user = hreq.getUserPrincipal();
         if (_log.isLoggable(Level.FINE))
         {
            _log.log(Level.FINE, "Filter request uri: " + hreq.getRequestURI());
            _log.log(Level.FINE, "Filter request principal: " + hreq.getUserPrincipal());
         }

         if (hreq.getRequestURI().toLowerCase().contains("login"))
         {
            if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "On login page, continue the chain.");
            chain.doFilter(req, resp);
         }
         else if (user == null)
         {
            String loginUrl = UserServiceFactory.getUserService().createLoginURL(hreq.getRequestURI());
            if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "NOT logged in, go to login page: " + loginUrl);
            hresp.sendRedirect(loginUrl);
         }
         else
         {
            if (_log.isLoggable(Level.FINE)) _log.log(Level.FINE, "Logged in, continue the chain.");
            chain.doFilter(req, resp);
         }
      }
      else
      {
         throw new ServletException("Unable to determine user from request");
      }
   }

   @Override
   public void destroy()
   {
   }
}
