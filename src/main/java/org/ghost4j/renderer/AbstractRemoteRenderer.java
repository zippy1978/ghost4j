/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.renderer;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import org.ghost4j.AbstractRemoteComponent;
import org.ghost4j.display.PageRaster;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.util.ImageUtil;
import org.ghost4j.util.JavaFork;

public abstract class AbstractRemoteRenderer extends AbstractRemoteComponent
	implements RemoteRenderer {

    protected abstract List<PageRaster> run(Document document, int begin,
	    int end) throws IOException, RendererException, DocumentException;

    /**
     * Starts a remote renderer server
     * 
     * @param remoteRenderer
     * @throws RendererException
     */
    protected static void startRemoteRenderer(RemoteRenderer remoteRenderer)
	    throws RendererException {

	try {

	    // get port
	    if (System.getenv("cajo.port") == null) {
		throw new RendererException(
			"No Cajo port defined for remote renderer");
	    }
	    int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

	    // export renderer
	    RemoteRenderer rendererCopy = remoteRenderer.getClass()
		    .newInstance();
	    rendererCopy.setMaxProcessCount(0);

	    Remote.config(null, cajoPort, null, 0);
	    ItemServer.bind(rendererCopy,
		    RemoteRenderer.class.getCanonicalName());

	} catch (Exception e) {
	    throw new RendererException(e);
	}
    }

    public List<PageRaster> remoteRender(Document document, int begin, int end)
	    throws IOException, RendererException, DocumentException {

	return this.run(document, begin, end);
    }

    public List<Image> render(Document document) throws IOException,
	    RendererException, DocumentException {

	return this.render(document, 0, document.getPageCount() - 1);
    }

    @SuppressWarnings("unchecked")
    public List<Image> render(Document document, int begin, int end)
	    throws IOException, RendererException, DocumentException {

	// check range
	if ((begin > end) || (end > document.getPageCount()) || (begin < 0)
		|| (end < 0)) {
	    throw new RendererException("Invalid page range");
	}

	if (maxProcessCount == 0) {

	    // perform actual processing
	    return ImageUtil.convertPageRastersToImages(run(document, begin,
		    end));

	} else {

	    // handle parallel processes

	    // wait for a process to get free
	    this.waitForFreeProcess();
	    processCount++;

	    // check if current class supports stand alone mode
	    if (!this.isStandAloneModeSupported()) {
		throw new RendererException(
			"Standalone mode is not supported by this renderer: no 'main' method found");
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
			RemoteRenderer.class);

		// copy renderer settings to remote renderer
		Remote.invoke(remote, "copySettings", this.extractSettings());

		// perform remote rendering
		Object[] args = { document, begin, end };
		return ImageUtil
			.convertPageRastersToImages((List<PageRaster>) Remote
				.invoke(remote, "remoteRender", args));

	    } catch (IOException e) {
		throw e;
	    } catch (Exception e) {
		throw new RendererException(e);
	    } finally {
		processCount--;
		fork.stop();
	    }
	}
    }

}
