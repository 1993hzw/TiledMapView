package cn.forward.tiledmapview.demo.lol;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

//asset resource > file:///android_asset/
public class LOLTileImageSource implements ITileImageSource {

    public String getUri(Tile tile) {
//        String uri = String.format(Locale.getDefault(),
//                "file:///android_asset/lol/%d/%d_%d.png", tile.level, tile.col, tile.row);

        return "file:///android_asset/lol/" + tile.level + "/" + tile.col + "_" + tile.row + ".png";
    }
}


