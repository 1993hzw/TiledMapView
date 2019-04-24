package cn.forward.tiledmapview.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;

import cn.forward.androids.utils.StatusBarUtil;
import cn.forward.androids.utils.Util;
import cn.forward.androids.views.ScrollPickerView;
import cn.forward.androids.views.StringScrollPicker;
import cn.forward.tiledmapview.LayerGroup;
import cn.forward.tiledmapview.MapLocationManager;
import cn.forward.tiledmapview.TiledMapView;
import cn.forward.tiledmapview.core.ITileLayer;
import cn.forward.tiledmapview.core.ITiledMapView;
import cn.forward.tiledmapview.core.LngLat;
import cn.forward.tiledmapview.core.MapPoint;
import cn.forward.tiledmapview.layer.google.GoogleOnlineTileImageSource;
import cn.forward.tiledmapview.layer.google.GoogleTileLayer;
import cn.forward.tiledmapview.layer.tianditu.TiandituOnlineTileImageSource;
import cn.forward.tiledmapview.layer.tianditu.TiandituTileLayer;
import cn.forward.tiledmapview.overlay.BitmapPixelOverlay;
import cn.forward.tiledmapview.overlay.TextPixelOverlay;

/**
 * @author ziwei huang
 */
public class TiledMapDemoActivity extends FragmentActivity {

    /**
     * Using Tianditu API, you need to apply for a key
     * 使用天地图API，需要申请秘钥
     *
     * @see <a href="https://console.tianditu.gov.cn/api/key">Tianditu API key</a>
     */
    public static String TIANDITU_KEY = "b34f09c6586e9741629c42f716b7494b"; // Only for test! Please apply for the key.

    public static final int REQUEST_CODE_PERMISSION = 100;
    public static final String TYPE = "type";
    private TileType mTileType;
    private ITiledMapView mMapView;
    private BitmapPixelOverlay mBitmapMapOverlay;
    private TextPixelOverlay mTextPixelOverlay;
    private LocationListener mLocationListener;
    private boolean mNeedAutoLocation;
    private CheckBox mTilesInfoCheckBox;
    private CheckBox mDebugBox, mMarkerBox;

    private LayerGroup<ITileLayer> mTileLayerGroup;
    private TiandituOnlineTileImageSource.ImgType mImgType = TiandituOnlineTileImageSource.ImgType.SATELLITE;
    private TiandituOnlineTileImageSource.ProjectionType mProjectionType = TiandituOnlineTileImageSource.ProjectionType.LNG_LAT;
    private boolean mHasMarker = true;

    @Override
    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        StatusBarUtil.setStatusBarTranslucent(this, true, false);
        setContentView(R.layout.layout_maptile);

