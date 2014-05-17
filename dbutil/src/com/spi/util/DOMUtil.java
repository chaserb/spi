package com.spi.util;

/*
 * Copyright Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Sample Utility class to work with DOM document
 */
public class DOMUtil
{

   /** Prints the specified node, then prints all of its children. */
   public static void printDOM(Node node, PrintWriter writer)
   {
      int type = node.getNodeType();
      switch (type)
      {
      // print the document element
      case Node.DOCUMENT_NODE:
      {
         writer.println("<?xml version=\"1.0\" ?>");
         printDOM(((Document) node).getDocumentElement(), writer);
         break;
      }

         // print element with attributes
      case Node.ELEMENT_NODE:
      {
         writer.print("<");
         writer.print(node.getNodeName());
         NamedNodeMap attrs = node.getAttributes();
         for (int i = 0; i < attrs.getLength(); i++)
         {
            Node attr = attrs.item(i);
            writer.print(" " + attr.getNodeName().trim() + "=\"" + attr.getNodeValue().trim() + "\"");
         }
         writer.println(">");

         NodeList children = node.getChildNodes();
         if (children != null)
         {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
               printDOM(children.item(i), writer);
         }

         break;
      }

         // handle entity reference nodes
      case Node.ENTITY_REFERENCE_NODE:
      {
         writer.print("&");
         writer.print(node.getNodeName().trim());
         writer.print(";");
         break;
      }

         // print cdata sections
      case Node.CDATA_SECTION_NODE:
      {
         writer.print("<![CDATA[");
         writer.print(node.getNodeValue().trim());
         writer.print("]]>");
         break;
      }

         // print text
      case Node.TEXT_NODE:
      {
         writer.print(node.getNodeValue().trim());
         break;
      }

         // print processing instruction
      case Node.PROCESSING_INSTRUCTION_NODE:
      {
         writer.print("<?");
         writer.print(node.getNodeName().trim());
         String data = node.getNodeValue().trim();
         {
            writer.print(" ");
            writer.print(data);
         }
         writer.print("?>");
         break;
      }
      }

      if (type == Node.ELEMENT_NODE)
      {
         writer.println();
         writer.print("</");
         writer.print(node.getNodeName().trim());
         writer.print('>');
      }
   }

   /**
    * Parse the XML file and create Document
    * 
    * @param fileName
    * @return Document
    */
   public static Document parse(String fileName, PrintWriter writer)
   {
      Document document = null;
      // Initiate DocumentBuilderFactory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      // To get a validating parser
      factory.setValidating(false);
      // To get one that understands namespaces
      factory.setNamespaceAware(true);

      try
      {
         // Get DocumentBuilder
         DocumentBuilder builder = factory.newDocumentBuilder();
         // Parse and load into memory the Document
         document = builder.parse(new File(fileName));
         return document;

      }
      catch (SAXParseException spe)
      {
         // Error generated by the parser
         writer.println("\n** Parsing error , line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
         writer.println(" " + spe.getMessage());
         // Use the contained exception, if any
         Exception x = spe;
         if (spe.getException() != null) x = spe.getException();
         x.printStackTrace();
      }
      catch (SAXException sxe)
      {
         // Error generated during parsing
         Exception x = sxe;
         if (sxe.getException() != null) x = sxe.getException();
         x.printStackTrace();
      }
      catch (ParserConfigurationException pce)
      {
         // Parser with specified options can't be built
         pce.printStackTrace();
      }
      catch (IOException ioe)
      {
         // I/O error
         ioe.printStackTrace();
      }

      return null;
   }

   /**
    * This method writes a DOM document to a file
    * 
    * @param filename
    * @param document
    */
   public static void writeXmlToFile(String filename, Document document, PrintWriter writer)
   {
      try
      {
         // Prepare the DOM document for writing
         Source source = new DOMSource(document);

         // Prepare the output file
         File file = new File(filename);
         Result result = new StreamResult(file);

         // Write the DOM document to the file
         // Get Transformer
         Transformer xformer = TransformerFactory.newInstance().newTransformer();
         // Write to a file
         xformer.transform(source, result);
      }
      catch (TransformerConfigurationException e)
      {
         writer.println("TransformerConfigurationException: " + e);
      }
      catch (TransformerException e)
      {
         writer.println("TransformerException: " + e);
      }
   }

   /**
    * Count Elements in Document by Tag Name
    * 
    * @param tag
    * @param document
    * @return number elements by Tag Name
    */
   public static int countByTagName(String tag, Document document)
   {
      NodeList list = document.getElementsByTagName(tag);
      return list.getLength();
   }

}