package cn.forward.tiledmapview.demo.lol.contour;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

//asset resource > file:///android_asset/
public class LOLTileImageSourceContour implements ITileImageSource {

    public String getUri(Tile tile) {
//        String uri = String.format(Locale.getDefault(),
//                "file:///android_asset/lol_contour/%d/%d_%d.png", tile.level, tile.col, tile.row);

        // consider performance
        return  "file:///android_asset/lol_contour/" + tile.level + "/" + tile.col + "_" + tile.row + ".png";
    }
}


