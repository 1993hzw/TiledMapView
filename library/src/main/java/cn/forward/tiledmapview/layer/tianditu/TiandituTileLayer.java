package cn.forward.tiledmapview.layer.tianditu;

import cn.forward.tiledmapview.config.LngLatProjectionTileConfig;
import cn.forward.tiledmapview.config.WebMercatorTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.ITileImageCache;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;
import cn.forward.tiledmapview.projection.LngLatProjection;
import cn.forward.tiledmapview.projection.WebMercatorProjection;

/**
 * @author ziwei huang
 */
public class TiandituTileLayer extends TileLayer {

    /**
     * @param mapView
     * @param imgType
     * @param projectionType
     * @param key            Using Tianditu API, you need to apply for a key. 使用天地图API，需要申请秘钥
     * @see <a href="https://console.tianditu.gov.cn/api/key">Tianditu API key</a>
     */
    public TiandituTileLayer(ITiledMapView mapView, TiandituOnlineTileImageSource.ImgType imgType, TiandituOnlineTileImageSource.ProjectionType projectionType, String key) {
        ITileImageCache imageCache = new TileImageCache(mapView, new TiandituOnlineTileImageSource(imgType, projectionType, key), new PicassoTileImageLoader());
        initialize(mapView.getContext(), imageCache);

        if (projectionType == TiandituOnlineTileImageSource.ProjectionType.LNG_LAT) {
            mapView.setTileConfig(new LngLatProjectionTileConfig(1, 18));
            IProjection projection = new LngLatProjection();
            mapView.setProjection(projection);
        } else {
            mapView.setTileConfig(new WebMercatorTileConfig(1, 18));
            IProjection projection = new WebMercatorProjection();
            mapView.setProjection(projection);
        }
    }
}
