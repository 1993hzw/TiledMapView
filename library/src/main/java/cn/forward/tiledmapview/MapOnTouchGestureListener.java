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

import android.graphics.PointF;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.util.ScaleGestureDetectorApi27;
import cn.forward.tiledmapview.util.TouchGestureDetector;

/**
 * @author ziwei huang
 */
public class MapOnTouchGestureListener extends TouchGestureDetector.OnTouchGestureListener {

    private ITiledMapView mMapView;

    private Scroller mScroller;
    private Runnable mFling;
    private Handler mFlingHandler;
    private long mLastFlingTime = 0;
    private double mLastFlingX, mLastFlingY;
    private int mMinFlingVelocity;

    public MapOnTouchGestureListener(ITiledMapView mapView) {
        mMapView = mapView;
        mFlingHandler = new Handler();
        mScroller = new Scroller(mapView.getContext());
        mMinFlingVelocity = ViewConfiguration.get(mapView.getContext()).getScaledMinimumFlingVelocity();
        mFling = new Runnable() {
            public void run() {
                if (mScroller.computeScrollOffset()) {
                    if (Math.abs(mScroller.getCurrVelocity()) < mMinFlingVelocity) {
                        mScroller.forceFinished(true);
                        return;
                    }

                    float curr = mScroller.getCurrX();
                    float currY = mScroller.getCurrY();
                    if (mMapView.offsetCenter((curr - mLastFlingX) * mMapView.getResolution(), (currY - mLastFlingY) * mMapView.getResolution())) {
                        mScroller.forceFinished(true);
                    } else {
                        long pass = SystemClock.elapsedRealtime() - mLastFlingTime;
                        mFlingHandler.postDelayed(mFling, 8 - pass);
                    }
                    mLastFlingX = curr;
                    mLastFlingY = currY;
                    mLastFlingTime = SystemClock.elapsedRealtime();
                }
            }
        };
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mScroller.forceFinished(true);
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.mLastFlingX = 0;
        this.mLastFlingY = 0;
        mScroller.fling(0, 0, -(int) velocityX, (int) velocityY, -mMapView.getViewWidth(), mMapView.getViewHeight(), -mMapView.getViewWidth(), mMapView.getViewHeight());
        this.mFlingHandler.post(mFling);
        mLastFlingTime = SystemClock.elapsedRealtime();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        zoom(new PointF(event.getX(), event.getY()), 2, true);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetectorApi27 detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetectorApi27 detector) {
        zoom(new PointF(detector.getFocusX(), detector.getFocusY()), detector.getScaleFactor(), false);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float dx, float dy) {
        mMapView.offsetCenter(dx * mMapView.getResolution(), -dy * mMapView.getResolution());
        return true;
    }

    public void zoom(final PointF pointF, double scale, boolean anim) {
        mMapView.zoomOnFocus(mMapView.viewPoint2MapPoint(pointF), scale, anim);
    }
}
