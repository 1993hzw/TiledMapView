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

import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import cn.forward.tiledmapview.core.ILayer;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.IProjection;

public abstract class AbstractLayer implements ILayer {

    public static final int NO_ID = -1;
    private int mId = NO_ID;
    private SparseArray<Object> mKeyedTags;
    private Object mTag;
    private WeakReference<Callback> mCallbackWeakReference;
    private boolean mIsVisible = true;
    private boolean mIsAttached = false;

    public boolean isVisible() {
        return this.mIsVisible;
    }

    public void setVisible(boolean visible) {
        this.mIsVisible = visible;
    }

    @Override
    @CallSuper
    public void onAttachedToView() {
        mIsAttached = true;
    }

    @Override
    @CallSuper
    public void onDetachedFromView() {
        mIsAttached = false;
    }

    @Override
    public boolean isDetachedFromView() {
        return !mIsAttached;
    }

    @Override
    public boolean isAttachedToView() {
        return mIsAttached;
    }

    @Override
    public <T extends ILayer> T findLayerById(int id) {
        if (id == NO_ID) {
            return null;
        }

        return mId == id ? (T) this : null;
    }

    @Override
    public void setId(@IdRes int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setTag(Object tag) {
        mTag = tag;
    }

    @Override
    public Object getTag() {
        return mTag;
    }

    @Override
    public void setTag(@IdRes int key, final Object tag) {
        if (mKeyedTags == null) {
            mKeyedTags = new SparseArray<Object>(2);
        }

        mKeyedTags.put(key, tag);
    }

    @Override
    public Object getTag(@IdRes int key) {
        return mKeyedTags != null ? mKeyedTags.get(key) : null;
    }

    @Override
    public final void setCallback(Callback cb) {
        mCallbackWeakReference = cb == null ? null : new WeakReference<>(cb);
    }

    @Override
    public final Callback getCallback() {
        return mCallbackWeakReference != null ? mCallbackWeakReference.get() : null;
    }

    @Override
    public void refreshItself() {
        Callback callback = getCallback();
        if (callback != null) {
            callback.refreshItself(this);
        }
    }

    @CallSuper
    @Override
    public void onProjectionChanged(ITiledMapView mapView, IProjection currentProjection, IProjection oldProjection) {

    }
}


