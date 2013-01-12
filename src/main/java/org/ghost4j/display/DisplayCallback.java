/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.display;

import org.ghost4j.GhostscriptException;

/**
 * Interface representing a display callback. A display callback provides method
 * to interract with the Ghostscript interpreter display. This can be usefull if
 * you are interested in capturing PS or PDF page rasters. Important: in order
 * to use a display callback, Ghostscript must be initialized with
 * -sDEVICE=display -sDisplayHandle and -dDisplayFormat arguments. Usually set
 * -sDisplayHandle to 0 and use -dDisplayFormat to define how display data will
 * be sent to the displayPage method. -dDisplayFormat=16#804 sets a standard RGB
 * ouput. Please refer to http://ghostscript.com/doc/8.54/Devices.htm to see how
 * to set display parameters.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface DisplayCallback {

    /**
     * Method called when new device has been opened. This is the first event
     * from this device.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayOpen() throws GhostscriptException;

    /**
     * Method called when device is about to be closed. Device will not be
     * closed until this function returns.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayPreClose() throws GhostscriptException;

    /**
     * Method called when device has been closed. This is the last event from
     * this device.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayClose() throws GhostscriptException;

    /**
     * Method called when device is about to be resized.
     * 
     * @param width
     *            Width
     * @param height
     *            Height
     * @param raster
     *            Raster
     * @param format
     *            Format
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayPreSize(int width, int height, int raster, int format)
	    throws GhostscriptException;

    /**
     * Method called when device has been resized.
     * 
     * @param width
     *            Width
     * @param height
     *            Height
     * @param raster
     *            Raster
     * @param format
     *            Format
     * @throws org.ghost4j.GhostscriptException
     */
    public void displaySize(int width, int height, int raster, int format)
	    throws GhostscriptException;

    /**
     * Method called on page flush.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public void displaySync() throws GhostscriptException;

    /**
     * Method called on show page.
     * 
     * @param width
     *            Width
     * @param height
     *            Height
     * @param raster
     *            Raster
     * @param format
     *            Format
     * @param copies
     *            Copies
     * @param flush
     *            Flush
     * @param imageData
     *            Byte array representing image data. Data layout and order is
     *            controlled by the -dDisplayFormat argument.
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayPage(int width, int height, int raster, int format,
	    int copies, int flush, byte[] imageData)
	    throws GhostscriptException;

    /**
     * Method called to notify whenever a portion of the raster is updated.
     * 
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     * @param width
     *            Width
     * @param height
     *            Height
     * @throws org.ghost4j.GhostscriptException
     */
    public void displayUpdate(int x, int y, int width, int height)
	    throws GhostscriptException;
}
