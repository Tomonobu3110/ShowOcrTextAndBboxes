package com.example.myocrtest;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

public class CustomTextBlock implements Parcelable {
    private String text;
    private Rect boundingBox;

    private Color color;

    public CustomTextBlock(String text, Rect boundingBox) {
        this.text = text;
        this.boundingBox = boundingBox;
    }

    protected CustomTextBlock(Parcel in) {
        text = in.readString();
        boundingBox = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Creator<CustomTextBlock> CREATOR = new Creator<CustomTextBlock>() {
        @Override
        public CustomTextBlock createFromParcel(Parcel in) {
            return new CustomTextBlock(in);
        }

        @Override
        public CustomTextBlock[] newArray(int size) {
            return new CustomTextBlock[size];
        }
    };

    public String getText() {
        return text;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeParcelable(boundingBox, flags);
    }
}
