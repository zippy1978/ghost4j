/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.example;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

/**
 * Example showing how to render pages of a PDF document using the high level
 * API.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class SimpleRendererExample {

    public static void main(String[] args) {

	try {

	    // load PDF document
	    PDFDocument document = new PDFDocument();
	    document.load(new File("input.pdf"));

	    // create renderer
	    SimpleRenderer renderer = new SimpleRenderer();

	    // set resolution (in DPI)
	    renderer.setResolution(300);

	    // render
	    List<Image> images = renderer.render(document);

	    // write images to files to disk as PNG
	    try {
		for (int i = 0; i < images.size(); i++) {
		    ImageIO.write((RenderedImage) images.get(i), "png",
			    new File((i + 1) + ".png"));
		}
	    } catch (IOException e) {
		System.out.println("ERROR: " + e.getMessage());
	    }

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	}

    }
}
