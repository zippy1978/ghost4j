/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.modifier;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.ghost4j.AbstractRemoteComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.util.JavaFork;

/**
 * Abstract remote modifier implementation. Used as base class for remote
 * modifiers.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractRemoteModifier extends AbstractRemoteComponent
	implements RemoteModifier {

    protected abstract Document run(Document source,
	    Map<String, Serializable> parameters) throws ModifierException,
	    DocumentException, IOException;

    /**
     * Starts a remote modifier server.
     * 
     * @param remoteModifier
     * @throws ModifierException
     */
    protected static void startRemoteModifier(RemoteModifier remoteModifier)
	    throws ModifierException {

	try {

	    // get port
	    if (System.getenv("cajo.port") == null) {
		throw new ModifierException(
			"No Cajo port defined for remote converter");
	    }
	    int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

	    // export modifier
	    RemoteModifier modifierCopy = remoteModifier.getClass()
		    .newInstance();
	    remoteModifier.setMaxProcessCount(0);

	    Remote.config(null, cajoPort, null, 0);
	    ItemServer.bind(modifierCopy,
		    RemoteModifier.class.getCanonicalName());

	} catch (Exception e) {
	    throw new ModifierException(e);
	}

    }

    public Document remoteModify(Document source,
	    Map<String, Serializable> parameters) throws ModifierException,
	    DocumentException, IOException {

	return run(source, parameters);

    }

    public Document modify(Document source, Map<String, Serializable> parameters)
	    throws ModifierException, DocumentException, IOException {

	if (maxProcessCount == 0) {

	    // perform actual processing
	    return run(source, parameters);

	} else {

	    // handle parallel processes

	    // wait for a process to get free
	    this.waitForFreeProcess();
	    processCount++;

	    // check if current class supports stand alone mode
	    if (!this.isStandAloneModeSupported()) {
		throw new ModifierException(
			"Standalone mode is not supported by this modifier: no 'main' method found");
	    }

	    // prepare new JVM
	    JavaFork fork = this.buildJavaFork();

	    // set JVM Xmx parameter according to the document size
	    int documentMbSize = ((source.getSize() / 1024 / 1024) + 1) * 2;
	    int xmxValue = 64 + documentMbSize;
	    fork.setXmx(xmxValue + "m");

	    int cajoPort = 0;

	    try {

		// start remove server
		cajoPort = this.startRemoteServer(fork);

		// get remote component
		Object remote = this.getRemoteComponent(cajoPort,
			RemoteModifier.class);

		// copy modifier settings to remote converter
		Remote.invoke(remote, "copySettings", this.extractSettings());

		// perform remote conversion
		Object[] args = { source, parameters };
		Document result = (Document) Remote.invoke(remote,
			"remoteModify", args);

		// return result
		return result;

	    } catch (Exception e) {
		throw new ModifierException(e);
	    } finally {
		processCount--;
		fork.stop();
	    }
	}

    }
}
