package com.spi.ordersplit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * Reads a PDF file at a given file location, and inspects the orders
 * contained therein. It then creates a new PDF file with one page per
 * order from the origin document, placing the text of each order at
 * the top of its new page.
 *  
 * @author chase.barrett
 */
public class PagedOrderSplitter {

	private static final Logger log = Logger.getLogger(PagedOrderSplitter.class.getName());
	private static final int NUM_PAGES = 5;
	
	private PDDocument inDoc;
	private PDDocument outDoc;
	private List<PDPage> pages;
	private File origin;
    private Map<Object, COSBase> clonedVersion = new HashMap<Object, COSBase>();
    
    /**
     * Create the PagedOrderSplitter. 
     * 
     * @param origin the input file which contains several orders on a single page.
     * @throws IOException if there is an error reading from the given file.
     */
	public PagedOrderSplitter(File origin) throws IOException {
		this.origin = origin;
		inDoc = PDDocument.load(origin);
		outDoc = new PDDocument();
		pages = new ArrayList<PDPage>();
	}
	
	/**
	 * Retrieve the file passed into this splitter's constructor.
	 * 
	 * @return the file from which this splitter reads.
	 */
	public File getOrigin() {
		return origin;
	}

	/**
	 * For each order in the origin document, create a page in the split
	 * document, and place the order contents onto each page.
	 * 
	 * @param file
	 */
	public void splitOrders() throws IOException, COSVisitorException {
		copyAndSplitOrders();
	}
	
	/**
	 * Retrieve the paged output document, which contains the 
	 * page-per-order parsed output from the origin file. This
	 * document will be empty until {@link #splitOrders()} is 
	 * invoked.
	 * 
	 * @return the paged output document
	 */
	public PDDocument getSplitDocument() {
		return outDoc;
	}
	
	/**
	 * Close the any internal resources, including the parsed
	 * paged document available from {@link #getSplitDocument()}.
	 */
	public void dispose() {
		try {
			inDoc.close();
			outDoc.close();
		} catch (IOException e) {
			log.log(Level.INFO, e, () -> "Could not close the input and/or output PDF documents");
		}
	}

	/**
	 * Copies orders from the source document and places them
	 * on their own, new page in the destination document.
	 * 
	 * @throws COSVisitorException 
	 */
	@SuppressWarnings({ "unchecked" })
	protected void copyAndSplitOrders() throws IOException, COSVisitorException {
		
		PDDocumentInformation destInfo = outDoc.getDocumentInformation();
		PDDocumentInformation srcInfo = inDoc.getDocumentInformation();
		destInfo.getDictionary().mergeInto(srcInfo.getDictionary());

        //finally append the pages
        List<PDPage> pages = inDoc.getDocumentCatalog().getAllPages();
        for(PDPage page : pages) {
//TODO Broken here        	
            PDPage newPage = new PDPage((COSDictionary)cloneForNewDocument(page.getCOSDictionary()));
//            newPage.setCropBox(page.findCropBox());
//            newPage.setMediaBox(page.findMediaBox());
//            newPage.setRotation(page.findRotation());
        }
	}

