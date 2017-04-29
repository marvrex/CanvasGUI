package com.canvasgui.canvasgui.connector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.LayoutParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marv on 04/12/2016.
 */

public class XMLToGUIDescriptionConverterTask extends AsyncTask<Context, Integer, ArrayList<GUIElementDescription>> {

    Context context;
    XmlPullParser xml;

    public XMLToGUIDescriptionConverterTask(Context context, XmlPullParser xml) {
        this.context = context;
        this.xml = xml;
    }

    @Override
    protected ArrayList<GUIElementDescription> doInBackground(Context[] contexts) {
        LayoutParser parser = new LayoutParser(context, xml);

        ArrayList<GUIElementDescription> guiElements = new ArrayList();
        try {
            guiElements = (ArrayList<GUIElementDescription>) parser.retrieveLayout();
        } catch (XmlPullParserException | IOException e) {
            Log.e("DescriptionConverter", e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return guiElements;
    }
}