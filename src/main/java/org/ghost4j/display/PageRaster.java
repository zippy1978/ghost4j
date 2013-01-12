/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.display;

import java.io.Serializable;

/**
 * Class representing a page raster (used by PageRasterDisplayCallBack)
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 * 
 */
public class PageRaster implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -977080307761838114L;
    private int width;
    private int height;
    private int raster;
    private int format;
    private byte[] data;

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

    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}
