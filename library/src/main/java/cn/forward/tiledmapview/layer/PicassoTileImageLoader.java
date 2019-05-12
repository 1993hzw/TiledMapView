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

package cn.forward.tiledmapview.layer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.WeakHashMap;

import cn.forward.tiledmapview.util.LogUtil;

public class PicassoTileImageLoader implements TileImageCache.ITileImageLoader {

    public static final String TAG = "PicassoTileImageLoader";

    static {
        Picasso.get();
    }

    private static WeakHashMap<TileImageCache.ILoaderCallback, Target> sTargets = new WeakHashMap<>();
    private Picasso.Priority mPriority = Picasso.Priority.NORMAL;

    public PicassoTileImageLoader() {
        this(Picasso.Priority.NORMAL);
    }

    public PicassoTileImageLoader(Picasso.Priority priority) {
        mPriority = priority;
    }

    @Override
    public void request(String uri, String tag, final TileImageCache.ILoaderCallback callback) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                sTargets.remove(callback);
                callback.onLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (LogUtil.sIsLog) {
                    LogUtil.e(TAG, "onBitmapFailed:" + e.getMessage());
                }
                sTargets.remove(callback);
                callback.onFailed(0);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        sTargets.put(callback, target);
        Picasso.get().load(uri).tag(tag).priority(mPriority).into(target);
    }

    @Override
    public void cancel(TileImageCache.ILoaderCallback callback) {
        Target target = sTargets.get(callback);
        if (target != null) {
            Picasso.get().cancelRequest(target);
        }
    }

    @Override
    public void cancelByTag(String tag) {
        Picasso.get().cancelTag(tag);
    }
}


