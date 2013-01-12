/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

/**
 * Represents font analysis data (used by FontAnalyzer).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FontAnalysisItem implements AnalysisItem {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3172902215702475060L;

    /**
     * Font name.
     */
    private String name;

    /**
     * Indicate is the font is embedded.
     */
    private boolean embedded;

    /**
     * Indicate is the embedded font is a subset or a fullset
     */
    private boolean subSet;

    @Override
    public String toString() {

	String embeddedString = "NOT_EMBEDDED";
	if (embedded) {
	    embeddedString = "EMBEDDED";
	}

	String setString = "FULL_SET";
	if (subSet) {
	    setString = "SUB_SET";
	}

	return name + ": " + embeddedString + " " + setString;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean isEmbedded() {
	return embedded;
    }

    public void setEmbedded(boolean embedded) {
	this.embedded = embedded;
    }

    public boolean isSubSet() {
	return subSet;
    }

    public void setSubSet(boolean subSet) {
	this.subSet = subSet;
    }
}
