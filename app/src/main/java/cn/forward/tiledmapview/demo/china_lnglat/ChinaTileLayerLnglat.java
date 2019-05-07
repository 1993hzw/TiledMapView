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

package cn.forward.tiledmapview.demo.china_lnglat;

import com.squareup.picasso.Picasso;

import cn.forward.tiledmapview.config.LngLatProjectionTileConfig;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;
import cn.forward.tiledmapview.projection.LngLatProjection;

/**
 * China tiles with Lng/Lat projection
 *
 * @author ziwei huang
 */
public class ChinaTileLayerLnglat extends TileLayer {


    public ChinaTileLayerLnglat(ITiledMapView mapView) {
        initialize(mapView.getContext(), new TileImageCache(mapView, new ChinaTileImageSourceLnglat(),
                new PicassoTileImageLoader(Picasso.Priority.HIGH)));

        mapView.setTileConfig(new LngLatProjectionTileConfig(1, 18));
        IProjection projection = new LngLatProjection();
        mapView.setProjection(projection);
    }

}
