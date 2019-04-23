package cn.forward.tiledmapview.core;

import android.graphics.Bitmap;

/**
 * @author ziwei huang
 */
public interface ITileImageCache {

    public void setPlaceHolder(Bitmap placeHolder);

    public Bitmap getPlaceHolder(Tile tile, ITiledMapView mapView);

    public void resize(int rowCount, int colCount);

    public Bitmap getTileBitmap(Tile tile, ITiledMapView mapView);

    public void clear();

    public ITiledMapView getMapView();

    public ITileImageSource getTileImageSource();

    public ITileLayer getTileLayer();

    public void setMapView(ITiledMapView mapView);

    public void setTileImageSource(ITileImageSource tileImageSource);

    public void setTileLayer(ITileLayer tileLayer);
}


