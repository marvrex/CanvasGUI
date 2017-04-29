package com.canvasgui.canvasgui.activities.gui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marv on 26/04/2017.
 */

public class LayoutButtonArrayAdapter extends ArrayAdapter {

    private Context context;

    public LayoutButtonArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
    }

    // Adds a new button to the Layout for its respective CanvasApp
    public void refreshLayout(LinearLayout activityLayout) {
        Button newLayoutButton = new Button(this.context);



        activityLayout.addView(activityLayout);
    }
}
