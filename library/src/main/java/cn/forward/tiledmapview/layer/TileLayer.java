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

package cn.forward.tiledmapview.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import cn.forward.tiledmapview.AbstractLayer;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.core.ITileLayer;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.Tile;
import cn.forward.tiledmapview.util.LogUtil;
import cn.forward.tiledmapview.util.ObjectRecycler;

public class TileLayer extends AbstractLayer implements ITileLayer, ITileImageCache.ILoaderCallback {

    public static final String TAG = "TileLayer";

    private ITiledMapView mTiledMapView;
    private ITileImageCache mTileImageCache = null;
    private RectF mTempRect = new RectF();
    private ObjectRecycler<TileWithDist> mTilesRecycler;
    private int mOffscreenTileLimit = 0;
    private TileWithDist[] mTiles;
    private Tile mFocusTile;

    // Load in the order of divergence with focus as the center
    // 以焦点为中心按发散状顺序加载
    private Comparator<TileWithDist> mTileComparable = new Comparator<TileWithDist>() {
        public int compare(TileWithDist tile1, TileWithDist tile2) {
            int d1 = tile1.getDistance();
            if (tile1.isNoDistance()) {
                d1 = (tile1.row - mFocusTile.row) * (tile1.row - mFocusTile.row) + (tile1.col - mFocusTile.col) * (tile1.col - mFocusTile.col);
                tile1.setDistance(d1);
            }
            int d2 = tile2.getDistance();
            if (tile2.isNoDistance()) {
                d2 = (tile2.row - mFocusTile.row) * (tile2.row - mFocusTile.row) + (tile2.col - mFocusTile.col) * (tile2.col - mFocusTile.col);
                tile2.setDistance(d2);
            }
            return compare(d1, d2);
        }

        int compare(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    };

    public TileLayer() {
    }

    public TileLayer(ITiledMapView mapView, ITileImageCache tileImageCache) {
        initialize(mapView, tileImageCache);
    }

    protected void initialize(ITiledMapView mapView, ITileImageCache tileImageCache) {
        mTiledMapView = mapView;
        this.mTileImageCache = tileImageCache;

        mTilesRecycler = new ObjectRecycler<>(new ObjectRecycler.ObjectGenerator<TileWithDist>() {
            @Override
            public TileWithDist generate() {
                return new TileWithDist();
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
            LogUtil.d(TAG, "titleCount <= 0.");
            return;
        }

        if (LogUtil.sIsLog) {
            LogUtil.d(TAG, String.format(Locale.getDefault(), "onDisplayInfoChanged: tiles count=%d index=[%d,%d-%d,%d] offsetScreenLimit=%d",
                    titleCount, leftTopRow, leftTopCol, rightBottomRow, rightBottomCol, getOffscreenTileLimit()));
        }

        resizeRecycler(tileRowCount, tileColCount);

        mTiles = new TileWithDist[titleCount];

        int tileId = 0;
        for (int i = leftTopRow; i <= rightBottomRow; i++) {
            for (int j = leftTopCol; j <= rightBottomCol; j++) {
                mTiles[tileId] = mTilesRecycler.get(i, j);
                mTiles[tileId].reset(tileDisplayInfo.getLevel(), i, j);
                tileId++;
            }
        }

        mFocusTile = tileConfig.mapPoint2Tile(mapView.getFocusCenter(), tileDisplayInfo.getLevel());
        Arrays.sort(mTiles, mTileComparable);

        for (int i = 0; i < mTiles.length; i++) { // request bitmap
            getTileImageCache().requestTileBitmap(mapView, mTiles[i], this);
        }
    }

    @Override
    public void draw(Canvas canvas, final ITiledMapView mapView, final ITileConfig tileConfig, final ITileDisplayInfo tileDisplayInfo) {
        if ((getTileImageCache() == null)) {
            return;
        }

        if (mTiles == null) {
            LogUtil.d(TAG, "mTiles is null, Stop drawing.");
            return;
        }

        float imgSizeScale = tileDisplayInfo.getLevelScale();
        float imgWidth = tileConfig.getTileWidth() * imgSizeScale;
        float imgHeight = tileConfig.getTileHeight() * imgSizeScale;

        for (int i = 0; i < mTiles.length; i++) {
            Tile tile = mTiles[i];
            // is visible to user?
            if (tile.row < tileDisplayInfo.getLeftTopRow() || tile.row > tileDisplayInfo.getRightBottomRow()) { // skip drawing
                continue;
            }
            if (tile.col < tileDisplayInfo.getLeftTopCol() || tile.col > tileDisplayInfo.getRightBottomCol()) { // skip drawing
                continue;
            }

            Bitmap bitmap = getTileImageCache().getTileBitmap(mapView, tile);
            PointF topLeftPoint = mapView.mapPoint2ViewPoint(tileConfig.getTileLeftTopMapPoint(tile));
            mTempRect.set(topLeftPoint.x, topLeftPoint.y, topLeftPoint.x + imgWidth + 0.5F, topLeftPoint.y + imgHeight + 0.5F);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, null, mTempRect, null);
            } else if (getTileImageCache().getPlaceHolder() != null) {
                canvas.drawBitmap(getTileImageCache().getPlaceHolder(), null, mTempRect, null);
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

    /**
     * Set the number of tiles that should be retained to either side of the current screen.
     * 设置应保留到当前屏幕任一侧的切片数量.
     */
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

    @Override
    public void onLoaded(Bitmap bitmap) {
        mTiledMapView.refresh();
    }

    @Override
    public void onFailed(int reason) {

    }

    private static class TileWithDist extends Tile {
        private int mDistance = Integer.MAX_VALUE;

        public void setDistance(int distance) {
            mDistance = distance;
        }

        public int getDistance() {
            return mDistance;
        }

        @Override
        public void reset(int level, int row, int col) {
            super.reset(level, row, col);
            mDistance = Integer.MAX_VALUE;
        }

        public boolean isNoDistance() {
            return mDistance == Integer.MAX_VALUE;
        }
    }
}


