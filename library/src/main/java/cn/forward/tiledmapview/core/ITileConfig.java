package cn.forward.tiledmapview.core;

public interface ITileConfig {
    public int getTileWidth();

    public int getTileHeight();

    public double getResolution(int level);

    /**
     * 获取指定分辨率下的级别
     * @param resolution
     * @return
     */
    public int findOptimalLevelByResolution(double resolution);

    public int getMaxTileRowIndex(int level);

    public int getMaxTileColIndex(int level);

    public int getMinTileRowIndex(int level);

    public int getMinTileColIndex(int level);

    public int getLevelCount();

    public int getMinLevel();

    public int getMaxLevel();

    public MapPoint getOriginTileMapPoint(int level);

    public Tile mapPoint2Tile(MapPoint mapPoint, int level);

    public MapPoint getTileRightBottomMapPoint(Tile tile);

    public MapPoint getTileCenterMapPoint(Tile tile);

    public MapPoint getTileLetTopMapPoint(Tile tile);

    public void config(ITiledMapView mapView);

}
