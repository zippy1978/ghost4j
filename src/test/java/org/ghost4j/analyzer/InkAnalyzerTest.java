/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.document.PSDocument;

/**
 * InkAnalyzer tests.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 * 
 */
public class InkAnalyzerTest extends TestCase {

    public InkAnalyzerTest(String testName) {
	super(testName);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testAnalyzeWithPDF() throws Exception {

	PDFDocument document = new PDFDocument();
	document.load(new File("input-2pages.pdf"));

	InkAnalyzer inkAnalyzer = new InkAnalyzer();
	List<AnalysisItem> result = inkAnalyzer.analyze(document);

	assertEquals(2, result.size());
	assertTrue((((InkAnalysisItem) result.get(0)).getC()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getM()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getY()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getK()) > 0);
    }

    public void testAnalyzeWithPS() throws Exception {

	PSDocument document = new PSDocument();
	document.load(new File("input-2pages.ps"));

	InkAnalyzer inkAnalyzer = new InkAnalyzer();
	List<AnalysisItem> result = inkAnalyzer.analyze(document);

	assertEquals(2, result.size());
	assertTrue((((InkAnalysisItem) result.get(0)).getC()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getM()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getY()) > 0);
	assertTrue((((InkAnalysisItem) result.get(0)).getK()) > 0);
    }

    public void testAnalyzeWithPDFMultiProcess() throws Exception {

	final PDFDocument document = new PDFDocument();
	document.load(new File("input.pdf"));

	final InkAnalyzer inkAnalyzer = new InkAnalyzer();
	inkAnalyzer.setMaxProcessCount(2);

	final List<AnalysisItem> result1 = new ArrayList<AnalysisItem>();
	final List<AnalysisItem> result2 = new ArrayList<AnalysisItem>();
	final List<AnalysisItem> result3 = new ArrayList<AnalysisItem>();

	Thread thread1 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 1 " + Thread.currentThread());
		    result1.addAll(inkAnalyzer.analyze(document));
		    System.out.println("END 1 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread1.start();

	Thread thread2 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 2 " + Thread.currentThread());
		    result2.addAll(inkAnalyzer.analyze(document));
		    System.out.println("END 2 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread2.start();

	// the last one will block until a previous one finishes
	Thread thread3 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 3 " + Thread.currentThread());
		    result3.addAll(inkAnalyzer.analyze(document));
		    System.out.println("END 3 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread3.start();

	thread1.join();
	thread2.join();
	thread3.join();

	assertEquals(1, result1.size());
	assertEquals(1, result2.size());
	assertEquals(1, result3.size());

    }
}
