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
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.MapRect;
import cn.forward.tiledmapview.projection.LngLatProjection;

public class LngLatProjectionTileConfig extends AbstractTileConfig {

    public static double RESOLUTION_OF_LEVEL1 = 0.70312500015485435; // 级别为1时的分辨率

    private final int mTileImgHeight;
    private final int mTileImgWidth;
    private final int[] mColMaxIndices;
    private final int[] mColMinIndices;
    private final int mMinLevel;
    private final int mMaxLevel;
    private final int mLevelCount; // 级别总数
    private final double[] mResolutions;
    private final int[] mRowMaxIndices; // 每一级别对应的瓦片最小索引
    private final int[] mRowMinIndices; // 每一级别对应的瓦片最大索引
    private final MapPoint[] mOriginTileMapPoints; // 瓦片原点(0,0)对应的世界坐标

    public LngLatProjectionTileConfig(int minLevel, int maxLevel) {

        if (minLevel < 0) {
            throw new IllegalArgumentException("min level must be equal to or greater than 0");
        }

        if (minLevel > maxLevel) {
            throw new IllegalArgumentException("min level must be equal to or smaller than max level.");
        }

        this.mTileImgWidth = 256;
        this.mTileImgHeight = 256;
        this.mMinLevel = minLevel;
        this.mMaxLevel = maxLevel;
        this.mLevelCount = maxLevel - minLevel + 1;

        this.mOriginTileMapPoints = new MapPoint[mLevelCount];
        this.mResolutions = new double[mLevelCount];
        this.mRowMinIndices = new int[mLevelCount];
        this.mRowMaxIndices = new int[mLevelCount];
        this.mColMinIndices = new int[mLevelCount];
        this.mColMaxIndices = new int[mLevelCount];
        for (int level = mMinLevel; level <= mMaxLevel; level++) {
            int index = level - mMinLevel;
            this.mOriginTileMapPoints[index] = new MapPoint(-180, 90);
            this.mResolutions[index] = RESOLUTION_OF_LEVEL1 / Math.pow(2, level - 1);
            this.mRowMinIndices[index] = 0;
            this.mColMinIndices[index] = 0;
            this.mRowMaxIndices[index] = (int) Math.pow(2, level - 1) - 1;
            this.mColMaxIndices[index] = (int) Math.pow(2, level - 1) * 2 - 1;
        }

    }


    @Override
    public int getTileWidth() {
        return mTileImgWidth;
    }

    @Override
    public int getTileHeight() {
        return mTileImgHeight;
    }

    @Override
    public double getResolution(int level) {
        return this.mResolutions[level - mMinLevel];
    }

    @Override
    public int getMaxTileRowIndex(int level) {
        return mRowMaxIndices[level - mMinLevel];
    }

    @Override
    public int getMaxTileColIndex(int level) {
        return mColMaxIndices[level - mMinLevel];
    }

    @Override
    public int getMinTileRowIndex(int level) {
        return mRowMinIndices[level - mMinLevel];
    }

    @Override
    public int getMinTileColIndex(int level) {
        return mColMinIndices[level - mMinLevel];
    }

    @Override
    public int getLevelCount() {
        return mLevelCount;
    }

    @Override
    public int getMinLevel() {
        return mMinLevel;
    }

    @Override
    public int getMaxLevel() {
        return mMaxLevel;
    }

    @Override
    public MapPoint getOriginTileMapPoint(int level) {
        return mOriginTileMapPoints[level - mMinLevel];
    }

    @Override
    public void config(ITiledMapView mapView) {
        ITileConfig tileConfig = mapView.getTileConfig();

        mapView.setMaxResolution(tileConfig.getResolution(tileConfig.getMinLevel()));
        // 最小分辨率为最大level相应分辨率的1/4
        mapView.setMinResolution(tileConfig.getResolution(tileConfig.getMaxLevel()) / 4);

        mapView.setCenterPoint(new MapPoint(0, 0));
        mapView.setResolution(mapView.getMaxResolution());
        mapView.setCenterBounds(new MapRect(-180, 90, 180, -90));
        IProjection projection = new LngLatProjection();
        mapView.setProjection(projection);

    }
}


