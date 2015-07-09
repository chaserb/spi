package com.spi.ordersplit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Utility to read Safeguard order sheets (*.pdf) and convert them to a new file
 * (*.pdf) which puts each order at the top of a blank page.
 */
public class App {
	
	private static final Logger log = Logger.getLogger(App.class.getName());

	public static final Pattern PDF_FILE_PATTERN = Pattern.compile("(.*)(\\.pdf)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern PROCESSED_PDF_FILE_PATTERN = Pattern.compile(".*\\.processed\\.pdf$", Pattern.CASE_INSENSITIVE);
	public static final FilenameFilter UNPROCESSED_FILTER = (File dir, String name) ->
		PDF_FILE_PATTERN.matcher(name).matches() && 
		!PROCESSED_PDF_FILE_PATTERN.matcher(name).matches();

	/**
	 * Main application method
	 * 
	 * @param args
	 *            the file name to be processed, or the directory name which
	 *            contains the *.pdf files to be processed. If empty, then
	 *            assume the current working directory contains the *.pdf files.
	 */
	public static void main(String[] args) {
		List<File> files = identifyFiles(args);
		for (File file : files) {
			try {
				File outFile = getOutFile(file);
				log.info(() -> "Processing file: " + file);
				log.info(() -> "     Writing to: " + outFile + "\n");
				PagedOrderSplitter splitter = new PagedOrderSplitter(PDDocument.load(file));
				splitter.splitOrders();
				splitter.getSplitDocument().save(outFile);
				splitter.dispose();
			} catch (Exception e) {
				log.log(Level.SEVERE, e, () -> "Error processing file: " + e);
			}
		}
	}

	/**
	 * Interpret the given input arguments to identify the files to be processed
	 * 
	 * @param args
	 *            the file name to be processed, or the directory name which
	 *            contains the *.pdf files to be processed. If empty, then
	 *            assume the current working directory contains the *.pdf files.
	 * @return the list of files to be processed
	 */
	public static List<File> identifyFiles(String[] args) {
		File[] fileArray;
		File givenFile = (args == null || args.length == 0) ? new File(".") : new File(args[0]); 
		if (givenFile.isDirectory()) {
			fileArray = givenFile.listFiles(UNPROCESSED_FILTER);
		} else if (UNPROCESSED_FILTER.accept(givenFile.getParentFile(), givenFile.getName())) {
			fileArray = new File[] { givenFile };
		} else {
			fileArray = new File[] {};
		}
		return Arrays.asList(fileArray);
	}

	/**
	 * Determine the name of the output file for a given input file. This will
	 * replace "*.pdf" with "*.processed.pdf"
	 * 
	 * @param inFile
	 *            the input PDF filename, which must end in "*.pdf"
	 * @throws IllegalArgumentException
	 *             if the given filename does not match the "*.pdf" pattern, as
	 *             captured in the {@link PDFFilter#PATTERN} filename pattern.
	 * @return an output filename that reflects the input filename, but inserts
	 *         the word processed as appropriate
	 */
	public static File getOutFile(File inFile) {
		Matcher matcher = PDF_FILE_PATTERN.matcher(inFile.getAbsolutePath());
		if (matcher.matches()) {
			StringBuffer outFileName = new StringBuffer();
			matcher.appendReplacement(outFileName, "$1.processed$2");
			return new File(outFileName.toString());
		} else {
			throw new IllegalArgumentException("Does not appear to be a PDF file: " + inFile);
		}
	}
}
