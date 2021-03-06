package com.spi.ordersplit;

import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
	static final double CONTENT_MAX_Y = 694.52d;
	static final double CONTENT_MIN_Y = 28.32d;
	static final double CONTENT_RANGE = CONTENT_MAX_Y - CONTENT_MIN_Y;
	static final double MOVE_CONTENT_TO_TOP = 80.0d;
	static final double ROW_HEIGHT = CONTENT_RANGE / ORDERS_PER_PAGE;
	static final double PRECISION = 1000.0d;
	static final PDFont DEFAULT_FONT = PDType1Font.HELVETICA;

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
		List<PDPage> pages = inDoc.getDocumentCatalog().getAllPages();
		for (int pageNum = 0; pageNum < pages.size(); pageNum++) {
			log.fine("Processing page #" + (pageNum + 1));
			PDPage page = pages.get(pageNum);
			List<Object> inTokens = page.getContents().getStream().getStreamTokens();
			PDPage outPage = null;
			PDPageContentStream outStream = null;
			double outPageYCursor = 0.0f;
			double outPageXCursor = 0.0f;
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
							outStream = null;
							outPageYCursor = 0.0f; // Keeps track of multiple TD requests in a single
							outPageXCursor = 0.0f; // text block, which start relative to a 0,0 origin and
							break;		           // then proceed relative to each previous "move text"
						case "TD": // Move Text Position
							double textY = ((COSFloat)operandStack.pop()).doubleValue();
							double textX = ((COSFloat)operandStack.pop()).doubleValue();
							if (outStream != null) {
								outStream.endText();
								outStream.close();
								outStream = null;
							}
							int outPageIndex = getPageIndexForObject(pageNum, textY + outPageYCursor); 
							outPage = getPageAtIndex(outPageIndex);
							if (outPage != null) {
								outPage.setResources(page.findResources());
								outStream = new PDPageContentStream(outDoc, outPage, true, true);
								outStream.setFont(DEFAULT_FONT, 8.0f); // Set a default or NO text will show
								outStream.beginText();                 // up if you start drawing strings
							}
							if (outStream != null) {
								float xTranslation = (float)(textX + outPageXCursor);
								float yTranslation = (float)(textY + outPageYCursor);
								yTranslation += (outPageIndex % ORDERS_PER_PAGE) * ROW_HEIGHT;
								yTranslation += MOVE_CONTENT_TO_TOP;
								outStream.moveTextPositionByAmount(xTranslation, yTranslation);
							}
							outPageYCursor += textY;
							outPageXCursor += textX;
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
								outStream.close();
								outStream = null;
							}
							break;
							
						// These operators fall outside the scope of a text object.
						// If the outStream is initialized, it will be because of 
						// a preceding text object. Ride its coat tails and write
						// these operations to the same page as the preceding text
						// object.
						case "m":  // Begin New Subpath
							float moveToY = ((COSFloat)operandStack.pop()).floatValue();
							float moveToX = ((COSFloat)operandStack.pop()).floatValue();
							if (outStream != null) {
								outStream.moveTo(moveToX, moveToY);
							}
							break;
						case "re": // Append Rectangle To Path
							float rectHeight = ((COSFloat)operandStack.pop()).floatValue();
							float rectWidth = ((COSFloat)operandStack.pop()).floatValue();
							float rectY = ((COSFloat)operandStack.pop()).floatValue();
							float rectX = ((COSFloat)operandStack.pop()).floatValue();
							if (outStream != null) {
								outStream.addRect(rectX, rectY, rectWidth, rectHeight);
							}
							break;
						case "l":  // Append Straight Line
							float lineY = ((COSFloat)operandStack.pop()).floatValue();
							float lineX = ((COSFloat)operandStack.pop()).floatValue();
							if (outStream != null) {
								outStream.lineTo(lineX, lineY);
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
						case "g":  // Set Non-Stroking Gray Level
							Object nonStrokingGrayLevelObject = operandStack.pop();
							if (outStream != null) {
								if (nonStrokingGrayLevelObject instanceof COSInteger) {
									int nonStrokingGrayLevelInt = ((COSInteger)nonStrokingGrayLevelObject).intValue();
									outStream.setStrokingColor(nonStrokingGrayLevelInt);
								} else if (nonStrokingGrayLevelObject instanceof COSFloat) {
									double nonStrokingGrayLevelDouble = ((COSFloat)nonStrokingGrayLevelObject).doubleValue();
									outStream.setStrokingColor(nonStrokingGrayLevelDouble);
								}
							}
							break;
						case "J":  // Set Line Cap Style
							int capStyle = ((COSInteger)operandStack.pop()).intValue();
							if (outStream != null) {
								outStream.setLineCapStyle(capStyle);
							}
							break;
						case "j":  // Set Line Join
							int lineJoinStyle = ((COSInteger)operandStack.pop()).intValue();
							if (outStream != null) {
								outStream.setLineJoinStyle(lineJoinStyle);
							}
							break;
						case "d":  // Set Line Dash
							int lineDashPhase = ((COSInteger)operandStack.pop()).intValue();
							float[] lineDashPattern = ((COSArray)operandStack.pop()).toFloatArray();
							if (outStream != null) {
								outStream.setLineDashPattern(lineDashPattern, lineDashPhase);
							}
							break;
						case "w":  // Set Line Width
							float lineWidth = ((COSFloat)operandStack.pop()).floatValue();
							if (outStream != null) {
								outStream.setLineWidth(lineWidth);
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
	 * Returns a destination page for a given index, creating any intermediate
	 * pages as necessary.
	 * 
	 * @param pageIndex
	 *            the 0-index number of the desired page
	 * @return the requested page, or null if pageIndex is less than zero.
	 */
	@SuppressWarnings("unchecked")
	protected PDPage getPageAtIndex(int pageIndex) {
		// Check the object is within the expected vertical range
		if (pageIndex < 0) { return null; }
		// Find or create the page
		List<PDPage> pages = outDoc.getDocumentCatalog().getAllPages();
		while (pageIndex >= pages.size()) {
			outDoc.addPage(new PDPage());
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
	protected int getPageIndexForObject(int objectPageNum, double objectYPosition) {
		// Ensure valid page number
		if (objectYPosition < CONTENT_MIN_Y || objectYPosition > CONTENT_MAX_Y) {
			return -1;
		}
		// Determine the vertical position within the expected content range
		double heightInRange = objectYPosition - CONTENT_MIN_Y;
		heightInRange = (Math.round(heightInRange * PRECISION) - 1) / PRECISION;
		// Determine the page number by calculating the ratio of its position
		// to the content range's height, and rounding any remainders up.
		int pageIndex = ORDERS_PER_PAGE - (int) Math.ceil(ORDERS_PER_PAGE * heightInRange
						/ CONTENT_RANGE);
		// Account for orders on previous pages
		return pageIndex + (objectPageNum * ORDERS_PER_PAGE);
	}
}
