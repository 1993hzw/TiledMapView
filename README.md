# TiledMapView

[![](https://jitpack.io/v/1993hzw/TiledMapView.svg)](https://jitpack.io/#1993hzw/TiledMapView)

Tiled map loader for Android , supports a variety of projections, including Web Mercator projection, latitude and longitude projection and custom projection; supports locating, adding layers and overlays.

***Android瓦片地图加载，支持多种投影，包括Web墨卡托投影，经纬度直投及自定义投影等；支持定位，添加图层和覆盖物。***

![googlemap](https://raw.githubusercontent.com/1993hzw/common/master/tiledmap/googlemap.gif)

![tianditu](https://raw.githubusercontent.com/1993hzw/common/master/tiledmap/tianditu.png)

# Usage 用法

#### Gradle 

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
 
dependencies {
    compile 'com.github.1993hzw:TiledMapView:1.0.0'
}
```

TiledMapView uses the library Picasso as the default images loader. So you should add the additional dependence if you want to use the Picasso :

***TiledMapView使用Picasso库作为默认图像加载程序。因此，如果你想使用Picasso，应该额外增加依赖：***

```gradle
dependencies {
    implementation 'com.squareup.picasso:picasso:2.71828'
}
```

#### Code 

Add the TiledMapView to your layout.xml:

***在布局里添加TiledMapView：***

```xml
<cn.forward.tiledmapview.TiledMapView
    android:id="@+id/mapview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```java
TiledMapView mapView = (TiledMapView)findViewById(R.id.mapview);
```

Now, you can add a tile layer. For example, you can add the Google tiled map:

***现在你可以添加瓦片图层。以加载谷歌地图为例：***

```java
TiledMapView mapView = (TiledMapView) findViewById(R.id.mapview);
ITileLayer googleTileLayer = new GoogleTileLayer(mMapView, GoogleOnlineTileImageSource.ImgType.SATILLITE_WITH_MARKER);
mapView.getLayerGroup().add(googleTileLayer);
```

Currently, Tiled MapView directly supports loading Google maps （[GoogleTileLayer](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/layer/google/GoogleTileLayer.java)）, Tianditu maps（[TiandituTileLayer](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/layer/tianditu/TiandituTileLayer.java)）, and the custom tiled maps.

***目前，TiledMapView直接支持加载谷歌地图（[GoogleTileLayer](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/layer/google/GoogleTileLayer.java)），天地图（[TiandituTileLayer](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/layer/tianditu/TiandituTileLayer.java)），以及自定义瓦片地图。***

Also, you can add some overlays:

***另外，你也可以添加覆盖物：***

```java
TextPixelOverlay textPixelOverlay = new TextPixelOverlay("Hello world!");
textPixelOverlay.setBackgroundColor(0x99ffffff);
textPixelOverlay.getTextPaint().setColor(Color.BLUE);
textPixelOverlay.getTextPaint().setTextSize(Util.dp2px(getApplicationContext(), 14));
textPixelOverlay.setLocationOnMap(0,-300);
mapView.getLayerGroup().add(textPixelOverlay);
```

You can use [BitmapPixelOverlay](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/overlay/BitmapPixelOverlay.java)/[BitmapMapOverlay](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/overlay/BitmapMapOverlay.java), if you want add a bitmap overlay.

***可以通过使用[BitmapPixelOverlay](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/overlay/BitmapPixelOverlay.java)/[BitmapMapOverlay](https://github.com/1993hzw/TiledMapView/blob/master/library/src/main/java/cn/forward/tiledmapview/overlay/BitmapMapOverlay.java)添加图片覆盖物***

# Extending 拓展

There is [a sample of LOL game map](https://github.com/1993hzw/TiledMapView/tree/master/app/src/main/java/cn/forward/tiledmapview/demo/lol) which shows how to load the custom tiled map.

***这里有一个加载[LOL游戏地图的示例](https://github.com/1993hzw/TiledMapView/tree/master/app/src/main/java/cn/forward/tiledmapview/demo/lol)，显示了如何加载自定义瓦片地图***

![lol](https://raw.githubusercontent.com/1993hzw/common/master/tiledmap/lol.png)

TiledMapView is a powerful, customizable and extensible loading library. There will be more documentation in the future, but you can now find more features by reading the code. Just enjoy it!

***TiledMapView是一个功能强大、可定制和可扩展的加载库。将来会提供更多的文档，当然，现在您可以通过阅读代码来找到更多的特性，尽情探索吧！***


# The developer 开发者

154330138@qq.com  hzw19933@gmail.com

Q&A <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=9cef40e0b665e25745323941baa9f3cd89a75bba055b9922ce3779fb691ea5bc"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="TiledMapLoader交流群" title="TiledMapLoader交流群"></a>  Group ID: 885437848

# License

  ```
  MIT License
  
  Copyright (c) 2018 huangziwei
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
  ```

# Donations 打赏

If this project helps you a lot and you want to support the project's development and maintenance of this project, feel free to scan the following QR code for donation. Your donation is highly appreciated. Thank you!

***如果这个项目对您有很大帮助，并且您想支持该项目的项目开发和维护，请扫描以下二维码进行捐赠。非常感谢您的支持！***

![donate](https://raw.githubusercontent.com/1993hzw/common/master/payment.png)
