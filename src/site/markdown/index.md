<p align="center">
<img src="images/home-logo.png"/>
</p>

<p align="center">
Ghost4J binds the Ghostscript C API to bring Ghostscript power to the Java world.
It also provides a high-level API to handle PDF and Postscript documents with objects.
</p>

### A simple example (PS to PDF conversion)

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

### Maven configuration

Ghost4J Maven artifact is available in a public repository for an easier integration in Maven projects.

If you do not wish to use Maven, binary distributions are available in the [Downloads](download.html) section.

<pre><code>
<repositories>

	...
	
	<repository>
		<id>org.ghost4j.repository.releases</id>
		<name>Ghost4J releases</name>
		<url>http://repo.ghost4j.org/maven2/releases</url>
	</repository>
	<repository>
		<id>org.ghost4j.repository.snapshots</id>
		<name>Ghost4J snapshots</name>
		<url>http://repo.ghost4j.org/maven2/snapshots</url>
	</repository>
	
	...
	
</repositories>

<dependencies>

	...
	
	<dependency>
		<groupId>org.ghost4j</groupId>
		<artifactId>ghost4j</artifactId>
		<version>0.4.6</version>
	</dependency>
	
	...
	
</dependencies>
</code></pre>

### Where to go next ?

* If you are interested into PS / PDF conversion or rendition have a look at the [High level API samples](highlevelapisamples.html).

* If you already know Ghostscript and want to perform advanced operations on documents, have a look at the [Core API samples](coreapisamples.html).

* If you plan to use Ghost4J in a highly concurrent environment (such as a web server), don't forget to read [Thread safety and multi-threading](threadsafetyandmultithreading.html).