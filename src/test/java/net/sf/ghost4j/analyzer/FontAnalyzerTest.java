/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.analyzer;

import java.io.File;
import java.util.List;

import net.sf.ghost4j.document.PDFDocument;
import junit.framework.TestCase;

/**
 * FontAnalyzer tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 *
 */
public class FontAnalyzerTest extends TestCase {

	public FontAnalyzerTest(String testName) {
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
		document.load(new File("input.pdf"));
		
		FontAnalyzer fontAnalyzer = new FontAnalyzer();
		List<AnalysisItem> result = fontAnalyzer.analyze(document);
		
		assertEquals(4, result.size());
	}
	
	public void testAnalyzeWithPDFMultiProcess() throws Exception {
		
		final PDFDocument document = new PDFDocument();
		document.load(new File("input.pdf"));
		
		final FontAnalyzer fontAnalyzer = new FontAnalyzer();
		fontAnalyzer.setMaxProcessCount(2);
		
		Thread thread1 = new Thread(){
        	public void run() {
        		try{
        			System.out.println("START 1 " + Thread.currentThread());
        			List<AnalysisItem> result = fontAnalyzer.analyze(document);
        			System.out.println("END 1 " + Thread.currentThread());
        			assertEquals(4, result.size());
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	};
        };
        thread1.start();
        
        Thread thread2 = new Thread(){
        	public void run() {
        		try{
        			System.out.println("START 2 " + Thread.currentThread());
        			List<AnalysisItem> result = fontAnalyzer.analyze(document);
        			System.out.println("END 2 " + Thread.currentThread());
        			assertEquals(4, result.size());
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	};
        };
        thread2.start();
        
        //the last one will block until a previous one finishes
        Thread thread3 = new Thread(){
        	public void run() {
        		try{
        			System.out.println("START 3 " + Thread.currentThread());
        			List<AnalysisItem> result = fontAnalyzer.analyze(document);
        			System.out.println("END 3 " + Thread.currentThread());
        			assertEquals(4, result.size());
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	};
        };
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
		
		
	}
}
