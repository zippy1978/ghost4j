Ghost4J
=======

<p align="center">
<img src="http://www.ghost4j.org/images/home-logo.png"/>
</p>


Ghost4J binds the Ghostscript C API to bring Ghostscript power to the Java world.
It also provides a high-level API to handle PDF and Postscript documents with objects.

### A simple PS to PDF conversion example

	package org.ghost4j.example;
	
	import java.io.File;
	import java.io.FileOutputStream;
	import org.apache.commons.io.IOUtils;
	import org.ghost4j.converter.PDFConverter;
	import org.ghost4j.document.PSDocument;
	
	/**
	 * Example showing how to convert a Postscript document to PDF using the high level API.
	 * @author Gilles Grousset (gi.grousset@gmail.com)
	 */
	public class PDFConverterExample {
	
	    public static void main(String[] args) {
	
	        FileOutputStream fos = null;
	        try{
	
	            //load PostScript document
	            PSDocument document = new PSDocument();
	            document.load(new File("input.ps"));
	
	            //create OutputStream
	            fos = new FileOutputStream(new File("rendition.pdf"));
	
	            //create converter
	            PDFConverter converter = new PDFConverter();
	
	            //set options
	            converter.setPDFSettings(PDFConverter.OPTION_PDFSETTINGS_PREPRESS);
	
	            //convert
	            converter.convert(document, fos);
	
	        } catch (Exception e) {
	            System.out.println("ERROR: " + e.getMessage());
	        } finally{
	            IOUtils.closeQuietly(fos);
	        }
	
	
	    }
	}
	
### Getting binaries

Binary distributions can be downloaded from [here](http://www.ghost4j.org/downloads.html)

### Documentation

Documentation is available from [http://www.ghost4j.org](http://www.ghost4j.org)
