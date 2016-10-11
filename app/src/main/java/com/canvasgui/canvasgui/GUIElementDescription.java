package com.canvasgui.canvasgui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A representation of a GUI component.
 * @type is the class name of the GUI component
 * Other variables represent additional information.
 * Created by Marv on 11/10/2016.
 */

public class GUIElementDescription implements Parcelable{
    private String type;
    private String text;
    private int x;
    private int y;

    public GUIElementDescription(String type, String text, int x, int y) {
        this.type = type;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public GUIElementDescription(Parcel parcel) {
        this.type = parcel.readString();
        this.text = parcel.readString();
        this.x = parcel.readInt();
        this.y = parcel.readInt();
    }

    public String getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.text);
        dest.writeInt(this.x);
        dest.writeInt(this.y);
    }

    public static final Parcelable.Creator<GUIElementDescription> CREATOR =
            new Parcelable.Creator<GUIElementDescription>() {

                @Override
                public GUIElementDescription createFromParcel(Parcel source) {
                    return new GUIElementDescription(source);
                }

                @Override
                public GUIElementDescription[] newArray(int size) {
                    return new GUIElementDescription[0];
                }
            };
}
