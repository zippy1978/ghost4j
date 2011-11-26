/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.DocumentException;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.document.PSDocument;
import net.sf.ghost4j.util.DiskStore;

/**
 * PS converter.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSConverter extends AbstractRemoteConverter {

    /**
     * PostScript language level: 1, 2 or 3.
     */
    private int languageLevel = 3;
    
     /**
     * Define standard paper size for the generated PDF file.
     * This parameter is ignored if a paper size is provided in the input file.
     * Default value is "letter".
     */
    private String paperSize = "letter";

    public PSConverter() {

        //set supported classes
        supportedDocumentClasses = new Class[2];
        supportedDocumentClasses[0] = PSDocument.class;
        supportedDocumentClasses[1] = PDFDocument.class;
    }

    /**
     * Main method used to start the converter in standalone 'slave mode'.
     * @param args
     * @throws ConverterException
     */
    public static void main(String args[]) throws ConverterException {

        startRemoteConverter(new PSConverter());
    }

    @Override
    public void run(Document document, OutputStream outputStream)
            throws IOException, ConverterException, DocumentException {

        //if no output = nothing to do
        if (outputStream == null) {
            return;
        }

        //assert document is supported
        this.assertDocumentSupported(document);

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //generate a unique diskstore key for output file
        DiskStore diskStore = DiskStore.getInstance();
        String outputDiskStoreKey = outputStream.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int) (Math.random() * (1000 - 0))));

        //generate a unique diskstore key for input file
        String inputDiskStoreKey = outputStream.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int) (Math.random() * (1000 - 0))));

        //write document to input file
        document.write(diskStore.addFile(inputDiskStoreKey));

        //prepare Ghostscript interpreter parameters
        String[] gsArgs = {
            //dummy value to prevent interpreter from blocking
            "-psconv",
            "-dNOPAUSE",
            "-dBATCH",
            "-dSAFER",
            "-dLanguageLevel=" + languageLevel,
            "-sPAPERSIZE=" + paperSize,
            "-sDEVICE=pswrite",
            //output to file, as stdout redirect does not work properly
            "-sOutputFile=" + diskStore.addFile(outputDiskStoreKey).getAbsolutePath(),
            "-q",
            "-f",
            // read from a file as stdin redirect does not work properly with PDF file as input
            diskStore.getFile(inputDiskStoreKey).getAbsolutePath()};

        try {

            //execute and exit interpreter
            synchronized (gs) {
                gs.initialize(gsArgs);
                gs.exit();
            }

            // write obtained file to output stream
            File outputFile = diskStore.getFile(outputDiskStoreKey);
            if (outputFile == null) {
                throw new ConverterException("Cannot retrieve file with key " + outputDiskStoreKey + " from disk store");
            }

            FileInputStream fis = new FileInputStream(outputFile);
            byte[] content = new byte[(int) outputFile.length()];
            fis.read(content);
            fis.close();

            outputStream.write(content);

        } catch (GhostscriptException e) {

            throw new ConverterException(e);

        } finally {

            //delete Ghostscript instance
            try {
                Ghostscript.deleteInstance();
            } catch (GhostscriptException e) {
                throw new ConverterException(e);
            }

            //remove temporary files
            diskStore.removeFile(outputDiskStoreKey);
            diskStore.removeFile(inputDiskStoreKey);
        }

    }

    public int getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(int languageLevel) {
        this.languageLevel = languageLevel;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }
}
