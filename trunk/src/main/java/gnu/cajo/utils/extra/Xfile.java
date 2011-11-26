package gnu.cajo.utils.extra;

import java.io.IOException;
import gnu.cajo.invoke.Remote;

/*
 * File transfer utility
 * Copyright (C) 2006 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Xfile.java is part of the cajo library.
 *
 * The cajo library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public Licence as published
 * by the Free Software Foundation, at version 3 of the licence, or (at your
 * option) any later version.
 *
 * Th cajo library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence
 * along with this library. If not, see http://www.gnu.org/licenses/lgpl.html
 */

/**
 * This class is used to transfer files between Java Virtual Machines.
 * This class normally resides in the codebase of <i>both</i> the client,
 * and the server. A client invokes only the static fetch method. This will
 * implicitly use the open and nextBlock methods on the server Xfile object,
 * to complete the transfer. A server simply instantiates, and remotes an
 * Xfile object, to enable file transfers. For threadsafety; a server should
 * give each client a <i>unique</i> instance of this class.<p>
 * <i><u>Note</u>:</i> this object will try to send any file the client
 * requests, therefore it should either be used by trusted clients, or with
 * access priviliges appropriately locked down. Another option is to override
 * the open method in a subclass, to check the requested resource against an
 * approved list, throwing an exception if not.
 *
 * @version 1.0, 25-Jan-06 Initial release
 * @author John Catherino
 */
public class Xfile {
   private final byte stub[] = new byte[1];
   private final int maxBlock;
   private java.io.InputStream is;
   private byte block[];
   /**
    * This field can allow the static fetch method to be invoked by a remote
    * client. By default its value is false, prohibiting this functionality.
    * Enabling this opens the possibility for a client to direct a server
    * object to transfer a file into itself, from yet some other remote
    * location, or even to transfer the data to yet another remote location.
    * This is a heady concept; an Xfile object can operate simultaneously as
    * a client, and a server; the implications are worthy of contemplation,
    * before enabling.
    */
   public static boolean remoteInvoke;
   /**
    * The constructor simply sets the maximum transfer block size.
    * @param maxBlock The largest possible block to return from the
    * nextBlock method. It is suggested not to exceed 64k bytes.
    * @throws IllegalArgumentException if the max block argument is
    * less than 256 bytes, or greater than 1MB.
    */
   public Xfile(int maxBlock) {
      if (maxBlock < 256 || maxBlock > 0x100000) throw
         new IllegalArgumentException("invalid max block size");
      this.maxBlock = maxBlock;
   }
   /**
    * This method is called by the client's static fetch method, to open a
    * resource on a remote Java Virtual Machine. Only one resource may be
    * open at a time. If a resource is currently open when this method is
    * called, the currently open resource stream will then be closed
    * silently. If the server is deployed in a jar file, it will check within
    * its jar first, to try to find the resource, before looking outside.
    * @param source The resource to be loaded, typically the path and name
    * of the file on the server's local filesystem.
    * @throws IOException If the specified resource does not exist, or
    * cannot be opened.
    */
   public void open(String source) throws IOException {
      if (is != null) try { is.close(); }
      catch(IOException x) {}
      is = getClass().getResourceAsStream(source);
      if (is == null) is = source.indexOf(':') == -1 ?
         new java.io.FileInputStream(source) :
         new java.net.URL(source).openStream();
   }
   /**
    * This method is called by the client's static fetch method, to request
    * subsequent blocks of the file, until the transfer is complete. It will
    * transfer the number of bytes available from the stream, at the time of
    * invocation, up to the maximum block size, specified in its constructor.
    * It will close the source stream automatically, with the request of the
    * final data block.
    * @return The next variably sized block from the source stream.
    * @throws IOException If a stream is not currently open, or if a read,
    * or close error occurred.
    */
   public byte[] nextBlock() throws IOException {
      if (is == null) throw new IOException("No file currently open");
      int size = is.available();
      if (block == null && maxBlock < size) block = new byte[maxBlock];
      byte buff[] = size == 0 ? stub : // theoretically necessary
         size < maxBlock ? new byte[size] : block;
      if (is.read(buff, 0, buff.length) != -1) return buff;
      else try { // transfer complete, close stream
         is.close();
         return null;
      } finally { is = null; }
   }
   /**
    * This is the only method used by clients, it fetches the file from the
    * specified source, and saves it to the specified destination. It will
    * first invoke the open method on the server item reference, it will
    * then call nextBlock repeatedly, storing the results, until the entire
    * file has been transferred. This method is synchronized, to provide
    * threadsafety on the client side.<p>
    * As a special case; the source and/or destination arguments can be URLs.
    * <p>For example:<ul>
    * <li>file://path/name.ext
    * <li>http://host/path/name.ext
    * <li>ftp://<i>[username:password@]</i>host/path/name.ext</ul><br>
    * however, only as permitted by the SecurityPolicy of the client and
    * server JVMs, and supported by their installed protocol handlers.
    * @param item The object, presumably a remote reference to an Xfile
    * object, with which to fetch the resource.
    * @param source The file resource, typically the path and name of the
    * file in the server's local filesystem, to be transferred.
    * @param dest The resource, typically the path and name of the file
    * in the clients's local filesystem, in which to transfer the data.
    * If the destination is local, and the path does not exist, it will
    * attempt to create it automatically.
    * @throws IOException If the source is invalid, the destination
    * is invalid, or either could not be opened. If the destination
    * already exists, it will be overwritten.
    * @throws java.rmi.RemoteException If a remote method invocation,
    * involved in the data transfer, fails.
    * @throws IllegalAccessException If this method is being invoked by a
    * remote JVM, and remote invocation has not been enabled.
    * @throws Exception By the remote JVM, if the operation is not
    * supported.
    */
   public static synchronized void fetch(
      Object item, String source, String dest) throws Exception {
      if (!remoteInvoke) try {
         java.rmi.server.RemoteServer.getClientHost();
         throw new IllegalAccessException("remote fetch disabled");
      } catch(java.rmi.server.ServerNotActiveException x) {}
      java.io.OutputStream os;
      if (dest.indexOf(':') == -1) {
         java.io.File f = new java.io.File(dest);
         if (f.getParent() != null) {
            java.io.File p = new java.io.File(f.getParent());
            if (!p.exists()) p.mkdirs();
         }
         os = new java.io.FileOutputStream(f);
      } else os = new java.net.URL(dest).openConnection().getOutputStream();
      try {
         Remote.invoke(item, "open", source);
         os = new java.io.BufferedOutputStream(os);
         for (byte[] msg = (byte[])Remote.invoke(item, "nextBlock", null);
            msg!= null; msg = (byte[])Remote.invoke(item, "nextBlock", null))
               os.write(msg, 0, msg.length);
      } finally {
         os.flush();
         os.close();
      }
   }
}
