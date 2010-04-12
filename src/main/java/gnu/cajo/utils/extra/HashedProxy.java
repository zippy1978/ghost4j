package gnu.cajo.utils.extra;

import java.io.*;

/*
 * Compressed Proxy Hasher
 * Copyright (c) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file HashedProxy.java is part of the cajo library.
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
 * This class is used to hash a ZippedProxy.  Primarily it is intended to
 * simply and quickly convert the serialized proxy representation in to, and
 * out of plaintext, for transmission over the network.  It should be noted
 * that the default hashing algorithm is <u>not</u> cryptographically strong.
 * <i>(The author lives in the United States and doesn't want to go to jail)</i>
 * It's purpose is to provide a simple example encryption, which could be
 * sufficient for many purposes. It can always be subclassed, to employ much
 * more sophisticated cryptography. ;-)
 * <p>The underlying paradigm is that rather than incurring the extensive
 * computational overhead of running encrypted streams between clients and
 * servers, it much more efficient to encrypt only those specific objects
 * containing sensitive information. It is even stronger than conventional
 * encrypted streams in that each object can employ different cryptography.
 * If the client and the server both have the derived class in their local
 * codebases; it is possible to employ 'pad' (i.e. <b>uncrackable</b>) cyphers.
 *
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public class HashedProxy extends gnu.cajo.utils.ZippedProxy {
   private static final long serialVersionUID = 0xABCDEF240305L;
   private void writeObject(ObjectOutputStream out) throws IOException {
      if (!hashed) {
         hashPayload();
         hashed = true;
      }
      out.defaultWriteObject();
   }
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      hashPayload();
   }
   /**
    * A flag to indicate if the payload has been hashed. To reduce overhead,
    * the payload is hashed only once per session.
    */
   protected transient boolean hashed;
   /**
    * A symetric hash algorithm, to convolve the array in to and out of plain
    * text. It is invoked once at the sending VM, then once at the receiving
    * VM.
    */
   protected void hashPayload() {
      for (int i = 0; i < payload.length; i++) {
         switch(payload[i] & 0x21) {
            case 0x00: payload[i] ^= 0x9A; break;
            case 0x01: payload[i] ^= 0x5C; break;
            case 0x20: payload[i] ^= 0xD6; break;
            default:   payload[i] ^= 0xC2;
         }
      }
   }
   /**
    * The constructor simply invokes the superclass ZippedProxy constructor.
    * @param proxy The client proxy object, to be hashed at the server, and
    * unhashed at the client.
    */
   public HashedProxy(Object proxy) { super(proxy); }
}
