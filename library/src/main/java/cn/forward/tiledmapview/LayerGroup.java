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

package cn.forward.tiledmapview;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.forward.tiledmapview.core.ILayer;
import cn.forward.tiledmapview.core.ILayerGroup;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;
import cn.forward.tiledmapview.core.ITileDisplayInfo;

public class LayerGroup<T extends ILayer> extends AbstractLayer implements ILayerGroup<T> {

    private List<T> mLayerList = new ArrayList<>();

    @Override
    public <T extends ILayer> T findLayerById(int id) {
        ILayer layer = super.findLayerById(id);
        if (layer != null) {
            return (T) layer;
        }

        //  Depth-first search
        for (int i = 0; i < size(); i++) {
            if (get(i).getId() == id) {
                return (T) get(i);
            }
        }

        return null;
    }

    @Override
    public void draw(Canvas canvas, ITiledMapView mapView, ITileConfig tileConfig, ITileDisplayInfo tileDisplayInfo) {
        for (int i = 0; i < size(); i++) {
            if (get(i).isVisible()) {
                int canvasSaveCount = canvas.save();
                get(i).draw(canvas, mapView, tileConfig, tileDisplayInfo);
                canvas.restoreToCount(canvasSaveCount);
            }
        }
    }

    private void doAttach(T layer) {
        if (isDetachedFromView()) { // 父容器detached
            return;
        }

        if (layer.isAttachedToView()) {
            throw new RuntimeException("The layer has been attached.");
        }

        layer.setCallback(getCallback());
        layer.onAttachedToView();
    }

    private void doDetach(T layer) {
        if (isDetachedFromView()) { // 父容器detached
            return;
        }

        layer.setCallback(null);
        layer.onDetachedFromView();
    }

    @Override
    public void onAttachedToView() {
        super.onAttachedToView(); // 父容器先attach
        for (int i = 0; i < size(); i++) {
            doAttach(get(i));
        }
    }

    @Override
    public void onDetachedFromView() {
        for (int i = 0; i < size(); i++) { // 子元素先detach
            doDetach(get(i));
        }
        super.onDetachedFromView();
    }

    @Override
    public void onProjectionChanged(ITiledMapView mapView, IProjection currentProjection, IProjection oldProjection) {
        super.onProjectionChanged(mapView, currentProjection, oldProjection);
        for (int i = 0; i < size(); i++) {
            if (get(i).isVisible()) {
                get(i).onProjectionChanged(mapView, currentProjection, oldProjection);
            }
        }
    }

    @Override
    public void onDisplayInfoChanged(@NonNull ITiledMapView mapView, @NonNull ITileConfig tileConfig, @NonNull ITileDisplayInfo tileDisplayInfo) {
        for (int i = 0; i < size(); i++) {
            if (get(i).isVisible()) {
                get(i).onDisplayInfoChanged(mapView, tileConfig, tileDisplayInfo);
            }
        }
    }

    @Override
    public T get(int index) {
        return this.mLayerList.get(index);
    }

    @Override
    public int size() {
        return this.mLayerList.size();
    }

    public void add(T layer) {
        add(mLayerList.size(), layer);
    }

    @Override
    public void add(int index, T layer) {
        if (contains(layer)) {
            throw new RuntimeException("The layer has been added.");
        }

        this.mLayerList.add(index, layer);
        doAttach(layer);
    }

    @Override
    public void clear() {
        List<T> copy = new ArrayList<>(mLayerList);
        for (int i = 0; i < copy.size(); i++) {
            T layer = copy.get(i);
            mLayerList.remove(layer);
            doDetach(layer);
        }
    }

    @Override
    public boolean remove(T layer) {
        if (this.mLayerList.remove(layer)) {
            doDetach(layer);
            return true;
        }

        return false;
    }

    @Override
    public T removeAt(int index) {
        T layer = this.mLayerList.remove(index);
        if (layer != null) {
            doDetach(layer);
        }
        return layer;
    }

    @Override
    public int indexOf(T layer) {
        return mLayerList.indexOf(layer);
    }

    @Override
    public boolean contains(T layer) {
        return mLayerList.contains(layer);
    }
}


