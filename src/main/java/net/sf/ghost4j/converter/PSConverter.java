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

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.document.PSDocument;
import net.sf.ghost4j.util.DiskStore;

/**
 * PS converter.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSConverter extends AbstractRemoteConverter {

	private int languageLevel = 3;
	
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
			throws IOException, ConverterException {
		
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
        String[] gsArgs = new String[12];
        
        gsArgs[0] = "-dNOPAUSE";
        gsArgs[1] = "-dBATCH";
        gsArgs[2] = "-dSAFER";
        
        //languageLevel
        gsArgs[3] = "-dLanguageLevel=" + languageLevel;
        
        gsArgs[4] = "-sDEVICE=pswrite";
        //output to file, as stdout redirect does not work properly
        gsArgs[5] = "-sOutputFile=" + diskStore.addFile(diskStoreKey).getAbsolutePath();
        gsArgs[6] = "-q";
        gsArgs[7] = "-c";
        gsArgs[8] = "save";
        gsArgs[9] = "pop";
        gsArgs[10] = "-f";
        gsArgs[11] = "-";
        
        try {

            //execute and exit interpreter
            synchronized(gs){
                InputStream is = new ByteArrayInputStream(document.getContent());
                gs.setStdIn(is);
                gs.initialize(gsArgs);
                Ghostscript.deleteInstance();
                is.close();
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

            //remove temporary file
            diskStore.removeFile(diskStoreKey);
        }

	}

	public void cloneSettings(RemoteConverter remoteConverter) {
		
		if ((remoteConverter instanceof PSConverter)){
			PSConverter psConverter = (PSConverter) remoteConverter;

            this.setLanguageLevel(psConverter.getLanguageLevel());
        }

	}

	public int getLanguageLevel() {
		return languageLevel;
	}

	public void setLanguageLevel(int languageLevel) {
		this.languageLevel = languageLevel;
	}

}
