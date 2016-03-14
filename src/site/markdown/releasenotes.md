Release notes
=============

#### Version 1.0.1 - 14/03/2016

 * Upgraded JNA to 4.1.0.

#### Version 1.0.0 - 16/06/2015

 * Artifact is now available from the Central Maven Repository.

#### Version 0.5.1 - 27/11/2013

* Fixed bug with antialiasing on *SimpleRenderer* (Thanks [squallssck](https://github.com/squallssck)).
* Fixed default autorotation of pages behavior in PDFConverter: by default no autorotage parameter is set anymore (Thanks [jmkgreen](https://github.com/jmkgreen)).

#### Version 0.5.0 - 25/01/2013

* Added *antialiasing* property on *SimpleRenderer*.
* Added *extract* method on *Document* to allow extraction of a range of pages to a new document.
* Added *append* method on *Document* to allow appending a document to the current one (may not be working in all cases with PostScript file, when document use different resources).
* Added *explode* method on *Document* to build one new document for each page of the current document.
* Upgraded xmlgraphics-commons to 1.4.
* Upgraded jna to 3.3.0.
* Added *PaperSize* class to manipulate paper sizes.
* Added *device* property to *PSConverter*, used to determine the Ghostscript device used for conversion. By default ps2write is now used if available in Ghostscript (see https://github.com/zippy1978/ghost4j/issues/1, thanks BXCY).
* Added *SafeAppenderModifier*, a modifier (new kind of component) able to append a document (any kind) to another (any kind) in a safe way (compared with the *append* method on *Document* class, that may not work when documents resources are different).
* Added *InkAnalyzer*, an analyzer in charge of retrieving CMYK ink coverage (in %) of each pages of a document.
* Added examples in *org.ghost4j.example* package.

#### Version 0.4.6 - 30/12/2012

* Renamed package *net.sf.ghost4j* to *org.ghost4j*.

#### Version 0.4.5 - 15/02/2012

* Fixed bug with *PSDocument* getPageCount method (Thanks Jörg!).
* Fixed bug on *Renderer* with 64bit archs (see http://bugs.ghostscript.com/show_bug.cgi?format=multiple&id=692754).
  
#### Version 0.4.4 - 26/11/2011

* Added *paperSize* property on *PDFConverter* and *PSConverter*.
* Support for 64bits architecture on Windows (see http://sourceforge.net/projects/ghost4j/forums/forum/886757/topic/4597527).

#### Version 0.4.3 - 01/06/2011

* Added *ghost4j.encoding* property support to specify the file encoding to use when working with stdin.
 
#### Version 0.4.2 - 18/03/2011

* Fixed multi-process support when running library from a Servlet container on Windows.

#### Version 0.4.1 - 17/03/2011

* Fixed multi-process support when running library from a Servlet container (Thanks Michael!).
  
#### Version 0.4.0 - 02/12/2010

* High level API with multi-process support and initial components.
* *PDFConverter*: convert Postscript or PDF documents to PDF.
* *PSConverter*: convert Postscript or PDF documents to Postscript.
* *FontAnalyzer*: analyze fonts in a PDF document.
* *SimpleRenderer*: render a Postscript or PDF document as images.
  
#### Version 0.3.3 - 04/09/2010

* Fixed display callback compatibility for old Ghostscript versions.

#### Version 0.3.2 - 03/14/2010

* Added Log4J support for messages outputted by the Ghostscript interpreter on stdout / stderr.

#### Version 0.3 - 07/26/2009

* Added display callback support to interact with Ghostscript raster output.
* Fixed bug on *Ghostscript.exit()* method: did not call the native API exit function.
    This bug resulted in: unable to delete output files, output file not completly written.
* Added code samples in *net.sf.ghost4j.example* package.
