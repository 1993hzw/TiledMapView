package cn.forward.tiledmapview.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso picasso = new Picasso.Builder(this).memoryCache(
                new LruCache(calculateMemoryCacheSize(this))).build();
        Picasso.setSingletonInstance(picasso);
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (int) (1024L * 1024L * memoryClass / 4);
    }
}
