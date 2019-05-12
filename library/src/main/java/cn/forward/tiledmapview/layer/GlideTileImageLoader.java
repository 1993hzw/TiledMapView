package cn.forward.tiledmapview.layer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITileImageCache;

/**
 * Created on 12/05/2019.
 */
public class GlideTileImageLoader implements TileImageCache.ITileImageLoader {

    private Context mContext;
    private Priority mPriority;
    private static WeakHashMap<TileImageCache.ILoaderCallback, Target> sTargets = new WeakHashMap<>();
    private static HashMap<String, List<WeakReference<Target>>> sTagTargets = new HashMap<>();

    public GlideTileImageLoader(Context context) {
        this(context, Priority.NORMAL);
    }

    public GlideTileImageLoader(Context context, Priority priority) {
        mContext = context;
        mPriority = priority;
    }


    @Override
    public void request(ITileConfig tileConfig, String uri, String tag, final ITileImageCache.ILoaderCallback callback) {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }

        RequestOptions options = new RequestOptions()
                .priority(mPriority);
        Target target = Glide.with(mContext)
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(new SimpleTarget<Bitmap>(tileConfig.getTileWidth(), tileConfig.getTileHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        callback.onLoaded(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        callback.onFailed("onLoadFailed");
                    }
                });
        sTargets.put(callback, target);
        List<WeakReference<Target>> list = sTagTargets.get(tag);
        if (list == null) {
            list = new ArrayList<>();
            sTagTargets.put(tag, list);
        }
        list.add(new WeakReference<Target>(target));
    }

    @Override
    public void cancel(ITileImageCache.ILoaderCallback callback) {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }

        Target target = sTargets.get(callback);
        if (target != null) {
            Glide.with(mContext).clear(target);
        }
    }

    @Override
    public void cancelByTag(String tag) {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }

        List<WeakReference<Target>> list = sTagTargets.get(tag);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Target target = list.get(i).get();
                if (target != null) {
                    Glide.with(mContext).clear(target);
                }
            }
            list.clear();
        }
    }

}
