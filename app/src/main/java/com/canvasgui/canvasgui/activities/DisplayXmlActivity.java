package com.canvasgui.canvasgui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;
import com.canvasgui.canvasgui.ViewApplier;

import java.util.List;

public class DisplayXmlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_display_xml);

        //create the layout...
        GridLayout layout = initGridLayout();
        //...and apply the xml views to it
        ViewApplier applier = new ViewApplier();
        List<GUIElementDescription> components = getIntent().getParcelableArrayListExtra("components");

        applier.applyViewsToLayout(this, layout, components);
    }

    @NonNull
    private GridLayout initGridLayout() {
        GridLayout layout = (GridLayout) findViewById(R.id.activity_display_xml);
        layout.setColumnCount(3);
        layout.setRowCount(6);
        return layout;
    }

}
