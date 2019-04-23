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
                LogUtil.e(TAG, "onBitmapFailed:" + e.getMessage());
                sTargets.remove(callback);
                callback.onFailed(0);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        sTargets.put(callback, target);
        Picasso.get().load(uri).tag(tag).into(target);
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


