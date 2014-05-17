/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.spi.util.Debug;

/**
 * @author Chase Barrett
 */
public class Database
{
   private static final String DEBUG_PREFIX = "Database.";

   static
   {
      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      }
      catch (Exception e)
      {
         Debug.debugException("Could not find the ODBC driver: ", e);
      }
   }

   public static Connection getConnection(String url, String user, String pass) throws SQLException
   {
      if (Debug.checkLevel(Debug.HIGH)) Debug.debug(Debug.HIGH, DEBUG_PREFIX + "getConnection(): url: " + url);
      return DriverManager.getConnection(url, user, pass);
   }

   public static void closeResultSet(ResultSet resultSet)
   {
      if (resultSet != null)
      {
         try
         {
            resultSet.close();
         }
         catch (SQLException e)
         {
         }
      }
   }

   public static void closeStatement(Statement statement)
   {
      if (statement != null)
      {
         try
         {
            statement.close();
         }
         catch (SQLException e)
         {
         }
      }
   }

   public static void closeConnection(Connection connection)
   {
      if (connection != null)
      {
         try
         {
            connection.close();
         }
         catch (SQLException e)
         {
         }
      }
   }
}