/*
 * Copyright (C) 2019  Ziwei Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package cn.forward.tiledmapview.util;

import java.util.Arrays;

public class GeoUtil {

    public static final double[] WEB_MERCATOR_COORDINATE_RANGE = new double[]{-20037508.3427892, 20037508.3427892};
    public static final double WEB_MERCATOR_LENGTH_HALF = 20037508.3427892;

    private static double PI = Math.PI;
    private static double x_PI = PI * 3000.0 / 180.0;
    private static double a = 6378245.0; // 长半轴
    private static double f = 0.00669342162296594323; // 扁率

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     */
    public static double[] bd09ToGcj02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double[] transformed = new double[2];
        transformed[0] = z * Math.cos(theta);
        transformed[1] = z * Math.sin(theta);
        return transformed;
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     */
    public static double[] gcj02ToBd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double[] transformed = new double[2];
        transformed[0] = z * Math.cos(theta) + 0.0065;
        transformed[1] = z * Math.sin(theta) + 0.006;
        return transformed;
    }

    /**
     * WGS84 转 GCj02
     */
    public static double[] wgs84ToGcj02(double lng, double lat) {
        double dlat = transformLatitude(lng - 105.0, lat - 35.0);
        double dlng = transformLongitude(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - f * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - f)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double[] transformed = new double[2];
        transformed[0] = lng + dlng;
        transformed[1] = lat + dlat;
        return transformed;
    }

    /**
     * GCJ02 转 WGS84
     */
    public static double[] gcj02ToWgs84(final double lng, final double lat) {
        double dLat = transformLatitude(lng - 105.0, lat - 35.0);
        double dLon = transformLongitude(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - f * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - f)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        double[] transformed = new double[2];
        transformed[0] = lng * 2 - mgLon;
        transformed[1] = lat * 2 - mgLat;
        return transformed;
    }

    /**
     * WGS84 转 BD-09
     */
    private static double[] wgs84ToBd09(double lng, double lat) {
        double[] lngLatGcj02 = wgs84ToGcj02(lng, lat);
        return gcj02ToBd09(lngLatGcj02[0], lngLatGcj02[1]);
    }

    /**
     * BD-09 转 WGS84
     */
    private static double[] bd09ToWgs84(double lng, double lat) {
        double[] lngLatGcj02 = bd09ToGcj02(lng, lat);
        return gcj02ToWgs84(lngLatGcj02[0], lngLatGcj02[1]);
    }

    private static double transformLatitude(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLongitude(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    // test
    public static void main(String[] args) {
        final double[] lnglatWgs84 = new double[]{116.40387397, 39.91488908};
        final double[] lnglatGcj02 = wgs84ToGcj02(lnglatWgs84[0], lnglatWgs84[1]);
        final double[] lnglatBd09 = gcj02ToBd09(lnglatGcj02[0], lnglatGcj02[1]);
        System.out.println("wgs84:" + Arrays.toString(lnglatWgs84));
        System.out.println("gcj02:" + Arrays.toString(lnglatGcj02));
        System.out.println("bd09:" + Arrays.toString(lnglatBd09));
        System.out.println();
        System.out.println("wgs84ToGcj02:" + Arrays.toString(lnglatGcj02)); // true
        System.out.println("gcj02ToWgs84:" + Arrays.toString(gcj02ToWgs84(lnglatGcj02[0], lnglatGcj02[1])));
        System.out.println();
        System.out.println("gcj02ToBd09:" + Arrays.toString(lnglatBd09));

        System.out.println();
        System.out.println("wgs84ToBd09:" + Arrays.toString(wgs84ToBd09(lnglatWgs84[0], lnglatWgs84[1])));
        System.out.println("bd09ToGcj02:" + Arrays.toString(bd09ToGcj02(lnglatBd09[0], lnglatBd09[1])));
        System.out.println("bd09ToWgs84:" + Arrays.toString(bd09ToWgs84(lnglatBd09[0], lnglatBd09[1])));
    }
}
