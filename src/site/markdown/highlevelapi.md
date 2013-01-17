High level API
==============
  
Since version 0.4.0, Ghost4J provides a high level API, to make document handling easier with Ghostscript.
  
The high level API is composed of the following items:
  
* **Documents:** A document is an object representing a document (usually a Postscript or PDF file). It provides methods to load, write and count pages on the document itself.

* **Components:** Components are processing units feed with documents. Families of components are: Analyzer, Converter, Modifier and Renderer. Not all types of documents are necessarily supported by components.

### Documents

Documents are represented by the <<org.ghost4j.document.Document>> interface.
	
Available implementations are:
  
<table>
	<tr><th>Class</th><th>Description</th></tr>
	<tr><td>org.ghost4j.document.PSDocument</td><td>Handle Postscript document</td></tr>
	<tr><td>org.ghost4j.document.PDFDocument</td><td>Handle PDF document</td></tr>
</table>

### Analyzers

Analyzers are components used to extract data of any kind out of a document. They are represented by the <<org.ghost4j.analyzer.Analyzer>> interface.

Available implementations are:

<table>
	<tr><th>Class</th><th>Description</th><th>Supports Postscript</th><th>Supports PDF</th></tr>
	<tr><td>org.ghost4j.analyzer.FontAnalyzer</td><td>Extract font information: font names and embedding status</td><td>No</td><td>Yes</td></tr>
	<tr><td>org.ghost4j.analyzer.InkAnalyzer</td><td>Extract ink coverage information for each page in % as CMYK components</td><td>Yes</td><td>Yes</td></tr>
</table>

### Converters

Converters are components used to convert documents to a given file format. They are represented by the <<org.ghost4j.converter.Converter>> interface.
   
Available implementations are:

<table>
	<tr><th>Class</th><th>Description</th><th>Supports Postscript</th><th>Supports PDF</th></tr>
	<tr><td>org.ghost4j.converter.PSConverter</td><td>Convert a document to a Postscript file</td><td>Yes</td><td>Yes</td></tr>
	<tr><td>org.ghost4j.converter.PDFConverter</td><td>Convert a document to a PDF file</td><td>Yes</td><td>Yes</td></tr>
</table>

### Modifiers

Modifiers are components used to modify documents. They are represented by the <<org.ghost4j.modifier.Modifier>> interface.

<table>
	<tr><th>Class</th><th>Description</th><th>Supports Postscript</th><th>Supports PDF</th></tr>
	<tr><td>org.ghost4j.modifier.SafeAppenderModifier</td><td>Append a document to another. Document types can be mixed</td><td>Yes</td><td>Yes</td></tr>
</table>

### Renderers

Renderers are components used to render document pages to image files. They are represented byt he <<org.ghost4j.renderer.Renderer>> interface.
   
Available implementations are:

<table>
	<tr><th>Class</th><th>Description</th><th>Supports Postscript</th><th>Supports PDF</th></tr>
	<tr><td>org.ghost4j.renderer.SimpleRenderer</td><td>Render a range of pages from a document to images</td><td>Yes</td><td>Yes</td></tr>
</table>

