package com.spi.ordersplit;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Reads a PDF file at a given file location, and inspects the orders contained
 * therein. It then creates a new PDF file with one page per order from the
 * origin document, placing the text of each order at the top of its new page.
 * 
 * @author chase.barrett
 */
public class PagedOrderSplitter {

	static final Logger log = Logger.getLogger(PagedOrderSplitter.class.getName());
	static final int ORDERS_PER_PAGE = 5;
	static final float CONTENT_MAX_Y = 694.52f;
	static final float CONTENT_MIN_Y = 28.32f;
	static final float CONTENT_RANGE = CONTENT_MAX_Y - CONTENT_MIN_Y;

	private PDDocument inDoc;
	private PDDocument outDoc;

	/**
	 * Create the PagedOrderSplitter.
	 * 
	 * @param origin
	 *            the input file which contains several orders on a single page.
	 */
	public PagedOrderSplitter(PDDocument sourceDocument) {
		inDoc = sourceDocument;
		outDoc = new PDDocument();
	}

	/**
	 * Retrieve the paged output document, which contains the page-per-order
	 * parsed output from the input document. This output document will be 
	 * empty until {@link #splitOrders()} is invoked.
	 * 
	 * @return the paged output document
	 */
	public PDDocument getSplitDocument() {
		return outDoc;
	}

	/**
	 * For each order in the origin document, create a page in the split
	 * document, and place the order contents onto each page.
	 * 
	 * @param file
	 */
	@SuppressWarnings("unchecked")
	public void splitOrders() throws IOException, COSVisitorException {
		// TODO Is This Necessary??
		PDDocumentInformation destInfo = outDoc.getDocumentInformation();
		PDDocumentInformation srcInfo = inDoc.getDocumentInformation();
		destInfo.getDictionary().mergeInto(srcInfo.getDictionary());

		// finally append the pages
		List<PDPage> pages = inDoc.getDocumentCatalog().getAllPages();
		for (int pageNum = 0; pageNum < pages.size(); pageNum++) {
			PDPage page = pages.get(pageNum);
			COSStream stream = page.getContents().getStream();
			List<Object> pageTokens = stream.getStreamTokens();
			List<Object> textObjectTokens = null;
			for (Object token : pageTokens) {
				if (token instanceof PDFOperator) {
					PDFOperator operator = (PDFOperator)token;
					if (operator.getOperation().equals("BT")) { // Begin Text
						
					}
				}
			}
		}
		// TODO Broken here
		// PDPage newPage = new
		// PDPage((COSDictionary)cloneForNewDocument(page.getCOSDictionary()));
		// newPage.getContents().getStream();
		// newPage.setCropBox(page.findCropBox());
		// newPage.setMediaBox(page.findMediaBox());
		// newPage.setRotation(page.findRotation());
	}

	/**
	 * Close the any internal resources, including the parsed paged document
	 * available from {@link #getSplitDocument()}.
	 */
	public void dispose() {
		try {
			inDoc.close();
			outDoc.close();
		} catch (IOException e) {
			log.log(Level.INFO,
					e,
					() -> "Could not close the input and/or output PDF documents");
		}
	}

	/**
	 * Returns a destination page for a certain object read from the input
	 * document. Which destination page to return is a function of the object's
	 * location in the input document, including its page number and Y-axis
	 * location on that page.
	 * 
	 * @param objectPageNum
	 *            the 0-index number of the page where the object is found in
	 *            the input document
	 * @param originPageYLocation
	 *            the Y-axis location of the object on the page from the input
	 *            document
	 * @return the destination page for this object
	 */
	@SuppressWarnings("unchecked")
	protected PDPage getPageForObject(int objectPageNum, float objectYPosition) {
		// Determine what page the object *should* land on
		int pageIndex = getPageIndexForObject(objectPageNum, objectYPosition);
		// Find or create the page
		List<PDPage> pages = outDoc.getDocumentCatalog().getAllPages();
		while (pageIndex >= pages.size()) {
			PDPage page = new PDPage();
			outDoc.addPage(page);
			pages = outDoc.getDocumentCatalog().getAllPages();
		}
		return pages.get(pageIndex);
	}

	/**
	 * Return the page number that a particular object should be written to.
	 * 
	 * @param objectOriginalPageNum
	 *            the page number (0 index) that the object appears on in the
	 *            given document
	 * @param objectYPosition
	 *            the vertical position of the object on the page in the given
	 *            document. Position is measured in pixels from the origin in
	 *            the lower left corner of the page.
	 * @return the 0 index page number that the object should appear on in the
	 *         output document.
	 * @throws IllegalArgumentException
	 *             if objectYPosition is outside the expected content range
	 *             (outside {@link #CONTENT_MAX_Y} and {@link #CONTENT_MIN_Y})
	 */
	protected int getPageIndexForObject(int objectOriginalPageNum,
			float objectYPosition) {
		// Ensure valid page number
		if (objectYPosition < CONTENT_MIN_Y || objectYPosition > CONTENT_MAX_Y) {
			throw new IllegalArgumentException(
					"Unexpected vertical position for an order object: "
							+ objectYPosition + " pixels");
		}
		// Determine the vertical position within the expected content range
		float heightInRange = objectYPosition - CONTENT_MIN_Y;
		// Determine the page number by calculating the ratio of its position
		// to the content range's height, and rounding any remainders up.
		int pageIndex = ORDERS_PER_PAGE
				- (int) Math.ceil(ORDERS_PER_PAGE * heightInRange / CONTENT_RANGE);
		// Account for orders on previous pages
		return pageIndex + (objectOriginalPageNum * ORDERS_PER_PAGE);
	}
}
