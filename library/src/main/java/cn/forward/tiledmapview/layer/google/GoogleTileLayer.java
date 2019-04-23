package cn.forward.tiledmapview.layer.google;

import cn.forward.tiledmapview.config.WebMercatorTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;
import cn.forward.tiledmapview.projection.WebMercatorProjection;

/**
 * @author ziwei huang
 */
public class GoogleTileLayer extends TileLayer {

    public GoogleTileLayer(ITiledMapView mapView, GoogleOnlineTileImageSource.ImgType imgType) {
        this(mapView, imgType, 2f, "zh-CN");
    }

    public GoogleTileLayer(ITiledMapView mapView, GoogleOnlineTileImageSource.ImgType imgType, float scale, String language) {
        initialize(mapView.getContext(), new TileImageCache(mapView, new GoogleOnlineTileImageSource(imgType, scale, language), new PicassoTileImageLoader()));

        mapView.setTileConfig(new WebMercatorTileConfig(0, 17, scale));
        IProjection projection = new WebMercatorProjection(); // 由于tile url没有gl=CN参数，故无需进行Wgs84ToGcj02Transformation
        mapView.setProjection(projection);
    }

}
