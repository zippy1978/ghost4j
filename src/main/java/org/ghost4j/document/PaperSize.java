/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a paper size. Standard sizes are defined as constants. Check
 * http://ghostscript.com/doc/current/Use.htm#Known_paper_sizes.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PaperSize implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -1614204334526018509L;

    /**
     * Standard paper sizes index map. Allows faster paer size lookup by name.
     */
    private static final Map<String, PaperSize> index = new HashMap<String, PaperSize>();

    public static final PaperSize LEDGER = new PaperSize("ledger", 1224, 792);
    public static final PaperSize LEGAL = new PaperSize("legal", 612, 1008);
    public static final PaperSize LETTER = new PaperSize("letter", 612, 792);
    public static final PaperSize ARCHE = new PaperSize("archE", 2592, 3456);
    public static final PaperSize ARCHD = new PaperSize("archD", 1728, 2592);
    public static final PaperSize ARCHC = new PaperSize("archC", 1296, 1728);
    public static final PaperSize ARCHB = new PaperSize("archB", 864, 1296);
    public static final PaperSize ARCHA = new PaperSize("archA", 648, 864);
    public static final PaperSize A0 = new PaperSize("a0", 2384, 3370);
    public static final PaperSize A1 = new PaperSize("a1", 1684, 2384);
    public static final PaperSize A2 = new PaperSize("a2", 1191, 1684);
    public static final PaperSize A3 = new PaperSize("a3", 842, 1191);
    public static final PaperSize A4 = new PaperSize("a4", 595, 842);
    public static final PaperSize A5 = new PaperSize("a5", 420, 595);
    public static final PaperSize A6 = new PaperSize("a6", 297, 420);
    public static final PaperSize A7 = new PaperSize("a7", 210, 297);
    public static final PaperSize A8 = new PaperSize("a8", 148, 210);
    public static final PaperSize A9 = new PaperSize("a9", 105, 148);
    public static final PaperSize A10 = new PaperSize("a10", 73, 105);

    /**
     * Paper width in points.
     */
    private final int width;

    /**
     * Paper height in points.
     */
    private final int height;

    /**
     * Paper name (if standard paper size)
     */
    private String name;

    /**
     * Constructor accepting dimensions.
     * 
     * @param width
     *            Width
     * @param height
     *            Height
     */
    public PaperSize(int width, int height) {
	this.width = width;
	this.height = height;
    }

    /**
     * Constructor accepting dimensions and name.
     * 
     * @param name
     *            Name. If provided, considered as a standard size (will be
     *            accessible with the getStandardPaperSize later on).
     * @param width
     *            Width
     * @param height
     *            Height
     */
    public PaperSize(String name, int width, int height) {
	this.width = width;
	this.height = height;
	this.name = name;

	// if name: add to the index of standard sizes
	if (this.name != null) {
	    synchronized (index) {
		index.put(this.name.toLowerCase(), this);
	    }
	}
    }

    /**
     * Returns a scaled PaperSize according to a scale factor.
     * 
     * @param factor
     *            Scale factor
     * @return Scaled PaperSize
     */
    public PaperSize scale(float factor) {

	return new PaperSize((int) (width * factor), (int) (height * factor));

    }

    /**
     * Returns a portrait orientation of the PaperSize.
     * 
     * @return A PaperSize.
     */
    public PaperSize portrait() {
	if (width > height) {
	    return new PaperSize(height, width);
	} else {
	    return new PaperSize(width, height);
	}
    }

    /**
     * Returns a landscape orientation of the PaperSize.
     * 
     * @return A PaperSize.
     */
    public PaperSize landscape() {
	if (width < height) {
	    return new PaperSize(height, width);
	} else {
	    return new PaperSize(width, height);
	}
    }

    /**
     * Looks for a standard paper size with a given name.
     * 
     * @param name
     *            Paper size name (not case sensitive).
     * @return PaperSize found or null
     */
    public static synchronized PaperSize getStandardPaperSize(String name) {

	return index.get(name.toLowerCase());
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public String getName() {
	return name;
    }

}
