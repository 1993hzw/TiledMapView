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


