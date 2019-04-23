package cn.forward.tiledmapview.projection;

import cn.forward.tiledmapview.core.ILngLatTransformation;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.LngLat;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.util.GeoUtil;

public class WebMercatorProjection implements IProjection {

    private ILngLatTransformation mLngLatTransformation;

    public WebMercatorProjection() {
        this(null);
    }

    public WebMercatorProjection(ILngLatTransformation transformation) {
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

        MapPoint localMapPoint = new MapPoint();
        localMapPoint.x = lngLatTransformed.longitude * GeoUtil.WEB_MERCATOR_LENGTH_HALF / 180;
        localMapPoint.y = Math.log(Math.tan((90 + lngLatTransformed.latitude) * Math.PI / 360)) / (Math.PI / 180)
                * (GeoUtil.WEB_MERCATOR_LENGTH_HALF / 180);
        return localMapPoint;
    }

    @Override
    public LngLat mapPoint2LngLat(MapPoint mapPoint) {
        if (mapPoint == null) {
            return null;
        }

        LngLat lngLat = new LngLat();
        lngLat.longitude = mapPoint.x / GeoUtil.WEB_MERCATOR_LENGTH_HALF * 180;
        lngLat.latitude = mapPoint.y / GeoUtil.WEB_MERCATOR_LENGTH_HALF * 180;
        lngLat.latitude = 180 / Math.PI * (2 * Math.atan(Math.exp(lngLat.latitude * Math.PI / 180)) - Math.PI / 2);

        LngLat lngLatTransformed = lngLat;
        if (mLngLatTransformation != null) {
            lngLatTransformed = mLngLatTransformation.revertTransform(lngLatTransformed);
        }
        return lngLatTransformed;
    }

}
