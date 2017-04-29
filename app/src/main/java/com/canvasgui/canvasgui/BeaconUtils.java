package com.canvasgui.canvasgui;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marv on 28/04/2017.
 */

public class BeaconUtils {



    public static String getStringFromBeaconIdentifier(Identifier identifier) {
        return UrlBeaconUrlCompressor.uncompress(identifier.toByteArray());
    }

    public static URL getURLFromBeaconIdentifier(Identifier identifier) {
        String location = getStringFromBeaconIdentifier(identifier);
        try {
            URL url = new URL(location);
            return url;
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

}
