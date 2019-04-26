package cn.forward.tiledmapview.demo.lol;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.forward.androids.utils.StatusBarUtil;
import cn.forward.androids.utils.Util;
import cn.forward.tiledmapview.TiledMapView;
import cn.forward.tiledmapview.demo.R;
import cn.forward.tiledmapview.demo.lol.contour.LOLTileLayerContour;
import cn.forward.tiledmapview.overlay.TextMapOverlay;
import cn.forward.tiledmapview.overlay.TextPixelOverlay;

public class LolMapDemoActivity extends FragmentActivity {

    private TiledMapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTranslucent(this, true, false);
        setContentView(R.layout.layout_lol);

        mMapView = findViewById(R.id.mapview);

        LOLTileLayer tileLayer = new LOLTileLayer(mMapView);
        mMapView.getLayerGroup().add(tileLayer);

        // crop
        final LOLTileLayerContour tileLayerCrop = new LOLTileLayerContour(mMapView);
        mMapView.getLayerGroup().add(tileLayerCrop);

        ((CheckBox) findViewById(R.id.tiles)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMapView.setShowTileInfo(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.debug)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiledMapView.setDebugMode(isChecked);
                mMapView.refresh();
            }
        });

        ((CheckBox) findViewById(R.id.contour)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tileLayerCrop.setVisible(isChecked);
                mMapView.refresh();
            }
        });


        TextPixelOverlay textPixelOverlay = new TextPixelOverlay("TextPixelOverlay");
        textPixelOverlay.setBackgroundColor(0x99ffffff);
        textPixelOverlay.getTextPaint().setFakeBoldText(true);
        textPixelOverlay.getTextPaint().setColor(Color.BLUE);
        textPixelOverlay.getTextPaint().setTextSize(Util.dp2px(getApplicationContext(), 14));
        mMapView.getLayerGroup().add(textPixelOverlay);

        TextMapOverlay textMapOverlay = new TextMapOverlay("TextMapOverlay");
        textMapOverlay.setBackgroundColor(0x99ffffff);
        textMapOverlay.getTextPaint().setFakeBoldText(true);
        textMapOverlay.getTextPaint().setColor(Color.RED);
        textMapOverlay.getTextPaint().setTextSize(Util.dp2px(getApplicationContext(), 64));
        textMapOverlay.setTopOffset(-300);
//        textMapOverlay.setLocationOnMap(0,300);
        mMapView.getLayerGroup().add(textMapOverlay);

    }
}
