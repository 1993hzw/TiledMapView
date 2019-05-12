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

import android.graphics.Bitmap;

import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileDisplayInfo;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.Tile;
import cn.forward.tiledmapview.util.LogUtil;
import cn.forward.tiledmapview.util.TileObjectRecycler;

public class TileImageCache implements ITileImageCache {
    public static final String TAG = "TileImageCache";

    private ITiledMapView mMapView;
    private ITileImageSource mTileImageSource = null;
    private ITileImageLoader mImageLoader;
    private TileObjectRecycler<TileImage> mTileImagesRecycler;
    private Bitmap mPlaceHolder;

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource) {
        this(mapView, tileImageSource, new GlideTileImageLoader(mapView.getContext()));
    }

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource, ITileImageLoader imageLoader) {
        this(mapView, tileImageSource, imageLoader, null);
    }

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource, ITileImageLoader imageLoader, Bitmap placeHolder) {
        this.mTileImageSource = tileImageSource;
        this.mMapView = mapView;
        mImageLoader = imageLoader;

        mTileImagesRecycler = new TileObjectRecycler<>(new TileObjectRecycler.ObjectGenerator<TileImage>() {
            @Override
            public TileImage generate() {
                return new TileImage(mImageLoader);
            }
        });

        mPlaceHolder = placeHolder;
    }

    public ITileImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public void setPlaceHolder(Bitmap placeHolder) {
        mPlaceHolder = placeHolder;
        mMapView.refresh();
    }

    @Override
    public Bitmap getPlaceHolder() {
        return mPlaceHolder;
    }

    @Override
    public void resize(ITileDisplayInfo tileDisplayInfo, int rowCount, int colCount) {
        mTileImagesRecycler.resize(tileDisplayInfo, rowCount, colCount);
    }

    @Override
    public Bitmap getTileBitmap(ITiledMapView mapView, Tile tile) {
        TileImage tileImage = getTileImage(tile);
        String uri = null;
        if (tile instanceof OptimizedTile) {
            uri = ((OptimizedTile) tile).getUri();
        }
        if (uri == null) {
            uri = mTileImageSource.getUri(tile);
        }
        Bitmap bitmap = tileImage.getTile(uri);
        return bitmap;
    }

    @Override
    public void requestTileBitmap(ITiledMapView mapView, Tile tile, ILoaderCallback callback) {
        TileImage tileImage = getTileImage(tile);
        String uri = null;
        if (tile instanceof OptimizedTile) {
            uri = ((OptimizedTile) tile).getUri();
        }
        if (uri == null) {
            uri = mTileImageSource.getUri(tile);
        }
        tileImage.requestTile(mapView.getTileConfig(), uri, callback);
    }

    private TileImage getTileImage(Tile tile) {
        return mTileImagesRecycler.get(tile.level, tile.row, tile.col);
    }

    @Override
    public void clear() {
        mImageLoader.cancelByTag(mImageLoader.toString());
    }

    @Override
    public ITileImageSource getTileImageSource() {
        return this.mTileImageSource;
    }

    @Override
    public void setTileImageSource(ITileImageSource tileImageSource) {
        this.mTileImageSource = tileImageSource;
    }

    private static class TileImage implements ILoaderCallback {
        private final ITileImageLoader mImageLoader;
        private InnerCallback mTarget;
        private Bitmap mBitmap;
        private String mUri;
        private ILoaderCallback mLoaderCallback;

        public TileImage(ITileImageLoader imageLoader) {
            mImageLoader = imageLoader;
        }

        public boolean isSameTask(String uri) {
            return uri.equals(mUri);
        }

        public Bitmap getTile(String uri) {
            if (isSameTask(uri)) {
                return mBitmap;
            }
            return null;
        }

        public void requestTile(ITileConfig tileConfig, String uri, ILoaderCallback callback) {
            Bitmap bitmap = getTile(uri);
            if (bitmap != null) { // loaded
                if (callback != null) {
                    callback.onLoaded(bitmap);
                }
                mLoaderCallback = null;
                return;
            }

            if (isSameTask(uri)) {
                if (mTarget != null && mTarget.isLoading) {
                    return;
                }
            }

            mUri = uri;
            mLoaderCallback = callback;
            mBitmap = null;

            if (mTarget != null) { // cancel last request
                mTarget.isLoading = false;
                mImageLoader.cancel(mTarget);
            }
            mTarget = new InnerCallback(this);
            if (LogUtil.sIsLog) {
                LogUtil.d(TAG, "request:" + mUri);
            }
            mImageLoader.request(tileConfig, mUri, mImageLoader.toString(), mTarget);
        }

        public String getUri() {
            return mUri;
        }

        @Override
        public void onLoaded(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mLoaderCallback != null) {
                mLoaderCallback.onLoaded(bitmap);
                mLoaderCallback = null;
            }
        }

        @Override
        public void onFailed(String msg) {
            if (mLoaderCallback != null) {
                mLoaderCallback.onFailed(msg);
                mLoaderCallback = null;
            }
        }
    }

    private static class InnerCallback implements ILoaderCallback {
        TileImage mTileImage;
        String nUri;

        boolean isLoading = true;

        public InnerCallback(TileImage tileImage) {
            mTileImage = tileImage;
            nUri = tileImage.getUri();
        }

        private boolean isValid() {
            return mTileImage.getUri().equals(nUri);
        }

        public void onLoaded(Bitmap bitmap) {
            if (!isLoading) {
                return; // has been canceled
            }

            isLoading = false;
            if (!isValid()) {
                return;
            }
            mTileImage.onLoaded(bitmap);
        }

        @Override
        public void onFailed(String msg) {
            isLoading = false;
            if (!isValid()) {
                return;
            }
            mTileImage.onFailed(msg);
        }

    }

    public interface ITileImageLoader {
        void request(ITileConfig tileConfig, String uri, String tag, ILoaderCallback callback);

        void cancel(ILoaderCallback callback);

        void cancelByTag(String tag);
    }

}