	/**
	 *
	 * @param base
	 * @return
	 * @throws IOException
	 * @throws COSVisitorException 
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	private COSBase cloneForNewDocument(Object base)
			throws IOException, COSVisitorException {
		if (base == null) {
			return null;
		}
		COSBase retval = (COSBase) clonedVersion.get(base);
		if (retval != null) {
			// we are done, it has already been converted.
		} else if (base instanceof List) {
			COSArray array = new COSArray();
			List<COSName> list = (List<COSName>) base;
			for (int i = 0; i < list.size(); i++) {
				array.add(cloneForNewDocument(list.get(i)));
			}
			retval = array;
		} else if (base instanceof COSObjectable && !(base instanceof COSBase)) {
			retval = cloneForNewDocument(((COSObjectable) base).getCOSObject());
			clonedVersion.put(base, retval);
		} else if (base instanceof COSObject) {
			COSObject object = (COSObject) base;
			retval = cloneForNewDocument(object.getObject());
			clonedVersion.put(base, retval);
		} else if (base instanceof COSArray) {
			COSArray newArray = new COSArray();
			COSArray array = (COSArray) base;
			for (int i = 0; i < array.size(); i++) {
				newArray.add(cloneForNewDocument(array.get(i)));
			}
			retval = newArray;
			clonedVersion.put(base, retval);
		} else if (base instanceof COSStream) {
// This seems to be the magic sauce right here homeboys			
			COSStream originalStream = (COSStream) base;
			List<Object> contentTokens = originalStream.getStreamTokens();
			log.log(Level.FINER, "\nBEGIN COSStream: " + originalStream.size() + " tokens in " + originalStream + "\n");
			for (Object token : contentTokens) {
				log.log(Level.FINER, token.toString());
			}
			log.log(Level.FINER, "\nEND COSStream: " + originalStream.size() + " tokens in " + originalStream + "\n");
			List<COSName> keys = originalStream.keyList();
			PDStream stream = new PDStream(outDoc,
					originalStream.getFilteredStream(), true);
			clonedVersion.put(base, stream.getStream());
			for (int i = 0; i < keys.size(); i++) {
				COSName key = (COSName) keys.get(i);
				stream.getStream().setItem(
						key,
						cloneForNewDocument(originalStream.getItem(key)));
			}
			retval = stream.getStream();
		} else if (base instanceof COSDictionary) {
			COSDictionary dic = (COSDictionary) base;
			List<COSName> keys = dic.keyList();
			retval = new COSDictionary();
			clonedVersion.put(base, retval);
			for (int i = 0; i < keys.size(); i++) {
				COSName key = (COSName) keys.get(i);
				((COSDictionary) retval).setItem(key,
						cloneForNewDocument(dic.getItem(key)));
			}
		} else {
			retval = (COSBase) base;
		}
		clonedVersion.put(base, retval);
		return retval;
	}

	public void processPage(PDPage inPage, PDDocument outDoc)
			throws IOException, COSVisitorException {
		PDStream inContents = inPage.getContents();
		COSStream inContentStream = inContents.getStream();
		PageSplittingVisitor visitor = new PageSplittingVisitor();
		for (int i = 0; i < 6; i++) {
			PDPage page = new PDPage();
			outDoc.addPage(page);
			visitor.getPages().add(page);
		}
		inContentStream.accept(visitor);
	}
	
	static final float MAX_Y = 694.52f;
	static final float MIN_Y = 28.32f;
	static final float RANGE_Y = MAX_Y - MIN_Y; 
	static final float BUFFER_Y = 0.5f;
	
	/**
	 * Returns a destination page for a certain object read from the input
	 * document. Which destination page to return is a function of the object's
	 * location in the input document, including its page number and Y-axis
	 * location on that page.
	 * 
	 * @param originPageNum
	 *            the number of the page where the object is found in the input
	 *            document
	 * @param originPageYLocation
	 *            the Y-axis location of the object on the page from the input
	 *            document
	 * @return the destination page for this object
	 */
	protected PDPage getPageForObjectAtHeight(int originPageNum, float originPageYLocation) {
		// Determine the object's position within the content range
		float heightInRange = originPageYLocation - MIN_Y;
		// Determine the page number by calculating the ratio of its position
		// to the content range's height, and rounding any remainders up.
		int pageIndex = NUM_PAGES - (int)Math.ceil(NUM_PAGES * heightInRange / RANGE_Y);
		// Ensure valid page number
		if (pageIndex < 0) { pageIndex = 0; }
		if (pageIndex >= NUM_PAGES) { pageIndex = NUM_PAGES - 1; }
		// Find or create the page
		while (pageIndex >= pages.size()) {
			pages.add(createNewDestinationPage());
		}
		return pages.get(pageIndex);
	}
	
	protected PDPage createNewDestinationPage() {
		PDPage page = new PDPage();
		outDoc.addPage(page);
		return page;
	}
}
