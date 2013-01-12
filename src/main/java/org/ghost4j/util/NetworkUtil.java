/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Network utilities class.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class NetworkUtil {

    /**
     * Finds an available port within a port range on a host
     * 
     * @param hostname
     *            Host name
     * @param startPort
     *            Port number starting the range
     * @param endPort
     *            Port number ending the range
     * @return An available port number, or 0 if none is available.
     */
    public static synchronized int findAvailablePort(String hostname,
	    int startPort, int endPort) {

	for (int port = startPort; port < (endPort + 1); port++) {

	    try {
		Socket socket = new Socket(InetAddress.getByName(hostname),
			port);
		// port not available
		socket.close();
	    } catch (IOException e) {
		// port available
		return port;
	    }
	}

	return 0;
    }

    /**
     * Waits until a port is listening on a given host. An exception is thrown
     * if the timeout is excedeed.
     * 
     * @param hostname
     *            Host name
     * @param port
     *            Port number
     * @param timeout
     *            Timeout in seconds
     * @throws IOException
     *             If a connection error occurs or if the timeout is exceeded
     */
    public static void waitUntilPortListening(String hostname, int port,
	    int timeout) throws IOException {

	int i = 0;
	while (i < timeout) {

	    // try to get connection
	    try {
		Socket socket = new Socket(InetAddress.getByName(hostname),
			port);
		// connection OK: exit
		socket.close();
		return;
	    } catch (IOException e) {
		// nothing
	    }

	    i++;

	    // wait for 1 second
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		// nothing
	    }
	}

	throw new IOException("Timeout waiting for port " + port + " to listen");
    }
}
