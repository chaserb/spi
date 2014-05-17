/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.spi.util.EqualityUtils;

public class TableSynch
{
   private static final int STRING = 0;
   private static final int INTEGER = 1;
   private static final int BOOLEAN = 2;
   private static final int DATE = 3;
   private static final int FLOAT = 4;
   private static final Date UPPER = new Date(System.currentTimeMillis());
   private static final Date LOWER = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 365 * 2));

   private static final String[] mFields = { "TRACKING NO", "REP", "ISS TO REP  DT", "REPS DUE DT", "WAGES", "MORGAGER", "PROP NO", "PROPERTY ADDRESS",
         "APPT NO", "CITY", "ST", "ZIP CODE", "RURAL", "MTG COMP", "LOAN NUMBER", "CUST WO NO", "REPORT TYPE", "PERSONAL CONTACT", "FOTO YN",
         "QTY PHOTOS REC", "RECD IN", "CNCL DATE", "REP COMP DATE", "RECD OT LATE", "RECD FROM REP", "FAXED SC", "DUE SERV", "COST", "SERVICE COMPANY",
         "VALUE", "OCCUPANCY", "PROPERTY DESCRIPTION", "FOR SALE", "FOR SALE PHONE", "INVOICE NO", "INVOICE DATE", "COMMENTS", "PAYMENT DATE", "PAYMENT AMT",
         "BALANCE", "REP PAID DATE", "AMOUNT PAID REP", "SG INSPECTION CODE", "SG INSPECTION TITLE", "SG INSTRUCTIONS", "SG PRINTED", "OVERLAP WITH OLD" };

   private static final int[] mFieldTypes = { INTEGER, // "TRACKING NO",
         STRING, // "REP",
         DATE, // "ISS TO REP  DT",
         DATE, // "REPS DUE DT",
         FLOAT, // "WAGES",
         STRING, // "MORGAGER",
         STRING, // "PROP NO",
         STRING, // "PROPERTY ADDRESS",
         STRING, // "APPT NO",
         STRING, // "CITY",
         STRING, // "ST",
         STRING, // "ZIP CODE",
         STRING, // "RURAL",
         STRING, // "MTG COMP",
         STRING, // "LOAN NUMBER",
         STRING, // "CUST WO NO",
         STRING, // "REPORT TYPE",
         STRING, // "PERSONAL CONTACT",
         STRING, // "FOTO YN",
         INTEGER, // "QTY PHOTOS REC",
         DATE, // "RECD IN",
         DATE, // "CNCL DATE",
         DATE, // "REP COMP DATE",
         STRING, // "RECD OT LATE",
         DATE, // "RECD FROM REP",
         DATE, // "FAXED SC",
         DATE, // "DUE SERV",
         FLOAT, // "COST",
         STRING, // "SERVICE COMPANY",
         INTEGER, // "VALUE",
         STRING, // "OCCUPANCY",
         STRING, // "PROPERTY DESCRIPTION",
         STRING, // "FOR SALE",
         STRING, // "FOR SALE PHONE",
         INTEGER, // "INVOICE NO",
         DATE, // "INVOICE DATE",
         STRING, // "COMMENTS",
         STRING, // "PAYMENT DATE",
         FLOAT, // "PAYMENT AMT",
         FLOAT, // "BALANCE",
         DATE, // "REP PAID DATE",
         FLOAT, // "AMOUNT PAID REP",
         STRING, // "SG INSPECTION CODE",
         STRING, // "SG INSPECTION TITLE",
         STRING, // "SG INSTRUCTIONS",
         BOOLEAN, // "SG PRINTED",
         BOOLEAN // "OVERLAP WITH OLD"
   };

   private static File mConflictsFile = new File("D:/working/spi/2009-06-15 Incident/conflicts.txt");
   private static PrintStream mConflictsOut;
   private static File mBaselineFile = new File("D:/working/spi/2009-06-15 Incident/baseline.txt");
   private static PrintStream mBaselineOut;
   private static File mUpdatesFile = new File("D:/working/spi/2009-06-15 Incident/updates.txt");
   private static PrintStream mUpdatesOut;

   private static final Map<Integer, List<Object>> mUpdatedRows = new TreeMap<Integer, List<Object>>();
   private static final Map<Integer, List<Object>> mNewRows = new TreeMap<Integer, List<Object>>();

   public static void main(String[] args) throws Exception
   {
      mConflictsOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(mConflictsFile, false)));
      mBaselineOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(mBaselineFile, false)));
      mUpdatesOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(mUpdatesFile, false)));
      int ordersMatch = 0;
      int ordersInBaseline = 0;
      int ordersInUpdates = 0;
      int ordersDifferent = 0;

      Connection dbBaseline = Database.getConnection("jdbc:odbc:baseline", null, null);
      Connection dbUpdates = Database.getConnection("jdbc:odbc:updates", null, null);

      // Construct the SELECT String
      StringBuffer selectSql = new StringBuffer();
      selectSql.append("SELECT ");
      for (int i = 0; i < mFields.length; i++)
      {
         selectSql.append("[");
         selectSql.append(mFields[i]);
         selectSql.append("]");
         if (i < mFields.length - 1)
         {
            selectSql.append(", ");
         }
      }
      selectSql.append(" FROM [Tracking Sheets]");

      // Select everything from the baseline
      Statement st = dbBaseline.createStatement();
      ResultSet rs = st.executeQuery(selectSql.toString());
      int count = 0;
      while (rs.next())
      {
         Integer trackingNo = rs.getInt(1);
         List<Object> row = getRow(rs, trackingNo);
         mUpdatedRows.put(trackingNo, row);
         count++;
         if (count % 100 == 0)
         {
            System.out.println(count);
         }
      }
      Database.closeResultSet(rs);
      Database.closeStatement(st);

      // Select everything from the updates
      st = dbUpdates.createStatement();
      rs = st.executeQuery(selectSql.toString());
      count = 0;
      while (rs.next())
      {
         Integer trackingNo = rs.getInt(1);
         List<Object> updatedRow = getRow(rs, trackingNo);
         List<Object> baselineRow = mUpdatedRows.get(trackingNo);
         if (baselineRow == null)
         {
            ordersInUpdates++;
            mNewRows.put(trackingNo, updatedRow);
         } else
         {
            if (updatedRow.equals(baselineRow))
            {
               ordersMatch++;
            } else
            {
               ordersDifferent++;
               resolveConflict(baselineRow, updatedRow);
            }
         }

         count++;
         if (count % 100 == 0)
         {
            System.out.println(count);
         }
      }
      Database.closeResultSet(rs);
      Database.closeStatement(st);

      // Construct the INSERT String
      StringBuffer insertSql = new StringBuffer();
      insertSql.append("INSERT INTO [Tracking Sheets] (");
      for (int i = 0; i < mFields.length; i++)
      {
         insertSql.append("[");
         insertSql.append(mFields[i]);
         insertSql.append("]");
         if (i < mFields.length - 1)
         {
            insertSql.append(", ");
         }
      }
      insertSql.append(") VALUES (");
      for (int i = 0; i < mFields.length; i++)
      {
         insertSql.append("?");
         if (i < mFields.length - 1)
         {
            insertSql.append(", ");
         }
      }
      insertSql.append(")");

      // Insert new rows into the table
      PreparedStatement ps = dbBaseline.prepareStatement(insertSql.toString());
      count = 0;
      int field = 0;
      Object value = null;
      for (Integer trackingNo : mNewRows.keySet())
      {
         try
         {
            List<Object> row = mNewRows.get(trackingNo);
            for (field = 0; field < mFields.length; field++)
            {
               value = row.get(field);
               switch (mFieldTypes[field])
               {
               case BOOLEAN:
                  ps.setBoolean(field + 1, (Boolean) value);
                  break;
               case DATE:
                  Date date = (Date) value;
                  if ((date == null) || (date.getTime() > UPPER.getTime()) || (date.getTime() < LOWER.getTime()))
                  {
                     date = (Date) row.get(27);
                     value = date;
                  }
                  ps.setDate(field + 1, date);
                  break;
               case FLOAT:
                  if (value == null)
                  {
                     value = new Float(0f);
                  }
                  ps.setFloat(field + 1, (Float) value);
                  break;
               case INTEGER:
                  if (value == null)
                  {
                     value = new Integer(0);
                  }
                  ps.setInt(field + 1, (Integer) value);
                  break;
               case STRING:
                  if (value == null)
                  {
                     value = " ";
                  }
                  ps.setString(field + 1, (String) value);
                  break;
               }
            }
            count++;
            if (count % 100 == 0)
            {
               System.out.println(count);
            }
            ps.executeUpdate();
         }
         catch (SQLException e)
         {
            mConflictsOut.println("Error INSERTING order " + trackingNo);
            mConflictsOut.println("    for object " + value);
            mConflictsOut.println("    on field " + mFields[--field]);
            mConflictsOut.println("    with excecption " + e);
            e.printStackTrace();
         }
      }
      Database.closeResultSet(rs);
      Database.closeStatement(ps);

      // Construct the UPDATE String
      StringBuffer updateSql = new StringBuffer();
      updateSql.append("UPDATE [Tracking Sheets] SET ");
      for (int i = 1; i < mFields.length; i++)
      {
         updateSql.append("[");
         updateSql.append(mFields[i]);
         updateSql.append("]=?");
         if (i < mFields.length - 1)
         {
            updateSql.append(", ");
         }
      }
      updateSql.append(" WHERE [TRACKING NO]=?");

      // Update new rows into the table
      ps = dbBaseline.prepareStatement(updateSql.toString());
      count = 0;
      field = 0;
      value = null;
      for (Integer trackingNo : mUpdatedRows.keySet())
      {
         try
         {
            List<Object> row = mUpdatedRows.get(trackingNo);
            for (field = 1; field < mFields.length; field++)
            {
               value = row.get(field);
               switch (mFieldTypes[field])
               {
               case BOOLEAN:
                  ps.setBoolean(field, (Boolean) value);
                  break;
               case DATE:
                  Date date = (Date) value;
                  if ((date == null) || (date.getTime() > UPPER.getTime()) || (date.getTime() < LOWER.getTime()))
                  {
                     date = (Date) row.get(27);
                     value = date;
                  }
                  ps.setDate(field, date);
                  break;
               case FLOAT:
                  if (value == null)
                  {
                     value = new Float(0f);
                  }
                  ps.setFloat(field, (Float) value);
                  break;
               case INTEGER:
                  if (value == null)
                  {
                     value = new Integer(0);
                  }
                  ps.setInt(field, (Integer) value);
                  break;
               case STRING:
                  if (value == null)
                  {
                     value = " ";
                  }
                  ps.setString(field, (String) value);
                  break;
               }
            }
            ps.setInt(mFields.length, (Integer) row.get(0));
            count++;
            if (count % 100 == 0)
            {
               System.out.println(count);
            }
            ps.executeUpdate();
         }
         catch (SQLException e)
         {
            mConflictsOut.println("Error UPDATING order " + trackingNo);
            mConflictsOut.println("    for object " + value);
            mConflictsOut.println("    on field " + mFields[--field]);
            mConflictsOut.println("    with excecption " + e);
            e.printStackTrace();
         }
      }
      Database.closeResultSet(rs);
      Database.closeStatement(ps);

      mConflictsOut.println("Matches: " + ordersMatch);
      mConflictsOut.println("Conflicts: " + ordersDifferent);
      mConflictsOut.println("New From Updates: " + ordersInUpdates);
      mConflictsOut.println("New From Baseline: " + ordersInBaseline);
      mConflictsOut.flush();
      mConflictsOut.close();
   }

   private static List<Object> getRow(ResultSet rs, int trackingNo) throws SQLException
   {
      List<Object> row = new ArrayList<Object>(mFields.length);
      row.add(trackingNo);
      for (int i = 1; i < mFields.length; i++)
      {
         switch (mFieldTypes[i])
         {
         case BOOLEAN:
            row.add(rs.getBoolean(i + 1));
            break;
         case DATE:
            row.add(rs.getDate(i + 1));
            break;
         case FLOAT:
            row.add(rs.getFloat(i + 1));
            break;
         case INTEGER:
            row.add(rs.getInt(i + 1));
            break;
         case STRING:
            row.add(rs.getString(i + 1));
            break;
         }
      }
      return row;
   }

   private static void resolveConflict(List<Object> baselineRow, List<Object> updatedRow)
   {
      List<Object> merged = new ArrayList<Object>(mFields.length);
      int trackingNo = (Integer) baselineRow.get(0);
      int baselineNulls = countNulls(baselineRow);
      int updatedNulls = countNulls(updatedRow);
      boolean reportable = false;
      mBaselineOut.println();
      mUpdatesOut.println();
      for (int i = 0; i < mFields.length; i++)
      {
         Object baseline = baselineRow.get(i);
         Object updated = updatedRow.get(i);
         if (EqualityUtils.equals(baseline, updated))
         {
            merged.add(baseline);
         } else if (baseline == null)
         {
            merged.add(updated);
         } else if (updated == null)
         {
            merged.add(baseline);
         } else
         {
            Object keep;
            Object ignore;
            if (baselineNulls < updatedNulls)
            {
               keep = baseline;
               ignore = updated;
            } else
            {
               keep = updated;
               ignore = baseline;
            }
            mConflictsOut.println("Order " + trackingNo + " was different in field " + mFields[i] + ", went with \"" + keep + "\" instead of \"" + ignore
                  + "\"");
            merged.add(keep);
            reportable = true;
         }
         mBaselineOut.println(trackingNo + " " + mFields[i] + " " + baseline);
         mUpdatesOut.println(trackingNo + " " + mFields[i] + " " + updated);
      }
      if (reportable && baselineNulls == updatedNulls)
      {
         mConflictsOut.println("NOT SURE WHAT TO DO WITH THE ABOVE, SINCE BOTH ROWS HAD THE SAME NUMBER OF NULLS");
      }
      mConflictsOut.println();
      mUpdatedRows.put(trackingNo, merged);
   }

   private static int countNulls(List<Object> row)
   {
      int count = 0;
      for (Object cell : row)
      {
         if (cell == null)
         {
            count++;
         }
      }
      return count;
   }
}
