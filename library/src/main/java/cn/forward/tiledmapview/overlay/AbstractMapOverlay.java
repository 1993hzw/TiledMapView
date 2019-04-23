package cn.forward.tiledmapview.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import cn.forward.tiledmapview.TiledMapView;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.MapRect;

/**
 * Overlay on the map, relative to map coordinates in size.
 * 地图坐标系上的覆盖物，相对于地图坐标的大小
 */
public abstract class AbstractMapOverlay extends AbstractOverlay {

    private MapRect mMapRect = new MapRect();
    private MapRect mBound = new MapRect(0, 0, 0, 0);

    private RectF mBoundOnView = new RectF();
    private float mCanvasScale = 1;

    // debug
    private Paint mPaintDebug;
    private MapRect mBoundRectDebug;

    public AbstractMapOverlay(double width, double height, int gravity) {
        super(width, height, gravity);
    }

    @CallSuper
    @Override
    public void onPropertiesChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        super.onPropertiesChanged(mapView, tileConfig, tileDisplayInfo);
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        MapPoint location = new MapPoint(getLocationOnMap());
        location.offset((float) (getLeftOffset() + getGravityLeftOffset()), -(float) (getTopOffset() + getGravityTopOffset()));

        mapView.getMapViewRect(mMapRect);
        mBound.offsetTo(location.x, location.y);
        mBound.right = mBound.left + getWidth();
        if (mMapRect.top < mMapRect.bottom) {
            mBound.bottom = mBound.top + getHeight();
        } else {
            mBound.bottom = mBound.top - getHeight();
        }

        PointF pointF = mapView.mapPoint2ViewPoint(new MapPoint(mBound.left, mBound.top));
        PointF pointF2 = mapView.mapPoint2ViewPoint(new MapPoint(mBound.right, mBound.bottom));
        mBoundOnView.set(pointF.x, pointF.y, pointF2.x, pointF2.y);
        mCanvasScale = (float) (1 / mapView.getResolution());

        mMapRect.sort(); // must sort
        mBound.sort();

    }

    @Override
    public final void prepareDraw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        if (mMapRect.intersects(mBound)) {
            int saveCount = canvas.save();
            // for preventing loss of accuracy, must translate, then scale
            canvas.clipRect(mBoundOnView);
            canvas.translate(mBoundOnView.left, mBoundOnView.top);
            canvas.scale(mCanvasScale, mCanvasScale, 0, 0);
            doDraw(canvas, mapView, tileConfig, tileDisplayInfo);

            if (TiledMapView.isDebugMode()) {
                if (mPaintDebug == null) {
                    mPaintDebug = new Paint();
                }
                canvas.drawColor(0x44ff0000);

                mPaintDebug.setStyle(Paint.Style.FILL);
                mPaintDebug.setColor(Color.GRAY);
                canvas.drawCircle(0, 0, 30 / mCanvasScale, mPaintDebug);
            }

            canvas.restoreToCount(saveCount);

            if (TiledMapView.isDebugMode()) {
                if (mBoundRectDebug == null) {
                    mBoundRectDebug = new MapRect();
                }
                mBoundRectDebug.set(mBound);

                if (mPaintDebug == null) {
                    mPaintDebug = new Paint();
                }
                mPaintDebug.setStyle(Paint.Style.STROKE);
                mPaintDebug.setStrokeWidth(35);
                mPaintDebug.setColor(0x22ffff00);
                PointF pointF = mapView.mapPoint2ViewPoint(new MapPoint(mBoundRectDebug.left, mBoundRectDebug.top));
                PointF pointF2 = mapView.mapPoint2ViewPoint(new MapPoint(mBoundRectDebug.right, mBoundRectDebug.bottom));
                canvas.drawRect(pointF.x, pointF.y, pointF2.x, pointF2.y, mPaintDebug);
                mPaintDebug.setStyle(Paint.Style.FILL);
                mPaintDebug.setColor(Color.GREEN);
                pointF = mapView.mapPoint2ViewPoint(getLocationOnMap());
                canvas.drawCircle(pointF.x, pointF.y, 10, mPaintDebug);
            }
        }
    }

}


