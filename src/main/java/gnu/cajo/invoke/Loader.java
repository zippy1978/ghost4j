package gnu.cajo.invoke;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.rmi.MarshalledObject;

/*
 * Generic Graphical Proxy Loader Dialog Box
 * Copyright (C) 2006 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Loader.java is part of the cajo library.
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
 * This package-internal helper monkey supports the operation of the generic
 * Client. When the client is launched as an application, with no arguments,
 * this graphical widget will be created. It allows the Java Virtual Machine
 * to host multiple proxy items, from remote servers. The user gets a small
 * AWT dialog box; into which to type the host, port, and item name. It will
 * take care of loading the proxy, and displaying its GUI, if it has one. If
 * the graphical proxy component implements WindowListener, it will be added
 * as a listener to its display frame automatically, before it is made
 * visible. Note: closing the dialog will intentionally terminate the JVM,
 * and consequently all of its loaded proxies. If the returned proxy is an
 * AWT component, it will be displayed in a double buffered frame, if it
 * is a JComponent, its setDoubleBuffered(true) will be called, before it
 * is displayed.
 * 
 *
 * @version 1.0, 02-Jan-06 Initial release
 * @author John Catherino
 */
final class Loader extends Frame implements WindowListener, ActionListener {
   private static final long serialVersionUID = 1L;
   private static final String TITLE = "cajo Proxy Viewer - ";
   private static Button load;
   private static TextField host, port, item, status;
   private final LinkedList proxies = new LinkedList();
   private boolean main;
   private Graphics gbuffer;
   private Image ibuffer;
   private Loader(String title) { super(title); }
   Loader() {
      super("Load cajo proxy");
      addWindowListener(this);
      main = true;
      setLayout(null);
      final int WIDTH = 250, HEIGHT = 200, ROW1 = HEIGHT / 6,
         ROW2 = 2 * ROW1, ROW3 = 3 * ROW1, ROW4 = 4 * ROW1;
      setSize(WIDTH, HEIGHT);
      Label label;
      add(label = new Label("Host:"));
      label.setBounds(WIDTH / 12, ROW1, 35, 20);
      add(host = new TextField());
      host.setBounds(WIDTH / 4, ROW1, 2 * WIDTH / 3, 20);
      add(label = new Label("Port:"));
      label.setBounds(WIDTH / 12, ROW2, 35, 20);
      add(port = new TextField("1198"));
      port.setBounds(7 * WIDTH / 12, ROW2, WIDTH / 3, 20);
      add(label = new Label("Item:"));
      label.setBounds(WIDTH / 12, ROW3, 35, 20);
      add(item = new TextField("main"));
      item.setBounds(WIDTH / 4, ROW3, 2 * WIDTH / 3, 20);
      add(load  = new Button("Load"));
      load.setBounds(WIDTH / 3, ROW4, WIDTH / 3, 25);
      load.addActionListener(this);
      add(status = new TextField("ready to load proxy"));
      status.setBounds(1, HEIGHT - 21, WIDTH - 2, 20);
      status.setEditable(false);
      status.setBackground(getBackground());
      setResizable(false);
      setVisible(true);
   }
   public void actionPerformed(ActionEvent e) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try {
         String url = "//" + host.getText().trim() + ':' +
            port.getText().trim() + '/'  + item.getText().trim();
         status.setText("requesting proxy " + url);
         Object proxy = Remote.getItem(url);
         proxy = Remote.invoke(proxy, "getProxy", null);
         if (proxy instanceof MarshalledObject)
            proxy = ((MarshalledObject)proxy).get();
         if (!(proxy instanceof RemoteInvoke))
            proxy = Remote.invoke(proxy, "init", new Remote(proxy));
         url = "cajo proxy - " + url;
         if (proxy instanceof JComponent) {
            JFrame frame = new JFrame(TITLE + url);
            if (proxy instanceof WindowListener)
               frame.addWindowListener((WindowListener)proxy);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add((JComponent)proxy);
            frame.pack();
            frame.setVisible(true);
         } else if (proxy instanceof Component) {
            Loader frame = new Loader(TITLE + url);
            frame.add((Component)proxy);
            if (proxy instanceof WindowListener)
               frame.addWindowListener((WindowListener)proxy);
            frame.addWindowListener(frame);
            frame.pack();
            frame.setVisible(true);
         } else proxies.add(proxy);
         status.setText("proxy loaded");
      } catch(Exception x) {
         status.setText(x.toString());
         Toolkit.getDefaultToolkit().beep();
      }
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }
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
   public void windowOpened(WindowEvent e)      {}
   public void windowActivated(WindowEvent e)   {}
   public void windowIconified(WindowEvent e)   {}
   public void windowDeiconified(WindowEvent e) {}
   public void windowDeactivated(WindowEvent e) {}
   public void windowClosing(WindowEvent e)     { dispose(); }
   public void windowClosed(WindowEvent e)      { if (main) System.exit(0); }
}
