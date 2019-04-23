package cn.forward.tiledmapview.core;

public interface ILngLatTransformation {
    public LngLat transform(LngLat lngLat);

    public LngLat revertTransform(LngLat lngLat);
}
