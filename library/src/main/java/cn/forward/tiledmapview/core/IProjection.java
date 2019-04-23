package cn.forward.tiledmapview.core;


public interface IProjection {

    public ILngLatTransformation getLngLatTransformation();

    public void setLngLatTransformation(ILngLatTransformation ellipsoid);

    public MapPoint lngLat2MapPoint(LngLat lngLat);

    public LngLat mapPoint2LngLat(MapPoint mapPoint);

}
