package cn.forward.tiledmapview.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * World Coordinate
 * @author ziwei huang
 */
public class MapRect implements Parcelable {
    public double left;
    public double top;
    public double right;
    public double bottom;

    public MapRect() {
    }

    public MapRect(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    public MapRect(MapRect mapRect) {
        if (mapRect == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = mapRect.left;
            top = mapRect.top;
            right = mapRect.right;
            bottom = mapRect.bottom;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapRect r = (MapRect) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }

    @Override
    public int hashCode() {
        int result = (left != +0.0f ? Long.valueOf(Double.doubleToLongBits(left)).hashCode() : 0);
        result = 31 * result + (top != +0.0f ? Long.valueOf(Double.doubleToLongBits(top)).hashCode() : 0);
        result = 31 * result + (right != +0.0f ? Long.valueOf(Double.doubleToLongBits(right)).hashCode() : 0);
        result = 31 * result + (bottom != +0.0f ? Long.valueOf(Double.doubleToLongBits(bottom)).hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MapRect(" + left + ", " + top + ", "
                + right + ", " + bottom + ")";
    }

    public final boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    public final double width() {
        return right - left;
    }

    public final double height() {
        return bottom - top;
    }

    public final double centerX() {
        return (left + right) * 0.5f;
    }

    public final double centerY() {
        return (top + bottom) * 0.5f;
    }

    public void setEmpty() {
        left = right = top = bottom = 0;
    }

    public void set(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(MapRect src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    public void offset(double dx, double dy) {
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
    }

    public void offsetTo(double newLeft, double newTop) {
        right += newLeft - left;
        bottom += newTop - top;
        left = newLeft;
        top = newTop;
    }

    public void inset(double dx, double dy) {
        left += dx;
        top += dy;
        right -= dx;
        bottom -= dy;
    }

    public boolean contains(double x, double y) {
        return left < right && top < bottom  // check for empty first
                && x >= left && x < right && y >= top && y < bottom;
    }

    public boolean contains(double left, double top, double right, double bottom) {
        // check for empty first
        return this.left < this.right && this.top < this.bottom
                // now check for containment
                && this.left <= left && this.top <= top
                && this.right >= right && this.bottom >= bottom;
    }

    public boolean contains(MapRect r) {
        // check for empty first
        return this.left < this.right && this.top < this.bottom
                // now check for containment
                && left <= r.left && top <= r.top
                && right >= r.right && bottom >= r.bottom;
    }

    /**
     * If rectangles a and b intersect, return true and set this rectangle to
     * that intersection, otherwise return false and do not change this
     * rectangle. No check is performed to see if either rectangle is empty.
     * To just test for intersection, use intersects()
     *
     * @param a The first rectangle being intersected with
     * @param b The second rectangle being intersected with
     * @return true iff the two specified rectangles intersect. If they do, set
     * this rectangle to that intersection. If they do not, return
     * false and do not change this rectangle.
     */
    public boolean setIntersect(MapRect a, MapRect b) {
        if (a.left < b.right && b.left < a.right
                && a.top < b.bottom && b.top < a.bottom) {
            left = Math.max(a.left, b.left);
            top = Math.max(a.top, b.top);
            right = Math.min(a.right, b.right);
            bottom = Math.min(a.bottom, b.bottom);
            return true;
        }
        return false;
    }

    public boolean intersects(MapRect mapRect){
        return intersects(mapRect.left, mapRect.top, mapRect.right, mapRect.bottom);
    }

    public boolean intersects(double left, double top, double right,
                              double bottom) {
        return this.left < right && left < this.right
                && this.top < bottom && top < this.bottom;
    }

    public void union(double left, double top, double right, double bottom) {
        if ((left < right) && (top < bottom)) {
            if ((this.left < this.right) && (this.top < this.bottom)) {
                if (this.left > left)
                    this.left = left;
                if (this.top > top)
                    this.top = top;
                if (this.right < right)
                    this.right = right;
                if (this.bottom < bottom)
                    this.bottom = bottom;
            } else {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }
        }
    }

    public void union(MapRect r) {
        union(r.left, r.top, r.right, r.bottom);
    }

    public void union(double x, double y) {
        if (x < left) {
            left = x;
        } else if (x > right) {
            right = x;
        }
        if (y < top) {
            top = y;
        } else if (y > bottom) {
            bottom = y;
        }
    }

    /**
     * Swap top/bottom or left/right if there are flipped (i.e. left > right
     * and/or top > bottom). This can be called if
     * the edges are computed separately, and may have crossed over each other.
     * If the edges are already correct (i.e. left <= right and top <= bottom)
     * then nothing is done.
     */
    public void sort() {
        if (left > right) {
            double temp = left;
            left = right;
            right = temp;
        }
        if (top > bottom) {
            double temp = top;
            top = bottom;
            bottom = temp;
        }
    }

    public void scale(double scale) {
        if (scale != 1.0f) {
            left = left * scale;
            top = top * scale;
            right = right * scale;
            bottom = bottom * scale;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(left);
        out.writeDouble(top);
        out.writeDouble(right);
        out.writeDouble(bottom);
    }

    public static final Parcelable.Creator<MapRect> CREATOR = new Parcelable.Creator<MapRect>() {
        public MapRect createFromParcel(Parcel in) {
            MapRect r = new MapRect();
            r.readFromParcel(in);
            return r;
        }

        public MapRect[] newArray(int size) {
            return new MapRect[size];
        }
    };

    public void readFromParcel(Parcel in) {
        left = in.readDouble();
        top = in.readDouble();
        right = in.readDouble();
        bottom = in.readDouble();
    }


}


