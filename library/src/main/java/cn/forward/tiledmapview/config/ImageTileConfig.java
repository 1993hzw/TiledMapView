package cn.forward.tiledmapview.config;

import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.MapRect;
import cn.forward.tiledmapview.core.Tile;

/**
 * 用于图片瓦片式加载
 */
public class ImageTileConfig extends AbstractTileConfig {

    private final double mResolutionOfLevel0; // 级别为０时的分辨率
    private final MapRect mWholeMapRect;

    private final int mTileImgHeight;
    private final int mTileImgWidth;
    private final int[] mColMaxIndices;
    private final int[] mColMinIndices;
    private final int mMinLevel;
    private final int mMaxLevel;
    private final int mLevelCount; // 级别总数
    private final double[] mResolutions;
    private final int[] mRowMaxIndices; // 每一级别对应的瓦片最小索引
    private final int[] mRowMinIndices; // 每一级别对应的瓦片最大索引
    private final MapPoint[] mOriginTileMapPoints; // 瓦片原点(0,0)对应的世界坐标

    public ImageTileConfig(ImageInfo imageInfo, int minLevel, int maxLevel) {
        this(imageInfo, minLevel, maxLevel, 1);

    }

    /**
     *
     * @param imageInfo 图片信息
     * @param minLevel
     * @param maxLevel
     * @param imgScale 瓦片图片缩放
     */
    public ImageTileConfig(ImageInfo imageInfo, int minLevel, int maxLevel, float imgScale) {
        if (minLevel < 0) {
            throw new IllegalArgumentException("min level must be equal to or greater than 0");
        }

        if (minLevel > maxLevel) {
            throw new IllegalArgumentException("min level must be equal to or smaller than max level.");
        }

        final int imageMaxLevel = findMaxLevel(imageInfo);
        final int maxImageSize = (int) (Math.pow(2, imageMaxLevel) * 256);
        mResolutionOfLevel0 = Math.pow(2, imageMaxLevel) * 256 / 256;
        double halfWidth = Math.pow(2, imageMaxLevel) * 256 / 2;
        mWholeMapRect = new MapRect(-halfWidth, halfWidth, imageInfo.getWidth() - halfWidth, -(imageInfo.getHeight() - halfWidth));
        MapPoint rightBottomPoint = new MapPoint(mWholeMapRect.right, mWholeMapRect.bottom);
        boolean rowAutoFit = imageInfo.getHeight() % 256 == 0;
        boolean colAutoFit = imageInfo.getWidth() % 256 == 0;

        this.mTileImgWidth = (int) (256 * imgScale);
        this.mTileImgHeight = (int) (256 * imgScale);
        this.mMinLevel = minLevel;
        this.mMaxLevel = maxLevel;
        this.mLevelCount = maxLevel - minLevel + 1;

        this.mOriginTileMapPoints = new MapPoint[mLevelCount];
        this.mResolutions = new double[mLevelCount];
        this.mRowMinIndices = new int[mLevelCount];
        this.mRowMaxIndices = new int[mLevelCount];
        this.mColMinIndices = new int[mLevelCount];
        this.mColMaxIndices = new int[mLevelCount];
        for (int level = mMinLevel; level <= mMaxLevel; level++) {
            int index = level - mMinLevel;
            this.mOriginTileMapPoints[index] = new MapPoint(mWholeMapRect.left, mWholeMapRect.top);
            this.mResolutions[index] = mResolutionOfLevel0 / imgScale / Math.pow(2, level);
            this.mRowMinIndices[index] = 0;
            this.mColMinIndices[index] = 0;

            Tile maxTile = new Tile(level, (int) ((mOriginTileMapPoints[index].y - rightBottomPoint.y) / mResolutions[index] / mTileImgHeight),
                    (int) ((rightBottomPoint.x - mOriginTileMapPoints[index].x) / mResolutions[index] / mTileImgWidth));
            // 处理图片大小刚好能被256整除的情况
            if (rowAutoFit || colAutoFit) {
                int scale = maxImageSize / (int) (Math.pow(2, level) * 256);
                if (rowAutoFit && imageInfo.getHeight() / scale % 256 == 0) {
                    maxTile.row--;
                }
                if (colAutoFit && imageInfo.getWidth() / scale % 256 == 0) {
                    maxTile.col--;
                }
            }
            this.mRowMaxIndices[index] = Math.min(maxTile.row, (int) Math.pow(2, level) - 1);
            this.mColMaxIndices[index] = Math.min(maxTile.col, (int) Math.pow(2, level) - 1);
        }

    }

    // 找到图片尺寸对应的最大级别
    private int findMaxLevel(ImageInfo imageInfo) {
        int level = -1;
        double maxSize = 0;
        do {
            level++;
            maxSize = Math.pow(2, level) * 256;
        } while (maxSize < imageInfo.getWidth() || maxSize < imageInfo.getHeight());
        return level;
    }


    @Override
    public int getTileWidth() {
        return mTileImgWidth;
    }

    @Override
    public int getTileHeight() {
        return mTileImgHeight;
    }

    @Override
    public double getResolution(int level) {
        return this.mResolutions[level - mMinLevel];
    }

    @Override
    public int getMaxTileRowIndex(int level) {
        return mRowMaxIndices[level - mMinLevel];
    }

    @Override
    public int getMaxTileColIndex(int level) {
        return mColMaxIndices[level - mMinLevel];
    }

    @Override
    public int getMinTileRowIndex(int level) {
        return mRowMinIndices[level - mMinLevel];
    }

    @Override
    public int getMinTileColIndex(int level) {
        return mColMinIndices[level - mMinLevel];
    }

    @Override
    public int getLevelCount() {
        return mLevelCount;
    }

    @Override
    public int getMinLevel() {
        return mMinLevel;
    }

    @Override
    public int getMaxLevel() {
        return mMaxLevel;
    }

    @Override
    public MapPoint getOriginTileMapPoint(int level) {
        return mOriginTileMapPoints[level - mMinLevel];
    }

    @Override
    public void config(ITiledMapView mapView) {
        ITileConfig tileConfig = mapView.getTileConfig();
        mapView.setMaxResolution(tileConfig.getResolution(tileConfig.getMinLevel()));
        // 最小分辨率为最大level相应分辨率的1/4
        mapView.setMinResolution(tileConfig.getResolution(tileConfig.getMaxLevel()) / 4);

        mapView.setCenterPoint(new MapPoint(0, 0));
        mapView.setResolution(mapView.getMaxResolution());
        mapView.setCenterBounds(new MapRect(mWholeMapRect));

    }
}


