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
import android.graphics.BitmapFactory;

import cn.forward.tiledmapview.R;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.ITileLayer;
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
        this.mTileImageSource = tileImageSource;
        this.mMapView = mapView;
        mImageLoader = imageLoader;

        mTileImagesRecycler = new ObjectRecycler<>(new ObjectRecycler.ObjectGenerator<TileImage>() {
            @Override
            public TileImage generate() {
                return new TileImage(mapView, mImageLoader);
            }
        });

        mPlaceHolder = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.drawable.grid);
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
    public Bitmap getPlaceHolder(Tile tile, ITiledMapView mapView) {
        return mPlaceHolder;
    }

    @Override
    public void resize(int rowCount, int colCount) {
        mTileImagesRecycler.resize(rowCount, colCount);
    }

    @Override
    public Bitmap getTileBitmap(Tile tile, ITiledMapView mapView) {
        TileImage tileImage = getTileImage(tile);
        Bitmap bitmap = tileImage.requestTile(mTileImageSource.getUri(tile));
        return bitmap;
    }

    private TileImage getTileImage(Tile tile) {
        return mTileImagesRecycler.get(tile.row, tile.col);
    }

    public void clear() {
        mImageLoader.cancelByTag(mImageLoader.toString());
    }

    public ITiledMapView getMapView() {
        return this.mMapView;
    }

    public ITileImageSource getTileImageSource() {
        return this.mTileImageSource;
    }

    public ITileLayer getTileLayer() {
        return this.mTileLayer;
    }

    public void setMapView(ITiledMapView mapView) {
        this.mMapView = mapView;
    }

    public void setTileImageSource(ITileImageSource tileImageSource) {
        this.mTileImageSource = tileImageSource;
    }

    public void setTileLayer(ITileLayer tileLayer) {
        this.mTileLayer = tileLayer;
    }

    private static class TileImage implements ILoaderCallback {
        private ITiledMapView mMapView;
        private ITileImageLoader mImageLoader;
        private InnerCallback mTarget;
        private Bitmap mBitmap;
        private String mUri;

        public TileImage(ITiledMapView mapView, ITileImageLoader imageLoader) {
            mMapView = mapView;
            mImageLoader = imageLoader;
        }

        public Bitmap requestTile(String uri) {
            ITileImageLoader oldImageLoader = mImageLoader;
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
            if (mTarget != null) { // cancel last doRequest
                mTarget.isLoading = false;
                oldImageLoader.cancel(mTarget);
            }
            mTarget = new InnerCallback(this);
            mImageLoader.request(mUri, mImageLoader.toString(), mTarget);

            return mBitmap;
        }

        public String getUri() {
            return mUri;
        }

        @Override
        public void onLoaded(Bitmap bitmap) {
            mBitmap = bitmap;
            mMapView.refresh();
        }

        @Override
        public void onFailed(int reason) {

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

    public interface ILoaderCallback {
        void onLoaded(Bitmap bitmap);

        void onFailed(int reason);
    }
}


