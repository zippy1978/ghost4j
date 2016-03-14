<p align="center">
<img src="http://www.ghost4j.org/images/home-logo.png"/>
</p>


Ghost4J binds the Ghostscript C API to bring Ghostscript power to the Java world.
It also provides a high-level API to handle PDF and Postscript documents with objects.

### Maven configuration

```xml
<dependencies>

	...

	<dependency>
		<groupId>org.ghost4j</groupId>
		<artifactId>ghost4j</artifactId>
		<version>1.0.1</version>
	</dependency>
	
	...
	
</dependencies>
```

If you want to use an older release (before 1.0.0), add the following repositories as well:

```xml
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
```

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
	
### Getting binaries

Binary distributions can be downloaded from [here](http://www.ghost4j.org/downloads.html)

### Documentation

Documentation is available from [http://www.ghost4j.org](http://www.ghost4j.org)
