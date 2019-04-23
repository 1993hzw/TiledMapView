package cn.forward.tiledmapview.projection;

import cn.forward.tiledmapview.core.ILngLatTransformation;
import cn.forward.tiledmapview.core.LngLat;
import cn.forward.tiledmapview.util.GeoUtil;

public class Wgs84ToGcj02Transformation implements ILngLatTransformation {
    @Override
    public LngLat transform(LngLat lngLat) {
        double[] values = GeoUtil.wgs84ToGcj02(lngLat.longitude, lngLat.latitude);
        return new LngLat(values[0], values[1]);
    }

    @Override
    public LngLat revertTransform(LngLat lngLat) {
        double[] values = GeoUtil.gcj02ToWgs84(lngLat.longitude, lngLat.latitude);
        return new LngLat(values[0], values[1]);
    }
}
