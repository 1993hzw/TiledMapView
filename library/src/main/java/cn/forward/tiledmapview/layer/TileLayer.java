package cn.forward.tiledmapview.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cn.forward.tiledmapview.AbstractLayer;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.core.ITileLayer;
import cn.forward.tiledmapview.core.Tile;
import cn.forward.tiledmapview.util.LogUtil;
import cn.forward.tiledmapview.util.ObjectRecycler;

public class TileLayer extends AbstractLayer implements ITileLayer {

    private ITileImageCache mTileImageCache = null;
    private RectF mTempRect = new RectF();
    private ObjectRecycler<Tile> mTilesRecycler;
    private int mOffscreenTileLimit = 1;
    private Tile[] mTiles;

    public TileLayer() {
    }

    public TileLayer(Context context, ITileImageCache tileImageCache) {
        initialize(context, tileImageCache);
    }

    protected void initialize(Context context, ITileImageCache tileImageCache) {
        this.mTileImageCache = tileImageCache;
        this.mTileImageCache.setTileLayer(this);

        mTilesRecycler = new ObjectRecycler<>(new ObjectRecycler.ObjectGenerator<Tile>() {
            @Override
            public Tile generate() {
                return new Tile();
            }
        });
    }

    private void resizeRecycler(int rowCount, int colCount) {
        mTilesRecycler.resize(rowCount, colCount);
        getTileImageCache().resize(rowCount, colCount);
    }

    @Override
    public void clearCache() {
        this.mTileImageCache.clear();
    }


    @Override
    public void onDisplayInfoChanged(@NonNull final ITiledMapView mapView, @NonNull final ITileConfig tileConfig, @NonNull final ITileDisplayInfo tileDisplayInfo) {

        int leftTopRow = Math.max(tileConfig.getMinTileRowIndex(tileDisplayInfo.getLevel()), tileDisplayInfo.getLeftTopRow() - mOffscreenTileLimit);
        int leftTopCol = Math.max(tileConfig.getMinTileColIndex(tileDisplayInfo.getLevel()), tileDisplayInfo.getLeftTopCol() - mOffscreenTileLimit);
        int rightBottomRow = Math.min(tileConfig.getMaxTileRowIndex(tileDisplayInfo.getLevel()), tileDisplayInfo.getRightBottomRow() + mOffscreenTileLimit);
        int rightBottomCol = Math.min(tileConfig.getMaxTileColIndex(tileDisplayInfo.getLevel()), tileDisplayInfo.getRightBottomCol() + mOffscreenTileLimit);

        int tileRowCount = (rightBottomRow - leftTopRow + 1);
        int tileColCount = (rightBottomCol - leftTopCol + 1);
        int titleCount = tileRowCount * tileColCount;
        if (tileRowCount <= 0 || tileColCount <= 0) {
            LogUtil.d("Tile", "titleCount <= 0.");
            return;
        }

        resizeRecycler(tileRowCount, tileColCount);

        mTiles = new Tile[titleCount];

        int tileId = 0;
        for (int i = leftTopRow; i <= rightBottomRow; i++) {
            for (int j = leftTopCol; j <= rightBottomCol; j++) {
                mTiles[tileId] = mTilesRecycler.get(i, j);
                mTiles[tileId].reset(tileDisplayInfo.getLevel(), i, j);
                tileId++;
            }
        }

        // 以焦点为中心按发散状顺序加载
        Arrays.sort(mTiles, new Comparator<Tile>() {
            Map<Tile, Integer> distMap = new HashMap<>();
            Tile focusTile = tileConfig.mapPoint2Tile(mapView.getFocusCenter(), tileDisplayInfo.getLevel());

            public int compare(Tile tile1, Tile tile2) {
                Integer d1 = distMap.get(tile1);
                if (d1 == null) {
                    d1 = (tile1.row - focusTile.row) * (tile1.row - focusTile.row) + (tile1.col - focusTile.col) * (tile1.col - focusTile.col);
                    distMap.put(tile1, d1);
                }
                Integer d2 = distMap.get(tile2);
                if (d2 == null) {
                    d2 = (tile2.row - focusTile.row) * (tile2.row - focusTile.row) + (tile2.col - focusTile.col) * (tile2.col - focusTile.col);
                    distMap.put(tile2, d2);
                }
                return d1.compareTo(d2);
            }
        });

    }

    @Override
    public void draw(Canvas canvas, final ITiledMapView mapView, final ITileConfig tileConfig, final ITileDisplayInfo tileDisplayInfo) {
        if ((getTileImageCache() == null)) {
            return;
        }

        if (mTiles == null) {
            LogUtil.d("Tile", "mTiles is null, Stop drawing.");
            return;
        }

        float imgSizeScale = tileDisplayInfo.getLevelScale();
        float imgWidth = tileConfig.getTileWidth() * imgSizeScale;
        float imgHeight = tileConfig.getTileHeight() * imgSizeScale;

        for (int i = 0; i < mTiles.length; i++) {
            Tile tile = mTiles[i];
            PointF topLeftPoint = mapView.mapPoint2ViewPoint(tileConfig.getTileLetTopMapPoint(tile));
            Bitmap bitmap = getTileImageCache().getTileBitmap(tile, mapView);
            mTempRect.set(topLeftPoint.x, topLeftPoint.y, topLeftPoint.x + imgWidth + 0.5F, topLeftPoint.y + imgHeight + 0.5F);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, null, mTempRect, null);
            } else {
                canvas.drawBitmap(getTileImageCache().getPlaceHolder(tile, mapView), null, mTempRect, null);
            }
        }
    }

    @Override
    public ITileImageCache getTileImageCache() {
        return this.mTileImageCache;
    }

    @Override
    public void setTileImageCache(ITileImageCache tileImageCache) {
        this.mTileImageCache = tileImageCache;
    }

    @Override
    public void setOffscreenTileLimit(int limit) {
        mOffscreenTileLimit = Math.max(0, limit);
    }

    @Override
    public int getOffscreenTileLimit() {
        return mOffscreenTileLimit;
    }

    @Override
    public void onDetachedFromView() {
        super.onDetachedFromView();

        if (getTileImageCache() != null) {
            getTileImageCache().clear();
        }
    }

}


