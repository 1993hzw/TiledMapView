package cn.forward.tiledmapview.layer.tianditu;

import java.util.Locale;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

//asset resource > file:///android_asset/
//http://lbs.tianditu.gov.cn/server/MapService.html
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

    public TiandituOnlineTileImageSource() {
        this(ImgType.SATELLITE, ProjectionType.WEB_MERCATOR);
    }

    public TiandituOnlineTileImageSource(ImgType imgType, ProjectionType projectionType) {
        mImgType = imgType;
        mProjectionType = projectionType;
    }

    public String getUri(Tile tile) {
        String uri = String.format(Locale.getDefault(),
                "https://t%s.tianditu.gov.cn/%s_%s/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=%s&STYLE=default&TILEMATRIXSET=%s&FORMAT=tiles&TILEMATRIX=%s&TILEROW=%s&TILECOL=%s&tk=%s",
                mServerId, mImgType, mProjectionType, mImgType, mProjectionType, tile.level, tile.row, tile.col, "b34f09c6586e9741629c42f716b7494b");
        return uri;
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
