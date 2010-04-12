/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.util;


public class ForkTest {

    public static void main(String[] args) throws Exception{

        System.out.println("START FORKED");
        Thread.sleep(1000);
        System.out.println("END FORKED");
    }
}
