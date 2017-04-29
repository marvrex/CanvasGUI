package com.canvasgui.canvasgui.connector;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is given a collection of detected urls and validates them,
 * thus URL which do not point to a Canvas-compatible layout are filtered.
 */

//TODO
public class CanvasXmlValidator {

    Set<String> validUrls;

    public CanvasXmlValidator() {
        validUrls = new HashSet<>();
    }

    public void validateURL(URL url) {
        isXMLResource(url);
    }

    private boolean isXMLResource(URL url) {
        boolean isXml = false;

        // [^/\\\\&\\?]+\\.\\w{3,4}(?=([\\?&].*$|$))

        return isXml;
    }
}
