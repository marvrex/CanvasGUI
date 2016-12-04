package com.canvasgui.canvasgui.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.canvasgui.canvasgui.DownloadXMLTask;
import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private List<GUIElementDescription> guiElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void retrieveLayout(View view) {

        ArrayList<GUIElementDescription> guiElements = new ArrayList<>();
        try {
            guiElements = new DownloadXMLTask().execute(this).get();
        } catch (InterruptedException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
        } catch (ExecutionException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
        }
        //put list into intent
        Intent intent = new Intent(this, DisplayXmlActivity.class);
        intent.putParcelableArrayListExtra("components", guiElements);
        startActivity(intent);
    }
}