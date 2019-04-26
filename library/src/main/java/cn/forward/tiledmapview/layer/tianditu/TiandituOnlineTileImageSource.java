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

import java.util.Locale;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

// http://lbs.tianditu.gov.cn/server/MapService.html
public class TiandituOnlineTileImageSource implements ITileImageSource {

    public enum ProjectionType {
        WEB_MERCATOR("w"), LNG_LAT("c");
        private String mSymbol;

        ProjectionType(String symbol) {
            mSymbol = symbol;
        }

        @Override
        public String toString() {
            return mSymbol;
        }
    }

    public enum ImgType {
        SATELLITE("img"), VECTOR("vec"), TERRAIN("ter"), SATELLITE_ONLY_MARKER("cia"), VECTOR_ONLY_MARKER("cva"), TERRAIN_ONLY_MARKER("cta");

        private String mSymbol;

        ImgType(String symbol) {
            mSymbol = symbol;
        }

        @Override
        public String toString() {
            return mSymbol;
        }
    }

    private int mServerId = 0; // 0-7
    private ImgType mImgType;
    private ProjectionType mProjectionType;
    private String mKey;

    public TiandituOnlineTileImageSource(String key) {
        this(ImgType.SATELLITE, ProjectionType.WEB_MERCATOR, key);
    }

    public TiandituOnlineTileImageSource(ImgType imgType, ProjectionType projectionType, String key) {
        mImgType = imgType;
        mProjectionType = projectionType;
        mKey = key;
    }

    public String getUri(Tile tile) {
        String uri = String.format(Locale.getDefault(),
                "https://t%s.tianditu.gov.cn/%s_%s/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=%s&STYLE=default&TILEMATRIXSET=%s&FORMAT=tiles&TILEMATRIX=%s&TILEROW=%s&TILECOL=%s&tk=%s",
                mServerId, mImgType, mProjectionType, mImgType, mProjectionType, tile.level, tile.row, tile.col, mKey);
        return uri;
    }

    public void setServerId(int serverId) {
        mServerId = serverId;
    }

    public int getServerId() {
        return mServerId;
    }

    public void setImgType(ImgType imgType) {
        mImgType = imgType;
    }

    public ImgType getImgType() {
        return mImgType;
    }

    public void setProjectionType(ProjectionType projectionType) {
        mProjectionType = projectionType;
    }

    public ProjectionType getProjectionType() {
        return mProjectionType;
    }
}
