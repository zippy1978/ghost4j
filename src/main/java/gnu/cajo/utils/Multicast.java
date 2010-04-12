package gnu.cajo.utils;

import gnu.cajo.invoke.*;
import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.rmi.MarshalledObject;

/*
 * Multicast Announcement Class
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Multicast.java is part of the cajo library.
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
 * This class can listen for UDP multicasts over the network, as well
 * as to send out UDP announcements.  The mechanism is <i>rigged</i> to send a
 * reference to a remote object as a zipped MarshalledObject (zedmob).  It also
 * allows a listening object to receive announced object referencess via a
 * callback mechanism. A single VM can use as many Multicast objects as it
 * wishes.
 * <p><i>Note:</i> this class requires that the network routers be configured
 * to pass IP multicast packets, at least for the multicast address used.  If
 * not, the packets will will only exist within the subnet of origination.
 *
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public final class Multicast implements Runnable {
   private Object callback;
   private Thread thread;
   /**
    * The network interface on which this multicast object is listening
    */
   public final InetAddress host;
   /**
    * A reference to the address on which this object is operating. It is
    * referenced by the called listener, and is valid for the duration of the
    * object's existence.
    */
   public final String address;
   /**
    * A reference to the port on which this object is operating. It is
    * referenced by the called listener, and is valid for the duration of the
    * object's existence.
    */
   public final int port;
   /**
    * A reference to the address of the calling VM, when the object is
    * listening. It is referenced by the called listener, and should be
    * considered valid for the duration of the invocation only.
    */
   public InetAddress iaddr;
   /**
    * A reference to a received remote object reference, when the object is
    * listening.  It is referenced by the called listener, and should be
    * considered valid for the duration of the invocation only.
    */
   public RemoteInvoke item;
   /**
    * The default constructor sets the internal fields to default values which
    * should be sufficient for most purposes. The multicast socket address
    * will be set to 244.0.23.162, which is officially registered with IANA
    * for cajo object reference announcements.  The UDP port number on which
    * this object will announce and listen is set to 1198, which is is also
    * assigned by the IANA, for cajo object reference acquisition.  The object
    * will listen on the same network interface being used for the server's RMI
    * communication. It sends and listens on the machine's default network
    * interface
    * @throws java.net.UnknownHostException If the default network interface
    * could not be resolved, <i>not very likely</i>.
    */
   public Multicast() throws UnknownHostException {
      this(null, "224.0.23.162", 1198);
   }
   /**
    * This constructor allows creation of Multicast objects on any
    * appropriate multicast address, and port number. It uses the default
    * network interface.
    * being used for the server's RMI communication.
    * @param address The multicast socket domain name, or address, on which
    * this object will listen.  It can be any address in the range 224.0.0.1
    * through 239.255.255.255.
    * @param port The UDP port number on which this object will announce and
    * listen, its value can be 0 - 65535. It is completely independent of all
    * TCP port numbers. Application specific meaning could be assigned to port
    * numbers, to identify broadcast types.
    * @throws java.net.UnknownHostException If the specified host address
    * could not be resolved, or is invalid.
    */
   public Multicast(String address, int port) throws UnknownHostException {
      this(null, address, port);
   }
   /**
    * The full constructor allows creation of Multicast objects on any
    * appropriate address, and port number. It uses the same network interface
    * being used for the server's RMI communication.
    * @param host The network interface on which to send or receive multicasts,
    * specified when a machine has more than one, otherwise use "0.0.0.0" to
    * send and receive on all of them
    * @param address The multicast socket domain name, or address, on which
    * this object will listen.  It can be any address in the range 224.0.0.1
    * through 239.255.255.255.
    * @param port The UDP port number on which this object will announce and
    * listen, its value can be 0 - 65535. It is completely independent of all
    * TCP port numbers. Application specific meaning could be assigned to port
    * numbers, to identify broadcast types.
    * @throws java.net.UnknownHostException If the specified host address
    * could not be resolved, or is invalid.
    */
   public Multicast(String host, String address, int port)
      throws UnknownHostException {
      if (host == null) host = InetAddress.getLocalHost().getHostAddress();
      this.host = InetAddress.getByName(host);
      this.address = address;
      this.port = port;
   }
   /**
    * This method is used to make UDP announcements on the network. The
    * provided object will first have its startThread method invoked with a
    * null argument, to signal it to start its main processing thread (if it
    * has one). Next it will have its setProxy method invoked remote reference
    * to itself, with which it can share with remote VMs, in an application
    * specific manner (again if it has one).
    * @param item The object reference to be sent in the announcement
    * packet, if it is not already remoted, it will be, automatically.
    * @param ttl The time-to-live of the broadcast packet. This roughly
    * specifies how many multicast enabled routers will pass this packet before
    * automatically discarding it. For example 16, should cover a medium sized
    * LAN. The maximum value is 255, which could theoretically cover the globe,
    * that is, in 1999. A value of 1 confines the packet to its immediate
    * subnet.
    * @throws IOException If a datagram socket could not be created, or the
    * packet could not be sent.
    */
   public void announce(Object item, int ttl) throws IOException {
      InetAddress group = InetAddress.getByName(address);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (!(item instanceof Remote)) item = new Remote(item);
      try {
         Remote.invoke(item, "startThread", null);
         Remote.invoke(item, "setProxy", new MarshalledObject(item));
      } catch(Exception x) {}
      ((Remote)item).zedmob(baos);
      byte packet[] = baos.toByteArray();
      baos.close();
      MulticastSocket ms = new MulticastSocket();
      try {
         ms.setInterface(host);
         ms.setTimeToLive(ttl);
         ms.send(new DatagramPacket(packet, packet.length, group, port));
      } finally { ms.close(); }
   }
   /**
    * This method is used to make UDP announcements on the network. The
    * provided object will first have its startThread method invoked with a
    * null argument, to signal it to start its main processing thread (if it
    * has one). If the proxy has a setItem method, it will be called with a
    * remote reference to the serving object. If the item implements a
    * setProxy method it will be called with a MarshalledObject containing
    * the proxy object.
    * @param item The object reference to be sent in the announcement
    * packet, if it is not already remoted, it will be, automatically.
    * @param ttl The time-to-live of the broadcast packet. This roughly
    * specifies how many multicast enabled routers will pass this packet before
    * automatically discarding it. For example 16, should cover a medium sized
    * LAN. The maximum value is 255, which could theoretically cover the globe,
    * that is, in 1999. A value of 1 confines the packet to its immediate
    * subnet.
    * @param proxy The proxy object to be sent to requesting clients.
    * @throws IOException If a datagram socket could not be created, or the
    * packet could not be sent.
    */
   public void announce(Object item, int ttl, Object proxy)
      throws IOException {
      InetAddress group = InetAddress.getByName(address);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (!(item instanceof Remote)) item = new Remote(item);
      try { Remote.invoke(proxy, "setItem", item); }
      catch(Exception x) {}
      try {
         Remote.invoke(item, "startThread", null);
         Remote.invoke(item, "setProxy", new MarshalledObject(proxy));
      } catch(Exception x) {}
      ((Remote)item).zedmob(baos);
      byte packet[] = baos.toByteArray();
      baos.close();
      MulticastSocket ms = new MulticastSocket();
      try {
         ms.setInterface(host);
         ms.setTimeToLive(ttl);
         ms.send(new DatagramPacket(packet, packet.length, group, port));
      } finally { ms.close(); }
   }
   /**
    * This method starts a thead to listen on the construction {@link #address
    * address} and {@link #port port}. The listening object will be called on
    * its public multicast method, with a reference to the calling Multicast
    * object.  This is to allow the possibility for a single listener, to
    * monitor multiple multicast objects. If a listener is used to monitor
    * multiple multicast objects, it may be invoked reentrantly, otherwise it
    * cannot. Listening will continue until the callback object's multicast
    * method retruns a non-null value.  If it does, this method would havt to
    * be called again to restart listening.
    * @param callback An object, presumably local to this VM, which is to
    * receive notifications about announcements.
    * @throws IllegalArgumentException If the object is actively listening, at
    * the time of the invocation.
    */
   public void listen(Object callback) {
      if (thread == null) {
         this.callback = callback;
         thread = new Thread(this);
         thread.setDaemon(true);
         thread.start();
      } else throw new IllegalArgumentException("Already listening");
   }
   /**
    * The monitor thread, it listens for multicasts.  It will sleep until
    * the arrival of a message.  The packet will be reconstituted into a
    * remote object reference, from its zedmob encapsulation.  The object
    * reference will be saved into the public item member variable, also the
    * calling VM's address will be extracted into the public address member
    * variable. The listener's multicast method will be called next with a
    * reference to this object. The multicast reference is used to access its
    * public member variables; the remote announcer's reference and IP address,
    * as well as the multicast address and port on which it was received.  The
    * second two members are of interest in the case where the same object is
    * listening on multiple multicast objects. If the method returns null, the
    * multicast listening will continue, otherwise it will be stopped. Once
    * stopped it can be restarted by the application as necessary, by invoking
    * the {@link #listen listen} method again.
    */
   public void run() {
      try {
         MulticastSocket ms = new MulticastSocket(port);
         ms.setInterface(host);
         ms.joinGroup(InetAddress.getByName(address));
         DatagramPacket dp = new DatagramPacket(new byte[0xFF00], 0xFF00);
         while(!thread.isInterrupted()) try {
            ms.receive(dp);
            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
            try {
               item = (RemoteInvoke)Remote.zedmob(bais);
               iaddr = dp.getAddress();
               Object quit = Remote.invoke(callback, "multicast", this);
               item = null;
               if (quit != null) break;
            } catch(Exception x) { x.printStackTrace(); }
            finally { bais.close(); }
         } catch(Exception x) { x.printStackTrace(); }
         ms.close();
         ms = null;
         thread = null;
      } catch(IOException x) { x.printStackTrace(); }
   }
   /**
    * The application method loads a zipped marshalled object (zedmob) to a
    * proxy from a URL, or a file, and allows it run in this virtual machine.
    * It will load an RMISecurityManager to protect the hosting
    * machine from potentially or accidentally dangerous proxies, if not
    * prohibited by the user at startup. It uses the {@link
    * gnu.cajo.invoke.Remote#getItem getitem} method of the {@link
    * gnu.cajo.invoke.Remote Remote} class to load the object. Following loading,
    * it will also create an rmiregistry, and bind a remote reference to it
    * under the name "main". This can allow remote clients to connect to, and
    * interact with, the object. It will announce its startup on a default
    * Multicast object, and then begin listening on it for further
    * announcements, which will be  passed to the loaded proxy object. It can
    * be configured using the following arguments, all arguments subsequent to
    * the ones specified in the command line can be omitted:<br><ul>
    * <li> args[0] The optional URL where to get the object: file:// http://
    * ftp:// ..., /path/name <serialized>, path/name <class>, or alternatively;
    * //[host][:port]/[name], where the object will be requested from a remote
    * rmiregistry and the returned reference cast to the Lookup interface and
    * invoked with a null reference, to return its proxy object.  If no
    * arguments are provided, the URL will be assumed to be
    * //localhost:1099/main.
    * <li> args[1] The optional external client host name, if using NAT.
    * <li> args[2] The optional external client port number, if using NAT.
    * <li> args[3] The optional internal client host name, if multi home/NIC.
    * <li> args[4] The optional internal client port number, if using NAT.
    * <li> args[5] The optional URL where to get a proxy object: file://
    * http:// ftp:// ..., //host:port/name (rmiregistry), /path/name
    * (serialized), or path/name (class).  It will be passed into the loaded
    * object as the sole argument to a setItem method.
    * </ul>
    */
   public static void main(String args[]) throws Exception {
      if (args.length == 0) args = new String[] { "///main" };
      String clientHost = args.length > 1 ? args[1] : null;
      int clientPort    = args.length > 2 ? Integer.parseInt(args[2]) : 0;
      String localHost  = args.length > 3 ? args[3] : null;
      int localPort     = args.length > 4 ? Integer.parseInt(args[4]) : 0;
      Remote.config(localHost, localPort, clientHost, clientPort);
      ItemServer.acceptProxies();
      Remote item = new Remote(Remote.getItem(args[0]));
      if (args.length > 5) Remote.invoke(item, "setProxy", Remote.getItem(args[5]));
      Multicast m = new Multicast();
      m.announce(ItemServer.bind(item, "main"), 16);
      m.listen(item);
   }
}
