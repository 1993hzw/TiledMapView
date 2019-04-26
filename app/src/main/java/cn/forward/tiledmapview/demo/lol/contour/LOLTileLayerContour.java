package cn.forward.tiledmapview.demo.lol.contour;

import cn.forward.tiledmapview.config.ImageInfo;
import cn.forward.tiledmapview.config.ImageTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;

/**
 * @author ziwei huang
 */
public class LOLTileLayerContour extends TileLayer {

    public LOLTileLayerContour(ITiledMapView mapView) {
        initialize(mapView.getContext(), new TileImageCache(mapView, new LOLTileImageSourceContour(),
                new PicassoTileImageLoader()));

        mapView.setTileConfig(new ImageTileConfig(new ImageInfo(12200, 10240), 0, 6));

        setOffscreenTileLimit(2);
    }

}
