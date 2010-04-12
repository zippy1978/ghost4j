package gnu.cajo.invoke;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.rmi.registry.*;
import java.rmi.MarshalledObject;

/*
 * Graphical Proxy Loader Applet / Application
 * Copyright (c) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Client.java is part of the cajo library.
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
 * This class is used to create a hosting VM to receive a graphical proxy
 * object, from a remote VM.  The client will require one outbound port, on
 * which to commuinicate with its proxy server and one on inbound, on which to
 * receive asynchronous callbacks from the server. It will also require one
 * short-term inbound port on which to receive the proxy class files. If the
 * client is behind a firewall, these will have to be open.
 *
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public final class Client extends java.applet.Applet {
   private static final long serialVersionUID = 1L;
   private static Object proxy;
   private Graphics gbuffer;
   private Image ibuffer;
   private static final class CFrame extends Frame implements WindowListener {
      private static final long serialVersionUID = 1L;
      public CFrame(String title) {
         super(title);
         addWindowListener(this);
      }
      public void update(Graphics g) { paint(g); }
      public void windowActivated(WindowEvent e)   {}
      public void windowDeactivated(WindowEvent e) {}
      public void windowOpened(WindowEvent e)      {}
      public void windowIconified(WindowEvent e)   {}
      public void windowDeiconified(WindowEvent e) {}
      public void windowClosing(WindowEvent e)     { dispose();      }
      public void windowClosed(WindowEvent e)      { System.exit(0); }
   }
   /**
    * The default constructor does nothing.  Initialization is done when
    * the applet is loaded into the browser, or when it is instantiated as
    * an application.
    */
   public Client() {}
   /**
    * This method provides the standard mechanism to identify this applet.
    * @return The identification string for this applet.
    */
   public String getAppletInfo() {
      return "cajo Proxy Applet, Copyright \u00A9 1999 by John Catherino";
   }
   /**
    * When running as an applet, this method describes the optional client
    * parameters. There are five such parameters which can be specified:
    * <ul>
    * <li>The <code>proxyName</code> parameter is the name of the proxy server
    * item registered in the server's rmiregistry.  Unspecified it will be
    * "main".
    * <li>The <code>proxyPort</code> parameter is the outbound port number on
    * which to contact the proxy server.  Unspecified it will be 1099.  If the
    * client is operating behind a firewall, the must be a permitted outbound
    * port.
    * <li>The <code>clientHost</code> parameter is the external domain name or
    * IP address the server must use to callback its proxy.  It may need to
    * be specified if the client is operating behind a NAT router. Unspecified
    * it will be the client's default host address.
    * <li>The <code>clientPort</code> parameter is the external inbound port
    * number on which the server can contact its proxy. It may need to be
    * specified if the client is behind NAT, to map to the correct local port.
    * If a firewall is being used, it must be a permitted inbound port.
    * Unspecified, it will be the same as the local port value below.
    * <li>The <code>localPort</code> parameter is the internal inbound port
    * number on which the server can contact its proxy. It may need to be
    * specified if the client is behind NAT, to map to the correct remote port.
    * Unspecified, it will be anonymous.
    * </ul>
    * @return The parameter / information array.
    */
   public String[][] getParameterInfo() {
      return new String[][] {
         { "proxyName",  "String",  "Server's proxy's registry name" },
         { "proxyPort",  "Integer", "Server's proxy's port number"   },
         { "clientHost", "String",  "Client's external host name"    },
         { "clientPort", "Integer", "Client's external port number"  },
         { "localPort",  "Integer", "Client's internal port number"  },
      };
   }
   /**
    * When running as an applet, this method will connect back to its hosting
    * server and request the item from the server's rmiregistry. Next it will
    * invoke a getProxy(null) on the remote reference to request its proxy
    * item.  If the item returns the proxy in a MarshalledObject, it will be
    * extracted automatically. If the returned object is a proxy, the client
    * will invoke its init method, passing it a remote reference itself, and
    * to obtain its primary graphical representation, which will then be added
    * into the applet's panel.  The proxy can pass its remote reference back
    * to its hosting item, or to other remote items, on which they can
    * asynchronously call it back.
    */
    public void init() {
      try {
         String proxyName  = getParameter("proxyName");
         String proxyPort  = getParameter("proxyPort");
         String clientHost = getParameter("clientHost");
         String clientPort = getParameter("clientPort");
         String localPort  = getParameter("localPort");
         int pPort = proxyPort  != null ? Integer.parseInt(proxyPort)  : 1099;
         int cPort = clientPort != null ? Integer.parseInt(clientPort) : 0;
         int lPort = localPort  != null ? Integer.parseInt(localPort)  : 0;
         if (proxyName == null) proxyName = "main";
         Remote.config("0.0.0.0", lPort, clientHost, cPort);
         proxy = LocateRegistry.getRegistry(getCodeBase().getHost(), pPort);
         proxy = ((Registry)proxy).lookup(proxyName);
         proxy = Remote.invoke(proxy, "getProxy", null);
         if (proxy instanceof MarshalledObject)
            proxy = ((MarshalledObject)proxy).get();
         if (!(proxy instanceof RemoteInvoke)) try {
            proxy = Remote.invoke(proxy, "init", new Remote(proxy));
         } catch(Exception x) {}
         if (proxy instanceof Component) {
            setLayout(new BorderLayout());
            add((Component)proxy);
            validate();
         }
      } catch (Exception x) { x.printStackTrace(); }
   }
   /**
    * This method is called from the AppleContext, each time the applet
    * becomes visible. It will attempt to invoke a no-arg start method on
    * the proxy, in the event that it supports one. Support of the method
    * by the proxy is optional.
    */
   public void start() {
      try { Remote.invoke(proxy, "start", null); }
      catch(Exception x) {}
   }
   /**
    * This method is called from the AppleContext, each time the applet
    * becomes invisible. It will attempt to invoke a no-arg stot method on
    * the proxy, in the event that it supports one. Support of the method
    * by the proxy is optional.
    */
   public void stop() {
      try { Remote.invoke(proxy, "stop", null); }
      catch(Exception x) {}
   }
   /**
    * This method is called from the AppleContext, when the applet is being
    * disposed. It will attempt to invoke a no-arg destroy method on
    * the proxy, in the event that it supports one. Support of the method
    * by the proxy is optional.
    */
   public void destroy() {
      try { Remote.invoke(proxy, "destroy", null); }
      catch(Exception x) {}
   }
   /**
    * The update method double buffers the applet's paint method, to reduce
    * flicker from the default background repainting.
    */
   public void update(Graphics g) {
      int tempW = getWidth(), tempH = getHeight();
      if (ibuffer == null ||
          ibuffer.getWidth(null) != tempW ||
          ibuffer.getHeight(null) != tempH) {
          if (ibuffer != null) ibuffer.flush();
          ibuffer = createImage(tempW, tempH);
          if (gbuffer != null) gbuffer.dispose();
          gbuffer = ibuffer.getGraphics();
      }
      gbuffer.clearRect(0, 0, tempW, tempH);
      paint(gbuffer);
      g.drawImage(ibuffer, 0, 0, null);
   }
   /**
    * This method is used by items to create a frame containing the AWT
    * or Swing component. If the component implements WindowListener, it
    * will be added to its display frame, before being made visible. For
    * AWT components, the frame will be automatically double buffered,
    * for JComponents.
    * @param component The AWT/Swing component, typically returned from a
    * proxy initialization, to be framed.
    * @return the AWT Frame or Swing JFrame containing the component, already
    * visible.
    */
   public static Frame frame(Component component, String title) {
      if (component instanceof JComponent) {
         JFrame frame = new JFrame(title);
         if (component instanceof WindowListener)
            frame.addWindowListener((WindowListener)component);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.getContentPane().add((JComponent)component);
         frame.pack();
         frame.setVisible(true);
         return frame;
      } else {
         Frame frame = new CFrame(title);
         if (component instanceof WindowListener)
            frame.addWindowListener((WindowListener)component);
         frame.add((Component)component);
         frame.pack();
         frame.setVisible(true);
         return frame;
      }
   }
   /**
    * The application creates a graphical proxy hosting VM. With the URL
    * argument provided, it will use the static {@link Remote#getItem getItem}
    * method of the {@link Remote Remote} class to contact the server. It will
    * then invoke a null-argument getProxy on the resulting reference to
    * request the primary proxy object of the item. If the proxy is a Swing
    * JComponent, it will be displayed in a JFrame. If it is an AWT Component,
    * it will be displayed in a Frame.<br><br>
    * When using Client from the command line, it is possible to set the
    * Client frame explicitly. To do this, simply type:<br><br><tt>
    * java -cp cajo.jar -Dgnu.cajo.invoke.Client.title="My Frame Title"
    * gnu.cajo.invoke.Client //myHost:1198/test</tt><br><br>
    * <i><u>Note</u>:</i> When running as an application (<i><u>except</u> via
    * WebStart</i>) it will load a NoSecurityManager, therefore, if no external
    * SecurityManager is specified in the startup command line; the arriving
    * proxies will have <i><u><b>full permissions</b></u></i> on this machine!<br><br>
    * To restrict client proxies permissions, use a startup invocation
    * similar to the following:<br><br>
    * <tt>java -cp cajo.jar -Djava.security.manager -Djava.security.policy=client.policy
    * ... gnu.cajo.invoke.Client ... </tt><br><br>
    * See the project client <a href=https://cajo.dev.java.net/client.html>
    * documentation</a>, for more details.<br><br>
    * The startup can take up to five additional optional configuration
    * parameters, in this order:<ul>
    * <li><tt>args[0] - </tt>The URL where to get the graphical proxy item:<br>
    * file:// http:// ftp:// ..., //host:port/name (rmiregistry), /path/name
    * (serialized), or path/name (class).<br>
    * If <i>unspecified,</i> a graphical loader utility will be launched.
    * <li><tt>args[1] - </tt>The optional external client port number,
    * if using NAT.
    * <li><tt>args[2] - </tt>The optional external client host name,
    * if using NAT.
    * <li><tt>args[3] - </tt>The optional internal client port number,
    * if using NAT.
    * <li><tt>args[4] - </tt>The optional internal client host name,
    * if multi home/NIC.</ul>
    */
   public static void main(String args[]) throws Exception {
      if (System.getSecurityManager() == null)
         System.setSecurityManager(new NoSecurityManager());
      if (args.length > 0) {
         int clientPort    = args.length > 1 ? Integer.parseInt(args[1]) : 0;
         String clientHost = args.length > 2 ? args[2] : null;
         int localPort     = args.length > 3 ? Integer.parseInt(args[3]) : 0;
         String localHost  = args.length > 4 ? args[4] : "0.0.0.0";
         Remote.config(localHost, localPort, clientHost, clientPort);
         proxy = Remote.getItem(args[0]);
         proxy = Remote.invoke(proxy, "getProxy", null);
         if (proxy instanceof MarshalledObject)
            proxy = ((MarshalledObject)proxy).get();
         if (!(proxy instanceof RemoteInvoke))
            proxy = Remote.invoke(proxy, "init", new Remote(proxy));
         if (proxy instanceof Component) {
            String title = "cajo Proxy Viewer";
            try {
               title = System.getProperty("gnu.cajo.invoke.Client.title");
            } catch (Exception x) {} // won't work in WebStart
            proxy = frame((Component)proxy, title + " - " + args[0]);
         }
      } else new Loader();
   }
}
