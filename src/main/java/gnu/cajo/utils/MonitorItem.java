package gnu.cajo.utils;

import gnu.cajo.invoke.*;
import java.rmi.server.RemoteServer;
import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.server.ServerNotActiveException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

/*
 * Item Invocation Monitor
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file MonitorItem.java is part of the cajo library.
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
 * This class is used to instrument an object for invocation logging
 * purposes. It is intended as a replacement for standard RMI logging, in that
 * this logger is aware of the Invoke package methodology, and can decode it
 * properly.  Specifically, it will gather information about the calling
 * client, the method called, the inbound and outbound data. It will also
 * record the approximate time between client invocations, the time used to
 * service the invocation, and the approximate percentage of free memory
 * available at the completion of the operation.  Subclassing of MonitorItem
 * is allowed; primarily to create self-monitoring classes.
 * <p><i>Note:</i> monitoring an object can be expensive in runtime efficiency.
 * It is best used for debug and performance analysis, during development, or
 * in production, for objects that would not be called very frequently.
 *
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public class MonitorItem implements Invoke {
   private final OutputStream os;
   private long count, oldtime = System.currentTimeMillis();
   /**
    * This flag can be used to selectively enable and disable monitoring
    * on a class-wide level. By default it is set to false, when true, no
    * output to any logstream will take place.
    */
   public static boolean CLASSOFF;
   /**
    * This flag can be used to selectively enable and disable monitoring
    * on a instance-wide level. By default it is set to false, when true, no
    * output to the logstream will take place.
    */
   public boolean LOCALOFF;
   /**
    * The object being monitored. It is declared as public to allow the
    * reference of the MontorItem, and its wrapped object, from a single
    * instance of MonitorItem.
    */
   public final Object item;
   /**
    * This creates the monitor object, to instrument the target object's use.
    * The the logging information will be sent to System.out automatically.
    * @param item The object to receive the client invocation.
    */
   public MonitorItem(Object item) { this(item, System.out); }
   /**
    * This creates the monitor object, to instrument the target object's use.
    * The the logging information is passed to the OutputStream, where it can
    * be logged to a file, a socket, or simply sent to the console (System.out).
    * The logged data is in text format.
    * @param item The object to receive the client invocation.
    * @param os The OutputStream to send the formatted log information.
    */
   public MonitorItem(Object item, OutputStream os) {
      this.item = item;
      this.os = os instanceof PrintStream ? os : new PrintStream(os);
   }
   /**
    * This creates the monitor object, to instrument the target object's use.
    * The the logging information is passed to an ObjectOutputStream.
    * <i>Note:</i> this type of monitoring provides both the greatest detail,
    * and can be most easily manipulated programmatically. However, it is even
    * </i>more</i> expensive than text logging. The log file can become
    * <i>extremely</i> large, if the objects passed in or out are complex, or
    * if the object is called frequently. Therefore, it is <u>highly</u>
    * recommended to implement the ObjectOutputStream on top of a
    * GZipOutputStream. <i><u>Note</u>:</i> to preserve remote object
    * codebase annotation, all of the objects associated with the particular
    * method invocation log are stored, in order, in an Object array,
    * contained within a java.rmi.MarshalledObject.
    * @param item The object to receive the client invocation.
    * @param os The ObjectOutputStream to send input and result objects.
    */
   public MonitorItem(Object item, ObjectOutputStream os) {
      this.item = item;
      this.os = os;
   }
   /**
    * This method logs the incoming calls, passing the caller's data to the
    * internal item. It records the following information:<ul>
    * <li> The name of the item being called
    * <li> The host address of the caller (or localhost w/trace)
    * <li> The method the caller is invoking
    * <li> The data the caller is sending
    * <li> The data resulting from the invocation, or the Exception
    * <li> The number of times this method has been called
    * <li> The idle time between invocations, in milliseconds.
    * <li> The run time of the invocation time, in milliseconds
    * <li> The free memory percentage, following the invocation</ul>
    * If the write operation to the log file results in an exception, the
    * stack trace of will be printed to System.err.<p>
    * <i><u>Note</u>:</i> Logging may be activated and deactivated
    * administratively as needed on both an instance-wide basis via the field
    * LOCALOFF, and on a class-wide basis via the static field CLASSOFF.
    * @param method The internal object's public method being called.
    * @param  args The arguments to pass to the internal object's method.
    * @return The sychronous data, if any, resulting from the invocation.
    * @throws RemoteException For a network related failure.
    * @throws NoSuchMethodException If the method/agruments signature cannot
    * be matched to the internal object's public method interface.
    * @throws Exception If the internal object's method rejects the invocation.
    */
   public Object invoke(String method, Object args) throws Exception {
      if (CLASSOFF || LOCALOFF) return Remote.invoke(item, method, args);
      long time = System.currentTimeMillis();
      Object result = null;
      try { return result = Remote.invoke(item, method, args); }
      catch(Exception x) {
         result = x;
         throw x;
      } finally {
         int run = (int)(System.currentTimeMillis() - time);
         String clientHost = null;
         try { clientHost = RemoteServer.getClientHost(); }
         catch(ServerNotActiveException x) {
            StackTraceElement stes[] = x.getStackTrace();
            StringBuffer sb = new StringBuffer("localhost <trace>");
            for (int i = 4; i < stes.length; i++) {
               sb.append("\n     method = ").append(stes[i].getClassName());
               sb.append('.').append(stes[i].getMethodName());
               if (stes[i].getLineNumber() >= 0) { // debug info available
                  sb.append("\n     file   = ");
                  sb.append(stes[i].getFileName());
                  sb.append(" line ").append(stes[i].getLineNumber());
               }
            }
            clientHost = sb.toString();
         }
         Runtime rt = Runtime.getRuntime();
         int freeMemory =
            (int)((rt.freeMemory() * 100) / rt.totalMemory());
         ObjectOutputStream oos =
             os instanceof ObjectOutputStream ? (ObjectOutputStream) os : null;
         PrintStream ps = os instanceof PrintStream ? (PrintStream)  os : null;
         synchronized(os) {
            try {
               if (oos != null) {
                  oos.writeObject( new MarshalledObject(new Object[] {
                     clientHost, item.getClass().getName() + " hashcode " + item.hashCode(),
                     method, args, result, new Long(++count), new Long(time),
                     new Long(time - oldtime), new Integer(run),
                     new Integer(freeMemory)
                  }));
                  oos.flush(); // just for good measure...
               } else if (ps != null) {
                  ps.print("Caller host = ");
                  ps.print(clientHost);
                  ps.print("\nObject call = ");
                  ps.print(item.getClass().getName() + " hashcode " + item.hashCode());
                  ps.print("\nMethod call = ");
                  ps.print(method);
                  ps.print("\nMethod args = ");
                  if (args instanceof java.rmi.MarshalledObject)
                     args = ((java.rmi.MarshalledObject)args).get();
                  if (args instanceof Object[]) {
                     ps.print("array");
                     for (int i = 0; i < ((Object[])args).length; i++) {
                        ps.print("\n\t[");
                        ps.print(i);
                        ps.print("] = ");
                        ps.print(((Object[])args)[i] != null ?
                           ((Object[])args)[i].toString() : "null");
                     }
                  } else ps.print(args != null ? args.toString() : "none");
                  ps.print("\nResult data = ");
                  if (result instanceof java.rmi.MarshalledObject)
                     result = ((java.rmi.MarshalledObject)result).get();
                  if (result instanceof Exception) {
                     ((Exception)result).printStackTrace(ps);
                  } else if (result instanceof Object[]) {
                     ps.print("array");
                     for (int i = 0; i < ((Object[])result).length; i++) {
                        ps.print("\n\t[");
                        ps.print(i);
                        ps.print("] = ");
                        if (((Object[])result)[i] != null)
                           ps.print(((Object[])result)[i].toString());
                        else ps.print("null");
                     }
                     ps.println();
                  } else ps.println(result != null ? result.toString() : "void");
                  ps.print("Call count  = ");
                  ps.print(++count);
                  ps.print("\nTime stamp  = ");
                  ps.print(time);
                  ps.print("\nIdle time   = ");
                  ps.print(time - oldtime);
                  ps.print(" ms");
                  ps.print("\nBusy time   = ");
                  ps.print(run);
                  ps.print(" ms");
                  ps.print("\nFree memory = ");
                  ps.print(freeMemory);
                  ps.print("%\n\n");
               }
            } catch(Exception x) { x.printStackTrace(); }
            oldtime = time;
         }
      }
   }
}
