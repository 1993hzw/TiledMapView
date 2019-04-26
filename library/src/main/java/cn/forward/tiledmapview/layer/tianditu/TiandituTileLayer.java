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
 
package cn.forward.tiledmapview.layer.tianditu;

import cn.forward.tiledmapview.config.LngLatProjectionTileConfig;
import cn.forward.tiledmapview.config.WebMercatorTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;
import cn.forward.tiledmapview.projection.LngLatProjection;
import cn.forward.tiledmapview.projection.WebMercatorProjection;

/**
 * @author ziwei huang
 */
public class TiandituTileLayer extends TileLayer {

    public TiandituTileLayer(ITiledMapView mapView, TiandituOnlineTileImageSource.ImgType imgType,
                             TiandituOnlineTileImageSource.ProjectionType projectionType,
                             String key) {
        this(mapView, imgType, projectionType, key, new PicassoTileImageLoader());
    }

    /**
     * @param mapView
     * @param imgType
     * @param projectionType
     * @param key            Using Tianditu API, you need to apply for a key. 使用天地图API，需要申请秘钥
     * @see <a href="https://console.tianditu.gov.cn/api/key">Tianditu API key</a>
     */
    public TiandituTileLayer(ITiledMapView mapView, TiandituOnlineTileImageSource.ImgType imgType,
                             TiandituOnlineTileImageSource.ProjectionType projectionType,
                             String key,
                             TileImageCache.ITileImageLoader tileImageLoader) {
        ITileImageCache imageCache = new TileImageCache(mapView, new TiandituOnlineTileImageSource(imgType, projectionType, key), tileImageLoader);
        initialize(mapView.getContext(), imageCache);

        if (projectionType == TiandituOnlineTileImageSource.ProjectionType.LNG_LAT) {
            mapView.setTileConfig(new LngLatProjectionTileConfig(1, 18));
            IProjection projection = new LngLatProjection();
            mapView.setProjection(projection);
        } else {
            mapView.setTileConfig(new WebMercatorTileConfig(1, 18));
            IProjection projection = new WebMercatorProjection();
            mapView.setProjection(projection);
        }
    }
}
