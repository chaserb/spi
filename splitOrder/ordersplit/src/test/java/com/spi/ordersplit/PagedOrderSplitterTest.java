package com.spi.ordersplit;

import static com.spi.ordersplit.PagedOrderSplitter.CONTENT_MAX_Y;
import static com.spi.ordersplit.PagedOrderSplitter.CONTENT_MIN_Y;
import static com.spi.ordersplit.PagedOrderSplitter.ROW_HEIGHT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PagedOrderSplitterTest {
	
	private static final Logger log = Logger.getLogger(PagedOrderSplitterTest.class.getName());

	public static final File TEST_IN_FILE = new File("./src/test/resources/INSPI_Report_2015-06-05_23.30.32.pdf");
	public static final File TEST_OUT_FILE = new File("./src/test/resources/INSPI_Report_2015-06-05_23.30.32.processed.pdf");
	public static final File TEST_SIMPLE_FILE = new File("./src/test/resources/simple.pdf"); 
	public static final float SMIDGE = 0.01f;
	
	public PDDocument inDoc;
	public PDDocument outDoc;
	
	@Before
	public void loadDocuments() throws IOException {
		inDoc = PDDocument.load(TEST_IN_FILE);
		outDoc = PDDocument.load(TEST_OUT_FILE);
	}
	
	@After
	public void closeDocuments() throws IOException {
		inDoc.close();
		outDoc.close();
	}
	@Test
	public void testPageIndexForObjectOutOfBounds() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		int beyondMax = splitter.getPageIndexForObject(0, CONTENT_MAX_Y + SMIDGE);
		int belowMin = splitter.getPageIndexForObject(0, CONTENT_MIN_Y - SMIDGE);

		assertEquals(-1, beyondMax);
		assertEquals(-1, belowMin);
	}
	
	@Test
	public void testPageIndexOnOrderFromFirstPage() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		int record0Top = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 0 * ROW_HEIGHT);
		int record0Bot = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 1 * ROW_HEIGHT + SMIDGE);
		int record1Top = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 1 * ROW_HEIGHT);
		int record1Bot = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 2 * ROW_HEIGHT + SMIDGE);
		int record2Top = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 2 * ROW_HEIGHT);
		int record2Bot = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 3 * ROW_HEIGHT + SMIDGE);
		int record3Top = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 3 * ROW_HEIGHT);
		int record3Bot = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 4 * ROW_HEIGHT + SMIDGE);
		int record4Top = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 4 * ROW_HEIGHT);
		int record4Bot = splitter.getPageIndexForObject(0, CONTENT_MAX_Y - 5 * ROW_HEIGHT + SMIDGE);
		
		assertEquals(0, record0Top);
		assertEquals(0, record0Bot);
		assertEquals(1, record1Top);
		assertEquals(1, record1Bot);
		assertEquals(2, record2Top);
		assertEquals(2, record2Bot);
		assertEquals(3, record3Top);
		assertEquals(3, record3Bot);
		assertEquals(4, record4Top);
		assertEquals(4, record4Bot);
	}

	
	@Test
	public void testPageIndexOnOrderFromSecondPage() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		int record0Top = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 0 * ROW_HEIGHT);
		int record0Bot = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 1 * ROW_HEIGHT + SMIDGE);
		int record1Top = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 1 * ROW_HEIGHT);
		int record1Bot = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 2 * ROW_HEIGHT + SMIDGE);
		int record2Top = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 2 * ROW_HEIGHT);
		int record2Bot = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 3 * ROW_HEIGHT + SMIDGE);
		int record3Top = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 3 * ROW_HEIGHT);
		int record3Bot = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 4 * ROW_HEIGHT + SMIDGE);
		int record4Top = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 4 * ROW_HEIGHT);
		int record4Bot = splitter.getPageIndexForObject(1, CONTENT_MAX_Y - 5 * ROW_HEIGHT + SMIDGE);
		
		assertEquals(5, record0Top);
		assertEquals(5, record0Bot);
		assertEquals(6, record1Top);
		assertEquals(6, record1Bot);
		assertEquals(7, record2Top);
		assertEquals(7, record2Bot);
		assertEquals(8, record3Top);
		assertEquals(8, record3Bot);
		assertEquals(9, record4Top);
		assertEquals(9, record4Bot);
	}
	
	@Test
	public void testGetPageOnOrderFromFirstPage() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		PDPage page0 = splitter.getPageAtIndex(splitter.getPageIndexForObject(0, CONTENT_MAX_Y - SMIDGE));
		PDPage page4 = splitter.getPageAtIndex(splitter.getPageIndexForObject(0, CONTENT_MIN_Y + SMIDGE));
		
		assertNotNull(page0);
		assertNotNull(page4);
		assertNotEquals(page0, page4);
		assertEquals(5, splitter.getSplitDocument().getDocumentCatalog().getAllPages().size());
		assertEquals(page0, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(0));
		assertEquals(page4, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(4));
	}
	
	@Test
	public void testGetPageOnOrderFromSecondPage() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		PDPage page5 = splitter.getPageAtIndex(splitter.getPageIndexForObject(1, CONTENT_MAX_Y - SMIDGE));
		PDPage page9 = splitter.getPageAtIndex(splitter.getPageIndexForObject(1, CONTENT_MIN_Y + SMIDGE));
		
		assertNotNull(page5);
		assertNotNull(page9);
		assertNotEquals(page5, page9);
		assertEquals(10, splitter.getSplitDocument().getDocumentCatalog().getAllPages().size());
		assertEquals(page5, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(5));
		assertEquals(page9, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(9));
	}
	
	@Test
	public void testSimpleFile() throws IOException, COSVisitorException {
		// Just to make sure I understand how to compose PDFs
		PDDocument doc = new PDDocument();
		PDFont font = PDType1Font.HELVETICA;
		// Create the first page
		PDPage page1 = new PDPage();
		PDPageContentStream stream1 = new PDPageContentStream(doc, page1);
		stream1.setFont(font, 30.0f);
		stream1.beginText();
		stream1.moveTextPositionByAmount(300.0f, 300.0f);
		stream1.drawString("Hello on page 1");
		stream1.endText();
		stream1.beginText();
		stream1.drawString("Hello Again on page 1");
		stream1.endText();
		stream1.close();
		// Create the second page
		PDPage page2 = new PDPage();
		PDPageContentStream stream2 = new PDPageContentStream(doc, page2);
		stream2.setFont(font, 30.0f);
		stream2.beginText();
		stream2.moveTextPositionByAmount(300.0f, 300.0f);
		stream2.drawString("Hello on page 2");
		stream2.endText();
		stream2.beginText();
		stream2.drawString("Hello Again on page 2");
		stream2.endText();
		stream2.close();
		// Close the document
		doc.addPage(page1);
		doc.addPage(page2);
		doc.save(TEST_SIMPLE_FILE);
		doc.close();
	}
	
	@Test
	public void testSplitOrders() throws COSVisitorException, IOException {
		// Simply dump tokens to the logger
		log.info("\n\n******* Input Tokens *******\n\n");
		try {
			PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
			splitter.splitOrders();
		} catch (Exception e) {
			log.log(Level.SEVERE, e, () -> "Caught an error on the input document");
		}
		log.info("\n\n******* Output Tokens *******\n\n");
		try {
			PagedOrderSplitter splitter = new PagedOrderSplitter(outDoc);
			splitter.splitOrders();
		} catch (Exception e) {
			log.log(Level.SEVERE, e, () -> "Caught an error on the output document");
		}
		log.info("\n\n******* Done Tokens *******\n\n");
	}
}
