/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

/**
 * Abstract document implementation.
 * Contains methods that are common to the different document types
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractDocument implements Document, Serializable{

    /**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7160779330993730486L;

	/**
     * Buffer size used while reading (loading) document content.
     */
    public static final int READ_BUFFER_SIZE = 1024;

    /**
     * Content of the document.
     */
    protected byte[] content;

    public abstract int getPageCount() throws DocumentException;

    public void load(File file) throws FileNotFoundException, IOException{

    	FileInputStream fis = new FileInputStream(file);
        load(fis);
        IOUtils.closeQuietly(fis);
    }

    public void load(InputStream inputStream) throws IOException{

        byte[]  buffer  = new byte[READ_BUFFER_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int readCount = 0;
        while ( (readCount = inputStream.read(buffer)) > 0){
            baos.write(buffer, 0, readCount);
        }
        content = baos.toByteArray();

        IOUtils.closeQuietly(baos);
    }
    
    public void write(File file) throws IOException {
    	
    	FileOutputStream fos = new FileOutputStream(file);
    	write(fos);
    	IOUtils.closeQuietly(fos);
    	
    }
    
    public void write(OutputStream outputStream) throws IOException {
    	
    	outputStream.write(content);
    	
    }

    public int getSize() {

        if (content == null){
            return 0;
        } else{
            return content.length;
        }
    }

    public byte[] getContent() {
        return content;
    }




}
