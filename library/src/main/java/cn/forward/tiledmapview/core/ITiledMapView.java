package cn.forward.tiledmapview.core;

import android.content.Context;
import android.graphics.PointF;

/**
 * @author ziwei huang
 */
public interface ITiledMapView {

    public Context getContext();

    public int getViewWidth();

    public int getViewHeight();

    public void refresh();

    public MapPoint getCenterPoint();

    public boolean setCenterPoint(MapPoint centerPoint);

    public MapPoint getFocusCenter();

    public void setFocusPoint(MapPoint focusPoint);

    public void getMapViewRect(MapRect mapRect);

    public double getMaxResolution();

    public void setMaxResolution(double maxResolution);

    public double getMinResolution();

    public void setMinResolution(double minResolution);

    public double getResolution();

    public void setResolution(double resolution);

    public boolean offsetCenter(double dx, double dy);

    public boolean offsetCenterTo(double x, double y);

    public ILayerGroup<ILayer> getLayerGroup();

    public void zoomOnFocus(MapPoint focusPoint, double zoom, boolean withAnim);

    public void zoomToCenter(MapPoint toCenterPoint, double zoom, boolean withAnim);

    public void setTouchDetector(IMapTouchDetector mapTouchDetector);

    public IMapTouchDetector getTouchDetector();

    public boolean setCenterBounds(MapRect mapRect);

    public void getCenterBounds(MapRect mapRect);

    public void setProjection(IProjection projection);

    public IProjection getProjection();

    public MapPoint viewPoint2MapPoint(PointF point);

    public PointF mapPoint2ViewPoint(MapPoint mapPoint);

    public MapPoint lngLat2MapPoint(LngLat lngLat);

    public Tile mapPoint2Tile(MapPoint mapPoint, int level);

    public boolean isShowTileInfo();

    public void setShowTileInfo(boolean paramBoolean);

    public ITileConfig getTileConfig();

    public void setTileConfig(ITileConfig tileConfig);

}


