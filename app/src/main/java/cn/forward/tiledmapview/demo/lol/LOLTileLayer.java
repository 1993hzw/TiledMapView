package cn.forward.tiledmapview.demo.lol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.forward.tiledmapview.config.ImageInfo;
import cn.forward.tiledmapview.config.ImageTileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.layer.PicassoTileImageLoader;
import cn.forward.tiledmapview.layer.TileImageCache;
import cn.forward.tiledmapview.layer.TileLayer;

/**
 * @author ziwei huang
 */
public class LOLTileLayer extends TileLayer {

    public LOLTileLayer(ITiledMapView mapView) {
        Bitmap placeHolder = BitmapFactory.decodeResource(mapView.getContext().getResources(), cn.forward.tiledmapview.R.drawable.grid);
        initialize(mapView.getContext(), new TileImageCache(mapView, new LOLTileImageSource(),
                new PicassoTileImageLoader(), placeHolder));

        mapView.setTileConfig(new ImageTileConfig(new ImageInfo(12200, 10240), 0, 6));

        setOffscreenTileLimit(2);
    }

}
