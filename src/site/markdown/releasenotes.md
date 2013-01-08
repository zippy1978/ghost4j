Release notes
=============

#### Snapshot version

* Added antialiasing parameter on SimpleRenderer.
* Added *extract* method on Document to allow extraction of a range of pages to a new document.
* Added *append* method on Document to allow appending a document to the current one (may not be working in all cases with PostScript file, when document use different resources).

#### Version 0.4.6 - 30/12/2012

* Renamed package net.sf.ghost4j to org.ghost4j.

#### Version 0.4.5 - 15/02/2012

* Fixed bug with PSDocument getPageCount method (Thanks Jörg!).
  
* Fixed bug with Renderer with 64bit archs (see http://bugs.ghostscript.com/show_bug.cgi?format=multiple&id=692754).
  
#### Version 0.4.4 - 26/11/2011

* Added paperSize property on PDFConverter and PSConverter.

* Support for 64bits architecture on Windows (see http://sourceforge.net/projects/ghost4j/forums/forum/886757/topic/4597527).

#### Version 0.4.3 - 01/06/2011

* Added ghost4j.encoding property support to specify the file encoding to use when working with stdin.
 
#### Version 0.4.2 - 18/03/2011

* Fixed multi-process support when running library from a Servlet container on Windows.

#### Version 0.4.1 - 17/03/2011

* Fixed multi-process support when running library from a Servlet container (Thanks Michael!).
  
#### Version 0.4.0 - 02/12/2010

* High level API with multi-process support and initial components.
  
* PDFConverter: convert Postscript or PDF documents to PDF.
  
* PSConverter: convert Postscript or PDF documents to Postscript.
  
* FontAnalyzer: analyze fonts in a PDF document.
  
* SimpleRenderer: render a Postscript or PDF document as images.
  
#### Version 0.3.3 - 04/09/2010

* Fixed display callback compatibility for old Ghostscript versions.

#### Version 0.3.2 - 03/14/2010

* Added Log4J support for messages outputted by the Ghostscript interpreter on stdout / stderr.

#### Version 0.3 - 07/26/2009

* Added display callback support to interact with Ghostscript raster output.

* Fixed bug on Ghostscript.exit() method: did not call the native API exit function.
    This bug resulted in: unable to delete output files, output file not completly written.

* Added code samples in net.sf.ghost4j.example package.
