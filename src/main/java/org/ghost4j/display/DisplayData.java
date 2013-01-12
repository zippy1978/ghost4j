/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.display;

import com.sun.jna.Pointer;

/**
 * Simple class used to store display callback data.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class DisplayData {

    private int width;
    private int height;
    private int raster;
    private int format;
    private Pointer pimage;

    public int getWidth() {
	return width;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public int getRaster() {
	return raster;
    }

    public void setRaster(int raster) {
	this.raster = raster;
    }

    public int getFormat() {
	return format;
    }

    public void setFormat(int format) {
	this.format = format;
    }

    public Pointer getPimage() {
	return pimage;
    }

    public void setPimage(Pointer pimage) {
	this.pimage = pimage;
    }
}
