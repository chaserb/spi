/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.vendor360;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;

import com.spi.util.Debug;
import com.spi.web.WebClient;

/**
 * @author Chase Barrett
 */
public class Web extends WebClient
{
   static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
   private static final String DEBUG_PREFIX = "Web.";
   static final String SOAP_ACTION = "SOAPAction";
   static final String WIP_METHOD = "GetInspectionsWIPByVendorID";
   static final String OVERDUE_METHOD = "GetOverdueInspectionsByVendorID";
   static final String DUE_TODAY_METHOD = "GetInspectionsDueTodayByVendorID";
   static final String EXECUTE_USER_SP_METHOD = "ExecuteUserSP";
   static final String GET_LAST_INSP_RESULTS_METHOD = "GetLastInspResults";
   static final String GET_VENDOR_ID_METHOD = "GetVendorIDFromVndrNum";
   static final String LOGIN_METHOD = "Login";

   private String mVendorNum;
   private String mVendorId;

   /**
    * Web constructor.
    * 
    * @param env client properties identifying the login URL, user name, and
    *        password to the MCS website.
    * @param name of the input provider
    */
   public Web(Properties env, String name)
   {
      super(env, name);
      setVendorNum(env.getProperty("com.spi." + name + ".inputVendor"));
   }

   @Override
   public void login() throws IOException
   {
      if (getVendorId() == null)
      {
         HttpURLConnection loginConn = getConnection(getLoginURL());
         Map<String, String> headerVars = new HashMap<String, String>();
         headerVars.put(SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.GET_VENDOR_ID_METHOD);
         InputStream is = getInput(loginConn, getVendorIDRequest(), headerVars);
         Document doc = parseInput(is);
         NodeList nodes = doc.getElementsByTagName("GetVendorIDFromVndrNumResult");
         setVendorId(nodes.item(0).getTextContent());
      }

      if (getAuthToken() == null)
      {
         HttpURLConnection loginConn = getConnection(getLoginURL());
         Map<String, String> headerVars = new HashMap<String, String>();
         headerVars.put(SOAP_ACTION, "http://mcs360.com/MCSNow/" + Web.LOGIN_METHOD);
         InputStream is = getInput(loginConn, getLoginRequest(), headerVars);
         Document doc = parseInput(is);
         NodeList nodes = doc.getElementsByTagName("Token");
         setAuthToken(nodes.item(0).getTextContent());
      }
   }

   @Override
   public InputStream getInput(String url) throws IOException
   {
      return getInput(url, null, null);
   }

   public InputStream getInput(String url, String post, Map<String, String> reqHeaderVars) throws IOException
   {
      return getInput(getConnection(url), post, reqHeaderVars);
   }

   public InputStream getInput(HttpURLConnection conn, String post, Map<String, String> reqHeaderVars) throws IOException
   {
      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): START");

      boolean postSet = post != null && post.length() > 0;

      // Set up the connection
      conn.setRequestProperty(USER_AGENT_HEADER, "Mozilla/4.0 (compatible; MSIE 6.0; " + "MS Web Services Client Protocol 2.0.50727.5456)");
      conn.setRequestProperty(CONTENT_TYPE_HEADER, "text/xml; charset=utf-8");
      conn.setRequestProperty(HOST_HEADER, conn.getURL().getHost());
      if (reqHeaderVars != null)
      {
         for (String propName : reqHeaderVars.keySet())
         {
            conn.setRequestProperty(propName, reqHeaderVars.get(propName));
         }
      }
      if (postSet)
      {
         conn.setRequestProperty(CONTENT_LENGTH_HEADER, Integer.toString(post.length()));
      }

      printRequestHeaders(conn);

      // If we have something to post, do it
      if (postSet)
      {
         if (Debug.checkLevel(Debug.MED))
         {
            Debug.debug(Debug.MED, "************ Post Data:");
            Debug.debug(Debug.MED, post.toString());
            Debug.debug(Debug.MED, "");
         }
         conn.setDoOutput(true);
         PrintWriter writer = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
         writer.write(post);
         writer.flush();
         writer.close();
      }

      // Get the connection's input stream
      BASE64Decoder decoder = new BASE64Decoder();
      ByteBuffer buf = decoder.decodeBufferToByteBuffer(conn.getInputStream());
      InputStream inStream = new GZIPInputStream(new ByteArrayInputStream(buf.array()));
      printResponseHeaders(conn);

      if (Debug.checkLevel(Debug.MED)) Debug.debug(Debug.MED, DEBUG_PREFIX + "getInput(): END");

