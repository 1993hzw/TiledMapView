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

public interface ITileConfig {
    public int getTileWidth();

    public int getTileHeight();

    public double getResolution(int level);

    /**
     * 获取指定分辨率下的级别
     * @param resolution
     * @return
     */
    public int findOptimalLevelByResolution(double resolution);

    public int getMaxTileRowIndex(int level);

    public int getMaxTileColIndex(int level);

    public int getMinTileRowIndex(int level);

    public int getMinTileColIndex(int level);

    public int getLevelCount();

    public int getMinLevel();

    public int getMaxLevel();

    public MapPoint getOriginTileMapPoint(int level);

    public Tile mapPoint2Tile(MapPoint mapPoint, int level);

    public MapPoint getTileRightBottomMapPoint(Tile tile);

    public MapPoint getTileCenterMapPoint(Tile tile);

    public MapPoint getTileLeftTopMapPoint(Tile tile);

    public void config(ITiledMapView mapView);

}
