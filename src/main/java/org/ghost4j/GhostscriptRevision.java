/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */
package org.ghost4j;

import java.util.Date;

/**
 * Class used to carry Ghostscript revision data.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptRevision {

    /**
     * Product name.
     */
    private String product;
    /**
     * Copyright.
     */
    private String copyright;
    /**
     * Revision number.
     */
    private String number;
    /**
     * Revision date.
     */
    private Date revisionDate;

    public String getProduct() {
	return product;
    }

    public void setProduct(String product) {
	this.product = product;
    }

    public String getCopyright() {
	return copyright;
    }

    public void setCopyright(String copyright) {
	this.copyright = copyright;
    }

    public Date getRevisionDate() {
	return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
	this.revisionDate = revisionDate;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }
}
