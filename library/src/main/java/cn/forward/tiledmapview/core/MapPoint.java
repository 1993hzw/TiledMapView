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

import java.util.Locale;

/**
 * World Coordinate
 * @author ziwei huang
 */
public class MapPoint implements Parcelable {
    public double x;
    public double y;

    public MapPoint() {
    }

    public MapPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MapPoint(MapPoint mapPoint) {
        this.x = mapPoint.x;
        this.y = mapPoint.y;
    }

    public void readFromParcel(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public void set(MapPoint point) {
        set(point.x, point.y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void cloneTo(MapPoint mapPoint) {
        mapPoint.x = this.x;
        mapPoint.y = this.y;
    }

    public double dist2(MapPoint mapPoint) {
        return (this.x - mapPoint.x) * (this.x - mapPoint.x) + (this.y - mapPoint.y) * (this.y - mapPoint.y);
    }

    public void offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[%s,%s]", x, y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    public static final Creator<MapPoint> CREATOR = new Creator<MapPoint>() {
        @Override
        public MapPoint createFromParcel(Parcel in) {
            MapPoint mapPoint = new MapPoint();
            mapPoint.readFromParcel(in);
            return mapPoint;
        }

        @Override
        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };
}


