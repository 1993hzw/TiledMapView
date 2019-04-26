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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.Gravity;

import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;

/**
 * Bitmap overlay on the pixel (such as the window), relative to pixel coordinates in size.
 * 像素坐标系上的图片覆盖物，如窗口，相对于像素坐标的大小
 */
public class BitmapPixelOverlay extends AbstractPixelOverlay {

    private Bitmap mBitmap;
    private RectF mRectF = new RectF();

    public BitmapPixelOverlay(Bitmap bitmap) {
        this(bitmap, bitmap.getWidth(), bitmap.getHeight(), Gravity.LEFT | Gravity.TOP);
    }

    public BitmapPixelOverlay(Bitmap bitmap, int gravity) {
        this(bitmap, bitmap.getWidth(), bitmap.getHeight(), gravity);
    }

    public BitmapPixelOverlay(Bitmap bitmap, double width, double height, int gravity) {
        super(width, height, gravity);
        mBitmap = bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        refreshItself();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void onDraw(Canvas canvas, ITiledMapView mapView, ITileConfig tileConfig, ITileDisplayInfo tileDisplayInfo) {
        mRectF.set(0, 0, (float) getWidth(), (float) getHeight());
        canvas.drawBitmap(mBitmap, null, mRectF, null);
    }
}
