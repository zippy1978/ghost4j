/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.display;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.ghost4j.GhostscriptException;
import org.ghost4j.util.ImageUtil;

/**
 * Display callback that stores device output as java Image (on image = one
 * page).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ImageWriterDisplayCallback implements DisplayCallback {

    /**
     * Holds document images.
     */
    private List<Image> images;

    /**
     * Constructor.
     */
    public ImageWriterDisplayCallback() {
	images = new ArrayList<Image>();
    }

    public void displayOpen() throws GhostscriptException {

    }

    public void displayPreClose() throws GhostscriptException {

    }

    public void displayClose() throws GhostscriptException {

    }

    public void displayPreSize(int width, int height, int raster, int format)
	    throws GhostscriptException {

    }

    public void displaySize(int width, int height, int raster, int format)
	    throws GhostscriptException {

    }

    public void displaySync() throws GhostscriptException {

    }

    public void displayPage(int width, int height, int raster, int format,
	    int copies, int flush, byte[] imageData)
	    throws GhostscriptException {

	// create new raster
	PageRaster pageRaster = new PageRaster();
	pageRaster.setWidth(width);
	pageRaster.setHeight(height);
	pageRaster.setRaster(raster);
	pageRaster.setFormat(format);
	pageRaster.setData(imageData);

	// convert to image and add to list
	images.add(ImageUtil.converterPageRasterToImage(pageRaster));

    }

    public void displayUpdate(int x, int y, int width, int height)
	    throws GhostscriptException {

    }

    public List<Image> getImages() {
	return images;
    }
}
