package cn.forward.tiledmapview.demo.china_lnglat;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

//asset resource > file:///android_asset/
public class ChinaTileImageSourceLnglat implements ITileImageSource {

    public String getUri(Tile tile) {
        return  "file:///android_asset/china_lnglat/" + tile.level + "/" + tile.col + "_" + tile.row + ".png";
    }
}


