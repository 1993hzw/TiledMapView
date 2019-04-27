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

        // The full image size is 12200x10240, which image level is 6.
        mapView.setTileConfig(new ImageTileConfig(new ImageInfo(12200, 10240), 0, 6));

        // You can also use the source image level, but it will fills the remaining map with blank tiles.
        //mapView.setTileConfig(new ImageTileConfig(6, 0, 6));

        setOffscreenTileLimit(1); //  Set the number of tiles that should be retained to either side of the current screen
    }

}
