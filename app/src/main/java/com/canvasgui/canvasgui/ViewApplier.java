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

    private ViewFactory viewFactory;

    public ViewApplier() {
        this.viewFactory = new ViewFactory();
    }

    public void applyViewsToLayout(Activity activity, GridLayout layout, List<GUIElementDescription> components) {
        //set context for all following views
        viewFactory.setContext(activity);

        for (GUIElementDescription component : components) {
            //gridlayout params
            GridLayout.Spec rowSpec = GridLayout.spec(component.getX());
            GridLayout.Spec columnSpec = GridLayout.spec(component.getY());

            layout.addView(viewFactory.build(component), new GridLayout.LayoutParams(rowSpec, columnSpec));
        }
    }
}
