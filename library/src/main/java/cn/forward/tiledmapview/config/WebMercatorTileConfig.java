package cn.forward.tiledmapview.config;

import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.ITileConfig;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.core.MapRect;
import cn.forward.tiledmapview.util.GeoUtil;

public class WebMercatorTileConfig extends AbstractTileConfig {

    public static double RESOLUTION_OF_LEVEL0 = 156543.03392804097D; // 级别为０时的分辨率

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

    public WebMercatorTileConfig(int minLevel, int maxLevel) {
        this(minLevel, maxLevel, 1);

    }

    public WebMercatorTileConfig(int minLevel, int maxLevel, float imgScale) {
        if (minLevel < 0) {
            throw new IllegalArgumentException("min level must be equal to or greater than 0");
        }

        if (minLevel > maxLevel) {
            throw new IllegalArgumentException("min level must be equal to or smaller than max level.");
        }

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
            this.mOriginTileMapPoints[index] = new MapPoint(GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[0], GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[1]);
            this.mResolutions[index] = RESOLUTION_OF_LEVEL0 / imgScale / Math.pow(2, level);
            this.mRowMinIndices[index] = 0;
            this.mColMinIndices[index] = 0;
            this.mRowMaxIndices[index] = (int) Math.pow(2, level) - 1;
            this.mColMaxIndices[index] = (int) Math.pow(2, level) - 1;
        }

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
        mapView.setCenterBounds(new MapRect(GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[0], GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[1],
                GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[1], GeoUtil.WEB_MERCATOR_COORDINATE_RANGE[0]));

    }
}


