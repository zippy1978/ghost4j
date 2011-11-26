package gnu.cajo.invoke;

/*
 * Full-Privilige Security Manager
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file NoSecurityManager.java is part of the cajo library.
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
 * This utility class creates a trivial SecurityManager for developing proxy
 * hosting clients.  It allows trusted clients and servers to operate
 * without restriction, and without the need for a security policy file.  It
 * effectively allows both clients and proxies <b>full permissions</b> on the
 * machine.  While this is very convenient for development purposes, this
 * clearly would be <u>very unwise to use in an untrusted environment</u>.  In
 * production, the user better impose his own security policy, and use the
 * following two interpreter switces:<p>
 * <b><code>-Djava.security.manager -Djava.security.policy=someURL</code></b><p>
 * This URL, or file, would contain the restrictions governing what both the
 * loaded proxy code, and the client code, are permitted to do. A minimal, but
 * functional policy file, would contain at least the following:<p><pre>
 * grant {
 *   permission java.net.SocketPermission "*:1024-", "accept";
 *   permission java.net.SocketPermission "*", "connect";
 * };</pre><p>This would allow the client, and its loaded code to open
 * server sockets on port 1024 and higher, and to connect to remote hosts
 * on any port, and <b>nothing else</b>.  It is slightly more permissive than
 * a standard applet sandbox, but still very safe for hosting machines.  Any
 * attempt to install a SecurityManager should enclose the operation in a
 * try/catch block, as the operation may be forbidden by the user via the
 * technique described above. The assignment would then result in the throwing
 * of a <b>SecurityException</b>. <p>If the server code is fully trusted,
 * a more flexible policy file could be used such as:<p><pre>
 * grant codeBase "file:${java.class.path}" {
 *    permission java.security.AllPermission;
 * };
 * grant {
 *   permission java.net.SocketPermission "*:1024-", "accept";
 *   permission java.net.SocketPermission "*", "connect";
 * };</pre><p>This will allow classes loaded from the local filesystem full
 * permissions, while only allowing downloaded code to make socket
 * connections in the manner of the first policy file.<p>
 * <i>Note:</i> either way, to allow proxies to run within this VM invites the
 * possibility of a <b>denial of service attack</b>, i.e. a proxy or, other
 * object, could consume all the VMs memory and compute cycles maliciously,
 * or even accidentially.  Therefore, it is recommended that proxy hosting
 * only be done on an expendible VM.
 */
public final class NoSecurityManager extends SecurityManager {
   /**
    * Nothing is performed in the constructor.  This class exists only to
    * short-circuit the permission checking mechanism of the Java runtime
    * by overriding the checkPermission method with a bodyless
    * implementation.
    */
   public NoSecurityManager() {}
   /**
    * In accordance with the SecurityManager paradigm, this method simply
    * returns, to indicate that a requested client operation is permitted.
    * Otherwise it could throw a SecurityException, but it never does.
    * This means, that without an explicitly specified policy file used
    * in the startup of an application using this security manager, <i>BOTH</i>
    * the client, <b>and its loaded proxies</b> have full permissions on this
    * machine.
    */
   public void checkPermission(java.security.Permission perm) {}
}
