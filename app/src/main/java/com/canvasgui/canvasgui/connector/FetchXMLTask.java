package com.canvasgui.canvasgui.connector;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This Task tries to download xml files from a given URL
 * while managing network resources used in the process.
 */

public class FetchXMLTask extends AsyncTask<URL, String, XmlPullParser> {

    private final String ENCODING_TYPE = "UTF-8";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected XmlPullParser doInBackground(URL... urls) {
        XmlPullParser pullParser = null;
        try {
            URL url = urls[0];
            URLConnection connection = getTargetConnection(url);
            connection.connect();

            //Download file
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            pullParser = factory.newPullParser();

            InputStream input = getInputStream(url);
            if (input != null)
                pullParser.setInput(getInputStream(url), ENCODING_TYPE);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return pullParser;
    }
    /*
     * Resolves a given URL to its final destination using the redirection's HTTP response code
     * @returns URLConnection the resolved connection to the final target URL
     */
    private URLConnection getTargetConnection(URL url) {
        String locationHeaderField = "location";

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);

            while(connection.getResponseCode() == HttpURLConnection.HTTP_MULT_CHOICE) {
                String targetLocation = connection.getHeaderField(locationHeaderField);
                connection = (HttpURLConnection) new URL(targetLocation).openConnection();
                connection.setInstanceFollowRedirects(false);
            }

            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
