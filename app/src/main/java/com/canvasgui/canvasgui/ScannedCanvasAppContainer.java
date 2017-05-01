package com.canvasgui.canvasgui;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.canvasgui.canvasgui.connector.CanvasXmlValidator;
import com.canvasgui.canvasgui.connector.FetchXMLTask;
import com.canvasgui.canvasgui.connector.XMLToGUIDescriptionConverterTask;

import org.altbeacon.beacon.Beacon;
import org.xmlpull.v1.XmlPullParser;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ScannedCanvasAppContainer {
    private Context context;
    private Map<ArrayList<GUIElementDescription>,String> apps;

    private CanvasXmlValidator validator;

    public ScannedCanvasAppContainer(Context context) {
        this.context = context;
        this.apps = new HashMap<>();
        this.validator = new CanvasXmlValidator();
    }

    private Map<ArrayList<GUIElementDescription>,String> mapAppsToLayoutURL(Collection<Beacon> beacons) {
        Map<ArrayList<GUIElementDescription>, String> map = new HashMap<>();
        final LayoutInflater  inflater = LayoutInflater.from(context) ;
        int id = 0;

        for (Beacon beacon : beacons) {
            ArrayList<GUIElementDescription> guiComponents;
            String appName;
            URL url = BeaconUtils.getURLFromBeaconIdentifier(beacon.getId1());

            try {
                Map<String,XmlPullParser> app = new FetchXMLTask().execute(url).get();

                //We expect only one entry to be returned from the FetchXMLTask
                Map.Entry<String, XmlPullParser> appEntry = app.entrySet().iterator().next();
                appName = appEntry.getKey();
                XmlPullParser parser = appEntry.getValue();

                guiComponents = new XMLToGUIDescriptionConverterTask(context, parser).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                // An error occurred, so we don't treat this beacon as a valid Canvas Beacon
                continue;
            }

            Button button = (Button)inflater.inflate(R.layout.canvas_app_button, null);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText(url.getPath());
            button.setId(id);

            map.put(guiComponents, appName);
            id++;
        }
        return map;
    }

    public void processBeacons(Collection<Beacon> beacons) {
       this.apps = mapAppsToLayoutURL(beacons);
    }

    public Map<ArrayList<GUIElementDescription>,String> getApps() {
        return apps;
    }

}
