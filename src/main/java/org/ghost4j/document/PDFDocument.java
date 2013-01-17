/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author ggrousset
 */
public class PDFDocument extends AbstractDocument {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6331191005700202153L;

    @Override
    public void load(InputStream inputStream) throws IOException {
	super.load(inputStream);

	// check that the file is a PDF
	ByteArrayInputStream bais = null;
	PdfReader reader = null;

	try {

	    bais = new ByteArrayInputStream(content);
	    reader = new PdfReader(bais);

	} catch (Exception e) {
	    throw new IOException("PDF document is not valid");
	} finally {
	    if (reader != null)
		reader.close();
	    IOUtils.closeQuietly(bais);
	}
    }

    public int getPageCount() throws DocumentException {

	int pageCount = 0;

	if (content == null) {
	    return pageCount;
	}

	ByteArrayInputStream bais = null;
	PdfReader reader = null;

	try {

	    bais = new ByteArrayInputStream(content);
	    reader = new PdfReader(bais);
	    pageCount = reader.getNumberOfPages();

	} catch (Exception e) {
	    throw new DocumentException(e);
	} finally {
	    if (reader != null)
		reader.close();
	    IOUtils.closeQuietly(bais);
	}

	return pageCount;

    }

    public Document extract(int begin, int end) throws DocumentException {

	this.assertValidPageRange(begin, end);

	PDFDocument result = new PDFDocument();

	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;

	if (content != null) {

	    com.lowagie.text.Document document = new com.lowagie.text.Document();

	    try {

		bais = new ByteArrayInputStream(content);
		baos = new ByteArrayOutputStream();

		PdfReader inputPDF = new PdfReader(bais);

		// create a writer for the outputstream
		PdfWriter writer = PdfWriter.getInstance(document, baos);

		document.open();
		PdfContentByte cb = writer.getDirectContent();

		PdfImportedPage page;

		while (begin <= end) {
		    document.newPage();
		    page = writer.getImportedPage(inputPDF, begin);
		    cb.addTemplate(page, 0, 0);
		    begin++;
		}

		document.close();

		result.load(new ByteArrayInputStream(baos.toByteArray()));

	    } catch (Exception e) {
		throw new DocumentException(e);
	    } finally {
		if (document.isOpen())
		    document.close();
		IOUtils.closeQuietly(bais);
		IOUtils.closeQuietly(baos);
	    }

	}

	return result;
    }

    @Override
    public void append(Document document) throws DocumentException {

	super.append(document);

	ByteArrayOutputStream baos = null;
	com.lowagie.text.Document mergedDocument = new com.lowagie.text.Document();

	try {

	    baos = new ByteArrayOutputStream();
	    PdfCopy copy = new PdfCopy(mergedDocument, baos);

	    mergedDocument.open();

	    // copy current document
	    PdfReader reader = new PdfReader(content);
	    int pageCount = reader.getNumberOfPages();
	    for (int i = 0; i < pageCount;) {
		copy.addPage(copy.getImportedPage(reader, ++i));
	    }

	    // copy new document
	    reader = new PdfReader(document.getContent());
	    pageCount = reader.getNumberOfPages();
	    for (int i = 0; i < pageCount;) {
		copy.addPage(copy.getImportedPage(reader, ++i));
	    }

	    mergedDocument.close();

	    // replace content with new content
	    content = baos.toByteArray();

	} catch (Exception e) {
	    throw new DocumentException(e);
	} finally {
	    if (mergedDocument.isOpen())
		mergedDocument.close();
	    IOUtils.closeQuietly(baos);
	}

    }

    public String getType() {
	return TYPE_PDF;
    }
}
