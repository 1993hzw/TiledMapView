package cn.forward.tiledmapview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import cn.forward.tiledmapview.util.LogUtil;

public class MapLocationManager {

    public static final int TWO_MINUTES = 2 * 60 * 1000;

    private static MapLocationManager sInstance;
    private Context mContext;
    private LocationManager mLocationManager;
    private Map<LocationListener, LocationListener> nGpsListenerMap = new HashMap<>();
    private Map<LocationListener, LocationListener> mNetworkListenerMap = new HashMap<>();


    private MapLocationManager(Context context) {
        mContext = context;
        mLocationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
    }

    public static MapLocationManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MapLocationManager.class) {
                if (sInstance == null) {
                    sInstance = new MapLocationManager(context.getApplicationContext());
                }
            }
        }

        return sInstance;
    }


    @SuppressLint("MissingPermission")
    private void switch2networkTemp(final LocationListener listener) {
        LogUtil.d("MapLocationManager", "switch2networkTemp");
        LocationListener locationListener = new LocationListener() {
            Location innerLastLocation = null;

            public void onLocationChanged(Location location) {
                if (MapLocationManager.this.isBetterLocation(location, this.innerLastLocation)) {
                    this.innerLastLocation = location;
                    listener.onLocationChanged(this.innerLastLocation);
                    LogUtil.d("MapLocationManager", "网络定位->经度：" + this.innerLastLocation.getLatitude() + "  纬度：" + this.innerLastLocation.getLongitude());
                    return;
                }
                LogUtil.d("MapLocationManager", "网络定位：不更新最新位置");
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {
            }
        };
        this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 5.0F, locationListener);
        this.mNetworkListenerMap.put(listener, locationListener);
    }

    private boolean isBetterLocation(Location location,
                                     Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    public boolean containsListener(LocationListener listener) {
        return nGpsListenerMap.containsKey(listener) || mNetworkListenerMap.containsKey(listener);
    }

    @SuppressLint("MissingPermission")
    public void removeListener(LocationListener paramLocationListener) {
        LocationListener localLocationListener1 = (LocationListener) this.nGpsListenerMap.get(paramLocationListener);
        LocationListener localLocationListener2 = (LocationListener) this.mNetworkListenerMap.get(paramLocationListener);
        if (localLocationListener1 != null) {
            LogUtil.d("MapLocationManager", "removeListener-->GPS");
            this.mLocationManager.removeUpdates(localLocationListener1);
            this.nGpsListenerMap.remove(paramLocationListener);
        }
        if (localLocationListener2 != null) {
            LogUtil.d("MapLocationManager", "removeListener-->NETWORK");
            this.mLocationManager.removeUpdates(localLocationListener2);
            this.mNetworkListenerMap.remove(paramLocationListener);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean addListener(final LocationListener listener) {
        if (containsListener(listener)) {
            throw new RuntimeException("The listener has been added.");
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.d("MapLocationManager", "没有gps定位权限");
            return false;
        }

        if (!this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LogUtil.d("MapLocationManager", "gps定位未开启");
        }

        Location lastLocation = null;
        if (this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            lastLocation = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if (this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            lastLocation = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        LocationListener innerLocationListener = new LocationListener() {
            Location innerLastLocation = null;

            public void onLocationChanged(Location location) {
                if (MapLocationManager.this.isBetterLocation(location, this.innerLastLocation)) {
                    this.innerLastLocation = location;
                    listener.onLocationChanged(this.innerLastLocation);
                    LogUtil.d("MapLocationManager", "gps定位");
                    return;
                }
                LogUtil.d("MapLocationManager", "gps定位：不更新最新位置");
            }

            public void onProviderDisabled(String provider) {
                LogUtil.d("MapLocationManager", "gps关闭");
                MapLocationManager.this.switch2networkTemp(listener);
            }

            public void onProviderEnabled(String provider) {
                LogUtil.d("MapLocationManager", "gps打开");
                MapLocationManager.this.mLocationManager.removeUpdates(mNetworkListenerMap.get(listener));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        if (lastLocation != null) {
            innerLocationListener.onLocationChanged(lastLocation);
        }

        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5.0F, innerLocationListener);
        this.nGpsListenerMap.put(listener, innerLocationListener);

        return true;
    }

}
