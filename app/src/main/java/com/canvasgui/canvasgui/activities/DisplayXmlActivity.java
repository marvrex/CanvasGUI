package com.canvasgui.canvasgui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;

import java.util.List;

public class DisplayXmlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_display_xml);

        GridLayout layout = (GridLayout) findViewById(R.id.activity_display_xml);
        layout.setColumnCount(3);
        layout.setRowCount(6);


        List<GUIElementDescription> components = getIntent().getParcelableArrayListExtra("components");
        TextView tempView;

        for (GUIElementDescription element : components) {
            //gridlayout params
            GridLayout.Spec rowSpec = GridLayout.spec(element.getX());
            GridLayout.Spec columnSpec = GridLayout.spec(element.getY());

            tempView = new TextView(this);
            tempView.setTextSize(36);
            tempView.setText(element.getType());

            layout.addView(tempView, new GridLayout.LayoutParams(rowSpec, columnSpec));
        }
    }
}
