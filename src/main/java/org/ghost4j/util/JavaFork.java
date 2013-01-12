/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Map;

/**
 * This class allows launching another JVM from the current JVM. It takes the
 * same classpath as the parent JVM.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class JavaFork implements Runnable {

    private static final String JAVA_COMMAND;
    private static final String PATH_SEPARATOR = System
	    .getProperty("path.separator");

    static {
	if (System.getProperty("os.name").toLowerCase().contains("windows")) {
	    JAVA_COMMAND = "javaw";
	} else {
	    JAVA_COMMAND = "java";
	}
    }

    /**
     * Start class of the JVM.
     */
    private Class<?> startClass;
    /**
     * Process object of the JVM. Is null if the JVM is not running.
     */
    private Process process;
    /**
     * If set to TRUE, output and error streams are redirected to the main JVM
     * output stream
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

    /**
     * Xmx parameter. Default value is set to 128M.
     */
    private String xmx = "128m";

    /**
     * Xms parameter. Default value is set to 64M.
     */
    private String xms = "64m";

    public void start(Class<?> startClass) {

	this.setStartClass(startClass);
	this.start();

    }

    public void start() {

	// start thread
	final Thread thread = new Thread(this);
	thread.setDaemon(false);
	thread.start();

	// register shutdown hook to wait for thread when JVM exists
	if (waitBeforeExiting) {
	    Runtime.getRuntime().addShutdownHook(new Thread() {

		@Override
		public void run() {
		    try {
			thread.join();
		    } catch (InterruptedException e) {
			// nothing
		    }
		}

	    });
	}

    }

    public void stop() {

	if (process != null) {
	    process.destroy();
	}
    }

    public void run() {

	// check if process is not already running
	if (process != null) {
	    throw new RuntimeException("Fork is already running");
	}

	// check if start class is set
	if (startClass == null) {
	    throw new RuntimeException("No start class defined");
	}

	// retrieve classpath
	String classPath = this.getCurrentClasspath();

	// build child process
	String ghost4JEncoding = System.getProperty("ghost4j.encoding");
	String fileEncoding = "-Dfile.encoding=";
	if (ghost4JEncoding != null) {
	    fileEncoding += ghost4JEncoding;
	} else {
	    fileEncoding += System.getProperty("file.encoding");
	}
	ProcessBuilder processBuilder = new ProcessBuilder(JAVA_COMMAND,
		fileEncoding, "-Xms" + xms, "-Xmx" + xmx, "-cp", classPath,
		startClass.getName());
	if (System.getProperty("jna.library.path") != null) {
	    String jnaLibraryPath = "-Djna.library.path="
		    + System.getProperty("jna.library.path");
	    processBuilder = new ProcessBuilder(JAVA_COMMAND, fileEncoding,
		    jnaLibraryPath, "-Xms" + xms, "-Xmx" + xmx, "-cp",
		    classPath, startClass.getName());
	}
	processBuilder.directory(new File(System.getProperty("user.dir")));
	processBuilder.environment().putAll(System.getenv());
	if (getEnvironment() != null) {
	    processBuilder.environment().putAll(getEnvironment());
	}

	// start
	try {
	    process = processBuilder.start();

	    // redirect output stream to main process output stream
	    if (redirectStreams) {
		// error stream
		processBuilder.redirectErrorStream(true);
		// standard stream
		StreamGobbler outputStreamGobbler = new StreamGobbler(
			process.getInputStream(), System.out);
		outputStreamGobbler.start();
	    }

	    process.waitFor();

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

    }

    private String getCurrentClasspath() {
	StringBuilder cpBuilder = new StringBuilder();
	URL[] urls = ((URLClassLoader) Thread.currentThread()
		.getContextClassLoader()).getURLs();

	for (int i = 0; i < urls.length; i++) {
	    // need to do some conversion to get the paths right
	    // otherwise paths get broken on windows
	    String s = urls[i].toExternalForm();

	    try {
		s = URLDecoder.decode(s, "UTF-8");
		urls[i] = new URL(s);
		s = new File(urls[i].getFile()).getAbsolutePath();
		cpBuilder.append(s);
		if (i < urls.length - 1)
		    cpBuilder.append(PATH_SEPARATOR);
	    } catch (UnsupportedEncodingException e) {
		// should never happen as we pass supported encoding UTF-8
	    } catch (MalformedURLException e) {
		// should also never happen at this point, but who knows ;-)
	    }
	}

	String cp = cpBuilder.toString();

	if (cp.isEmpty() || cp.contains("surefirebooter")) {
	    // if called from Maven: use the java.class.path property as
	    // classpath
	    return System.getProperty("java.class.path");
	} else {
	    return cp;
	}

    }

    public Class<?> getStartClass() {
	return startClass;
    }

    public void setStartClass(Class<?> startClass) {
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

    public String getXmx() {
	return xmx;
    }

    public void setXmx(String xmx) {
	this.xmx = xmx;
    }

    public String getXms() {
	return xms;
    }

    public void setXms(String xms) {
	this.xms = xms;
    }

}
