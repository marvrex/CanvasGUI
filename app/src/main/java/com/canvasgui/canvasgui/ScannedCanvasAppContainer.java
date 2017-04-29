package com.canvasgui.canvasgui;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.canvasgui.canvasgui.connector.CanvasXmlValidator;

import org.altbeacon.beacon.Beacon;

import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScannedCanvasAppContainer {
    private Context context;
    private Map<Button, URL> apps;

    private CanvasXmlValidator validator;

    public ScannedCanvasAppContainer(Context context) {
        this.context = context;
        this.apps = new HashMap<>();
        this.validator = new CanvasXmlValidator();
    }

    private Map<Button,URL> mapAppsToLayoutURL(Collection<Beacon> beacons) {
        Map<Button, URL> map = new HashMap<>();

        int id = 0;
        for (Beacon beacon : beacons) {
            URL url = BeaconUtils.getURLFromBeaconIdentifier(beacon.getId1());

            Button button = new Button(context);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText(url.getPath());
            button.setId(id);

            map.put(button, url);
            id++;
        }
        return map;
    }

    public void processBeacons(Collection<Beacon> beacons) {
       this.apps = mapAppsToLayoutURL(beacons);
    }

    public Map<Button, URL> getApps() {
        return apps;
    }

}
