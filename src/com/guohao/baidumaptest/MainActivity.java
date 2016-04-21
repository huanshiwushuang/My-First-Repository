package com.guohao.baidumaptest;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private MapView mapView;
	private String provider;
	private LocationManager locationManager;
	private BaiduMap baiduMap;
	private LocationListener listener;
	private	MyLocationData.Builder builder;
	private Boolean isFirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		
		initView();
		getLocation();
	}
	
	private void navigateTo(Location location) {
		
		if (isFirst) {
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
			baiduMap.animateMapStatus(update);
			update = MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			isFirst = false;
		}
		builder.latitude(location.getLatitude());
		builder.longitude(location.getLongitude());
		MyLocationData locationData = builder.build();
		baiduMap.setMyLocationData(locationData);
	}

	private void getLocation() {
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		}else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		}else {
			Toast.makeText(this, "没有可用的定位服务", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.d("guohao", "provider:->"+provider);
		Location location = locationManager.getLastKnownLocation(provider);
		Log.d("guohao", "dingwei :"+location);
		if (location != null) {
			navigateTo(location);
		}else {
			Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
		}
		locationManager.requestLocationUpdates(provider, 5000, 1, listener);
	}

	private void initView() {
		mapView = (MapView) findViewById(R.id.map_view);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		baiduMap = mapView.getMap();
		builder = new MyLocationData.Builder();
		//设置我的位置可用
		baiduMap.setMyLocationEnabled(true);
		
		listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					navigateTo(location);
				}
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		baiduMap.setMyLocationEnabled(false);
	}
}
