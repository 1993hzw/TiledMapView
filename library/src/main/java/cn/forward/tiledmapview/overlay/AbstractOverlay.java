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
 
package cn.forward.tiledmapview.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.Gravity;

import cn.forward.tiledmapview.AbstractLayer;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.MapPoint;

public abstract class AbstractOverlay extends AbstractLayer {

    private double mWidth, mHeight;
    private double mLeftOffset = 0, mTopOffset = 0;
    private MapPoint mLocation = new MapPoint();
    private boolean mPropertiesChanged = false;

    private int mGravity = Gravity.LEFT | Gravity.TOP; // 绘制的重心，使重心对齐 mLocation
    private double mGravityLeftOffset = 0, mGravityTopOffset = 0;
    private int mBackgroundColor = Color.TRANSPARENT;

    public AbstractOverlay(double width, double height, int gravity) {
        mWidth = width;
        mHeight = height;
        mGravity = gravity;
    }

    public void setLocationOnMap(double x, double y) {
        mLocation.set(x, y);
        notifyPropertiesChanged();
    }

    public MapPoint getLocationOnMap() {
        return mLocation;
    }

    public void setWidth(double width) {
        mWidth = width;
        notifyPropertiesChanged();
    }

    public void setHeight(double height) {
        mHeight = height;
        notifyPropertiesChanged();
    }

    public double getWidth() {
        return mWidth;
    }

    public double getHeight() {
        return mHeight;
    }

    /**
     * The coordinate axis is downward
     *
     * @param leftOffset
     */
    public void setLeftOffset(double leftOffset) {
        mLeftOffset = leftOffset;
        notifyPropertiesChanged();
    }

    /**
     * The coordinate axis is downward
     *
     * @param topOffset
     */
    public void setTopOffset(double topOffset) {
        mTopOffset = topOffset;
        notifyPropertiesChanged();
    }

    public double getLeftOffset() {
        return mLeftOffset;
    }

    public double getTopOffset() {
        return mTopOffset;
    }

    public void notifyPropertiesChanged() {
        mPropertiesChanged = true;
        refreshItself();
    }

    /**
     * The center of drawing gravity, aligning the location when drawing.
     * 绘制的重心，绘制时重心对齐 location 的位置
     *
     * @param gravity
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        if (mGravity == Gravity.NO_GRAVITY) {
            mGravityLeftOffset = 0;
            mGravityTopOffset = 0;
        }
        notifyPropertiesChanged();
    }

    /**
     * The coordinate axis is downward
     */
    protected void setGravityLeftOffset(double gravityLeftOffset) {
        mGravityLeftOffset = gravityLeftOffset;
        notifyPropertiesChanged();
    }

    public double getGravityLeftOffset() {
        return mGravityLeftOffset;
    }

    /**
     * The coordinate axis is downward
     */
    protected void setGravityTopOffset(double gravityTopOffset) {
        mGravityTopOffset = gravityTopOffset;
        notifyPropertiesChanged();
    }

    public double getGravityTopOffset() {
        return mGravityTopOffset;
    }

    @CallSuper
    @Override
    public void onDisplayInfoChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        onPropertiesChanged(mapView, tileConfig, tileDisplayInfo); // onDisplayInfoChanged回调时一定会触发onPropertiesChanged
    }

    @CallSuper
    public void onPropertiesChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        if (mGravity != Gravity.NO_GRAVITY) {
            switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    mGravityLeftOffset = -getWidth() / 2f;
                    break;
                case Gravity.RIGHT:
                    mGravityLeftOffset = -getWidth();
                    break;
                default:
                    mGravityLeftOffset = 0;
            }

            switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.CENTER_VERTICAL:
                    mGravityTopOffset = -getHeight() / 2f;
                    break;
                case Gravity.BOTTOM:
                    mGravityTopOffset = -getHeight();
                    break;
                default:
                    mGravityTopOffset = 0;
            }
        }

    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @CallSuper
    @Override
    public final void draw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        if (mPropertiesChanged) {
            onPropertiesChanged(mapView, tileConfig, tileDisplayInfo);
            mPropertiesChanged = false;
        }
        prepareDraw(canvas, mapView, tileConfig, tileDisplayInfo);
    }

    public abstract void prepareDraw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo);

    public void doDraw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        if (mBackgroundColor != Color.TRANSPARENT) {
            canvas.drawColor(mBackgroundColor);
        }
        onDraw(canvas, mapView, tileConfig, tileDisplayInfo);
    }

    public abstract void onDraw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo);

}


