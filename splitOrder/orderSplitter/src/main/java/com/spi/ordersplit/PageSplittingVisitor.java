package com.spi.ordersplit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDPage;

public class PageSplittingVisitor implements ICOSVisitor {

	private static final Logger log = Logger.getLogger(PageSplittingVisitor.class.getName());

	private List<PDPage> pages = new ArrayList<PDPage>();
	private int cursor = 0;
	
	public PageSplittingVisitor() {	}
	
	public List<PDPage> getPages() {
		return pages;
	}

	public void setPages(List<PDPage> pages) {
		this.pages = pages;
	}

	@Override
	public Object visitFromArray(COSArray obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromBoolean(COSBoolean obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromDictionary(COSDictionary obj)
			throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromDocument(COSDocument obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromFloat(COSFloat obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromInt(COSInteger obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromName(COSName obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromNull(COSNull obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromStream(COSStream obj) throws COSVisitorException {
		return null;
	}

	@Override
	public Object visitFromString(COSString obj) throws COSVisitorException {
		return null;
	}

	private COSStream getStream() throws COSVisitorException {
		try {
			return getPages().get(cursor).getContents().getStream();
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
	}
}
