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

import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.ITileLayer;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.Tile;
import cn.forward.tiledmapview.util.ObjectRecycler;

public class TileImageCache implements ITileImageCache {
    public static final String TAG = "TileImageCache";

    private ITiledMapView mMapView;
    private ITileImageSource mTileImageSource = null;
    private ITileImageLoader mImageLoader;
    private ITileLayer mTileLayer;
    private ObjectRecycler<TileImage> mTileImagesRecycler;
    private Bitmap mPlaceHolder;

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource) {
        this(mapView, tileImageSource, new PicassoTileImageLoader());
    }

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource, ITileImageLoader imageLoader) {
        this(mapView, tileImageSource, imageLoader, null);
    }

    public TileImageCache(final ITiledMapView mapView, ITileImageSource tileImageSource, ITileImageLoader imageLoader, Bitmap placeHolder) {
        this.mTileImageSource = tileImageSource;
        this.mMapView = mapView;
        mImageLoader = imageLoader;

        mTileImagesRecycler = new ObjectRecycler<>(new ObjectRecycler.ObjectGenerator<TileImage>() {
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
    public void resize(int rowCount, int colCount) {
        mTileImagesRecycler.resize(rowCount, colCount);
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
        tileImage.requestTile(uri, callback);
    }

    private TileImage getTileImage(Tile tile) {
        return mTileImagesRecycler.get(tile.row, tile.col);
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

        public Bitmap getTile(String uri) {
            String oldTag = mUri;
            mUri = uri;
            if (mUri.equals(oldTag)) {
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    return mBitmap;
                }

                if (mTarget.isLoading) {
                    return null;
                }
            }

            mBitmap = null;
            return null;
        }

        public void requestTile(String uri, ILoaderCallback callback) {
            mLoaderCallback = null;
            Bitmap bitmap = getTile(uri);
            if (bitmap != null) { // loaded
                if (callback != null) {
                    callback.onLoaded(bitmap);
                }
                return;
            }

            mLoaderCallback = callback;

            if (mTarget != null) { // cancel last doRequest
                mTarget.isLoading = false;
                mImageLoader.cancel(mTarget);
            }
            mTarget = new InnerCallback(this);
            mImageLoader.request(mUri, mImageLoader.toString(), mTarget);
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
        public void onFailed(int reason) {
            if (mLoaderCallback != null) {
                mLoaderCallback.onFailed(reason);
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
        public void onFailed(int reason) {
            isLoading = false;
            if (!isValid()) {
                return;
            }
            mTileImage.onFailed(reason);
        }

    }

    public interface ITileImageLoader {
        void request(String uri, String tag, ILoaderCallback callback);

        void cancel(ILoaderCallback callback);

        void cancelByTag(String tag);
    }

}


