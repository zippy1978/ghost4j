/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Network utilities class.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class NetworkUtil {

	/**
	 * Finds an available port within a port range on a host
	 * @param hostname Host name
	 * @param startPort Port number starting the range
	 * @param endPort Port number ending the range
	 * @return An available port number, or 0 if none is available.
	 */
	public static int findAvailablePort(String hostname, int startPort, int endPort){
		
		for ( int port = startPort; port < (endPort +1); port++) {
            
			try {
				Socket socket = new Socket(InetAddress.getByName(hostname),port);
				//port not available
				socket.close();
			} catch (IOException e) {
				//port available
				return port;
			}
        }
		
		return 0;
	}
	
	/**
	 * Waits until a port is listening on a given host. An exception is thrown if the timeout is excedeed.
	 * @param hostname Host name
	 * @param port Port number
	 * @param timeout Timeout in milliseconds
	 * @throws UnknownHostException If host name is unknown
	 * @throws IOException If a connection error occurs or if the timeout is exceeded
	 */
	public static void waitUntilPortListening(String hostname, int port, int timeout) throws UnknownHostException, IOException{
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(InetAddress.getByName(hostname), port),timeout);
		socket.close();
	}
}
