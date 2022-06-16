package com.wajahatkarim3.longimagecamera;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class BitmapImage implements Parcelable {

    private Bitmap bitmap;

     public BitmapImage(Bitmap bitmap, int i){
        this.bitmap = bitmap;
     }

    protected BitmapImage(Parcel in) {
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<BitmapImage> CREATOR = new Creator<BitmapImage>() {
        @Override
        public BitmapImage createFromParcel(Parcel in) {
            return new BitmapImage(in);
        }

        @Override
        public BitmapImage[] newArray(int size) {
            return new BitmapImage[size];
        }
    };

    public BitmapImage() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bitmap, flags);
    }
}
