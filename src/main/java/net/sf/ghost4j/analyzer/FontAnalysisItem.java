/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.analyzer;

/**
 * Represents font analysis data (used by FontAnalyzer).
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FontAnalysisItem implements AnalysisItem {

	/**
	 * Font name.
	 */
	private String name;
	
	/**
	 * Indicate is the font is embedded.
	 */
	private boolean embedded;
	
	@Override
	public String toString() {
		
		String embeddedString = "NOT EMBEDDED";
		if (embedded) {
			embeddedString = "EMBEDDED";
		}
		
		return name + ": " + embeddedString;
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
}
