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

package cn.forward.tiledmapview.layer.google;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.forward.tiledmapview.config.WebMercatorTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;
import cn.forward.tiledmapview.projection.WebMercatorProjection;
import cn.forward.tiledmapview.projection.Wgs84ToGcj02Transformation;

/**
 * @author ziwei huang
 */
public class GoogleTileLayer extends TileLayer {

    public GoogleTileLayer(ITiledMapView mapView, GoogleOnlineTileImageSource.ImgType imgType) {
        this(mapView, imgType, 2f, "zh-CN", new PicassoTileImageLoader());
    }

    public GoogleTileLayer(ITiledMapView mapView, GoogleOnlineTileImageSource.ImgType imgType, float scale, String language, TileImageCache.ITileImageLoader imageLoader) {
        Bitmap placeHolder = BitmapFactory.decodeResource(mapView.getContext().getResources(), cn.forward.tiledmapview.R.drawable.grid);
        initialize(mapView.getContext(), new TileImageCache(mapView, new GoogleOnlineTileImageSource(imgType, scale, language), imageLoader, placeHolder));

        mapView.setTileConfig(new WebMercatorTileConfig(0, 17, scale));
        IProjection projection = new WebMercatorProjection(new Wgs84ToGcj02Transformation()); // 由于tile url有gl=CN参数，故需进行Wgs84ToGcj02Transformation
        mapView.setProjection(projection);
    }

}
