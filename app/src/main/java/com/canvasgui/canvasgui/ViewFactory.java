package com.canvasgui.canvasgui;

import android.view.View;

/**
 * Created by Marv on 15/10/2016.
 */

public class ViewFactory {

    public ViewFactory() {}

    public View createView(GUIElementDescription descr) {

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
    }
}
