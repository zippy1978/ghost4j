package gnu.cajo.utils;

import java.io.*;
import java.net.*;
import gnu.cajo.invoke.Remote;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * RMI Codebase and Graphical Proxy Server
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file CodebaseServer.java is part of the cajo library.
 *
 * The cajo library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public Licence as published
 * by the Free Software Foundation, at version 3 of the licence, or (at your
 * option) any later version.
 *
 * The cajo library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence
 * along with this library. If not, see http://www.gnu.org/licenses/lgpl.html
 */

/**
 * The standard mechanism to send proxies, and other complex objects to remote
 * VMs. It requires one outbound port. The port can be anonymous, i.e. selected
 * from any available free port at runtime, or it can be explicitly specified,
 * usually to operate through a firewall. It also provides the generic graphical
 * proxy host client service, as an Applet, and via WebStart.
 * <p>
 * <i><u>Note</u>:</i> There can be at most <i>one</i> CodebaseServer
 * instance per JVM.
 * 
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public final class CodebaseServer extends Thread {
   private static final SimpleDateFormat formatter =
      new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
   private static final byte[] // http headers:
      bye = ("HTTP/1.0 404 Not Found\r\n" // unsupported request
         + "Content-type: text/html\r\n"
         + "Server: cajo/CodebaseServer\r\n"
         + "Connection: close\r\n\r\n"
         + "<html><head><title>404: URL Not Found</title></head><body>"
         + "<h1>404 - Not Found</h1>"
         + "The requested resource is not available from this server.<br><br>"
         + "<hr><i>gnu.cajo.utils.CodebaseServer - The cajo project: "
         + "<a href=https://cajo.dev.java.net>https://cajo.dev.java.net</a>."
         + "</i></body></html>").getBytes(),
      apl = ("HTTP/1.0 200 OK\r\n"
         + "Content-type: text/html\r\n"
         + "Server: cajo/CodebaseServer\r\n"
         + "Last-Modified: " + formatter.format(new Date()) + "\r\n"
         + "Connection: close\r\n\r\n").getBytes(), // for applets
      jws = ("HTTP/1.0 200 OK\r\n"
         + "Content-type: application/x-java-jnlp-file\r\n"
         + "Server: cajo/CodebaseServer\r\n"
         + "Last-Modified: " + formatter.format(new Date()) + "\r\n"
         + "Connection: close\r\n\r\n").getBytes(), // for WebStart
      jarHdr = ("HTTP/1.0 200 OK\r\n" // for jar files
         + "Content-type: application/x-java-archive\r\n"
         + "Server: cajo/CodebaseServer\r\n").getBytes(), // for WebStart
      classHdr = ("HTTP/1.0 200 OK\r\n" // for class files
         + "Content-type: application/x-java-vm\r\n"
         + "Server: cajo/CodebaseServer\r\n").getBytes(), // for WebStart
      imgHdr = ("HTTP/1.0 200 OK\r\n" // for image files
         + "Content-type: image/jpeg\r\n"
         + "Server: cajo/CodebaseServer\r\n").getBytes(), // for WebStart

      end = ( // http footers:
         "PLUGINSPAGE=\"http://java.sun.com/j2se/1.5.0/download.html\">\r\n"
         + "</EMBED></COMMENT></OBJECT></CENTER></BODY></HTML>")
         .getBytes(),
      out = ("  </application-desc>\r\n" + "</jnlp>").getBytes();
   private final byte[] top, mid, tip, xml;
   private final ServerSocket ss;
   private PrintStream log;
   /**
    * This is the inbound ServerSocket port number providing both the HTTP
    * client tag and codebase jar service. If the server is behind a firewall,
    * this port, must be made accessible from outside. If the port argument used
    * in the constructor was zero, it will use an anonymous port; i.e. one
    * selected by the OS from any ports available at runtime. In that case, the
    * port actually offered by the operating system will be stored here
    * automatically, following construction.
    */
   public final int serverPort;
   /**
    * This is the inbound ServerSocket port number providing both the HTTP
    * client tag and codebase jar service. If the server is behind a firewall,
    * this port, must be made accessible from outside. If the port argument used
    * in the constructor was zero, it will use an anonymous port; i.e. one
    * selected by the OS from any ports available at runtime. In that case, the
    * port actually offered by the operating system will be stored here
    * automatically, following construction.
    * 
    * @deprecated The preferred field to check is <tt>serverPort</tt> as this
    * field is mutable unfortunately; it remains solely to maintain backward
    * compatibility.
    */
   public static int port;
   /**
    * The main constructor will start up the server's codebase transport
    * mechanism on the specified port. To shut the service down, call its
    * inherited interrupt method. All other constructors in this class call it.
    * 
    * @param jars An array of strings representing the path and filename of a
    * library jar needed by the client application. The CodebaseServer will
    * serve them to the remote JVM. The server will first search for the jar in
    * its own executable jar file, if that fails, then it will check the local
    * filesystem. The jars could represent individual proxies, or
    * general-purpose shared libraries.
    * <p>The first jar named in the array will be assumed to be the jar
    * containing the main client class.
    * <p><i><u>Note</u>:</i> if this value is null, it indicates that the proxy
    * codebase is <i>not</i> in a jar. The server will then look first in its
    * own jar file for the class files to send, and if not found, it will next
    * look in its working directory. This feature provides an extremely simple,
    * essentially zero-configuration, approach to proxy codebase service.
    * <p>As a special safety feature, if the jar from which the service items
    * are running is given the <i>special</> name <tt>service.jar</tt> it
    * <b>not</i> be allowed to be requested over the network. This would be
    * important for example, if you did not wish to share the server.jar file.
    * @param port The TCP port on which to serve the codebase, and client
    * applet. It can be zero, to use an anonymous port. If zero, the actual port
    * selected by the OS at runtime will be stored in the
    * {@link #serverPort serverPort} member.
    * @param client The name of the graphical client class to be furnished as an
    * Applet, or via WebStart. For example, the generic cajo standard graphical
    * proxy is: <tt>gnu.cajo.invoke.Client</tt>
    * @param title The optional application specific titile to show in the
    * browser, when running as an applet.
    * @param vendor The optional vendor name of the WebStart application.
    * @param icon The optional custom desktop icon to represent the WebStart
    * application, it can also be null. It is typically of the form
    * "images/icon.gif", where it will be requested from the server. The image
    * can be in either GIF or JPEG format.
    * @param splash The optional splash screen image to represent the WebStart
    * application while it is loading, it can also be null. It is typically of
    * the form "images/splash.jpeg", where it will be requested from the server.
    * The image can be in either GIF or JPEG format.
    * @throws IOException If the HTTP socket providing the codebase and applet
    * tag service could not be created.
    */
   public CodebaseServer(String jars[], int port, String client, String title,
         String vendor, String icon, String splash) throws IOException {
      String temp = client.replace('.', '/') + ".class";
      StringBuffer base = new StringBuffer();
      if (title == null) title = "cajo Proxy Viewer";
      if (jars != null) {
         base.append(jars[0]);
         for (int i = 1; i < jars.length; i++) {
            base.append(", ");
            base.append(jars[i]);
         }
      } else base.append("client.jar");
      top = ("<HTML><HEAD><TITLE>" // create instance specific response data:
         + title
         + "</TITLE>\r\n"
         + "<META NAME=\"description\" content=\"Graphical cajo proxy client\">\r\n"
         + "<META NAME=\"copyright\" content=\"Copyright (c) 1999 John Catherino\">\r\n"
         + "<META NAME=\"author\" content=\"John Catherino\">\r\n"
         + "<META NAME=\"generator\" content=\"CodebaseServer\">\r\n"
         + "</HEAD><BODY leftmargin=\"0\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" rightmargin=\"0\">\r\n"
         + "<CENTER><OBJECT classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\"\r\n"
         + "WIDTH=\"100%\" HEIGHT=\"100%\"\r\n"
         + "CODEBASE=\"http://java.sun.com/products/plugin/autodl/jinstall-1_5_0-windows-i586.cab#Version=1,5,0,0\">\r\n"
         + "<PARAM NAME=\"draggable\" VALUE=\"true\">\r\n"
         + "<PARAM NAME=\"archive\" VALUE=\"" + base.toString() + "\">\r\n"
         + "<PARAM NAME=\"type\" VALUE=\"application/x-java-applet;version=1.5\">\r\n"
         + "<PARAM NAME=\"code\" VALUE=\"" + temp + "\">\r\n"
         ).getBytes();
      mid = ("<COMMENT><EMBED type=\"application/x-java-applet;version=1.5\"\r\n"
         + "ARCHIVE=\"" + base.toString() + "\"\r\n"
         + "CODE=\"" + temp + "\"\r\n" + "WIDTH=\"100%\" HEIGHT=\"100%\"\r\n"
         + "DRAGGABLE=\"true\"\r\n"
         ).getBytes();
      ss = Remote.getDefaultServerHost() == null
         ? new ServerSocket(port)
         : new ServerSocket(port, 50, InetAddress.getByName(Remote.getDefaultServerHost()));
      serverPort = port == 0 ? ss.getLocalPort() : port;
      CodebaseServer.port = serverPort; // legacy
      tip = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
         + "<jnlp spec=\"1.5+\"\r\n" + "  codebase=" + "\"http://"
         + (Remote.getDefaultClientHost() != null
         ? Remote.getDefaultClientHost()
         : InetAddress.getLocalHost().getHostAddress()) + ':' + serverPort
         + "\"\r\n").getBytes();
      base = new StringBuffer("  <information>\r\n"
         + "    <title>" + title + "</title>\r\n"
         + "    <vendor>" + (vendor != null ? vendor : "The cajo project") + "</vendor>\r\n"
         + "    <description>Graphical cajo proxy client</description>\r\n"
         + "    <homepage href=\"https://cajo.dev.java.net\"/>\r\n"
         + (icon == null ? "" : "    <icon href=\"" + icon + "\"/>\r\n")
         + (splash == null ? "" : "    <icon href=\"" + splash + "\" kind=\"splash\"/>\r\n")
         + "    <shortcut><desktop/></shortcut>\r\n"
         + "  </information>\r\n" + "  <resources>\r\n"
         + "    <j2se version=\"1.5+\"/>\r\n");
      if (jars != null) {
         base.append("    <jar href=\"" + jars[0] + "\" main=\"true\"/>\r\n");
         for (int i = 1; i < jars.length; i++)
            base.append("    <jar href=\"" + jars[i] + "\" download=\"lazy\"/>\r\n");
      } else base.append("    <jar href=\"client.jar\" main=\"true\"/>\r\n");
      base.append("  </resources>\r\n");
      base.append("  <application-desc main-class=\"");
      base.append(client);
      base.append("\">\r\n");
      xml = base.toString().getBytes();
      String loc = "http://" + (Remote.getDefaultClientHost() != null ?
         Remote.getDefaultClientHost() :
            InetAddress.getLocalHost().getHostAddress())
               + ':' + CodebaseServer.port + '/';
      base = new StringBuffer();
      if (jars != null) {
         for (int i = 0; i < jars.length; i++) {
            base.append(loc);
            base.append(jars[i]);
            if (i < jars.length - 1) base.append(' ');
         }
      } else base.append(loc);
      if (System.getProperty("java.rmi.server.codebase") != null) {
         base.append(' ');
         base.append(System.getProperty("java.rmi.server.codebase"));
      }
      System.setProperty("java.rmi.server.codebase", base.toString());
      setDaemon(true); // don't stay awake just because of us
      start(); // ready to accept clients
   }
   /**
    * This constructor will start up the server's codebase transport mechanism
    * on the specified port, using the specified codebase jar file, with the
    * specified clent.
    * @param base The path and name of the file containing the proxy codebase
    * jar file.
    * @param port The TCP port on which to serve the codebase, and client
    * applet.
    * @param client The name of the graphical client class to be furnished as an
    * Applet, or via WebStart.
    * @throws IOException If the HTTP socket providing the codebase and applet
    * tag service could not be created.
    */
   public CodebaseServer(String base, int port, String client)
      throws IOException {
      this(base != null ? new String[]{"client.jar", base} :
         null, port, client, null, null, null, null);
   }
   /**
    * This constructor simply calls the three argument constructor, providing
    * the standard cajo generic graphical client as the client argument.
    * @param base The path and name of the file containing the proxy codebase
    * jar file.
    * @param port The TCP port on which to serve the codebase, and client
    * applet.
    * @throws IOException If the HTTP socket providing the codebase and applet
    * tag service could not be created.
    */
   public CodebaseServer(String base, int port) throws IOException {
      this(base, port, "gnu.cajo.invoke.Client");
   }
   /**
    * This method can be used to log the client requests of the Codebase
    * server. The log can range from System.out, to a network socket
    * OutputStream.<p>
    * <i><u>Note</u>:</i> Only one log stream can be assigned to the
    * CodebaseServer at any given time.
    * @param log The OutputStream to record the client requests. If this
    * argument is null, no change will take place.
    */
   public void setLog(OutputStream log) {
      if (log != null) this.log = log instanceof PrintStream ?
         (PrintStream)log : new PrintStream(log);
   }
   /**
    * The server thread method, it will send the proxy codebase, and it will
    * also support installing the hosting {@link gnu.cajo.invoke.Client Client},
    * or application specific host, in a Java-enabled browser, or as a web start
    * application via JNLP.
    * <p> The format of a browser's proxy request URL one required, and five
    * optional parameters, utilizing the following format:
    * <p><code>
    * http://serverHost[:serverPort]/[clientPort][:localPort][-proxyName][!]
    * </code><p>
    * Where the parameters have the following meanings:
    * <ul>
    * <li><i>serverHost</i> The domain name, or IP address of the proxy
    * server.
    * <li><i>serverPort</i> The port number of the applet/codebases service,
    * unspecified it will be 80.
    * <li><i>clientPort</i> The client's external port number on which the
    * remote proxy can be reached. It is often explicitly specified when the
    * client is behind a firewall, or is using port translation. Unspecified, it
    * will be the same as the localPort value, described next.
    * <li><i>localPort</i> The client's internal port number on which the
    * remote proxy can be reached. Unspecified, it will be selected anonymously
    * by the client at runtime.
    * <li><i>proxyName</i> The registered name of the proxy serving object, by
    * default "main", however a single server can support multiple service
    * objects.
    * <li><i>!</i> This operator causes the proxy to be sent using JNLP. This
    * will launch the proxy as an application on the client using WebStart.
    * </ul>
    * <p>
    * To unspecify any optional object, simply omit it, from the URL, along
    * with its preceeding delimiter, if any. The <u>order</u> of the arguments
    * must be maintained however.
    * <p>
    * <i>Note:</i> other object servers can share this instance, by placing
    * their proxy classes or jar files in the same working directory. However,
    * those object servers will not be able to use the client service feature,
    * as it is unique to the VM in which the CodebaseServer is running.
    * <p>
    * As a safety precaution, the server will send any requested jar or class
    * file in or below its working directory <i>except</i> the jar file of the
    * server itself. Typically people do not want to give this file out.
    */
   public void run() {
      try {
         byte msg[] = new byte[0x1000]; // allocate a 4k data transfer buffer
         while (!isInterrupted()) {
            Socket s = ss.accept();
            try {
               InputStream is = s.getInputStream();
               OutputStream os = 
                  new BufferedOutputStream(s.getOutputStream(), 0x8000);
               int ix = is.read(msg);
               String itemName = null;
               scan : for (int i = 0; i < ix; i++) { // scan client request
                  if (msg[i] == '/') {
                     for (int j = i + 1; j < msg.length; j++) {
                        if (msg[j] == ' ') {
                           itemName = new String(msg, i, j - i);
                           break scan;
                        }
                     }
                  }
               }
               String clientHost = s.getInetAddress().getHostAddress();
               if (log != null)
                  log.println("Client " + clientHost + " request: " + itemName);
               if (itemName == null) os.write(bye); // invalid request
               else if (itemName.indexOf('.') == -1
                  && itemName.indexOf('/', 1) == -1) {
                  try { // URL request: parse arguments
                     int ia = itemName.indexOf(':') != -1 ? itemName
                        .indexOf(':') : itemName.indexOf('-') != -1
                        ? itemName.indexOf('-')
                        : itemName.indexOf('!') != -1
                        ? itemName.indexOf('!') : itemName.length();
                     int ib = itemName.indexOf('-') != -1 ? itemName
                        .indexOf('-') : itemName.indexOf('!') != -1
                        ? itemName.indexOf('!')
                        : itemName.length();
                     int ic = itemName.indexOf('!') != -1
                        ? itemName.indexOf('!') : itemName.length();
                     String clientPort = ia > 1
                        ? itemName.substring(1, ia) : "0";
                     String localPort = ib > ia
                        ? itemName.substring(ia + 1, ib) : "0";
                     String proxyName = ic > ib
                        ? itemName.substring(ib + 1, ic) : "main";
                     Integer.parseInt(clientPort); // test URL validity
                     Integer.parseInt(localPort); // test URL vaidity
                     int proxyPort = Remote.getDefaultClientPort();
                     if (itemName.indexOf('!') == -1) { // Applet request
                        byte iex[] = ( // used by Exploder:
                           "<PARAM NAME=\"clientHost\" VALUE=\"" + clientHost
                           + "\">\r\n"
                           + "<PARAM NAME=\"clientPort\" VALUE=\""
                           + clientPort + "\">\r\n"
                           + "<PARAM NAME=\"localPort\"  VALUE=\""
                           + localPort + "\">\r\n"
                           + "<PARAM NAME=\"proxyPort\"  VALUE=\""
                           + proxyPort + "\">\r\n"
                           + "<PARAM NAME=\"proxyName\"  VALUE=\""
                           + proxyName + "\">\r\n").getBytes();
                        byte nav[] = ( // used by Navigator and Appletviewer:
                           "clientHost=\"" + clientHost + "\"\r\n"
                           + "clientPort=\"" + clientPort + "\"\r\n"
                           + "localPort=\"" + localPort + "\"\r\n"
                           + "proxyPort=\"" + proxyPort + "\"\r\n"
                           + "proxyName=\"" + proxyName + "\"\r\n").getBytes();
                        os.write(apl);
                        os.write(top);
                        os.write(iex); // return client specific applet page
                        os.write(mid);
                        os.write(nav);
                        os.write(end);
                     } else { // WebStart request
                        byte obj[] = ("  href=\"" + clientPort + ':'
                           + localPort + '-' + proxyName + "!\">\r\n").getBytes();
                        byte arg[] = ("    <argument>//"
                           + Remote.getDefaultClientHost() + ':' + proxyPort + '/'
                           + proxyName + "</argument>\r\n"
                           + "    <argument>" + clientPort
                           + "</argument>\r\n" + "    <argument>"
                           + clientHost + "</argument>\r\n"
                           + "    <argument>" + localPort + "</argument>\r\n")
                           .getBytes();
                        os.write(jws);
                        os.write(tip);
                        os.write(obj); // return client specific jnlp document
                        os.write(xml);
                        os.write(arg);
                        os.write(out);
                     }
                  } catch (Exception x) { os.write(bye); }
               } else if (!itemName.endsWith("service.jar")) {
                  if ( // file request
                     itemName.equals("/favicon.ico") ||
                     itemName.endsWith(".jar")       ||
                     itemName.endsWith(".class")     ||
                     itemName.endsWith(".gif")       ||
                     itemName.endsWith(".jpg")       ||
                     itemName.endsWith(".jpeg")
                  ) try {
                     InputStream ris = getClass().getResourceAsStream(itemName);
                     if (ris == null) // resource not inside server jar
                     ris = new FileInputStream('.' + itemName);
                     os.write(itemName.endsWith(".jar") ? jarHdr :
                        itemName.endsWith(".class") ? classHdr : imgHdr);
                     os.write(formatter.format(new Date(new File('.' +
                        itemName).lastModified())).getBytes());
                     os.write("\r\nConnection: close\r\n\r\n".getBytes());
                     for (int i = ris.read(msg); i != -1; i = ris.read(msg))
                        os.write(msg, 0, i);
                     ris.close();
                  } catch (Exception x) { os.write(bye); }
                  else os.write(bye); // send only jar, class, or image files
               } else os.write(bye); // no other requests are honored
               os.flush(); // make sure all bytes are sent
               os.close(); // terminate client connection
               is.close(); // terminate further requests from client
            } catch (Exception x) { x.printStackTrace(); }
            try { s.close(); } catch (Exception x) { x.printStackTrace(); }
         }
      } catch (Exception x) { x.printStackTrace(); }
      try { ss.close(); } catch (Exception x) { x.printStackTrace(); }
   }
   /**
    * The application creates a utility server to share any jar and class
    * files in its working directory and subdirectories. It is very useful in
    * application development. If a port number is provided as an argument,
    * it will be used, otherwise it will be opened on an anonymous port.
    */
   public static void main(String args[]) throws Exception {
      CodebaseServer c = args.length == 0
         ? new CodebaseServer(null, 0)
         : new CodebaseServer(null, Integer.parseInt(args[0]));
      System.out.println("Codebase service on port " + c.serverPort);
   }
}
