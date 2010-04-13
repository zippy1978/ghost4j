/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.converter;

import java.io.IOException;
import net.sf.ghost4j.document.Document;

/**
 * Interface defining a remote converter (for Ghostscript multi process support).
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteConverter extends Converter {

    /**
     * Sets max parallel convertion processes allowed for the converter
     * @param maxProcessCount
     */
    public void setMaxProcessCount(int maxProcessCount);

    /**
     * Converts a document and return results as a byte array.
     * This convertion method does not use a stream to output result in order to support remote calls on it.
     * @param document Document to convert
     * @return Converted document as a byte array
     * @throws IOException
     * @throws ConverterException
     */
    public byte[] remoteConvert(Document document) throws IOException, ConverterException;

    /**
     * Clones settings of a converter to another one.
     * This operation is allowed only when converters (source and target) are instances of the same class.
     * @param remoteConverter
     */
    public void cloneSettings(RemoteConverter remoteConverter);
}
