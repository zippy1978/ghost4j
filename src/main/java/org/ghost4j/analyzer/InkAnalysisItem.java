/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

/**
 * Represents font analysis data (used by InkAnalyzer).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class InkAnalysisItem implements AnalysisItem {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6192330688526591124L;

    /**
     * Page index. First page starts at 1.
     */
    private int pageIndex;

    /**
     * Usage (ratio) of the cyan color on the page.
     */
    private double C;

    /**
     * Usage (ratio) of the magenta color on the page.
     */
    private double M;

    /**
     * Usage (ratio) of the yellow color on the page.
     */
    private double Y;

    /**
     * Usage (ratio) of the black color on the page.
     */
    private double K;

    @Override
    public String toString() {

	return "Page " + this.getPageIndex() + " C: " + this.getC() + " M: "
		+ this.getM() + " Y: " + this.getY() + " K: " + this.getK();
    }

    public int getPageIndex() {
	return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
	this.pageIndex = pageIndex;
    }

    public double getC() {
	return C;
    }

    public void setC(double c) {
	C = c;
    }

    public double getM() {
	return M;
    }

    public void setM(double m) {
	M = m;
    }

    public double getY() {
	return Y;
    }

    public void setY(double y) {
	Y = y;
    }

    public double getK() {
	return K;
    }

    public void setK(double k) {
	K = k;
    }
}
