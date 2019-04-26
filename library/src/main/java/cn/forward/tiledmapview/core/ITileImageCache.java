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
 
package cn.forward.tiledmapview.core;

import android.graphics.Bitmap;

/**
 * @author ziwei huang
 */
public interface ITileImageCache {

    public void setPlaceHolder(Bitmap placeHolder);

    public Bitmap getPlaceHolder(Tile tile, ITiledMapView mapView);

    public void resize(int rowCount, int colCount);

    public Bitmap getTileBitmap(Tile tile, ITiledMapView mapView);

    public void clear();

    public ITiledMapView getMapView();

    public ITileImageSource getTileImageSource();

    public ITileLayer getTileLayer();

    public void setMapView(ITiledMapView mapView);

    public void setTileImageSource(ITileImageSource tileImageSource);

    public void setTileLayer(ITileLayer tileLayer);
}


