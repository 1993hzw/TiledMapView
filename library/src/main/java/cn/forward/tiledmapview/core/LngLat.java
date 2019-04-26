/*
 * Copyright (C) 2019  Ziwei Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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


