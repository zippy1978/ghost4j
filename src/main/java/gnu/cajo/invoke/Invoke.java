package gnu.cajo.invoke;

/*
 * Generic Polymorphic Item Communication Interface
 * Copyright (c) 1999 John Catherino
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Invoke.java is part of the cajo library.
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
 * The generic inter-component communication interface, and foundation for
 * this paradigm. This provides a standard communication interface between
 * objects.  It is used to pass arguments to, and recieve synchronous
 * responses from, the receiving object.
 * <p>The implementation is so <i>extrmely simple</i>, it's included here:
 * <p><pre>
 * public interface Invoke extends Serializable {
 *   Object invoke(String method, Object args[]) throws Exception;
 * }</pre><p>
 *
 * @version 1.0, 01-Nov-99  Initial release
 * @author John Catherino
 */
public interface Invoke extends java.io.Serializable {
   /**
    * Used by other objects to pass data into this object, and receive
    * synchronous data responses from it, if any. The invocation may, or may
    * not contain inbound data.  It may, or may not, return a data object
    * response.  The functionality of this method is completely application
    * specific. This interface serves only to define the format of
    * communication between items.<p>
    * <i>Note:</i> this method could be called reentrantly, by many objects,
    * simultaneously.  If this would cause a problem, the critical sections
    * of this item's method must be synchronized. In general, synchronising
    * the whole method is <i>strongly</i> discouraged, as it could block
    * multiple clients far too generally.<p>
    * @param  method The name of the method to be invoked on the object,
    * except in extremely special circumstances, it should <i>not</i> be null.
    * @param  args The data relevant to the invocation. It can be a single
    * object, an array or objects, or simply null.
    * @return Any synchronous data defined by a subclass' implementation,
    * it can be an array of of objects, or possibly null
    * @throws Exception As needed by the application. <i>Note:</i> subclasses
    * of Exception can be thrown, to allow clients the opportunity to catch
    * only specific types, these exceptions could also contain application
    * specific methods, and fields, to supplement the error information.
    */
   Object invoke(String method, Object args) throws Exception;
}
