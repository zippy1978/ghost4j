/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.converter;

import gnu.cajo.Cajo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.util.JavaFork;

/**
 * Abstract remote converter implementation.
 * Used as base class for remote converters.
 * @author Gilles Grousset (gi.grousset@gmail.com)
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

    /**
     * Starts a remote converter server.
     * @param remoteConverter
     * @throws ConverterException
     */
    public static void startRemoteConverter(RemoteConverter remoteConverter) throws ConverterException{

        try {

            //get port
            if (System.getenv("cajo.port") == null){
                throw new ConverterException("No Cajo port defined for remote converter");
            }
            int cajoPort = Integer.parseInt(System.getenv("cajo.port"));

            //start cajo server
            Cajo cajo = new Cajo(cajoPort, null, null);

            //export converter
            remoteConverter.setMaxProcessCount(0);
            cajo.export(remoteConverter);


        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public synchronized byte[] remoteConvert(Document document) throws IOException, ConverterException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        run(document, baos);

        byte[] result = baos.toByteArray();
        baos.close();

        return result;
    }

    @Override
    public synchronized void convert(Document document, OutputStream outputStream) throws IOException, ConverterException {

        if (maxProcessCount == 0) {

            //perform actual processing
            run(document, outputStream);

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

            //TODO get free TCP port to run Cajo on
            int cajoPort = 1198;

            //start new JVM with current converter
            JavaFork fork = new JavaFork();
            fork.setRedirectStreams(true);
            fork.setWaitBeforeExiting(false);
            fork.setStartClass(this.getClass());

            //add extra environment variables
            Map<String, String> environment = new HashMap<String, String>();
            //Cajo port
            environment.put("cajo.port", String.valueOf(cajoPort));
            fork.setEnvironment(environment);

            fork.start();

            //send document to new JVM
            try {

                //TODO wait for the remote JVM to start
                Thread.sleep(800);

                //register cajo
                //TODO: add dynamic client port attribution
                Cajo cajo = new Cajo(7777, null, null);
                cajo.register("127.0.0.1", cajoPort);

                //get remote converter
                Object refs[] = cajo.lookup(Converter.class);
                if (refs.length == 0) {
                    //not converter found
                    fork.stop();
                    throw new ConverterException("No remote converter process found");
                }
                RemoteConverter remoteConverter = (RemoteConverter) cajo.proxy(refs[0], RemoteConverter.class);

                // clone converter settings
                remoteConverter.cloneSettings(this);

                //perform remote convertion
                byte[] result = remoteConverter.remoteConvert(document);

                //write result to output stream
                outputStream.write(result);


            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                fork.stop();
            }


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
