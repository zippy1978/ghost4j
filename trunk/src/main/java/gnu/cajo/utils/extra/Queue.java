package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Invoke;
import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;
import gnu.cajo.utils.MonitorItem;
import gnu.cajo.utils.Multicast;

import java.rmi.RemoteException;
import java.util.LinkedList;

/*
 * cajo asynchronous object method invocation queue
 * Copyright (C) 2006 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Queue.java is part of the cajo library.
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
 * This class is a cajo-based implementation of the message communication
 * paradigm. One or more producer objects can invoke methods on this
 * object, and the invocations will be asynchronously performed on one 
 * <i>(point to point),</i> or more <i>(publish/subscribe),</i> consumer
 * objects.  A producer, by virtue of having its method invocation return
 * immediately and successfully, can be certain its invocation has been
 * enqueued, and will be invoked on the member consumers <i>(if any).</i> The
 * consumers can be either local or remote objects.<p>
 * A producer enqueues its invocations for the member consumers, by invoking the
 * corresponding method on its reference to an instance of this object. Its
 * argument(s), if any, will be invoked on the matching method of each of the
 * consumer objects, in a separate thread. This creates a dynamic buffer of
 * invocations.<p>
 * Due to the asynchronous disconnection between the producer and consumer(s),
 * no results can be returned from the method invocations. If a producer
 * wishes to receive data from each of the consumers, it could provide a
 * callback reference as one of the arguments, for example.<p>
 * Interactions between producers and consumers can be as simple as a single
 * method taking no arguments - <i>notification,</i> to a method taking a
 * single
 * argument - <i>messaging,</i> to multiple methods taking multiple arguments -
 * <i>remote procedure calls.</i><p>
 * Finally, this class is serialisable, to allow passing of Queue objects
 * between machines, and replication of Queue objects, the possibilities this
 * creates require a bit of thought indeed...
 *
 * @version 1.0, 25-Jun-06
 * @author John Catherino
 */
public class Queue implements Invoke {
   private static final long serialVersionUID = 1L;
   /**
    * Some manner of commonly agreed upon descriptor for the subject matter
    * about which the producers and consumers are interested. It must be
    * serialisable.
    */
   protected final Object topic;
   /**
    * The list of all pending producer method invocations.
    */
   protected LinkedList invocations = new LinkedList();
   /**
    * The list of consumers, remote and local, to receive producer invocations.
    */
   protected LinkedList consumers = new LinkedList();
   /**
    * This is the thread performing the asynchronous invocation operation,
    * invoking the corresponding method on consumer objects. It is
    * instantiated dynamically, upon the first producer invocation.
    */
   protected transient Thread thread;
   /**
    * The constructor simply assigns the topic for the object and returns, as
    * the object is entirely event driven. <i><u>Note</u>:</i> the descriptor
    * object <i>must</i> be serialisable.
    * @param topic A descriptor object, mutually agreed upon by all participants
    */
   public Queue(Object topic) { this.topic = topic; }
   /**
    * This method is used to request the topic of the producer/consumer
    * community. <i><u>Note</u>:</i> the object returned may be unknown to
    * caller, thus resulting in a NoClassDefFoundError, in that case, it is
    * probably not a good idea to join the community as either a producer or
    * consumer.
    * @return The community descriptor
    */
   public Object topic() { return topic; }
   /**
    * This method is used to add an object to list of consumers awaiting
    * producer invocation. 
    * @param consumer The object wishing to subscribe
    */
   public synchronized void enqueue(Object consumer) {
      consumers.add(consumer);
   }
   /**
    * This method is used to remove a consumer from the list.
    * @param consumer The object wishing to unsubscribe
    */
   public synchronized void dequeue(Object consumer) {
      consumers.remove(consumer);
   }
   /**
    * This method is called to suspend method invocation dispatching. Producer
    * invocations will continue to queue. This method is iddmpotent. It can
    * only be invoked locally.
    */
   public synchronized void pause() {
      if (thread != null && !thread.isInterrupted()) thread.interrupt();
   }
   /**
    * This method is called to resume method invocation dispatching. This
    * method is idempotent. It can only be invoked locally.
    */
   public synchronized void resume() {
      if (thread != null && thread.isInterrupted()) thread = null;
   }
   /**
    * This is the method a producer, local or remote, would invoke, to be
    * performed in a message-based fashion, asynchronously, on all registered
    * consumers. This method returns immediately, implicitly guaranteeing the
    * invocation has been successfully enqueued.<p>
    * <i><u>Note</u>:</i> normally invocation of the methods topic, enque, and
    * deque will <i>not</i> be passed along to consumers, as they are performed
    * on the Queue instance. However, invoking topic with any arguments, and
    * enqueue or dequeue with no arguments, these <i>will</i> be passed on to
    * consumers, as they do not apply to the Queue instance.
    * @param method The public method name on the consumer objects to be
    * invoked
    * @param args The argument(s) to invoke on the consumer object's method,
    * there can be none, to simply perform notification, there can be a single
    * argument to provide data, or there can be a collection to engage a
    * behaviour, presumably, the subscribed object has a matching public method
    * signature.
    * @return null Since consumer invocation  is performed asynchronously, there
    * can be no synchronous return data, a callback object reference can be
    * provided as an argument, if result data is required

    * @throws java.rmi.RemoteException If the invocation failed to enqueue,
    * due to a network related error
    */
   public synchronized Object invoke(String method, Object args) {
      if (method.equals("enqueue") && args != null) {
         enqueue(args);
         return null;
      } else if (method.equals("dequeue") && args != null) {
         dequeue(args);
         return null;
      } else if (method.equals("topic") && args == null) return topic();
      if (thread == null) {
         thread = new Thread(new Runnable() {
            public void run() {
               try {
                  do {
                     String method;
                     Object args, consumers[];
                     synchronized(Queue.this) {
                        while (invocations.size() == 0) Queue.this.wait();
                        method = (String)invocations.removeFirst();
                        args = invocations.removeFirst();
                        if (Queue.this.consumers.isEmpty()) continue;
                        consumers = Queue.this.consumers.toArray();
                     }
                     for (int i = 0; i < consumers.length; i++) try {
                        Remote.invoke(consumers[i], method, args);
                     } catch(RemoteException x) { dequeue(consumers[i]); }
                     catch(Exception x) {}
                     catch(Throwable t) {}
                  } while(!thread.isInterrupted());
               } catch(InterruptedException x) {}
            }
         });
         thread.start();
      }
      invocations.add(method);
      invocations.add(args);
      notify();
      return null;
   }
   /**
    * This method will start up a remotely accessible Queue object, in its own
    * JVM. For illustrative purposes, the queue object will be wrapped in a
    * MonitorItem, which outputs to the console. It will bind locally under
    * its topic name, and announce itself over multicast, using the cajo IANA
    * UDP address, on port 1198.
    * @param args The first string, if defined, will be the topic string,
    * undefined it will be "void", the second, if defined, will be the TCP port
    * number on which the queue will accept invocations, undefined it will be
    * 1198, it can be zero, to use an anonymous port
    * @throws Exception For startup or machine/network configuration issues
    */
   public static void main(String args[]) throws Exception {
      String topic = args.length > 0 ? args[0] : "void";
      int port = args.length > 1 ? Integer.parseInt(args[1]) : 1198;
      Remote.config(null, port, null, port);
      Object queue = new Remote(new MonitorItem(new Queue(topic)));
      System.out.println("Queue started\n");
      ItemServer.bind(queue, topic);
      new Multicast("224.0.23.162", 1198).announce(queue, 32);
   }
}
