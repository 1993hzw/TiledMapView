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

import android.graphics.Canvas;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

/**
 * @author ziwei huang
 */
public interface ILayer {

    public void onProjectionChanged(ITiledMapView mapView, IProjection currentProjection, IProjection oldProjection);

    public void onDisplayInfoChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo);

    public void draw(Canvas canvas, @NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo);

    public boolean isVisible();

    public void setVisible(boolean visible);

    public void onAttachedToView();

    public void onDetachedFromView();

    public boolean isDetachedFromView();

    public boolean isAttachedToView();

    public <T extends ILayer> T findLayerById(@IdRes int id);

    public void setId(@IdRes int id);

    public int getId();

    public void setTag(Object tag);

    public Object getTag();

    public void setTag(@IdRes int key, final Object tag);

    public Object getTag(@IdRes int key);

    public void setCallback(ILayer.Callback cb);

    public ILayer.Callback getCallback();

    public void refreshItself();

    public interface Callback {
        public void refreshItself(ILayer layer);
    }

}


