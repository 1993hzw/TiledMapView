package cn.forward.tiledmapview.core;

public interface ITileDisplayInfo {

    public int getLevel();
    public float getLevelScale();
    public int getLeftTopRow();
    public int getLeftTopCol();
    public int getRightBottomRow();
    public int getRightBottomCol();
}
