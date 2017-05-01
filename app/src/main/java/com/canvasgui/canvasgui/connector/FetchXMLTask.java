package com.canvasgui.canvasgui.connector;

import android.os.AsyncTask;

import org.apache.commons.io.FilenameUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This Task tries to download xml files from a given URL
 * while managing network resources used in the process.
 */

public class FetchXMLTask extends AsyncTask<URL, String, Map<String, XmlPullParser>> {

    private final String ENCODING_TYPE = "UTF-8";

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Map<String, XmlPullParser> doInBackground(URL... urls) {
        Map<String, XmlPullParser> app = new HashMap<>();
        XmlPullParser pullParser;

        try {
            URL url = urls[0];
            String targetAddress = resolveTargetAdress(url);
            URLConnection connection = getTargetConnection(targetAddress);
            connection.connect();

            //Download file
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            pullParser = factory.newPullParser();

            InputStream input = getInputStream(url);
            if (input != null) {
                pullParser.setInput(getInputStream(url), ENCODING_TYPE);
                String appName = FilenameUtils.getBaseName(targetAddress);
                app.put(appName, pullParser);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return app;
    }

    private String resolveTargetAdress(URL url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            return urlConnection.getHeaderField("location");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Resolves a given URL to its final destination using the redirection's HTTP response code
     * @returns URLConnection the resolved connection to the final target URL
     */
    private URLConnection getTargetConnection(String location) {
        String locationHeaderField = "location";

        try {
            URL url = new URL(location);
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
