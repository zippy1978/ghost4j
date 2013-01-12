/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.converter;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ghost4j.AbstractRemoteComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.util.JavaFork;

/**
 * Abstract remote converter implementation. Used as base class for remote
 * converters.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractRemoteConverter extends AbstractRemoteComponent
	implements RemoteConverter {

    protected abstract void run(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException;

    /**
     * Starts a remote converter server.
     * 
     * @param remoteConverter
     * @throws ConverterException
     */
    protected static void startRemoteConverter(RemoteConverter remoteConverter)
	    throws ConverterException {

	try {

	    // get port
	    if (System.getenv("cajo.port") == null) {
		throw new ConverterException(
			"No Cajo port defined for remote converter");
	    }
	    int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

	    // export converter
	    RemoteConverter converterCopy = remoteConverter.getClass()
		    .newInstance();
	    converterCopy.setMaxProcessCount(0);

	    Remote.config(null, cajoPort, null, 0);
	    ItemServer.bind(converterCopy,
		    RemoteConverter.class.getCanonicalName());

	} catch (Exception e) {
	    throw new ConverterException(e);
	}

    }

    public byte[] remoteConvert(Document document) throws IOException,
	    ConverterException, DocumentException {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	run(document, baos);

	byte[] result = baos.toByteArray();
	baos.close();

	return result;

    }

    public void convert(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException {

	if (maxProcessCount == 0) {

	    // perform actual processing
	    run(document, outputStream);

	} else {

	    // handle parallel processes

	    // wait for a process to get free
	    this.waitForFreeProcess();
	    processCount++;

	    // check if current class supports stand alone mode
	    if (!this.isStandAloneModeSupported()) {
		throw new ConverterException(
			"Standalone mode is not supported by this converter: no 'main' method found");
	    }

	    // prepare new JVM
	    JavaFork fork = this.buildJavaFork();

	    // set JVM Xmx parameter according to the document size
	    int documentMbSize = (document.getSize() / 1024 / 1024) + 1;
	    int xmxValue = 64 + documentMbSize;
	    fork.setXmx(xmxValue + "m");

	    int cajoPort = 0;

	    try {

		// start remove server
		cajoPort = this.startRemoteServer(fork);

		// get remote component
		Object remote = this.getRemoteComponent(cajoPort,
			RemoteConverter.class);

		// copy converter settings to remote converter
		Remote.invoke(remote, "copySettings", this.extractSettings());

		// perform remote conversion
		byte[] result = (byte[]) Remote.invoke(remote, "remoteConvert",
			document);

		// write result to output stream
		outputStream.write(result);

	    } catch (IOException e) {
		throw e;
	    } catch (Exception e) {
		throw new ConverterException(e);
	    } finally {
		processCount--;
		fork.stop();
	    }
	}

    }
}
