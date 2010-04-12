/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.util;

import java.io.File;
import java.util.Map;

/**
 * This class allows launching another JVM from the current JVM.
 * It takes the same classpath as the parent JVM.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class JavaFork implements Runnable {

    /**
     * Start class of the JVM.
     */
    private Class startClass;
    /**
     * Process object of the JVM.
     * Is null if the JVM is not running.
     */
    private Process process;
    /**
     * If set to TRUE, output and error streams are redirected to the main JVM output stream
     */
    private boolean redirectStreams;
    /**
     * If set to TRUE, main JVM will wait for this JVM to stop before exiting.
     */
    private boolean waitBeforeExiting = false;

    /**
     * Additional environment variables.
     */
    private Map<String, String> environment;

    public void start(Class startClass) {

        this.setStartClass(startClass);
        this.start();

    }

    public void start() {

        //start thread
        final Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();

        //register shutdown hook to wait for thread when JVM exists
        if (waitBeforeExiting){
            Runtime.getRuntime().addShutdownHook(new Thread(){

                @Override
                public void run() {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //nothing
                    }
                }

            });
        }

    }

    public void run() {

        //check if process is not already running
        if (process != null) {
            throw new RuntimeException("Fork is already running");
        }

        //check if start class is set
        if (startClass == null) {
            throw new RuntimeException("No start class defined");
        }

        //retrieve classpath
        String classPath = System.getProperty("java.class.path");

        //build child process
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", classPath, startClass.getName());
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.environment().putAll(System.getenv());
        if (getEnvironment() != null){
           processBuilder.environment().putAll(getEnvironment());
        }

        //start
        try {
            process = processBuilder.start();

            //redirect output stream to main process output stream
            if (redirectStreams) {
                //error stream
                processBuilder.redirectErrorStream(true);
                //standard stream
                StreamGobbler outputStreamGobbler = new StreamGobbler(process.getInputStream(), System.out);
                outputStreamGobbler.start();
            }

            process.waitFor();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Class getStartClass() {
        return startClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    public boolean getRedirectStreams() {
        return redirectStreams;
    }

    public void setRedirectStreams(boolean redirectStreams) {
        this.redirectStreams = redirectStreams;
    }

    public boolean getWaitBeforeExiting() {
        return waitBeforeExiting;
    }

    public void setWaitBeforeExiting(boolean waitBeforeExiting) {
        this.waitBeforeExiting = waitBeforeExiting;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }
}
