/*
 * Copyright (C) 2019  Ziwei Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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
