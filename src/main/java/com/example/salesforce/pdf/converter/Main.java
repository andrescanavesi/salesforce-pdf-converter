package com.example.salesforce.pdf.converter;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andres Canavesi
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * BEFORE RUNNING Go to Config class and set up your credentials
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            File file1 = new File("htmlexamples/example.html");

            String html = new Scanner(file1).useDelimiter("\\Z").next();

            SalesforcePdfConverter salesforcePdfConverter = new SalesforcePdfConverter();
            File filePdf = salesforcePdfConverter.convert(html);
            LOG.log(Level.INFO, "Pdf saved at: {0}", filePdf.getAbsolutePath());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
