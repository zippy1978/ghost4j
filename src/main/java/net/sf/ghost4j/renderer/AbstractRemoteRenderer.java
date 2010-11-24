/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.renderer;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import net.sf.ghost4j.AbstractRemoteComponent;
import net.sf.ghost4j.display.PageRaster;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.DocumentException;
import net.sf.ghost4j.util.ImageUtil;
import net.sf.ghost4j.util.JavaFork;

import org.apache.log4j.Logger;

public abstract class AbstractRemoteRenderer extends AbstractRemoteComponent implements RemoteRenderer{

    /**
     * Log4J logger used to log messages.
     */
    private Logger logger = Logger.getLogger(AbstractRemoteRenderer.class.getName());
    
    public abstract List<PageRaster> run(Document document, int begin, int end) throws IOException, RendererException, DocumentException;
    
    /**
     * Starts a remote renderer server
     * @param remoteRenderer
     * @throws RendererException
     */
    public static void startRemoteRenderer(RemoteRenderer remoteRenderer) throws RendererException{
    	
    	try {

            //get port
            if (System.getenv("cajo.port") == null){
                throw new RendererException("No Cajo port defined for remote renderer");
            }
            int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

            //export renderer
            RemoteRenderer rendererCopy = remoteRenderer.getClass().newInstance();
            rendererCopy.setMaxProcessCount(0);
            
            Remote.config(null, cajoPort, null, 0);
            ItemServer.bind(rendererCopy, RemoteRenderer.class.getCanonicalName());


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
    
    public List<Image> render(Document document, int begin, int end)
    		throws IOException, RendererException, DocumentException {
    	
    	//check range
		if ((begin > end) || (end > document.getPageCount()) || (begin < 0) || (end < 0)) {
			throw new RendererException("Invalid page range");
		}
		
		if (maxProcessCount == 0) {

            //perform actual processing
            return ImageUtil.convertPageRastersToImages(run(document, begin, end));

        } else {
            
            //handle parallel processes

            //wait for a process to get free
            this.waitForFreeProcess();
            processCount++;

            //check if current class supports stand alone mode
            if (!this.isStandAloneModeSupported()){
                throw new RendererException("Standalone mode is not supported by this renderer: no 'main' method found");
            }
            
            //prepare new JVM
            JavaFork fork = this.buildJavaFork();
            
            //set JVM Xmx parameter according to the document size
            int documentMbSize = (document.getSize() / 1024 / 1024) + 1;
            int xmxValue = 64 + documentMbSize;
            fork.setXmx(xmxValue + "m");
            
            int cajoPort = 0;
            try {
            	
            	//start remove server
            	cajoPort = this.startRemoteServer(fork);
            	
            	//get remote component
            	Object remote = this.getRemoteComponent(cajoPort, RemoteRenderer.class);
            	
                //perform remote rendering
            	Object[] args = {document, begin, end};
            	return ImageUtil.convertPageRastersToImages((List<PageRaster>)Remote.invoke(remote, "remoteRender", args));


            } catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RendererException(e);
            } finally {
            	processCount--;
                fork.stop();
            }
        }
    }
    
    
}
