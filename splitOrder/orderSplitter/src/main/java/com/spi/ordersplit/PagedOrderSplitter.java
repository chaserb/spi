package com.spi.ordersplit;

import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Reads a PDF file at a given file location, and inspects the orders contained
 * therein. It then creates a new PDF file with one page per order from the
 * origin document, placing the text of each order at the top of its new page.
 * 
 * @author chase.barrett
 */
public class PagedOrderSplitter {

	static final Logger log = Logger.getLogger(PagedOrderSplitter.class
			.getName());
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
	 * parsed output from the input document. This output document will be empty
	 * until {@link #splitOrders()} is invoked.
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

		List<PDPage> pages = inDoc.getDocumentCatalog().getAllPages();
		for (int pageNum = 0; pageNum < pages.size(); pageNum++) {
			log.fine("Processing page #" + (pageNum + 1));
			PDPage page = pages.get(pageNum);
			List<Object> inTokens = page.getContents().getStream().getStreamTokens();
			PDPage outPage = null;
			PDPageContentStream outStream = null;
			Stack<Object> operandStack = new Stack<Object>();
			for (Iterator<Object> iter = inTokens.iterator(); iter.hasNext();) {
				Object inToken = iter.next();

				// Look for a text object that falls within the vertical
				// (y-axis) range of order content, and if you find one,
				// determine which page and output stream it should be
				// written to. Other objects that fall outside the vertical
				// range will be ignored.
				if (inToken instanceof PDFOperator) {
					PDFOperator operator = (PDFOperator)inToken;
					log.finer("PDF Operator: " + operator.getOperation());
					switch (operator.getOperation()) {
					
						case "J":  // Set Line Cap Style
						case "d":  // Set Line Dash
						case "g":  // Set Gray Level
						case "j":  // Set Line Join
						case "l":  // Append Straight Line
						case "m":  // Begin New Subpath
						case "w":  // Set Line Width

						// The following operators pertain to a text object. For
						// each text object, presume that the "Move Text Position"
						// operator will be the first operator inside the text 
						// object and use its Y-position to determine which page 
						// the object and any trailing operands should be written
						// to.
						//
						// Whenever a new text object is encountered, reset the
						// outPage and outStream to ensure we're writing to the
						// correct page.
						case "BT": // Begin Text
							outPage = null;
							if (outStream != null) { outStream.close(); }
							outStream = null;
							break;		
						case "TD": // Move Text Position
							float textY = ((COSFloat)operandStack.pop()).floatValue();
							float textX = ((COSFloat)operandStack.pop()).floatValue();
							if (outPage == null) {
								outPage = getPageForObject(pageNum, textY);
							}
							if (outPage != null && outStream == null) {
								outPage.setResources(page.findResources());
								outStream = new PDPageContentStream(outDoc, outPage, true, true);
								outStream.beginText();
							}
							if (outStream != null) {
								outStream.moveTextPositionByAmount(textX, textY);
							}
							break;
						case "Tf": // Set Text Font and Size
							float fontSize = ((COSFloat)operandStack.pop()).floatValue();
							String fontName = ((COSName)operandStack.pop()).getName();
							if (outStream != null) {
								outStream.setFont(page.getResources().getFonts().get(fontName), fontSize);
							}
							break;
						case "Tj": // Show Text
							String text = ((COSString)operandStack.pop()).getString();
							if (outStream != null) {
								outStream.drawString(text);
							}
							break;
						case "ET": // End Text Object
							if (outStream != null) {
								outStream.endText();
							}
							break;
							
						// These operators fall outside the scope of a text object.
						// If the outStream is initialized, it will be because of 
						// a preceding text object. Ride its coat tails and write
						// these operations to the same page as the preceding text
						// object.
						case "re": // Append Rectangle To Path
							float rectHeight = ((COSFloat)operandStack.pop()).floatValue();
							float rectWidth = ((COSFloat)operandStack.pop()).floatValue();
							float rectY = ((COSFloat)operandStack.pop()).floatValue();
							float rectX = ((COSFloat)operandStack.pop()).floatValue();
							if (outStream != null) {
								outStream.addRect(rectX, rectY, rectWidth, rectHeight);
							}
							break;
						case "G":  // Set Gray Level
							Object grayLevelObject = operandStack.pop();
							if (outStream != null) {
								if (grayLevelObject instanceof COSInteger) {
									int grayLevelInt = ((COSInteger)grayLevelObject).intValue();
									outStream.setStrokingColor(grayLevelInt);
								} else if (grayLevelObject instanceof COSFloat) {
									float grayLevelFloat = ((COSFloat)grayLevelObject).floatValue();
									outStream.setStrokingColor(grayLevelFloat);
								}
							}
							break;
						case "Do": // Invoke Named XObject
							operandStack.pop();
							// Ignore this for now
							break;
						case "cm": // Concatenate Matrix to Current Transformation Matrix
							operandStack.pop();
							operandStack.pop();
							operandStack.pop();
							operandStack.pop();
							operandStack.pop();
							operandStack.pop();
							// Ignore this for now
						default:
					}
					if (outStream != null) {
						switch (operator.getOperation()) {
							// These operations are not related to text objects,
							// do not require operands, and therefore do not
							// need to clean up the operand stack. Only invoke these
							// operations if the outStream is initialized.
							case "q":  // Save Graphics State
								outStream.saveGraphicsState();
								break;
							case "W":  // Set Clipping Path Using Non Zero Winding Rule
								outStream.clipPath(PathIterator.WIND_NON_ZERO);
								break;
							case "n":  // End Path Without Filling or Stroking
								break;
							case "Q":  // Restore Graphics State
								outStream.restoreGraphicsState();
								break;
							case "S":  // Stroke Path
								outStream.restoreGraphicsState();
								break;
							case "f*": // Fill Path
								outStream.fill(PathIterator.WIND_EVEN_ODD);
							default:
						}
					}
				} else {
					operandStack.push(inToken);
				}
			}
			if (outStream != null) { outStream.close(); }
		}
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
	 * @return the destination page for this object, or null if the text object
	 *         is outside the expected range.
	 */
	@SuppressWarnings("unchecked")
	protected PDPage getPageForObject(int objectPageNum, float objectYPosition) {
		// Determine what page the object *should* land on
		int pageIndex = getPageIndexForObject(objectPageNum, objectYPosition);
		// Check the object is within the expected vertical range
		if (pageIndex < 0) { return null; }
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
	 * @param objectPageNum
	 *            the page number (0 index) that the object appears on in the
	 *            given document
	 * @param objectYPosition
	 *            the vertical position of the object on the page in the given
	 *            document. Position is measured in pixels from the origin in
	 *            the lower left corner of the page.
	 * @return the 0 index page number that the object should appear on in the
	 *         output document. If the value falls outside the expected content
	 *         range, return -1
	 */
	protected int getPageIndexForObject(int objectPageNum, float objectYPosition) {
		// Ensure valid page number
		if (objectYPosition < CONTENT_MIN_Y || objectYPosition > CONTENT_MAX_Y) {
			return -1;
		}
		// Determine the vertical position within the expected content range
		float heightInRange = objectYPosition - CONTENT_MIN_Y;
		// Determine the page number by calculating the ratio of its position
		// to the content range's height, and rounding any remainders up.
		int pageIndex = ORDERS_PER_PAGE
				- (int) Math.ceil(ORDERS_PER_PAGE * heightInRange
						/ CONTENT_RANGE);
		// Account for orders on previous pages
		return pageIndex + (objectPageNum * ORDERS_PER_PAGE);
	}
}
