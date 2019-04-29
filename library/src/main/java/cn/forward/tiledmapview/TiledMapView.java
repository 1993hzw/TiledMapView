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

package cn.forward.tiledmapview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import cn.forward.tiledmapview.core.ILayer;
import cn.forward.tiledmapview.core.ILayerGroup;
import cn.forward.tiledmapview.core.IMapTouchDetector;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.LngLat;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.MapRect;
import cn.forward.tiledmapview.core.Tile;
import cn.forward.tiledmapview.util.LogUtil;

/**
 * https://github.com/1993hzw/TiledMapView
 */
public class TiledMapView extends FrameLayout implements ITiledMapView, ILayer.Callback {

    private static boolean sDebugMode = false;

    public static final String TAG = "MapView";

    private IMapTouchDetector mMapTouchDetector;
    private IMapTouchDetector mDefaultMapTouchDetector;
    private final ILayerGroup<ILayer> mLayerGroup;
    final MapPoint mCenterPoint = new MapPoint(); // 屏幕中心对应的点
    private MapPoint mFocusPoint = null; // 焦点中心，比如双值缩放的中心点，图片以该点向外依次加载
    private double mMaxResolution;
    private double mMinResolution;
    private double mResolution;
    private MapRect mMapRect = new MapRect();
    private MapRect mCenterBounds = new MapRect();
    private IProjection mProjection;

    private final InnerTileDisplayInfo mTileDisplayInfo = new InnerTileDisplayInfo();
    private boolean mIsShowTileInfo = false; // 是否显示网格信息
    private ITileConfig mTileConfig = null;
    private MapRect mTempMapRect = new MapRect();

    private ValueAnimator mZoomAnimator;

    // debug info
    private Paint mRectPaint;
    private TextPaint mTextPaint;

    private int mFlags = 0;

    private static final int FLAG_ON_DISPLAY_INFO_CHANGED = 1 << 0;

    // debgu
    private Paint mPaintDebug;

    public TiledMapView(Context context) {
        this(context, null);
    }

