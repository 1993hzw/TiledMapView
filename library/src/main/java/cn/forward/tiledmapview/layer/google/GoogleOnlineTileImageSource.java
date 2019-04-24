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


