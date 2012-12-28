High level API samples
======================

This section shows some code examples using the high level API.

#### Convert Postscript file to PDF using PDFConverter

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


#### Count pages of a Postscript document using PSDocument

	package org.ghost4j.example;
	
	import java.io.File;
	
	import org.ghost4j.document.PSDocument;
	
	/**
	 * Example showing how to count pages of a PostScript.
	 * @author Gilles Grousset (gi.grousset@gmail.com)
	 */
	public class PSPageCountExample {
	
	    public static void main(String[] args) {
	        try {
	
	            PSDocument psDocument = new PSDocument();
	            psDocument.load(new File("input.ps"));
	            System.out.println("Page count is : " + psDocument.getPageCount());
	
	        } catch (Exception e) {
	            System.out.println("ERROR: " + e.getMessage());
	        }
	    }
	}

#### List fonts of a PDF document using FontAnalyzer

	package org.ghost4j.example;
	
	import java.io.File;
	import java.util.List;
	
	import org.ghost4j.analyzer.AnalysisItem;
	import org.ghost4j.analyzer.FontAnalyzer;
	import org.ghost4j.document.PDFDocument;
	
	
	/**
	 * Example showing how to list fonts of a PDF document using the high level API.
	 * @author Gilles Grousset (gi.grousset@gmail.com)
	 */
	public class FontAnalyzerExample {
	
		public static void main(String[] args) {
	
			try {
	
				// load PDF document
				PDFDocument document = new PDFDocument();
				document.load(new File("input.pdf"));
	
				// create analyzer
				FontAnalyzer analyzer = new FontAnalyzer();
	
				// analyze
				List<AnalysisItem> fonts = analyzer.analyze(document);
	
				// print result
				for (AnalysisItem analysisItem : fonts) {
					System.out.println(analysisItem);
	
				}
	
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage());
			}
	
		}
	}

#### Render a PDF document using SimpleRenderer

	package org.ghost4j.example;
	
	import java.awt.Image;
	import java.awt.image.RenderedImage;
	import java.io.File;
	import java.io.IOException;
	import java.util.List;
	
	import javax.imageio.ImageIO;
	
	import org.ghost4j.analyzer.AnalysisItem;
	import org.ghost4j.analyzer.FontAnalyzer;
	import org.ghost4j.document.PDFDocument;
	import org.ghost4j.renderer.SimpleRenderer;
	
	
	/**
	 * Example showing how to render pages of a PDF document using the high level API.
	 * @author Gilles Grousset (gi.grousset@gmail.com)
	 */
	public class SimpleRendererExample {
	
		public static void main(String[] args) {
	
			try {
	
				// load PDF document
				PDFDocument document = new PDFDocument();
				document.load(new File("input.pdf"));
	
				// create renderer
				SimpleRenderer renderer = new SimpleRenderer();
				
				// set resolution (in DPI)
				renderer.setResolution(300);
	
				// render
				List<Image> images = renderer.render(document);
	
				// write images to files to disk as PNG
		        try {
		            for (int i = 0; i < images.size(); i++) {
		                ImageIO.write((RenderedImage) images.get(i), "png", new File((i + 1) + ".png"));
		            }
		        } catch (IOException e) {
		            System.out.println("ERROR: " + e.getMessage());
		        }
	
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage());
			}
	
		}
	}