    public TiledMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);

        mLayerGroup = new LayerGroup();
        mLayerGroup.setCallback(this);

        mDefaultMapTouchDetector = mMapTouchDetector = new MapTouchDetector(context, new MapOnTouchGestureListener(this));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTileConfig == null) {
            return;
        }

        limitCenterPoint();
        updateMapRect();
    }

    @Override
    public int getViewWidth() {
        return getWidth();
    }

    @Override
    public int getViewHeight() {
        return getHeight();
    }

    @Override
    public void refresh() {
        invalidate();
    }

    @Override
    public MapPoint getCenterPoint() {
        return new MapPoint(this.mCenterPoint);
    }

    @Override
    public MapPoint getFocusCenter() {
        if (this.mFocusPoint != null) {
            return this.mFocusPoint;
        }
        return getCenterPoint();
    }

    @Override
    public ILayerGroup<ILayer> getLayerGroup() {
        return this.mLayerGroup;
    }

    private void zoomOnFocus(final MapPoint focusPoint, double zoom) {
        final PointF viewPoint = mapPoint2ViewPoint(focusPoint);
        setResolution(getResolution() / zoom);
        MapPoint centerPoint = new MapPoint(focusPoint);
        centerPoint.x += getResolution() * (getWidth() / 2F - viewPoint.x);
        centerPoint.y -= getResolution() * (getHeight() / 2F - viewPoint.y);
        setCenterPoint(centerPoint);
        setFocusPoint(focusPoint);
    }


    @Override
    public void zoomOnFocus(final MapPoint focusPoint, final double zoom, boolean withAnim) {
        if (withAnim) {
            if (mZoomAnimator != null && mZoomAnimator.isRunning()) {
                mZoomAnimator.cancel();
            }
            final double startAnimResolution = getResolution();
            this.mZoomAnimator = ValueAnimator.ofFloat(1, (float) zoom);
            mZoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                public void onAnimationUpdate(ValueAnimator animator) {
                    float value = (float) animator.getAnimatedValue();
                    zoomOnFocus(focusPoint, value * getResolution() / startAnimResolution);
                }
            });
            mZoomAnimator.setDuration(500L);
            this.mZoomAnimator.start();
        } else {
            zoomOnFocus(focusPoint, zoom);
        }
    }

    private void zoomToCenter(final MapPoint centerPoint, double zoom) {
        setResolution(getResolution() / zoom);
        setCenterPoint(centerPoint);
    }

    @Override
    public void zoomToCenter(final MapPoint toCenterPoint, double zoom, boolean withAnim) {
        if (withAnim) {
            if (mZoomAnimator != null && mZoomAnimator.isRunning()) {
                mZoomAnimator.cancel();
            }

            final double startAnimResolution = getResolution();
            this.mZoomAnimator = ValueAnimator.ofFloat(1, (float) zoom);
            mZoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                MapPoint from = getCenterPoint();
                double totalX = toCenterPoint.x - from.x;
                double totalY = toCenterPoint.y - from.y;

                public void onAnimationUpdate(ValueAnimator animator) {
                    float fraction = animator.getAnimatedFraction();
                    float value = (float) animator.getAnimatedValue();
                    MapPoint center = new MapPoint(from.x + totalX * fraction, from.y + totalY * fraction);
                    zoomToCenter(center, value * getResolution() / startAnimResolution);
                }
            });
            mZoomAnimator.setDuration(500);
            this.mZoomAnimator.start();
        } else {
            zoomToCenter(toCenterPoint, zoom);
        }
    }

    @Override
    public void getMapViewRect(MapRect mapRect) {
        mapRect.set(mMapRect);
    }

    private void updateMapRect() {
        MapPoint mapPointLeftTop = viewPoint2MapPoint(new PointF(0, 0));
        MapPoint mapPointRightBtm = viewPoint2MapPoint(new PointF(getWidth(), getHeight()));
        mMapRect.set(mapPointLeftTop.x, mapPointLeftTop.y, mapPointRightBtm.x, mapPointRightBtm.y);
        updateTileDisplayInfo();
    }

    @Override
    public double getMaxResolution() {
        return this.mMaxResolution;
    }

    @Override
    public double getMinResolution() {
        return this.mMinResolution;
    }

    @Override
    public double getResolution() {
        return this.mResolution;
    }

    @Override
    public void setTouchDetector(IMapTouchDetector mapTouchDetector) {
        mMapTouchDetector = mapTouchDetector;
    }

    @Override
    public IMapTouchDetector getTouchDetector() {
        return mMapTouchDetector;
    }

    public IMapTouchDetector getDefaultTouchDetector() {
        return mDefaultMapTouchDetector;
    }

    @Override
    public boolean setCenterBounds(MapRect mapRect) {
        mCenterBounds.set(mapRect);
        mCenterBounds.sort();
        invalidate();
        return limitCenterPoint();
    }

    @Override
    public void getCenterBounds(MapRect mapRect) {
        mapRect.set(mCenterBounds);
    }

    @Override
    public void setProjection(IProjection projection) {
        IProjection old = mProjection;
        mProjection = projection;
        mLayerGroup.onProjectionChanged(this, mProjection, old);
    }

    @Override
    public IProjection getProjection() {
        return mProjection;
    }

    private boolean limitCenterPoint() {
        if (mCenterBounds.isEmpty()) {
            return false;
        }

        boolean isOutOfBounds = false;
        if (mCenterPoint.x < mCenterBounds.left) {
            mCenterPoint.x = mCenterBounds.left;
            isOutOfBounds = true;
        } else if (mCenterPoint.x > mCenterBounds.right) {
            mCenterPoint.x = mCenterBounds.right;
            isOutOfBounds = true;
        }

        if (mCenterPoint.y < mCenterBounds.top) {
            mCenterPoint.y = mCenterBounds.top;
            isOutOfBounds = true;
        } else if (mCenterPoint.y > mCenterBounds.bottom) {
            mCenterPoint.y = mCenterBounds.bottom;
            isOutOfBounds = true;
        }

        return isOutOfBounds;
    }

    @Override
    public boolean offsetCenter(double dx, double dy) {
        this.mCenterPoint.offset(dx, dy);
        boolean isOutOfBound = limitCenterPoint();
        updateMapRect();
        invalidate();
        return isOutOfBound;
    }

    @Override
    public boolean offsetCenterTo(double x, double y) {
        mCenterPoint.set(x, y);
        boolean isOutOfBound = limitCenterPoint();
        updateMapRect();
        invalidate();
        return isOutOfBound;
    }

    @Override
    public boolean setCenterPoint(MapPoint centerPoint) {
        centerPoint.cloneTo(mCenterPoint);
        boolean isOutOfBound = limitCenterPoint();
        updateMapRect();
        invalidate();
        return isOutOfBound;
    }

    @Override
    public void setFocusPoint(MapPoint focusPoint) {
        if (focusPoint == null) {
            this.mFocusPoint = null;
            return;
        }
        this.mFocusPoint = new MapPoint(focusPoint);
        invalidate();
    }

    @Override
    public void setMaxResolution(double maxResolution) {
        this.mMaxResolution = maxResolution;
        invalidate();
    }

    @Override
    public void setMinResolution(double minResolution) {
        this.mMinResolution = minResolution;
        invalidate();
    }

    @Override
    public void setResolution(double resolution) {
        if ((resolution <= getMaxResolution()) && (resolution >= getMinResolution())) {
            this.mResolution = resolution;
            updateMapRect();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTileConfig == null) {
            LogUtil.e(TAG, "ITileConfig is null");
            return;
        }

        if (hasFlag(FLAG_ON_DISPLAY_INFO_CHANGED)) {
            clearFlag(FLAG_ON_DISPLAY_INFO_CHANGED);
            mLayerGroup.onDisplayInfoChanged(this, mTileConfig, mTileDisplayInfo);
        }

        if (mLayerGroup.isVisible()) {
            this.mLayerGroup.draw(canvas, this, mTileConfig, mTileDisplayInfo);
        }

        if (isShowTileInfo()) {
            initTileInfo();
            float imgSizeScale = mTileDisplayInfo.getLevelScale();
            float imgWidth = mTileConfig.getTileWidth() * imgSizeScale;
            float imgHeight = mTileConfig.getTileHeight() * imgSizeScale;
            Tile tile = new Tile();
            for (int i = mTileDisplayInfo.getLeftTopRow(); i <= mTileDisplayInfo.getRightBottomRow(); i++) {
                for (int j = mTileDisplayInfo.getLeftTopCol(); j <= mTileDisplayInfo.getRightBottomCol(); j++) {
                    tile.reset(mTileDisplayInfo.getLevel(), i, j);
                    PointF topLeftPoint = mapPoint2ViewPoint(mTileConfig.getTileLetTopMapPoint(tile));
                    canvas.drawRect(topLeftPoint.x, topLeftPoint.y, topLeftPoint.x + imgWidth, topLeftPoint.y + imgHeight, mRectPaint);
                    StaticLayout staticLayout = new StaticLayout("Level=" + tile.level + "\nR=" + tile.row + "\nC=" + tile.col,
                            mTextPaint, (int) imgWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                    int canvasSaveCount = canvas.save();
                    canvas.translate(topLeftPoint.x + imgWidth / 2.0F, topLeftPoint.y + (imgHeight - staticLayout.getHeight()) / 2);
                    staticLayout.draw(canvas);
                    canvas.restore();
                    canvas.restoreToCount(canvasSaveCount);
                }
            }
        }

        if (TiledMapView.sDebugMode) {
            if (mPaintDebug == null) {
                mPaintDebug = new Paint();
            }
            mPaintDebug.setStyle(Paint.Style.FILL);
            mPaintDebug.setColor(Color.BLUE);
            PointF center = mapPoint2ViewPoint(getCenterPoint());
            canvas.drawCircle(center.x, center.y, 20, mPaintDebug);
            mPaintDebug.setColor(Color.CYAN);
            PointF focus = mapPoint2ViewPoint(getFocusCenter());
            canvas.drawCircle(focus.x, focus.y, 20, mPaintDebug);
            mPaintDebug.setStyle(Paint.Style.STROKE);
            mPaintDebug.setStrokeWidth(10);
            mPaintDebug.setColor(Color.RED);
            PointF pointF = mapPoint2ViewPoint(new MapPoint(mMapRect.left, mMapRect.top));
            PointF pointF2 = mapPoint2ViewPoint(new MapPoint(mMapRect.right, mMapRect.bottom));
            canvas.drawRect(pointF.x, pointF.y, pointF2.x, pointF2.y, mPaintDebug);
        }
    }


    private void initTileInfo() {
        if (mRectPaint != null) {
            return;
        }

        mRectPaint = new Paint();
        mRectPaint.setARGB(255, 255, 0, 0);
        mRectPaint.setStrokeWidth(3.0F);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(28);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mMapTouchDetector != null) {
            return mMapTouchDetector.onTouchEvent(ev);
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public MapPoint viewPoint2MapPoint(PointF point) {
        MapPoint mapPoint = new MapPoint();
        mapPoint.x = this.mCenterPoint.x + (point.x - getWidth() / 2.0F) * getResolution();
        mapPoint.y = this.mCenterPoint.y + (getHeight() / 2.0F - point.y) * getResolution();
        return mapPoint;
    }

    @Override
    public PointF mapPoint2ViewPoint(MapPoint mapPoint) {
        PointF localPointF = new PointF();
        localPointF.x = ((float) ((mapPoint.x - this.mCenterPoint.x) / getResolution()) + getWidth() / 2.0F);
        localPointF.y = (-(float) ((mapPoint.y - this.mCenterPoint.y) / getResolution()) + getHeight() / 2.0F);
        return localPointF;
    }

    @Override
    public MapPoint lngLat2MapPoint(LngLat lngLat) {
        if (mProjection == null) {
            return new MapPoint(lngLat.longitude, lngLat.latitude);
        }

        return mProjection.lngLat2MapPoint(lngLat);
    }

    @Override
    public Tile mapPoint2Tile(MapPoint mapPoint, int level) {
        return mTileConfig.mapPoint2Tile(mapPoint, level);
    }

    @Override
    public ITileConfig getTileConfig() {
        return this.mTileConfig;
    }

    private void updateTileDisplayInfo() {
        double resolution = getResolution();
        int level = getTileConfig().findOptimalLevelByResolution(resolution);
        mTileDisplayInfo.level = level;
        mTileDisplayInfo.levelScale = (float) (mTileConfig.getResolution(level) / getResolution());

        getMapViewRect(mTempMapRect);
        Tile tileTopLeft = mapPoint2Tile(new MapPoint(mTempMapRect.left, mTempMapRect.top), level);
        Tile tileBottomRight = mapPoint2Tile(new MapPoint(mTempMapRect.right, mTempMapRect.bottom), level);
        int row1 = Math.max(tileTopLeft.row, getTileConfig().getMinTileRowIndex(level));
        int col1 = Math.max(tileTopLeft.col, getTileConfig().getMinTileColIndex(level));
        int row2 = Math.min(tileBottomRight.row + 1, getTileConfig().getMaxTileRowIndex(level));
        int col2 = Math.min(tileBottomRight.col + 1, getTileConfig().getMaxTileColIndex(level));
        mTileDisplayInfo.topLeftRow = row1;
        mTileDisplayInfo.topLeftCol = col1;
        mTileDisplayInfo.rightBottomRow = row2;
        mTileDisplayInfo.rightBottomCol = col2;

        addFlag(FLAG_ON_DISPLAY_INFO_CHANGED);
        invalidate();
    }

    private boolean hasFlag(int flag) {
        return (mFlags & flag) != 0;
    }

    private void addFlag(int flag) {
        mFlags = mFlags | flag;
    }

    private void clearFlag(int flag) {
        mFlags = mFlags & ~flag;
    }

    @Override
    public boolean isShowTileInfo() {
        return this.mIsShowTileInfo;
    }

    @Override
    public void setShowTileInfo(boolean showGridInfo) {
        this.mIsShowTileInfo = showGridInfo;
        invalidate();
    }

    @Override
    public void setTileConfig(ITileConfig tileConfig) {
        this.mTileConfig = tileConfig;
        mTileConfig.config(this);
        limitCenterPoint();
        updateMapRect();
        invalidate();
    }

    @Override
    public ITileDisplayInfo getTileDisplayInfo() {
        return mTileDisplayInfo;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLayerGroup.onAttachedToView();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mZoomAnimator != null && mZoomAnimator.isRunning()) {
            mZoomAnimator.cancel();
        }

        mLayerGroup.onDetachedFromView();
    }

    @Override
    public void refreshItself(ILayer layer) {
        refresh();
    }

    private static class InnerTileDisplayInfo implements ITileDisplayInfo {
        int level;
        float levelScale;
        int topLeftRow;
        int topLeftCol;
        int rightBottomRow;
        int rightBottomCol;

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public float getLevelScale() {
            return levelScale;
        }

        @Override
        public int getLeftTopRow() {
            return topLeftRow;
        }

        @Override
        public int getLeftTopCol() {
            return topLeftCol;
        }

        @Override
        public int getRightBottomRow() {
            return rightBottomRow;
        }

        @Override
        public int getRightBottomCol() {
            return rightBottomCol;
        }
    }

    /**
     * if you open the debug mode , you can see the important drawing details
     *
     * @param debugMode
     */
    public static void setDebugMode(boolean debugMode) {
        sDebugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return sDebugMode;
    }

    public static void openLog(boolean open) {
        LogUtil.sIsLog = open;
    }

    public static boolean isLogOpened() {
        return LogUtil.sIsLog;
    }
}


