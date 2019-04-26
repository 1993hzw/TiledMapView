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
 
package cn.forward.tiledmapview.core;


public interface IProjection {

    public ILngLatTransformation getLngLatTransformation();

    public void setLngLatTransformation(ILngLatTransformation ellipsoid);

    public MapPoint lngLat2MapPoint(LngLat lngLat);

    public LngLat mapPoint2LngLat(MapPoint mapPoint);

}
