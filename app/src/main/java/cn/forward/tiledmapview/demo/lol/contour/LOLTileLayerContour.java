package cn.forward.tiledmapview.demo.lol.contour;

import cn.forward.tiledmapview.config.ImageInfo;
import cn.forward.tiledmapview.config.ImageTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.layer.GlideTileImageLoader;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;

/**
 * @author ziwei huang
 */
public class LOLTileLayerContour extends TileLayer {

    public LOLTileLayerContour(ITiledMapView mapView) {
        initialize(mapView, new TileImageCache(mapView, new LOLTileImageSourceContour(),
                new GlideTileImageLoader(mapView.getContext())));

        // The full image size is 12200x10240, which image level is 6.
        mapView.setTileConfig(new ImageTileConfig(new ImageInfo(12200, 10240), 0, 6));

        // You can also use the source image level, but it will fills the remaining map with blank tiles.
        //mapView.setTileConfig(new ImageTileConfig(6, 0, 6));

        setOffscreenTileLimit(1); //  Set the number of tiles that should be retained to either side of the current screen
    }

}
