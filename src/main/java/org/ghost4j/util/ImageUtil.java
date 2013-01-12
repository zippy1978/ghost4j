/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.util;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import org.ghost4j.display.PageRaster;

/**
 * Image utilities class.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ImageUtil {

    /**
     * Converts a list of PageRaster objects to a list of Image objects
     * 
     * @param rasters
     *            Page rasters to convert
     * @return A list of images
     */
    public static List<Image> convertPageRastersToImages(
	    List<PageRaster> rasters) {

	List<Image> result = new ArrayList<Image>();

	for (PageRaster raster : rasters) {
	    result.add(converterPageRasterToImage(raster));
	}

	return result;
    }

    /**
     * Converts a PageRaster object to an Image object. Raster data is supposed
     * to hold RGB image data
     * 
     * @param raster
     *            Page raster to convert
     * @return An image
     */
    public static Image converterPageRasterToImage(PageRaster raster) {

	// create raster
	DataBufferByte dbb = new DataBufferByte(raster.getData(),
		raster.getData().length);
	WritableRaster wr = Raster.createInterleavedRaster(dbb,
		raster.getWidth(), raster.getHeight(), raster.getRaster(), 3,
		new int[] { 0, 1, 2 }, null);

	// create color space
	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	ColorModel cm = new ComponentColorModel(cs, false, false,
		Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

	// create image and return it
	return new BufferedImage(cm, wr, false, null);
    }
}
