package cn.forward.tiledmapview.overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.Gravity;

import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;

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
