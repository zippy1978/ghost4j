/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.util.DiskStore;

/**
 * Font analyzer: analyze fonts used in a document.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 * 
 */
public class FontAnalyzer extends AbstractRemoteAnalyzer {

    public FontAnalyzer() {

	// set supported classes
	supportedDocumentClasses = new Class[1];
	supportedDocumentClasses[0] = PDFDocument.class;
    }

    /**
     * Main method used to start the analyzer in standalone 'slave mode'.
     * 
     * @param args
     * @throws AnalyzerException
     */
    public static void main(String args[]) throws AnalyzerException {

	startRemoteAnalyzer(new FontAnalyzer());
    }

    /**
     * @return A list of FontAnalysisItem
     */
    @Override
    public List<AnalysisItem> run(Document document) throws IOException,
	    AnalyzerException, DocumentException {

	// assert document is supported
	this.assertDocumentSupported(document);

	// support PDF documents only at the moment
	return run((PDFDocument) document);
    }

    private List<AnalysisItem> run(PDFDocument document) throws IOException,
	    AnalyzerException {

	// get Ghostscript instance
	Ghostscript gs = Ghostscript.getInstance();

	// generate a unique diskstore key
	DiskStore diskStore = DiskStore.getInstance();
	String inputDiskStoreKey = diskStore.generateUniqueKey();
	// write document to input file
	document.write(diskStore.addFile(inputDiskStoreKey));

	// prepare args
	String[] gsArgs = {
		"-fonta",
		"-dQUIET",
		"-dNOPAUSE",
		"-dBATCH",
		"-dNODISPLAY",
		"-sFile="
			+ diskStore.getFile(inputDiskStoreKey)
				.getAbsolutePath(), "-sOutputFile=%stdout",
		"-f", "-" };

	// load .ps script
	InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream("script/AnalyzePDFFonts.ps");

	try {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    // execute and exit interpreter
	    synchronized (gs) {

		gs.setStdIn(is);
		gs.setStdOut(baos);
		gs.initialize(gsArgs);
	    }

	    // parse results from stdout
	    List<AnalysisItem> result = new ArrayList<AnalysisItem>();
	    String scriptResult = baos.toString();

	    String[] lines = scriptResult.split("\n");
	    boolean inResults = false;
	    for (String line : lines) {

		if (line.equals("---")) {
		    // start of result output detected
		    inResults = true;
		} else if (inResults) {
		    String[] columns = line.split(" ");
		    if (columns.length == 2) {
			// create new font analysis item object
			FontAnalysisItem font = new FontAnalysisItem();

			// remove prefix from font name
			String name = columns[0];
			String[] nameParts = name.split("\\+");
			if (nameParts.length > 1) {
			    name = nameParts[1];
			    // if prefix: it is a subset
			    font.setSubSet(true);
			}
			font.setName(name);
			font.setEmbedded(false);
			if (columns[1].equals("EM") || columns[1].equals("SU")) {
			    font.setEmbedded(true);
			}
			result.add(font);
		    }
		}

	    }

	    baos.close();
	    return result;

	} catch (GhostscriptException e) {

	    throw new AnalyzerException(e);

	} finally {

	    IOUtils.closeQuietly(is);

	    // delete Ghostscript instance
	    try {
		Ghostscript.deleteInstance();
	    } catch (GhostscriptException e) {
		throw new AnalyzerException(e);
	    }

	    // remove temporary file
	    diskStore.removeFile(inputDiskStoreKey);
	}

    }

}
