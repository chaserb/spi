/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.spi.util.EqualityUtils;
import com.spi.util.StringUtil;

public interface InputProvider
{
   public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

   public String getName();

   public void setName(String name);

   public String getServiceCompany();

   public List<Order> getAllOrders();

   public boolean ignoresOld();

   public boolean ignoresNew();

   public boolean generatesTrackingNumber();

   public int getMaxTrackingNumber();

   public int getMinTrackingNumber();

   public Date getEarliestDate();

   public Statistics getStatistics();

   public void setEnvironment(Properties props);

   public List<OutputField> getSupportedFields();

   public WageFigures getWageFigures();

   public enum OutputField
   {
      LOAN_NO
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getLoanNo());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setLoanNo(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getLoanNo())));
         }

         public String getName()
         {
            return "loan number";
         }
      },
      REP
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getRep());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setRep(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getRep())));
         }
      },
      REPORT_TYPE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getReportType());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setReportType(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getReportType())));
         }

         public String getName()
         {
            return "report type";
         }
      },
      RECD_IN
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setDate(column, new java.sql.Date(order.getRecdIn().getTime()));
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setRecdIn(rs.getDate(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            Date value = rs.getDate(column);
            return ((value == null) && (!EqualityUtils.equals(value, order.getRecdIn())));
         }

         public String getName()
         {
            return "recd in";
         }
      },
      DUE_SERV
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setDate(column, new java.sql.Date(order.getDueServ().getTime()));
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setDueServ(rs.getDate(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            Date value = rs.getDate(column);
            return ((value == null) && (!EqualityUtils.equals(value, order.getDueServ())));
         }

         public String getName()
         {
            return "due serv";
         }
      },
      REP_COMP_DATE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setDate(column, new java.sql.Date(order.getRepCompDate().getTime()));
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setRepCompDate(rs.getDate(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            Date value = rs.getDate(column);
            return ((value == null) && (!EqualityUtils.equals(value, order.getRepCompDate())));
         }

         public String getName()
         {
            return "rep comp date";
         }
      },
      MORTGAGER
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            String value = order.getMortgager();
            if (StringUtil.isEmpty(value))
            {
               value = "Unknown";
            }
            ps.setString(column, value);
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setMortgager(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getMortgager())));
         }

         public String getName()
         {
            return "morgager";
         }
      },
      PROP_NO
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getPropNo());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setPropNo(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getPropNo())));
         }

         public String getName()
         {
            return "prop no";
         }
      },
      PROPERTY_ADDRESS
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getPropertyAddress());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setPropertyAddress(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getPropertyAddress())));
         }

         public String getName()
         {
            return "property address";
         }
      },
      CITY
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getCity());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setCity(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getCity())));
         }
      },
      ST
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getSt());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setSt(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getSt())));
         }
      },
      ZIP_CODE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getZipCode());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setZipCode(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getZipCode())));
         }

         public String getName()
         {
            return "zip code";
         }
      },
      IS_RURAL
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, (order.getIsRural()) ? "Yes" : "No");
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            String booString = rs.getString(getName());
            order.setRural("Yes".equalsIgnoreCase(booString));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getIsRural())));
         }

         public String getName()
         {
            return "rural";
         }
      },
      PHOTOS_Y_N
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getPhotosYN());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setPhotosYN(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getPhotosYN())));
         }

         public String getName()
         {
            return "foto yn";
         }
      },
      PROPERTY_DESCRIPTION
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getPropertyDescription());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setPropertyDescription(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return (!EqualityUtils.equals(value, order.getPropertyDescription()));
         }

         public String getName()
         {
            return "property description";
         }
      },
      VALUE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setInt(column, order.getValue());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setValue(rs.getInt(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            int value = rs.getInt(column);
            return ((rs.wasNull()) && (value != order.getValue()));
         }
      },
      OCCUPANCY
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getOccupancy());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setOccupancy(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            String newValue = order.getOccupancy();
            return (!StringUtil.isEmpty(newValue) && !EqualityUtils.equals(value, newValue));
         }
      },
      PERSONAL_CONTACT
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getPersonalContact());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setPersonalContact(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getPersonalContact())));
         }

         public String getName()
         {
            return "personal contact";
         }
      },
      FOR_SALE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getForSale());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setForSale(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getForSale())));
         }

         public String getName()
         {
            return "for sale";
         }
      },
      FOR_SALE_PHONE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getForSalePhone());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setForSalePhone(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            String newValue = order.getForSalePhone();
            return ((StringUtil.isEmpty(value)) && (!StringUtil.isEmpty(newValue)) && (!EqualityUtils.equals(value, newValue)));
         }

         public String getName()
         {
            return "for sale phone";
         }
      },
      COMMENTS
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getComments());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setComments(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getComments())));
         }
      },
      INVOICE_NO
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getInvoiceNo());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setInvoiceNo(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getInvoiceNo())));
         }

         public String getName()
         {
            return "invoice no";
         }
      },
      INVOICE_DATE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setDate(column, new java.sql.Date(order.getInvoiceDate().getTime()));
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setInvoiceDate(rs.getDate(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            Date value = rs.getDate(column);
            return ((value == null) && (!EqualityUtils.equals(value, order.getInvoiceDate())));
         }

         public String getName()
         {
            return "invoice date";
         }
      },
      MTG_COMP
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getMtgComp());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setMtgComp(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getMtgComp())));
         }

         public String getName()
         {
            return "mtg comp";
         }
      },
      COST
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setFloat(column, order.getCost());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setCost(rs.getFloat(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            float value = rs.getFloat(column);
            return ((rs.wasNull()) && (value != order.getCost()));
         }
      },
      WAGES
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setFloat(column, order.getWages());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setWages(rs.getFloat(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            float value = rs.getFloat(column);
            return ((rs.wasNull()) && (value != order.getWages()));
         }
      },
      SG_INSPECTION_CODE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getSGInspectionCode());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setSGInspectionCode(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getSGInspectionCode())));
         }

         public String getName()
         {
            return "sg inspection code";
         }
      },
      SG_INSPECTION_TITLE
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getSGInspectionTitle());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setSGInspectionTitle(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getSGInspectionTitle())));
         }

         public String getName()
         {
            return "sg inspection title";
         }
      },
      SG_INSTRUCTIONS
      {
         public void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException
         {
            ps.setString(column, order.getSGInstructions());
         }

         public void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException
         {
            order.setSGInstructions(rs.getString(getName()));
         }

         public boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException
         {
            String value = rs.getString(column);
            return ((StringUtil.isEmpty(value)) && (!EqualityUtils.equals(value, order.getSGInstructions())));
         }

         public String getName()
         {
            return "sg instructions";
         }
      };

      public void addSelectClause(StringBuffer sql)
      {
         sql.append("[" + getName() + "]");
      }

      public void addUpdateClause(StringBuffer sql)
      {
         addSelectClause(sql);
         sql.append(" = ?");
      }

      public abstract void applyUpdateParameter(Order order, int column, PreparedStatement ps) throws SQLException;

      public abstract void getSelectedParameter(SPIOrder order, ResultSet rs) throws SQLException;

      public String getName()
      {
         return toString().toLowerCase();
      }

      /**
       * Returns true if this output field requires an update, in that its value
       * in the result set is either empty or null.
       * 
       * @param order
       * @param column
       * @param rs
       * @return
       * @throws SQLException
       */
      public abstract boolean requiresUpdate(Order order, int column, ResultSet rs) throws SQLException;
   }
}
