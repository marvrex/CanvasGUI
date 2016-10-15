package com.canvasgui.canvasgui;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marv on 15/10/2016.
 */

public class ViewFactory {

    private Map components;

    public ViewFactory() {
        this.components = new HashMap();
    }

    private View createView(GUIElementDescription descr) {
        //instantiate a new view object
        View view = initViewType(descr);

        return view;
    }

    private View initViewType(GUIElementDescription descr) {
        try {
            String prefix = "android.view.";
            String fqn = prefix + descr.getType();
            return (View) Class.forName(fqn).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map getComponents() {
        return this.components;
    }
}
