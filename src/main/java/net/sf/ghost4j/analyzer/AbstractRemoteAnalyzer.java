package net.sf.ghost4j.analyzer;

import gnu.cajo.Cajo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ghost4j.component.DocumentNotSupported;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.util.JavaFork;
import net.sf.ghost4j.util.NetworkUtil;

import org.apache.log4j.Logger;

public abstract class AbstractRemoteAnalyzer extends AbstractAnalyzer implements RemoteAnalyzer {

    /**
     * Log4J logger used to log messages.
     */
    private Logger logger = Logger.getLogger(AbstractRemoteAnalyzer.class.getName());
    
    /**
     * Maximum number of parallel processes allowed for the converter.
     */
    protected int maxProcessCount = 0;
    /**
     * Number of parallel processes running.
     */
    protected int processCount = 0;
    
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
            remoteAnalyzer.setMaxProcessCount(0);
            cajo.export(remoteAnalyzer);


        } catch (Exception e) {
            throw new AnalyzerException(e);
        }
    }
    
    public List<AnalysisItem> remoteAnalyze(Document document)
    		throws IOException, AnalyzerException , DocumentNotSupported{
    	
    	return run(document);
    }
    
	@Override
	public List<AnalysisItem> analyze(Document document) throws IOException,
			AnalyzerException, DocumentNotSupported {
		
		 if (maxProcessCount == 0) {

	            //perform actual processing
	            return run(document);

	        } else {
	            
	            //handle parallel processes

	            //wait for a process to get free
	            while (processCount >= maxProcessCount) {
	                try {
	                    Thread.sleep(1000);
	                } catch (Exception e) {
	                    //nothing
	                }
	            }
	            processCount++;

	            //check if current class supports stand alone mode
	            try {
	                Method method = this.getClass().getMethod("main", String[].class);
	            } catch (Exception ex) {
	                throw new AnalyzerException("Standalone mode is not supported by this analyzer: no 'main' method found");
	            }
	            
	            //prepare new JVM
	            JavaFork fork = new JavaFork();
	            fork.setRedirectStreams(true);
	            fork.setWaitBeforeExiting(false);
	            fork.setStartClass(this.getClass());
	            
	            //set JVM Xmx parameter according to the document size
	            int documentMbSize = (document.getSize() / 1024 / 1024) + 1;
	            int xmxValue = 64 + documentMbSize;
	            fork.setXmx(xmxValue + "m");
	            
	            int cajoPort = 0;
	            RemoteAnalyzer remoteAnalyzer = null;

	            try {
	            	
	            	synchronized (AbstractRemoteAnalyzer.class) {

			            //get free TCP port to run Cajo server on
			            cajoPort = NetworkUtil.findAvailablePort("127.0.0.1", 5000, 6000);
			            if (cajoPort == 0){
			            	throw new IOException("No port available to start remote analyzer");
			            }
			            logger.debug(Thread.currentThread() + " uses " + cajoPort + " as server port");
			            
			            //add extra environment variables to JVM
			            Map<String, String> environment = new HashMap<String, String>();
			            //Cajo port
			            environment.put("cajo.port", String.valueOf(cajoPort));
			            fork.setEnvironment(environment);
			            
			            //start new JVM with current analyzer
			            fork.start();
			
			            //send document to new JVM
		
		                //wait for the remote JVM to start
		            	NetworkUtil.waitUntilPortListening("127.0.0.1", cajoPort, 10000);
		            	
		            	//find Cajo client port available
		                int cajoClientPort = NetworkUtil.findAvailablePort("127.0.0.1", 7000, 8000);
		                if (cajoClientPort == 0){
		                	throw new IOException("No port available to connect to remote analyzer");
		                }
		                logger.debug(Thread.currentThread() + " uses " + cajoPort + " as client port");
		                
		                //register cajo
		                Cajo cajo = new Cajo(cajoClientPort, null, null);
		                cajo.register("127.0.0.1", cajoPort);
		                
		                
		                //get remote converter
		                Object refs[] = cajo.lookup(Analyzer.class);
		                if (refs.length == 0) {
		                    //not converter found
		                    fork.stop();
		                    throw new AnalyzerException("No remote analyzer process found");
		                }
		                remoteAnalyzer = (RemoteAnalyzer) cajo.proxy(refs[0], RemoteAnalyzer.class);

		                // clone converter settings
		                remoteAnalyzer.cloneSettings(this);
	            	
	            	}

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
	
    public int getMaxProcessCount() {
        return maxProcessCount;
    }

    public void setMaxProcessCount(int maxProcessCount) {
        this.maxProcessCount = maxProcessCount;
    }

    public int getProcessCount() {
        return processCount;
    }

}
