package cn.forward.tiledmapview.demo.china;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

//asset resource > file:///android_asset/
public class ChinaTileImageSource implements ITileImageSource {

    public String getUri(Tile tile) {
        return  "file:///android_asset/china/" + tile.level + "/" + tile.col + "_" + tile.row + ".png";
    }
}


