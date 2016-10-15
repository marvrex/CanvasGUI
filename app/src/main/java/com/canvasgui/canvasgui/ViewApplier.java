package com.canvasgui.canvasgui;

import android.app.Activity;
import android.content.Intent;
import android.widget.GridLayout;
import android.widget.TextView;

import com.canvasgui.canvasgui.activities.DisplayXmlActivity;

import java.util.List;

/**
 * Created by Marvin on 10/15/2016.
 */

public class ViewApplier {

    public ViewApplier() {

    }

    public void applyViewsToLayout(Activity activity, GridLayout layout, List<GUIElementDescription> components) {
        TextView tempView;

        for (GUIElementDescription element : components) {
            //gridlayout params
            GridLayout.Spec rowSpec = GridLayout.spec(element.getX());
            GridLayout.Spec columnSpec = GridLayout.spec(element.getY());

            //TODO remove debug code
            tempView = new TextView(activity);
            tempView.setTextSize(36);
            tempView.setText(element.getType());

            layout.addView(tempView, new GridLayout.LayoutParams(rowSpec, columnSpec));
        }
    }
}
