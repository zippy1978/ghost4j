package gnu.cajo.utils.extra;

import gnu.cajo.invoke.*;
import java.io.*;

/*
 * Object Serialisation Compressor/Decompressor
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 * Copyright (c) 2005 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Zedmobject.java is part of the cajo library.
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
 * This class is used to transfer an object between Virtual Machines as a
 * zipped marshalled object (zedmob). It will compress the object
 * automatically on serialisation, and decompress it automatically upon
 * deserialisation. This will incur a small runtime penalty, however, if
 * the object is large and highly compressable, or the data link is slow,
 * or the cost per byte to transmit data is high, this can become highly
 * advantageous.
 *
 * @version 1.0, 16-Mar-05 Initial release
 * @author John Catherino
 */
public final class Zedmobject implements Invoke {
   private static final long serialVersionUID = 0x369121518L;
   private byte payload[]; // compressed object transport buffer
   private void writeObject(ObjectOutputStream out) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Remote.zedmob(baos, object);
      payload = baos.toByteArray();
      out.defaultWriteObject();
   }
   private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
      in.defaultReadObject();
      ByteArrayInputStream bais = new ByteArrayInputStream(payload);
      object = Remote.zedmob(bais);
      payload = null;
   }
   /**
    * The wrapped object. Normally it would be accessed through the wrapper's
    * Invoke interface. However, direct access is provided for cases where
    * the wrapped object is purely data, for example a byte[]. It is declared
    * transient since it never travels over the network in its uncompressed
    * form.
    */
   public transient Object object;
   /**
    * The constructor simply assigns the reference to the member object
    * referece. The only restriction, quite naturally, is that the object
    * argument <i>must</i> be serialisable.
    * @param object The object to be de/compressed receipt/transmission.
    */
   public Zedmobject(Object object) { this.object = object; }
   /**
    * The invocation interface to the internal object. It is used to invoke
    * any of the object's public methods. It uses the Remote.invoke paradigm
    * internally, to make the presence of the zedmobject transparent.
    * @param method The name of the public method to invoke.
    * @param args The data, to be provided to the method as arguments, if any.
    * @throws Exception For any method-specific reasons.
    */
   public Object invoke(String method, Object args) throws Exception {
      return Remote.invoke(object, method, args);
   }
}
