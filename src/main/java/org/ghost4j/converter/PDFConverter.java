/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PSDocument;
import org.ghost4j.document.PaperSize;
import org.ghost4j.util.DiskStore;

/**
 * PDF converter.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConverter extends AbstractRemoteConverter {

    public static final int OPTION_AUTOROTATEPAGES_NONE = 0;
    public static final int OPTION_AUTOROTATEPAGES_ALL = 1;
    public static final int OPTION_AUTOROTATEPAGES_PAGEBYPAGE = 2;
    public static final int OPTION_AUTOROTATEPAGES_OFF = 3;

    public static final int OPTION_PROCESSCOLORMODEL_RGB = 0;
    public static final int OPTION_PROCESSCOLORMODEL_GRAY = 1;
    public static final int OPTION_PROCESSCOLORMODEL_CMYK = 2;

    public static final int OPTION_PDFSETTINGS_DEFAULT = 0;
    public static final int OPTION_PDFSETTINGS_SCREEN = 1;
    public static final int OPTION_PDFSETTINGS_EBOOK = 2;
    public static final int OPTION_PDFSETTINGS_PRINTER = 3;
    public static final int OPTION_PDFSETTINGS_PREPRESS = 4;

    /**
     * Define auto rotate pages behaviour. Can be OPTION_AUTOROTATEPAGES_NONE,
     * OPTION_AUTOROTATEPAGES_ALL, OPTION_AUTOROTATEPAGES_PAGEBYPAGE or
     * OPTION_AUTOROTATEPAGES_OFF (default).
     */
    private int autoRotatePages = OPTION_AUTOROTATEPAGES_OFF;

    /**
     * Define process color model. Can be OPTION_PROCESSCOLORMODEL_RGB,
     * OPTION_PROCESSCOLORMODEL_GRAY or OPTION_PROCESSCOLORMODEL_CMYK.
     */
    private int processColorModel;

    /**
     * Define PDF settings to use. Can be OPTION_PDFSETTINGS_DEFAULT,
     * OPTION_PDFSETTINGS_SCREEN, OPTION_PDFSETTINGS_EBOOK,
     * OPTION_PDFSETTINGS_PRINTER or OPTION_PDFSETTINGS_PREPRESS.
     */
    private int PDFSettings;

    /**
     * Define PDF version compatibility level (default is "1.4").
     */
    private String compatibilityLevel = "1.4";

    /**
     * Enable PDFX generation (default is false).
     */
    private boolean PDFX = false;

    /**
     * Define standard paper size for the generated PDF file. This parameter is
     * ignored if a paper size is provided in the input file. Default value is
     * "letter".
     */
    private PaperSize paperSize = PaperSize.LETTER;

    public PDFConverter() {

	// set supported classes
	supportedDocumentClasses = new Class[1];
	supportedDocumentClasses[0] = PSDocument.class;
    }

    /**
     * Main method used to start the converter in standalone 'slave mode'.
     * 
     * @param args
     * @throws ConverterException
     */
    public static void main(String args[]) throws ConverterException {

	startRemoteConverter(new PDFConverter());
    }

    /**
     * Run method called to perform the actual process of the converter.
     * 
     * @param document
     * @param outputStream
     * @throws IOException
     * @throws ConverterException
     * @throws DocumentException
     */
    @Override
    public void run(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException {

	// if no output = nothing to do
	if (outputStream == null) {
	    return;
	}

	// assert document is supported
	this.assertDocumentSupported(document);

	// get Ghostscript instance
	Ghostscript gs = Ghostscript.getInstance();

	// generate a unique diskstore key
	DiskStore diskStore = DiskStore.getInstance();
	String diskStoreKey = diskStore.generateUniqueKey();

	// prepare Ghostscript interpreter parameters
	int argCount = 15;
	if (autoRotatePages != OPTION_AUTOROTATEPAGES_OFF) {
	    argCount++;
	}
	String[] gsArgs = new String[argCount];

	gsArgs[0] = "-ps2pdf";
	gsArgs[1] = "-dNOPAUSE";
	gsArgs[2] = "-dBATCH";
	gsArgs[3] = "-dSAFER";

	int paramPosition = 3;

	// autorotatepages
	switch (autoRotatePages) {
	case OPTION_AUTOROTATEPAGES_NONE:
	    paramPosition++;
	    gsArgs[paramPosition] = "-dAutoRotatePages=/None";
	    break;
	case OPTION_AUTOROTATEPAGES_ALL:
	    paramPosition++;
	    gsArgs[paramPosition] = "-dAutoRotatePages=/All";
	    break;
	case OPTION_AUTOROTATEPAGES_PAGEBYPAGE:
	    paramPosition++;
	    gsArgs[paramPosition] = "-dAutoRotatePages=/PageByPage";
	    break;
	default:
	    // nothing
	    break;

	}

	// processcolormodel
	paramPosition++;
	switch (processColorModel) {
	case OPTION_PROCESSCOLORMODEL_CMYK:
	    gsArgs[paramPosition] = "-dProcessColorModel=/DeviceCMYK";
	    break;
	case OPTION_PROCESSCOLORMODEL_GRAY:
	    gsArgs[paramPosition] = "-dProcessColorModel=/DeviceGray";
	    break;
	default:
	    gsArgs[paramPosition] = "-dProcessColorModel=/DeviceRGB";
	}

	// pdf settings
	paramPosition++;
	switch (PDFSettings) {
	case OPTION_PDFSETTINGS_EBOOK:
	    gsArgs[paramPosition] = "-dPDFSETTINGS=/ebook";
	    break;
	case OPTION_PDFSETTINGS_SCREEN:
	    gsArgs[paramPosition] = "-dPDFSETTINGS=/screen";
	    break;
	case OPTION_PDFSETTINGS_PRINTER:
	    gsArgs[paramPosition] = "-dPDFSETTINGS=/printer";
	    break;
	case OPTION_PDFSETTINGS_PREPRESS:
	    gsArgs[paramPosition] = "-dPDFSETTINGS=/prepress";
	    break;
	default:
	    gsArgs[paramPosition] = "-dPDFSETTINGS=/default";
	}

	// compatibilitylevel
	paramPosition++;
	gsArgs[paramPosition] = "-dCompatibilityLevel=" + compatibilityLevel;

	// PDFX
	paramPosition++;
	gsArgs[paramPosition] = "-dPDFX=" + PDFX;

	// papersize
	paramPosition++;
	gsArgs[paramPosition] = "-dDEVICEWIDTHPOINTS=" + paperSize.getWidth();
	paramPosition++;
	gsArgs[paramPosition] = "-dDEVICEHEIGHTPOINTS=" + paperSize.getHeight();

	paramPosition++;
	gsArgs[paramPosition] = "-sDEVICE=pdfwrite";
	// output to file, as stdout redirect does not work properly
	paramPosition++;
	gsArgs[paramPosition] = "-sOutputFile="
		+ diskStore.addFile(diskStoreKey).getAbsolutePath();
	paramPosition++;
	gsArgs[paramPosition] = "-q";
	paramPosition++;
	gsArgs[paramPosition] = "-f";
	paramPosition++;
	gsArgs[paramPosition] = "-";

	InputStream is = new ByteArrayInputStream(document.getContent());

	try {

	    // execute and exit interpreter
	    synchronized (gs) {
		gs.setStdIn(is);
		gs.initialize(gsArgs);

	    }

	    // write obtained file to output stream
	    File outputFile = diskStore.getFile(diskStoreKey);
	    if (outputFile == null) {
		throw new ConverterException("Cannot retrieve file with key "
			+ diskStoreKey + " from disk store");
	    }

	    FileInputStream fis = new FileInputStream(outputFile);
	    byte[] content = new byte[(int) outputFile.length()];
	    fis.read(content);
	    fis.close();

	    outputStream.write(content);

	} catch (GhostscriptException e) {

	    throw new ConverterException(e);

	} finally {

	    IOUtils.closeQuietly(is);

	    // delete Ghostscript instance
	    try {
		Ghostscript.deleteInstance();
	    } catch (GhostscriptException e) {
		throw new ConverterException(e);
	    }

	    // remove temporary file
	    diskStore.removeFile(diskStoreKey);
	}

    }

    public int getAutoRotatePages() {
	return autoRotatePages;
    }

    public void setAutoRotatePages(int autoRotatePages) {
	this.autoRotatePages = autoRotatePages;
    }

    public int getProcessColorModel() {
	return processColorModel;
    }

    public void setProcessColorModel(int processColorModel) {
	this.processColorModel = processColorModel;
    }

    public String getCompatibilityLevel() {
	return compatibilityLevel;
    }

    public void setCompatibilityLevel(String compatibilityLevel) {
	this.compatibilityLevel = compatibilityLevel;
    }

    public int getPDFSettings() {
	return PDFSettings;
    }

    public void setPDFSettings(int PDFSettings) {
	this.PDFSettings = PDFSettings;
    }

    public boolean isPDFX() {
	return PDFX;
    }

    public void setPDFX(boolean PDFX) {
	this.PDFX = PDFX;
    }

    public PaperSize getPaperSize() {
	return paperSize;
    }

    public void setPaperSize(PaperSize paperSize) {
	this.paperSize = paperSize;
    }

    public void setPaperSize(String paperSizeName) {

	PaperSize found = PaperSize.getStandardPaperSize(paperSizeName);
	if (found != null) {
	    this.setPaperSize(found);
	}
    }

}
