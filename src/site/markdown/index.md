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
&lt;repositories&gt;
	...
	&lt;repository&gt;
		&lt;id&gt;org.ghost4j.repository.releases&lt;/id&gt;
		&lt;name&gt;Ghost4J releases&lt;/name&gt;
		&lt;url&gt;http://repo.ghost4j.org/maven2/releases&lt;/url&gt;
	&lt;/repository&gt;
	&lt;repository&gt;
		&lt;id&gt;org.ghost4j.repository.snapshots&lt;/id&gt;
		&lt;name&gt;Ghost4J snapshots&lt;/name&gt;
		&lt;url&gt;http://repo.ghost4j.org/maven2/snapshots&lt;/url&gt;
	&lt;/repository&gt;
	...
&lt;/repositories&gt;

&lt;dependencies&gt;
	...
	&lt;dependency&gt;
		&lt;groupId&gt;org.ghost4j&lt;/groupId&gt;
		&lt;artifactId&gt;ghost4j&lt;/artifactId&gt;
		&lt;version&gt;0.4.6&lt;/version&gt;
	&lt;/dependency&gt;
	
	... or
	
	&lt;dependency&gt;
		&lt;groupId&gt;org.ghost4j&lt;/groupId&gt;
		&lt;artifactId&gt;ghost4j&lt;/artifactId&gt;
		&lt;version&gt;0.4.7-SNAPSHOT&lt;/version&gt;
	&lt;/dependency&gt;
	...
&lt;/dependencies&gt;
</code></pre>

### Where to go next ?

* If you are interested into PS / PDF conversion or rendition have a look at the [High level API samples](highlevelapisamples.html).

* If you already know Ghostscript and want to perform advanced operations on documents, have a look at the [Core API samples](coreapisamples.html).

* If you plan to use Ghost4J in a highly concurrent environment (such as a web server), don't forget to read [Thread safety and multi-threading](threadsafetyandmultithreading.html).