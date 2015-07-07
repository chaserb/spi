package com.spi.ordersplit;

import static com.spi.ordersplit.PagedOrderSplitter.CONTENT_MAX_Y;
import static com.spi.ordersplit.PagedOrderSplitter.CONTENT_MIN_Y;
import static com.spi.ordersplit.PagedOrderSplitter.ORDERS_PER_PAGE;
import static com.spi.ordersplit.PagedOrderSplitter.CONTENT_RANGE;
import static com.spi.test.util.ThrowableCaptor.captureThrowable;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Before;
import org.junit.Test;

public class PagedOrderSplitterTest {
	
	public static final File TEST_FILE= new File("src/test/resources/INSPI_Report_2015-06-05_23.30.32.pdf");
	public static final float SMIDGE = 0.01f;
	public static final float ROW_HEIGHT = CONTENT_RANGE / ORDERS_PER_PAGE;
	
	public PDDocument inDoc;
	
	@Before
	public void loadDocument() throws IOException {
		inDoc = PDDocument.load(TEST_FILE);
	}

	@Test
	public void testPageIndexForObjectOutOfBounds() {
		PagedOrderSplitter splitter = new PagedOrderSplitter(inDoc);
		
		Throwable beyondMax = captureThrowable(() -> splitter.getPageIndexForObject(0, CONTENT_MAX_Y + SMIDGE));
		Throwable belowMin = captureThrowable(() -> splitter.getPageIndexForObject(0, CONTENT_MIN_Y - SMIDGE));

		assertTrue(beyondMax instanceof IllegalArgumentException);
		assertTrue(belowMin instanceof IllegalArgumentException);
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
		
		PDPage page0 = splitter.getPageForObject(0, CONTENT_MAX_Y - SMIDGE);
		PDPage page4 = splitter.getPageForObject(0, CONTENT_MIN_Y + SMIDGE);
		
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
		
		PDPage page5 = splitter.getPageForObject(1, CONTENT_MAX_Y - SMIDGE);
		PDPage page9 = splitter.getPageForObject(1, CONTENT_MIN_Y + SMIDGE);
		
		assertNotNull(page5);
		assertNotNull(page9);
		assertNotEquals(page5, page9);
		assertEquals(10, splitter.getSplitDocument().getDocumentCatalog().getAllPages().size());
		assertEquals(page5, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(5));
		assertEquals(page9, splitter.getSplitDocument().getDocumentCatalog().getAllPages().get(9));
	}
}
