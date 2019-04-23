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


