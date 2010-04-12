/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.ghost4j.converter;

import gnu.cajo.Cajo;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.util.JavaFork;

/**
 *
 * @author ggrousset
 */
public abstract class AbstractRemoteConverter extends AbstractConverter implements RemoteConverter {

    /**
     * Maximum number of parallel processes allowed for the converter.
     */
    protected int maxProcessCount = 0;
    /**
     * Number of parallel processes running
     */
    protected int processCount = 0;

    public boolean isConverting() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroy() {
        System.exit(0);
    }

    @Override
    public synchronized void convert(Document document, OutputStream outputStream) throws IOException, ConverterException {

        if (maxProcessCount == 0) {

            System.out.println("--------CONVERT " + System.getenv("ghost4j.child"));

            //perform actual processing
            run(document, outputStream);

            //if the current process is a child: stop current JVM
            if (System.getenv("ghost4j.child") != null) {
                System.exit(0);
            }

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
                throw new ConverterException("Standalone mode is not supported by this converter: no 'main' method found");
            }

            //get free TCP port to run Cajo on
            //TODO
            int cajoPort = 1198;

            //start new JVM with current converter
            JavaFork fork = new JavaFork();
            fork.setRedirectStreams(true);
            //TODO: add a parameter in converter to control exiting method
            fork.setWaitBeforeExiting(false);
            fork.setStartClass(this.getClass());

            //add extra environment variables
            Map<String, String> environment = new HashMap<String, String>();
            //Cajo port
            environment.put("cajo.port", String.valueOf(cajoPort));
            //indicates that the process is a child
            environment.put("ghost4j.child", "YES");
            fork.setEnvironment(environment);

            fork.start();

            //send document to new JVM
            try {

                //register cajo
                //TODO: add dynamic client port attribution
                Cajo cajo = new Cajo(7777, null, null);
                cajo.register("127.0.0.1", cajoPort);

                //get remote converter
                Object refs[] = cajo.lookup(Converter.class);
                if (refs.length == 0) {
                    //not converted found
                    throw new ConverterException("No remote converter process found");
                }
                Converter remoteConverter = (Converter) cajo.proxy(refs[0], Converter.class);
                System.out.println(">>> REMOTE CONVERTION");
                remoteConverter.convert(document, outputStream);


            } catch (Exception e) {
                throw new IOException(e);
            }

            //TODO: handle finish
            processCount--;
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
