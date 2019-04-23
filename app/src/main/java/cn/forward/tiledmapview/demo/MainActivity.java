package cn.forward.tiledmapview.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.forward.tiledmapview.TiledMapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TiledMapView.setDebugMode(false);
    }

    public void openGoogle(View view) {
        Intent intent = new Intent(this, TiledMapDemoActivity.class);
        intent.putExtra(TiledMapDemoActivity.TYPE, TileType.Google);
        startActivity(intent);
    }

    public void openTianditu(View view) {
        Intent intent = new Intent(this, TiledMapDemoActivity.class);
        intent.putExtra(TiledMapDemoActivity.TYPE, TileType.Tianditu);
        startActivity(intent);
    }

    public void openLol(View view) {
        Intent intent = new Intent(this, LolMapDemoActivity.class);
        startActivity(intent);
    }
}
