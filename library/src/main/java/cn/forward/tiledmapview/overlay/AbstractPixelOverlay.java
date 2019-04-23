package cn.forward.tiledmapview.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import cn.forward.tiledmapview.TiledMapView;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileDisplayInfo;

/**
 * Overlay on the pixel (such as the window), relative to pixel coordinates in size.
 * 像素坐标系上的覆盖物，如窗口，相对于像素坐标的大小
 */
public abstract class AbstractPixelOverlay extends AbstractOverlay {

    private PointF mLocationOnView;

    // debug
    private Paint mPaintDebug;

    public AbstractPixelOverlay(double width, double height, int gravity) {
        super(width, height, gravity);
    }

    @CallSuper
    @Override
    public void onPropertiesChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        super.onPropertiesChanged(mapView, tileConfig, tileDisplayInfo);
        mLocationOnView = mapView.mapPoint2ViewPoint(getLocationOnMap());
        mLocationOnView.offset((float) (getLeftOffset() + getGravityLeftOffset()), (float) (getTopOffset() + getGravityTopOffset()));
    }

    public boolean intersects(float left0, float top0, float right0, float bottom0,
                              float left, float top, float right, float bottom) {
        return left0 < right && left < right0
                && top0 < bottom && top < bottom0;
    }

    @Override
    public final void prepareDraw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        if (intersects(0, 0, mapView.getViewWidth(), mapView.getViewHeight(),
                mLocationOnView.x, mLocationOnView.y, (float) (mLocationOnView.x + getWidth()), (float) (mLocationOnView.y + getHeight()))) {
            int saveCount = canvas.save();

            canvas.translate(mLocationOnView.x, mLocationOnView.y);
            canvas.clipRect(0, 0, (float) (getWidth()), (float) (getHeight()));
            doDraw(canvas, mapView, tileConfig, tileDisplayInfo);

            if (TiledMapView.isDebugMode()) {
                if (mPaintDebug == null) {
                    mPaintDebug = new Paint();
                }
                canvas.drawColor(0x44ff0000);

                mPaintDebug.setStyle(Paint.Style.FILL);
                mPaintDebug.setColor(Color.GRAY);
                canvas.drawCircle(0, 0, 30, mPaintDebug);
            }

            canvas.restoreToCount(saveCount);


            if (TiledMapView.isDebugMode()) {
                if (mPaintDebug == null) {
                    mPaintDebug = new Paint();
                }
                mPaintDebug.setStyle(Paint.Style.STROKE);
                mPaintDebug.setStrokeWidth(35);
                mPaintDebug.setColor(0x22ffff00);
                canvas.drawRect(mLocationOnView.x, mLocationOnView.y, (float) (mLocationOnView.x + getWidth()), (float) (mLocationOnView.y + getHeight()), mPaintDebug);
                mPaintDebug.setStyle(Paint.Style.FILL);
                mPaintDebug.setColor(Color.GREEN);
                PointF pointF = mapView.mapPoint2ViewPoint(getLocationOnMap());
                canvas.drawCircle(pointF.x, pointF.y, 10, mPaintDebug);
            }
        }

    }
}


