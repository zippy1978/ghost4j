/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface defining a document that can be handled by the library.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Document {

    public static final String TYPE_POSTSCRIPT = "PostScript";
    public static final String TYPE_PDF = "PDF";

    /**
     * Load document from a File.
     * 
     * @param file
     *            File.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void load(File file) throws FileNotFoundException, IOException;

    /**
     * Load document from an InputStream.
     * 
     * @param inputStream
     * @throws IOException
     */
    public void load(InputStream inputStream) throws IOException;

    /**
     * Write document to a File.
     * 
     * @param file
     *            File.
     * @throws IOException
     */
    public void write(File file) throws IOException;

    /**
     * Write document to an OutputStream
     * 
     * @param outputStream
     * @throws IOException
     */
    public void write(OutputStream outputStream) throws IOException;

    /**
     * Return document page count
     * 
     * @return Number of pages.
     */
    public int getPageCount() throws DocumentException;

    /**
     * Return the type of the document.
     * 
     * @return A String representing the document type name.
     */
    public String getType();

    /**
     * Return document size
     * 
     * @return Document size in bytes.
     */
    public int getSize();

    /**
     * Return document content as a byte array
     * 
     * @return Byte array
     */
    public byte[] getContent();

    /**
     * Return a new document containing pages of a given range. Note : begin and
     * end indicies start at 1
     * 
     * @param begin
     *            Index of the first page to extract
     * @param end
     *            Index of the last page to extract
     * @return A new document.
     */
    public Document extract(int begin, int end) throws DocumentException;

    /**
     * Append pages of another document to the current document.
     * 
     * @param document
     *            Document ot append
     * @throws DocumentException
     */
    public void append(Document document) throws DocumentException;

    /**
     * Separate each pages to a new document.
     * 
     * @return A list of Document.
     * @throws DocumentException
     */
    public List<Document> explode() throws DocumentException;
}
