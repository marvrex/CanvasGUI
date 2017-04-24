package com.canvasgui.canvasgui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marv on 04/12/2016.
 */

public class DownloadXMLTask extends AsyncTask<Context, Integer, ArrayList<GUIElementDescription>> {

    @Override
    protected ArrayList<GUIElementDescription> doInBackground(Context[] contexts) {
        LayoutParser parser = new LayoutParser(contexts[0]);



        ArrayList<GUIElementDescription> guiElements = new ArrayList();
        try {
            guiElements = (ArrayList<GUIElementDescription>) parser.retrieveLayout();
        } catch (XmlPullParserException | IOException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return guiElements;
    }
}