package cn.forward.tiledmapview.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ziwei huang
 */
public class LngLat implements Parcelable {
    public double longitude;
    public double latitude;

    public LngLat() {
    }

    public LngLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LngLat(LngLat mapPoint) {
        this.longitude = mapPoint.longitude;
        this.latitude = mapPoint.latitude;
    }

    public void readFromParcel(Parcel in) {
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public void cloneTo(LngLat mapPoint) {
        mapPoint.longitude = this.longitude;
        mapPoint.latitude = this.latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public static final Creator<LngLat> CREATOR = new Creator<LngLat>() {
        @Override
        public LngLat createFromParcel(Parcel in) {
            LngLat mapPoint = new LngLat();
            mapPoint.readFromParcel(in);
            return mapPoint;
        }

        @Override
        public LngLat[] newArray(int size) {
            return new LngLat[size];
        }
    };
}


