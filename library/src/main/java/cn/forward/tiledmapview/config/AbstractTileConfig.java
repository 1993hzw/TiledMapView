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
 
package cn.forward.tiledmapview.config;

import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.Tile;

public abstract class AbstractTileConfig implements ITileConfig {

    public boolean isValidLevel(int level) {
        return level >= getMinLevel() && level <= getMaxLevel();
    }

    public Tile mapPoint2Tile(MapPoint mapPoint, int level) {
        int i = (int) ((mapPoint.x - getOriginTileMapPoint(level).x) / getResolution(level) / getTileWidth());
        return new Tile(level, (int) ((getOriginTileMapPoint(level).y - mapPoint.y) / getResolution(level) / getTileHeight()), i);
    }

    public MapPoint getTileRightBottomMapPoint(Tile tile) {
        return new MapPoint((tile.col + 1) * getResolution(tile.level) * getTileWidth() + getOriginTileMapPoint(tile.level).x,
                -(tile.row + 1) * getResolution(tile.level) * getTileHeight() + getOriginTileMapPoint(tile.level).y);
    }

    public MapPoint getTileCenterMapPoint(Tile tile) {
        return new MapPoint((tile.col + 0.5D) * getResolution(tile.level) * getTileWidth() + getOriginTileMapPoint(tile.level).x,
                -(tile.row + 0.5D) * getResolution(tile.level) * getTileHeight() + getOriginTileMapPoint(tile.level).y);
    }

    public MapPoint getTileLeftTopMapPoint(Tile tile) {
        return new MapPoint(tile.col * getResolution(tile.level) * getTileWidth() + getOriginTileMapPoint(tile.level).x,
                -tile.row * getResolution(tile.level) * getTileHeight() + getOriginTileMapPoint(tile.level).y);
    }

    /**
     * 根据分辨率获取最合适的level.
     * (以最接近的level分辨率为准)
     *
     * @param resolution
     * @return
     */
    @Override
    public int findOptimalLevelByResolution(double resolution) {
        double diff = Math.abs(getResolution(getMinLevel()) - resolution);
        int level = getMinLevel();
        for (int i = getMinLevel() + 1; i <= getMaxLevel(); i++) {
            double diff2 = Math.abs(getResolution(i) - resolution);
            if (diff2 < diff) {
                diff = diff2;
                level = i;
            }
        }
        return level;
    }
}
