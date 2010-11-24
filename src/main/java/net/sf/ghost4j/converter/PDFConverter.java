/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.DocumentException;
import net.sf.ghost4j.document.PSDocument;
import net.sf.ghost4j.util.DiskStore;

/**
 * PDF converter.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConverter extends AbstractRemoteConverter{

    public static final int OPTION_AUTOROTATEPAGES_NONE = 0;
    public static final int OPTION_AUTOROTATEPAGES_ALL = 1;
    public static final int OPTION_AUTOROTATEPAGES_PAGEBYPAGE = 2;

    public static final int OPTION_PROCESSCOLORMODEL_RGB = 0;
    public static final int OPTION_PROCESSCOLORMODEL_GRAY = 1;
    public static final int OPTION_PROCESSCOLORMODEL_CMYK = 2;

    public static final int OPTION_PDFSETTINGS_DEFAULT = 0;
    public static final int OPTION_PDFSETTINGS_SCREEN = 1;
    public static final int OPTION_PDFSETTINGS_EBOOK = 2;
    public static final int OPTION_PDFSETTINGS_PRINTER = 3;
    public static final int OPTION_PDFSETTINGS_PREPRESS = 4;

    private int autoRotatePages;
    private int processColorModel;
    private int PDFSettings;
    private String compatibilityLevel = "1.4";
    private boolean PDFX = false;

    public PDFConverter() {

        //set supported classes
        supportedDocumentClasses = new Class[1];
        supportedDocumentClasses[0] = PSDocument.class;
    }
    
    /**
     * Main method used to start the converter in standalone 'slave mode'.
     * @param args
     * @throws ConverterException

     */
    public static void main(String args[]) throws ConverterException {

        startRemoteConverter(new PDFConverter());
    }


    /**
     * Run method called to perform the actual process of the converter.
     * @param document
     * @param outputStream
     * @throws IOException
     * @throws ConverterException
     * @throws DocumentException
     */
    public void run(Document document, OutputStream outputStream) throws IOException, ConverterException, DocumentException {

        //if no output = nothing to do
        if (outputStream == null){
            return;
        }

        //assert document is supported
        this.assertDocumentSupported(document);

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //generate a unique diskstore key
        DiskStore diskStore = DiskStore.getInstance();
        String diskStoreKey = outputStream.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int)(Math.random() * (1000-0))));

        //prepare Ghostscript interpreter parameters
        String[] gsArgs = new String[14];
        
        gsArgs[0] = "-ps2pdf";
        gsArgs[1] = "-dNOPAUSE";
        gsArgs[2] = "-dBATCH";
        gsArgs[3] = "-dSAFER";

        //autorotatepages
        switch(autoRotatePages){
            case OPTION_AUTOROTATEPAGES_NONE:
                gsArgs[4] = "-dAutoRotatePages=/None";
                break;
            case OPTION_AUTOROTATEPAGES_ALL:
                 gsArgs[4] = "-dAutoRotatePages=/All";
                break;
            default:
                 gsArgs[4] = "-dAutoRotatePages=/PageByPage";
                break;
        }

        //processcolormodel
        switch(processColorModel){
            case OPTION_PROCESSCOLORMODEL_CMYK:
                gsArgs[5] = "-dProcessColorModel=/DeviceCMYK";
                break;
            case OPTION_PROCESSCOLORMODEL_GRAY:
                gsArgs[5] = "-dProcessColorModel=/DeviceGray";
                break;
            default:
                gsArgs[5] = "-dProcessColorModel=/DeviceRGB";
        }

        //pdf settings
        switch(PDFSettings){
            case OPTION_PDFSETTINGS_EBOOK:
                gsArgs[6] = "-dPDFSETTINGS=/ebook";
                break;
            case OPTION_PDFSETTINGS_SCREEN:
                gsArgs[6] = "-dPDFSETTINGS=/screen";
                break;
            case OPTION_PDFSETTINGS_PRINTER:
                gsArgs[6] = "-dPDFSETTINGS=/printer";
                break;
            case OPTION_PDFSETTINGS_PREPRESS:
                gsArgs[6] = "-dPDFSETTINGS=/prepress";
                break;
            default:
                gsArgs[6] = "-dPDFSETTINGS=/default";
        }


        //compatibilitylevel
        gsArgs[7] = "-dCompatibilityLevel=" + compatibilityLevel;

        //PDFX
        gsArgs[8] = "-dPDFX=" + PDFX;


        gsArgs[9] = "-sDEVICE=pdfwrite";
        //output to file, as stdout redirect does not work properly
        gsArgs[10] = "-sOutputFile=" + diskStore.addFile(diskStoreKey).getAbsolutePath();
        gsArgs[11] = "-q";
        gsArgs[12] = "-f";
        gsArgs[13] = "-";
        
        InputStream is = new ByteArrayInputStream(document.getContent());

        try {

            //execute and exit interpreter
            synchronized(gs){
                gs.setStdIn(is);
                gs.initialize(gsArgs);
                
            }

            // write obtained file to output stream
            File outputFile = diskStore.getFile(diskStoreKey);
            if (outputFile == null){
            	throw new ConverterException("Cannot retrieve file with key " + diskStoreKey + " from disk store");
            }

            FileInputStream fis = new FileInputStream(outputFile);
            byte[] content = new byte[(int)outputFile.length()];
            fis.read(content);
            fis.close();

            outputStream.write(content);

        } catch (GhostscriptException e) {

           throw new ConverterException(e);

        } finally{
        	
        	IOUtils.closeQuietly(is);
        	
        	//delete Ghostscript instance
        	try {
				Ghostscript.deleteInstance();
			} catch (GhostscriptException e) {
				throw new ConverterException(e);
			}

            //remove temporary file
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

    
}