      return inStream;
   }

   /**
    * @return the vendorNum
    */
   public String getVendorNum()
   {
      return mVendorNum;
   }

   /**
    * @param vendorNum the vendorNum to set
    */
   public void setVendorNum(String vendorNum)
   {
      mVendorNum = vendorNum;
   }

   /**
    * @return the vendorId
    */
   public String getVendorId()
   {
      return mVendorId;
   }

   /**
    * @param vendorId the vendorId to set
    */
   public void setVendorId(String vendorId)
   {
      mVendorId = vendorId;
   }

   @Override
   public Document parseInput(InputStream inStream)
   {
      try
      {
         // Get Document Builder Factory
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         return builder.parse(inStream);
      }
      catch (ParserConfigurationException e)
      {
         Debug.debugException("The underlying parser does not support the requested features.", e);
      }
      catch (FactoryConfigurationError e)
      {
         Debug.debugException("Error occurred obtaining Document Builder Factory.", e);
      }
      catch (Exception e)
      {
         Debug.debugException("Error occurred parsing a document.", e);
      }
      return null;
   }

   @Override
   protected String getDefaultLoginURL()
   {
      return "http://ws.mcs360.com/webservices_comp/mcsnow.asmx";
   }

   String getVendorIDRequest()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<GetVendorIDFromVndrNum xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<VndrNum>");
      buf.append(getVendorNum());
      buf.append("</VndrNum>");
      buf.append("</GetVendorIDFromVndrNum>");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }

   String getLoginRequest()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<Login xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<LoginInfo>");
      buf.append("<CompanyID>");
      buf.append(getVendorId());
      buf.append("</CompanyID>");
      buf.append("<UserName>");
      buf.append(getInputUser());
      buf.append("</UserName>");
      buf.append("<Password>");
      buf.append(getInputPass());
      buf.append("</Password>");
      buf.append("<UserType>3</UserType>");
      buf.append("</LoginInfo>");
      buf.append("</Login>");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }

   String getWorkInProgressRequest()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<" + WIP_METHOD + " xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<User>");
      buf.append("<Token>");
      buf.append(getAuthToken());
      buf.append("</Token>");
      buf.append("</User>");
      buf.append("<VendorID>");
      buf.append(getVendorId());
      buf.append("</VendorID>");
      buf.append("</" + WIP_METHOD + ">");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }
   
   String getOverdueInspectionsRequest()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<" + OVERDUE_METHOD + " xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<User>");
      buf.append("<Token>");
      buf.append(getAuthToken());
      buf.append("</Token>");
      buf.append("</User>");
      buf.append("<VendorID>");
      buf.append(getVendorId());
      buf.append("</VendorID>");
      buf.append("</" + OVERDUE_METHOD + ">");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }
   
   String getInspectionsDueTodayRequest()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<" + DUE_TODAY_METHOD + " xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<User>");
      buf.append("<Token>");
      buf.append(getAuthToken());
      buf.append("</Token>");
      buf.append("</User>");
      buf.append("<VendorID>");
      buf.append(getVendorId());
      buf.append("</VendorID>");
      buf.append("</" + DUE_TODAY_METHOD + ">");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }
   
   String getEnteredForDateRangeRequest(Date earliestDate)
   {
      Date today = new Date();
      if (today.compareTo(earliestDate) > 0)
      {
         earliestDate = today;
      }

      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");
      buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      buf.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<ExecuteUserSP xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<User>");
      buf.append("<Token>");
      buf.append(getAuthToken());
      buf.append("</Token>");
      buf.append("</User>");
      buf.append("<sSPName>rp_InspectionsEnteredbyVendor</sSPName>");
      buf.append("<sParameterSepWithTilda>Start_date = " + DATE_FORMAT.format(earliestDate) + "~End_Date = " + DATE_FORMAT.format(today) + "~Vendor_id = " + getVendorId() + "</sParameterSepWithTilda>");
      buf.append("<bReturn>true</bReturn>");
      buf.append("</ExecuteUserSP>");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }

   String getInspectionRequest(Vendor360Order order) throws Exception
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
      buf.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
      buf.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
      buf.append("<soap:Body>");
      buf.append("<GetLastInspResults xmlns=\"http://mcs360.com/MCSNow\">");
      buf.append("<User>");
      buf.append("<Token>");
      buf.append(getAuthToken());
      buf.append("</Token>");
      buf.append("</User>");
      buf.append("<PMID>");
      buf.append(order.getPMId());
      buf.append("</PMID>");
      buf.append("<DateOrdered>");
      buf.append(Vendor360Access.formatDate(order.getRecdIn()));
      buf.append("</DateOrdered>");
      buf.append("<Days>60</Days>");
      buf.append("<InspectionID>");
      buf.append(order.getInspectionId());
      buf.append("</InspectionID>");
      buf.append("</GetLastInspResults>");
      buf.append("</soap:Body>");
      buf.append("</soap:Envelope>");
      return buf.toString();
   }
}