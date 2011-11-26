package gnu.cajo.invoke;

/*
 * Generic Polymorphic Inter-VM Item Communication Interface
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file RemoteInvoke.java is part of the cajo library.
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
 * The Remote Component Communication Interface, and reason for this package.
 * An empty extension of the Invoke interface, it allows both local, and
 * remote objects, i.e. those from another VM, to be handled interchangably in
 * code, through their superclass interface Invoke. When a VM wishes to allow
 * remote access to an object, the local object would be passed to the
 * constructor of the {@link Remote Remote} class included in this package.
 * <p>The implementation is so trivial, it is included it here:<p>
 * <code>public interface RemoteInvoke extends Invoke, Remote {}</code>
 * <p><i>Note:</i> this interface is never implemented by classes directly,
 * rather, a client only uses this interface to test if an object is remote,
 * in cases where that would be of interest to the application.
 *<p> To test the locality of an object reference:<p>
 * <pre>
 * if (foo instanceof RemoteInvoke) { // the object reference is remote
 *    ...
 * } else { // the object reference is local
 *   ...
 * }
 * </pre>
 *
 * @version 1.0, 01-Nov-99
 * @author John Catherino  Initial release
 */

public interface RemoteInvoke extends Invoke, java.rmi.Remote {}
