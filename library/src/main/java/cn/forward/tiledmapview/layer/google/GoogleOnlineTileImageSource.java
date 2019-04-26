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

import java.util.Locale;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

public class GoogleOnlineTileImageSource implements ITileImageSource {

    public enum ImgType {
        SATELLITE("s"), VECTOR_WITH_MARKER("m"), TERRAIN("t"), SATILLITE_WITH_MARKER("y"), MARKER("h"), TERRAIN_WITH_MARKER("p");

        private String mSymbol;

        ImgType(String symbol) {
            mSymbol = symbol;
        }

        @Override
        public String toString() {
            return mSymbol;
        }
    }

    private int mServerId = 0; // 0-3
    private ImgType mImgType;
    private float mScale;
    private String mLanguage;

    public GoogleOnlineTileImageSource() {
        this(ImgType.SATELLITE);
    }

    public GoogleOnlineTileImageSource(ImgType imgType) {
        this(imgType, 1, "zh-CN");
    }

    public GoogleOnlineTileImageSource(ImgType imgType, float scale, String language) {
        mImgType = imgType;
        mScale = scale;
        mLanguage = language;
    }

    public String getUri(Tile tile) {
        String uri = String.format(Locale.getDefault(),
                "https://mt%s.google.cn/maps/vt?lyrs=%s&scale=%s&hl=%s&x=%d&y=%d&z=%d",
                mServerId, mImgType, mScale, mLanguage, tile.col, tile.row, tile.level);
        return uri;
    }

    public void setServerId(int serverId) {
        mServerId = serverId;
    }

    public int getServerId() {
        return mServerId;
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public float getScale() {
        return mScale;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setImgType(ImgType imgType) {
        mImgType = imgType;
    }

    public ImgType getImgType() {
        return mImgType;
    }
}


