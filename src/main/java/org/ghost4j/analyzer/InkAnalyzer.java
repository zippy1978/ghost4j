/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.document.PSDocument;
import org.ghost4j.util.DiskStore;

/**
 * Ink analyzer: analyze ink coverage of a document. For some reason, the API
 * crashed when trying to analyze a document on more than one page. To prevent
 * the crash, each page of the document is analyzed separately.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 * 
 */
public class InkAnalyzer extends AbstractRemoteAnalyzer implements Analyzer {

    public InkAnalyzer() {

	// set supported classes
	supportedDocumentClasses = new Class[2];
	supportedDocumentClasses[0] = PDFDocument.class;
	supportedDocumentClasses[1] = PSDocument.class;
    }

    /**
     * Main method used to start the analyzer in standalone 'slave mode'.
     * 
     * @param args
     * @throws AnalyzerException
     */
    public static void main(String args[]) throws AnalyzerException {

	startRemoteAnalyzer(new InkAnalyzer());
    }

    /**
     * Performs ink analysis on a single page document.
     * 
     * @param page
     *            Single page document
     * @return An AnalysisItem
     * @throws IOException
     * @throws AnalyzerException
     * @throws DocumentException
     */
    private InkAnalysisItem analyzeSinglePage(Document page)
	    throws IOException, AnalyzerException, DocumentException {

	// get Ghostscript instance
	Ghostscript gs = Ghostscript.getInstance();

	// generate a unique diskstore key for input and output
	DiskStore diskStore = DiskStore.getInstance();
	String inputDiskStoreKey = diskStore.generateUniqueKey();
	String outputDiskStoreKey = diskStore.generateUniqueKey();
	// write page to input file
	page.write(diskStore.addFile(inputDiskStoreKey));

	// prepare args
	// strange thing : result cannot be get with stdout (need to store in a
	// temp file)
	String[] gsArgs = { "-inkcov", "-dBATCH", "-dNOPAUSE", "-dQUIET",
		"-sDEVICE=inkcov",
		"-sOutputFile=" + diskStore.addFile(outputDiskStoreKey), "-f",
		diskStore.getFile(inputDiskStoreKey).getAbsolutePath() };

	FileReader fr = null;

	try {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    // execute and exit interpreter
	    synchronized (gs) {
		gs.initialize(gsArgs);
		gs.exit();
	    }

	    // parse results from stdout
	    fr = new FileReader(diskStore.getFile(outputDiskStoreKey));
	    char[] chars = new char[100];
	    fr.read(chars);
	    String output = new String(chars).trim();
	    InkAnalysisItem item = new InkAnalysisItem();

	    // CMYK
	    if (output.endsWith("CMYK OK")) {
		String[] components = output.split("CMYK")[0].split("  ");

		if (components.length == 4) {
		    item.setC(this.parseValue(components[0]));
		    item.setM(this.parseValue(components[1]));
		    item.setY(this.parseValue(components[2]));
		    item.setK(this.parseValue(components[3]));
		}
	    }

	    baos.close();

	    return item;

	} catch (Exception e) {

	    throw new AnalyzerException(e);

	} finally {

	    IOUtils.closeQuietly(fr);

	    // delete Ghostscript instance
	    try {
		Ghostscript.deleteInstance();
	    } catch (GhostscriptException e) {
		throw new AnalyzerException(e);
	    }

	    // remove temporary files
	    diskStore.removeFile(inputDiskStoreKey);
	    diskStore.removeFile(outputDiskStoreKey);
	}

    }

    /**
     * Parse a comma value to a double
     * 
     * @param value
     *            Value as String
     * @return a double
     * @throws ParseException
     */
    private double parseValue(String value) throws ParseException {
    	return Double.parseDouble(value.trim().replace(",","."));
    }

    @Override
    public List<AnalysisItem> run(Document document) throws IOException,
	    AnalyzerException, DocumentException {

	// assert document is supported
	this.assertDocumentSupported(document);

	// assert inkcov device is supported
	try {
	    this.assertDeviceSupported("inkcov");
	} catch (GhostscriptException e) {
	    throw new AnalyzerException(e);
	}

	List<AnalysisItem> result = new ArrayList<AnalysisItem>();

	// separate pages
	List<Document> pages = document.explode();

	// analyze each page separately
	int i = 1;
	for (Document page : pages) {
	    InkAnalysisItem item = this.analyzeSinglePage(page);
	    item.setPageIndex(i);
	    result.add(item);
	    i++;
	}

	return result;
    }
}
