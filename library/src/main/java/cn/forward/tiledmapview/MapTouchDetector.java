package cn.forward.tiledmapview;

import android.content.Context;

import cn.forward.tiledmapview.core.IMapTouchDetector;
import cn.forward.tiledmapview.util.TouchGestureDetector;

public class MapTouchDetector extends TouchGestureDetector implements IMapTouchDetector {
    public MapTouchDetector(Context context, IOnTouchGestureListener listener) {
        super(context, listener);
    }
}
