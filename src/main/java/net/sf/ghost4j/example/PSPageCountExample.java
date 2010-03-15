/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.ghost4j.example;

import java.io.File;
import net.sf.ghost4j.document.PSDocument;

/**
 *
 * @author ggrousset
 */
public class PSPageCountExample {

    public static void main(String[] args) {
        try {

            PSDocument psDocument = new PSDocument();
            psDocument.load(new File("input.ps"));
            System.out.println("Page count is : " + psDocument.getPageCount());

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
