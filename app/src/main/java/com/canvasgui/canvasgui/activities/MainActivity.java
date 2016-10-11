package com.canvasgui.canvasgui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.LayoutParser;
import com.canvasgui.canvasgui.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //read test layout and print its content
    public void retrieveLayout(View view) {

        LayoutParser parser = new LayoutParser(this);
        ArrayList<GUIElementDescription> list = new ArrayList();
        try {
           list = (ArrayList<GUIElementDescription>) parser.retrieveLayout();
        } catch (XmlPullParserException | IOException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }

        //put list into intent
        Intent intent = new Intent(this, DisplayXmlActivity.class);
        intent.putParcelableArrayListExtra("components", list);
        startActivity(intent);
    }
}
