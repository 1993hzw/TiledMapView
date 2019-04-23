package cn.forward.tiledmapview.projection;

import cn.forward.tiledmapview.core.ILngLatTransformation;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.LngLat;
import cn.forward.tiledmapview.core.MapPoint;

public class LngLatProjection implements IProjection {

    private ILngLatTransformation mLngLatTransformation;

    public LngLatProjection() {
        this(null);
    }

    public LngLatProjection(ILngLatTransformation transformation) {
        mLngLatTransformation = transformation;
    }

    @Override
    public ILngLatTransformation getLngLatTransformation() {
        return mLngLatTransformation;
    }

    @Override
    public void setLngLatTransformation(ILngLatTransformation ellipsoid) {
        mLngLatTransformation = ellipsoid;
    }

    @Override
    public MapPoint lngLat2MapPoint(final LngLat lngLat) {
        if (lngLat == null) {
            return null;
        }

        LngLat lngLatTransformed = lngLat;
        if (mLngLatTransformation != null) {
            lngLatTransformed = mLngLatTransformation.transform(lngLat);
        }

        return new MapPoint(lngLatTransformed.longitude, lngLatTransformed.latitude);

    }

    @Override
    public LngLat mapPoint2LngLat(MapPoint mapPoint) {
        if (mapPoint == null) {
            return null;
        }

        LngLat lngLat = new LngLat(mapPoint.x, mapPoint.y);
        LngLat lngLatTransformed = lngLat;
        if (mLngLatTransformation != null) {
            lngLatTransformed = mLngLatTransformation.revertTransform(lngLat);
        }
        return lngLatTransformed;
    }

}
