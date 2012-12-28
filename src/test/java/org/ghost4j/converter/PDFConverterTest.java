/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.ghost4j.converter.PDFConverter;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.document.PSDocument;

import junit.framework.TestCase;

/**
 * PDFConverter tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConverterTest extends TestCase {
    
    public PDFConverterTest(String testName) {
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

    public void testConvertWithPS() throws Exception {

        PSDocument document = new PSDocument();
        document.load(new File("input.ps"));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFConverter converter = new PDFConverter();
        converter.convert(document, baos);

        assertTrue(baos.size() > 0);

        baos.close();
    }

    public void testConvertWithPSMultiProcess() throws Exception {

        final PSDocument document = new PSDocument();
        document.load(new File("input.ps"));

        final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        final ByteArrayOutputStream baos3 = new ByteArrayOutputStream();

        final PDFConverter converter = new PDFConverter();
        converter.setMaxProcessCount(2);
        
        Thread thread1 = new Thread(){
        	public void run() {
        		try{
        			System.out.println("START 1 " + Thread.currentThread());
        			converter.convert(document, baos1);
        			System.out.println("END 1 " + Thread.currentThread());
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
        			converter.convert(document, baos2);
        			System.out.println("END 2 " + Thread.currentThread());
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
        			converter.convert(document, baos3);
        			System.out.println("END 3 " + Thread.currentThread());
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	};
        };
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        assertTrue(baos1.size() > 0);
        baos1.close();
        
        assertTrue(baos2.size() > 0);
        baos2.close();
        
        assertTrue(baos3.size() > 0);
        baos3.close();
    }

    public void testConvertWithUnsupportedDocument() throws Exception {

        PDFDocument document = new PDFDocument();
        document.load(new File("input.pdf"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFConverter converter = new PDFConverter();
        try{
            converter.convert(document, baos);
            fail("Test failed");
        } catch(DocumentException e){
            assertTrue(e.getMessage().startsWith("Documents of class"));
        }

        baos.close();
    }

}