        mTileType = (TileType) getIntent().getSerializableExtra(TYPE);
        mMapView = findViewById(R.id.mapview);
        mTilesInfoCheckBox = ((CheckBox) findViewById(R.id.tiles));
        mTilesInfoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDebug(isChecked);
            }
        });
        mDebugBox = findViewById(R.id.debug);
        mDebugBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiledMapView.setDebugMode(isChecked);
                mMapView.refresh();
            }
        });
        mDebugBox.setChecked(TiledMapView.isDebugMode());
        mMarkerBox = findViewById(R.id.marker);
        mMarkerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHasMarker = isChecked;
                if (mTileLayerGroup != null) {
                    updateMap();
                }
                mMapView.refresh();
            }
        });
        mMarkerBox.setChecked(mHasMarker);
        init(mMapView);
    }

    public void init(final ITiledMapView mapView) {

        mTileLayerGroup = new LayerGroup<>(); // tile level
        mapView.getLayerGroup().add(mTileLayerGroup);

        initOverlay(mapView); // overlay level

        StringScrollPicker picker = findViewById(R.id.picker);
        switch (mTileType) {
            case Google:
                updateMap();
                addLocation(mapView);
                picker.setVisibility(View.GONE);
                break;
            case Tianditu:
                picker.setData(Arrays.asList(getString(R.string.lng_lat_projection), getString(R.string.web_mercator_projection)));
                picker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
                    @Override
                    public void onSelected(ScrollPickerView scrollPickerView, int i) {
                        if (i == 0) {
                            mProjectionType = TiandituOnlineTileImageSource.ProjectionType.LNG_LAT;
                        } else {
                            mProjectionType = TiandituOnlineTileImageSource.ProjectionType.WEB_MERCATOR;
                        }

                        updateMap();
                        addLocation(mapView);
                    }
                });
                picker.setSelectedPosition(0);
                break;
        }

        RadioGroup radioGroup = findViewById(R.id.img_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.satellite) {
                    mImgType = TiandituOnlineTileImageSource.ImgType.SATELLITE;
                } else if (checkedId == R.id.vector) {
                    mImgType = TiandituOnlineTileImageSource.ImgType.VECTOR;
                } else if (checkedId == R.id.terrain) {
                    mImgType = TiandituOnlineTileImageSource.ImgType.TERRAIN;
                }
                updateMap();
            }
        });
    }

    private void initOverlay(final ITiledMapView mapView) {
        mBitmapMapOverlay = new BitmapPixelOverlay(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mBitmapMapOverlay.setVisible(false);
        mBitmapMapOverlay.setId(R.id.bitmap_location);

        mTextPixelOverlay = new TextPixelOverlay(getString(R.string.here), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        mTextPixelOverlay.setVisible(false);
        mTextPixelOverlay.setBackgroundColor(0x99ffffff);
        mTextPixelOverlay.getTextPaint().setFakeBoldText(true);
        mTextPixelOverlay.getTextPaint().setColor(Color.BLUE);
        mTextPixelOverlay.getTextPaint().setTextSize(Util.dp2px(getApplicationContext(), 14));

        if (!mapView.getLayerGroup().contains(mBitmapMapOverlay)) {
            mapView.getLayerGroup().add(mBitmapMapOverlay);
        }

        if (!mapView.getLayerGroup().contains(mTextPixelOverlay)) {
            mapView.getLayerGroup().add(mTextPixelOverlay);
        }

        if (TiledMapView.isDebugMode()) { // test
            mBitmapMapOverlay = mapView.getLayerGroup().findLayerById(R.id.bitmap_location);
            if (mBitmapMapOverlay == null) {
                throw new AssertionError("findLayerById error");
            }

            mBitmapMapOverlay.setTag("hello");
            if (mBitmapMapOverlay.getTag() != "hello") {
                throw new AssertionError("getTag error");
            }

            mBitmapMapOverlay.setTag(R.id.bitmap_location, "hello");
            if (mBitmapMapOverlay.getTag(R.id.bitmap_location) != "hello") {
                throw new AssertionError("getTag by key error");
            }
        }
    }

    private void showDebug(boolean show) {
        mMapView.setShowTileInfo(show);
    }

    private void updateMap() {
        mTileLayerGroup.clear();
        if (mTileType == TileType.Tianditu) {
            // tile
            mTileLayerGroup.add(new TiandituTileLayer(mMapView, mImgType, mProjectionType, TIANDITU_KEY));
            if (mHasMarker) {
                switch (mImgType) {
                    case SATELLITE:
                        mTileLayerGroup.add(new TiandituTileLayer(mMapView, TiandituOnlineTileImageSource.ImgType.SATELLITE_ONLY_MARKER, mProjectionType, TIANDITU_KEY));
                        break;
                    case VECTOR:
                        mTileLayerGroup.add(new TiandituTileLayer(mMapView, TiandituOnlineTileImageSource.ImgType.VECTOR_ONLY_MARKER, mProjectionType, TIANDITU_KEY));
                        break;
                    case TERRAIN:
                        mTileLayerGroup.add(new TiandituTileLayer(mMapView, TiandituOnlineTileImageSource.ImgType.TERRAIN_ONLY_MARKER, mProjectionType, TIANDITU_KEY));
                        break;
                }
            }
        } else { // google
            if (mHasMarker) {
                switch (mImgType) {
                    case SATELLITE:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.SATILLITE_WITH_MARKER));
                        break;
                    case VECTOR:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.VECTOR_WITH_MARKER));
                        break;
                    case TERRAIN:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.TERRAIN_WITH_MARKER));
                        break;
                }
            } else {
                switch (mImgType) {
                    case SATELLITE:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.SATELLITE));
                        break;
                    case VECTOR:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.VECTOR_WITH_MARKER));
                        break;
                    case TERRAIN:
                        mTileLayerGroup.add(new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.TERRAIN));
                        break;
                }
            }
        }
        showDebug(mTilesInfoCheckBox.isChecked());
    }

    private void addLocation(final ITiledMapView mapView) {
        if (mLocationListener == null) {
            mLocationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    mBitmapMapOverlay.setVisible(true);
                    LngLat lngLat = new LngLat(location.getLongitude(), location.getLatitude());
                    MapPoint mapPoint = mapView.lngLat2MapPoint(lngLat);
                    mBitmapMapOverlay.setLocationOnMap(mapPoint.x, mapPoint.y);
                    mBitmapMapOverlay.setWidth(100);
                    mBitmapMapOverlay.setHeight(100);
                    mTextPixelOverlay.setVisible(true);
                    mTextPixelOverlay.setLocationOnMap(mapPoint.x, mapPoint.y);

                    if (mNeedAutoLocation) {
                        mNeedAutoLocation = false;
                        mapView.zoomToCenter(mapPoint, mapView.getResolution() / (mapView.getMinResolution() * 4), true);
                    }

                    /*for(int i=0;i<100;i++){
                        mBitmapMapOverlay = new BitmapPixelOverlay(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        mBitmapMapOverlay.setVisible(true);
                        mBitmapMapOverlay.setLocationOnMap(mapPoint.x, mapPoint.y);
                        mMapView.getLayerGroup().add(mBitmapMapOverlay);
                        mBitmapMapOverlay.setWidth(100);
                        mBitmapMapOverlay.setHeight(100);
                    }*/

                    mapView.refresh();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
        MapLocationManager.getInstance(this).removeListener(mLocationListener);
        if (!MapLocationManager.getInstance(this).addListener(mLocationListener)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
        }


    }

    public void location(View view) {
        mNeedAutoLocation = true;
        addLocation(mMapView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapLocationManager.getInstance(this).removeListener(mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        addLocation(mMapView);
                    } else {
                        Toast.makeText(this, "Please grant the GPS permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}


