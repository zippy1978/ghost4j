package net.sf.ghost4j.analyzer;

import gnu.cajo.Cajo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ghost4j.component.AbstractRemoteComponent;
import net.sf.ghost4j.component.DocumentNotSupported;
import net.sf.ghost4j.converter.RemoteConverter;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.util.JavaFork;
import net.sf.ghost4j.util.NetworkUtil;

import org.apache.log4j.Logger;

public abstract class AbstractRemoteAnalyzer extends AbstractRemoteComponent implements RemoteAnalyzer {

    /**
     * Log4J logger used to log messages.
     */
    private Logger logger = Logger.getLogger(AbstractRemoteAnalyzer.class.getName());
    
    public abstract List<AnalysisItem> run(Document document) throws IOException, AnalyzerException, DocumentNotSupported;
    
    /**
     * Starts a remote analyzer server.
     * @param remoteAnalyzer
     * @throws AnalyzerException
     */
    public static void startRemoteAnalyzer(RemoteAnalyzer remoteAnalyzer) throws AnalyzerException{

        try {

            //get port
            if (System.getenv("cajo.port") == null){
                throw new AnalyzerException("No Cajo port defined for remote converter");
            }
            int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

            //start cajo server
            Cajo cajo = new Cajo(cajoPort, null, null);

            //export analyzer
            RemoteAnalyzer analyzerCopy = remoteAnalyzer.getClass().newInstance();
            analyzerCopy.cloneSettings(remoteAnalyzer);
            analyzerCopy.setMaxProcessCount(0);
            cajo.export(analyzerCopy);


        } catch (Exception e) {
            throw new AnalyzerException(e);
        }
    }
    
    public List<AnalysisItem> remoteAnalyze(Document document)
    		throws IOException, AnalyzerException , DocumentNotSupported{
    	
    	return run(document);
    }
    

	public List<AnalysisItem> analyze(Document document) throws IOException,
			AnalyzerException, DocumentNotSupported {
		
		 if (maxProcessCount == 0) {

	            //perform actual processing
	            return run(document);

	        } else {
	            
	            //handle parallel processes

	            //wait for a process to get free
	            this.waitForFreeProcess();
	            processCount++;

	            //check if current class supports stand alone mode
	            if (!this.isStandAloneModeSupported()){
	                throw new AnalyzerException("Standalone mode is not supported by this analyzer: no 'main' method found");
	            }
	            
	            //prepare new JVM
	            JavaFork fork = this.buildJavaFork();
	            
	            //set JVM Xmx parameter according to the document size
	            int documentMbSize = (document.getSize() / 1024 / 1024) + 1;
	            int xmxValue = 64 + documentMbSize;
	            fork.setXmx(xmxValue + "m");
	            
	            int cajoPort = 0;
	            RemoteAnalyzer remoteAnalyzer = null;

	            try {
	            	
	            	
	            	//start remove server
	            	cajoPort = this.startRemoteServer(fork);
	            	
	            	//get remote component
	            	remoteAnalyzer = (RemoteAnalyzer) this.getRemoteComponent(cajoPort, RemoteAnalyzer.class);
	            	
	                //perform remote analyze
	            	return remoteAnalyzer.remoteAnalyze(document);


	            } catch (IOException e) {
	                throw e;
	            }
	            catch (Exception e) {
	                throw new AnalyzerException(e);
	            } finally {
	            	processCount--;
	                fork.stop();
	            }
	        }
		 
	}

}
