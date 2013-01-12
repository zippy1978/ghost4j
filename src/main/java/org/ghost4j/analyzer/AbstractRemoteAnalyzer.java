/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.analyzer;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.io.IOException;
import java.util.List;

import org.ghost4j.AbstractRemoteComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.util.JavaFork;

public abstract class AbstractRemoteAnalyzer extends AbstractRemoteComponent
	implements RemoteAnalyzer {

    protected abstract List<AnalysisItem> run(Document document)
	    throws IOException, AnalyzerException, DocumentException;

    /**
     * Starts a remote analyzer server.
     * 
     * @param remoteAnalyzer
     * @throws AnalyzerException
     */
    protected static void startRemoteAnalyzer(RemoteAnalyzer remoteAnalyzer)
	    throws AnalyzerException {

	try {

	    // get port
	    if (System.getenv("cajo.port") == null) {
		throw new AnalyzerException(
			"No Cajo port defined for remote analyzer");
	    }
	    int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

	    // export analyzer
	    RemoteAnalyzer analyzerCopy = remoteAnalyzer.getClass()
		    .newInstance();
	    analyzerCopy.setMaxProcessCount(0);

	    Remote.config(null, cajoPort, null, 0);
	    ItemServer.bind(analyzerCopy,
		    RemoteAnalyzer.class.getCanonicalName());

	} catch (Exception e) {
	    throw new AnalyzerException(e);
	}
    }

    @SuppressWarnings("unchecked")
    public List<AnalysisItem> analyze(Document document) throws IOException,
	    AnalyzerException, DocumentException {

	if (maxProcessCount == 0) {

	    // perform actual processing
	    return run(document);

	} else {

	    // handle parallel processes

	    // wait for a process to get free
	    this.waitForFreeProcess();
	    processCount++;

	    // check if current class supports stand alone mode
	    if (!this.isStandAloneModeSupported()) {
		throw new AnalyzerException(
			"Standalone mode is not supported by this analyzer: no 'main' method found");
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
			RemoteAnalyzer.class);

		// copy analyzer settings to remote analyzer
		Remote.invoke(remote, "copySettings", this.extractSettings());

		// perform remote analyze
		return (List<AnalysisItem>) Remote.invoke(remote, "run",
			document);

	    } catch (IOException e) {
		throw e;
	    } catch (Exception e) {
		throw new AnalyzerException(e);
	    } finally {
		processCount--;
		fork.stop();
	    }
	}

    }

}
