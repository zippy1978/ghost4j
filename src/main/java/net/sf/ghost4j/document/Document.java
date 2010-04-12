/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Interface defining a document that can be handled by the library.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Document{

    /**
     * Load document from a File.
     * @param file File.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void load(File file) throws FileNotFoundException, IOException;

    /**
     * Load document from an InputStream.
     * @param inputStream
     * @throws IOException
     */
    public void load(InputStream inputStream) throws IOException;

    /**
     * Return document page count
     * @return Number of pages.
     */
    public int getPageCount() throws DocumentException;

    /**
     * Return the type of the document.
     * @return A String representing the document type name.
     */
    public String getType();

    /**
     * Return document size
     * @return Document size in bytes.
     */
    public int getSize();

    /**
     * Return an InputStream on the document content
     * @return InputStream
     */
    public InputStream getInputStream();

}
