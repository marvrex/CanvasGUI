package com.canvasgui.canvasgui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marv on 15/10/2016.
 */

public class ViewFactory {

    private String itemNotSupportedMessage = "The given view type is not supported and thus could not be added. View Type: ";

    private Context context;

    public ViewFactory() {
    }

    public View build(GUIElementDescription descr) {
        //instantiate a new view object
        View view = initViewType(descr);

        return view;
    }

    private View initViewType(GUIElementDescription descr) {
        try {
            return implementProperties(descr);
        } catch (ItemNotSupportedException e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
        return null;
    }

    private View implementProperties(GUIElementDescription descr) throws ItemNotSupportedException {
        String viewType = descr.getType().toLowerCase();

        if (viewType.equals(SupportedComponentsConstants.TEXTVIEW)) {
            TextView textView = new TextView(context);
            if (descr.getText() != null) {
                textView.setText(descr.getText());
            }
            textView.setTextSize(descr.getTextSize());
            return textView;
        } else if (viewType.equals(SupportedComponentsConstants.BUTTON)) {
            Button buttonView = new Button(context);
            if (descr.getText() != null) {
                buttonView.setText(descr.getText());
            }
            buttonView.setTextSize(descr.getTextSize());
            return buttonView;
        } else if (viewType.equals(SupportedComponentsConstants.IMAGEVIEW)) {

        } else {
            throw new ItemNotSupportedException(itemNotSupportedMessage + descr.getType());
        }

        return null;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
