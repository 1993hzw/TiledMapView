package cn.forward.tiledmapview.core;

/**
 * @author ziwei huang
 */
public interface ITileLayer extends ILayer {
    public void clearCache();

    public ITileImageCache getTileImageCache();

    public void setTileImageCache(ITileImageCache tileImageCache);

    public void setOffscreenTileLimit(int limit);

    public int getOffscreenTileLimit();

}


