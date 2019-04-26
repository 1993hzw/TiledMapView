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
 * Tile Coordinate
 * @author ziwei huang
 */
public class Tile implements Parcelable {
    public int col;
    public int level;
    public int row;

    public Tile() {

    }

    public Tile(Tile tile) {
        this(tile.level, tile.row, tile.col);
    }

    public Tile(int level, int row, int col) {
        this.level = level;
        this.row = row;
        this.col = col;
    }

    public void readFromParcel(Parcel in) {
        col = in.readInt();
        level = in.readInt();
        row = in.readInt();
    }

    public void reset(int level, int row, int col) {
        this.level = level;
        this.row = row;
        this.col = col;
    }

    public String toString() {
        return this.level + "-" + this.row + "-" + this.col;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tile)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Tile tile = (Tile) obj;
        if (level != tile.level || row != tile.row || col != tile.col) {
            return false;
        }

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(col);
        dest.writeInt(level);
        dest.writeInt(row);
    }

    public static final Creator<Tile> CREATOR = new Creator<Tile>() {
        @Override
        public Tile createFromParcel(Parcel in) {
            Tile tile = new Tile();
            tile.readFromParcel(in);
            return tile;
        }

        @Override
        public Tile[] newArray(int size) {
            return new Tile[size];
        }
    };
}


