package cn.forward.tiledmapview.layer;

import cn.forward.tiledmapview.core.ITileImageSource;
import cn.forward.tiledmapview.core.Tile;

public class OptimizedTile extends Tile {
    private int mDistance = Integer.MAX_VALUE;
    private String mUri;
    private int mLastLevel = Integer.MIN_VALUE, mLastRow = Integer.MIN_VALUE, mLastCol = Integer.MIN_VALUE;

    public void setDistance(int distance) {
        mDistance = distance;
    }

    public int getDistance() {
        return mDistance;
    }

    public void reset(int level, int row, int col, ITileImageSource source) {
        reset(level, row, col);
        if (mUri == null) {
            mUri = source.getUri(this);
        }
    }

    public String getUri() {
        if (mLastRow != row || mLastCol != col || mLastLevel != level) {
            mUri = null;
        }
        return mUri;
    }

    @Override
    public void reset(int level, int row, int col) {
        super.reset(level, row, col);
        mDistance = Integer.MAX_VALUE;
        if (mLastRow != row || mLastCol != col || mLastLevel != level) {
            mLastLevel = level;
            mLastRow = row;
            mLastCol = col;
            mUri = null;
        }
    }

    public boolean isNoDistance() {
        return mDistance == Integer.MAX_VALUE;
    }
}
