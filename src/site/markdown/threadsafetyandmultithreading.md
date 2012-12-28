Thread safety and multi-threading
=================================

### Thead safety

By default the Ghostscript API can have only one interpreter instance in the same native process. 
This means that when writing a program using the API, operations called on the interpreter instance must be synchronous.
  
To make sure a Ghostscript process is thread safe when using Ghost4J, always call it like this:
  
	//get Ghostscript instance
	Ghostscript gs = Ghostscript.getInstance();
	  
	try {
	
		synchronized(gs){
	    
		    //call interpreter operations here
			    	
			gs.initialize(gsArgs);
			gs.exit();
		}
		
	}catch (GhostscriptException e) {
	        	
		//handle interpreter exceptions here
	
	} finally{
	        	
		//delete interpreter instance (safer)
		try {
			Ghostscript.deleteInstance();
		} catch (GhostscriptException e) {
			//nothing
	}    

### Multi-threading

Making sure the Ghostscript is thread safe is a first step. But what if Ghost4J is to be used in a multi-thread / multi-user environment (in a webapp for instance)?
 
If Ghost4J is used to write a document conversion webapp, using a single Ghostscript interpreter may be a real problem if users have to wait for a previous user request to complete.
 
To get over this limitation Ghost4J provides multi-threading support on its high level API components (since version 0.4.0).
 
How it is possible? : component processing takes place in different JVMs.
 
Components in the main JVM are able to start other JVMs (running in other system processes) and control them using the [cajo](https://cajo.dev.java.net) library (embedded in the ghost4j JAR file).
 
To make sure 'slave' JVMs can be created from the main JVM, check if Java can be launched from command line using the **java** command.
 
Multi-threading behavior can be controlled by setting the **maxProcessCount** property on a component (when available):
 
* When = 0: multi-threading is disabled. Component will have to wait for the Ghostscript interpreter to get free before starting its processing.
  
* When > 0: multi-threading is enabled. Component processing will not take place in the main JVM but in a 'slave' JVM. 
  The value given to **maxProcessCount** indicates how many 'slave' JVMs can run concurrently for the component.
  When the max number of 'slave' JVMs is reached, new processing requests will wait for another processing to complete.
 
Here is how a **PDFConverter** component is setup to allow multi-threading with 2 'slave' JVMs:
 
	//create converter
	PDFConverter converter = new PDFConverter();
	
	//set multi-threading
	converter.setMaxProcessCount(2);
 
   
 