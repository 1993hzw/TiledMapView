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
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Gravity;

import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.ITiledMapView;

/**
 * Text overlay on the pixel (such as the window), relative to pixel coordinates in size.
 * 像素坐标系上的文字覆盖物，如窗口，相对于像素坐标的大小
 */
public class TextPixelOverlay extends AbstractPixelOverlay {

    public static final int WRAP_CONTENT = -1;

    private CharSequence mText;
    private Layout mLayout;
    private TextPaint mTextPaint;

    public TextPixelOverlay(CharSequence text) {
        this(text, WRAP_CONTENT, WRAP_CONTENT, Gravity.LEFT | Gravity.TOP);
    }

    public TextPixelOverlay(CharSequence text, int gravity) {
        this(text, WRAP_CONTENT, WRAP_CONTENT, gravity);
    }

    public TextPixelOverlay(CharSequence text, double width, double height, int gravity) {
        super(width, height, gravity);
        mText = text;
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(14);
    }

    public TextPaint getTextPaint() {
        return mTextPaint;
    }

    public void setText(CharSequence text) {
        setText(text, WRAP_CONTENT, WRAP_CONTENT);
    }

    public void setText(CharSequence text, double width, double height) {
        mText = text;
        setWidth(width);
        setHeight(height);
        notifyPropertiesChanged();
    }

    public CharSequence getText() {
        return mText;
    }

    @Override
    public void onPropertiesChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        float width = getWidth() == WRAP_CONTENT ? measureText(mText.toString()) : (float) getWidth();
        mLayout = new StaticLayout(mText, mTextPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (getWidth() == WRAP_CONTENT) {
            setWidth(mLayout.getWidth());
        }
        setWidth(mLayout.getWidth());
        if (getHeight() == WRAP_CONTENT) {
            setHeight(mLayout.getHeight());
        }

        super.onPropertiesChanged(mapView, tileConfig, tileDisplayInfo);
    }

    private float measureText(String text) {
        String[] list = text.split("\n");
        float max = 0;
        for (String s : list) {
            float w = mTextPaint.measureText(s);
            if (w > max) {
                max = w;
            }
        }
        return max;
    }

    @Override
    public void onDraw(Canvas canvas, ITiledMapView mapView, ITileConfig tileConfig, ITileDisplayInfo tileDisplayInfo) {
        mLayout.draw(canvas);
    }
}
