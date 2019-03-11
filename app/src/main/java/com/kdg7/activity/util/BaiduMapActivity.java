package com.kdg7.activity.util;

import java.io.File;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.common.Constant;

/**
 * 百度地图
 * 
 * @author zdkj
 *
 */
public class BaiduMapActivity extends FrameActivity {

	private String msgStr, cityStr, keyStr;
	private MapView bmapView;
	private Button bt_search;
	private EditText et_search;
	private BaiduMap mBaiduMap;
	private PoiSearch mPoiSearch;
	private BDLocation location;
	private LocationClient mLocClient;
	private BDLocationListener myListener = new MyLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try {
			SDKInitializer.initialize(getApplicationContext());
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			appendMainBody(R.layout.activity_util_baidumap);
			initVariable();
			initView();
			initListeners();
		} catch (Exception e) {
			msgStr = "初始化失败";
			Message msg = new Message();
			msg.what = Constant.FAIL;
			handler.sendMessage(msg);
		}

	}

	protected void initVariable() {
		bmapView = (MapView) findViewById(R.id.bmapView);
		bt_search = (Button) findViewById(R.id.bt_search);
		et_search = (EditText) findViewById(R.id.et_search);
		mBaiduMap = bmapView.getMap();

		Intent intent = getIntent();
		keyStr = intent.getStringExtra("keyStr");
		et_search.setText(keyStr);
		
		mLocClient = new LocationClient(getApplicationContext()); 
		mLocClient.registerLocationListener(myListener); 

		setLocationClientOption();
	}

	protected void initView() {
		tv_title.setText("百度地图");
	}

	protected void initListeners() {

		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_topback:
					onBackPressed();
					break;
				case R.id.bt_search:
					if (!isNotNull(et_search)) {
						toastShowMessage("��¼���ַ");
						return;
					}
					keyStr = et_search.getText().toString().trim();
					startSearch();
					break;
				}
			}
		};

		topBack.setOnClickListener(onClickListener);
		bt_search.setOnClickListener(onClickListener);
	}

	protected void getWebService(String s) {

	}

	private void startSearch() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(cityStr)
				.keyword(keyStr).pageNum(0));

	}

	private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {

		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult result) {

		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			if (result == null || result.error!=SearchResult.ERRORNO.NO_ERROR) {
				mBaiduMap.clear();
				toastShowMessage("未找到结果");
			} else {
				mBaiduMap.clear();
				PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
			}
			mPoiSearch.destroy();
		}

	};

	private void setLocationClientOption() {

		final LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.disableCache(true);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void setCenter() {
		cityStr = location.getCity();
		LatLng cenpt = new LatLng(location.getLatitude(),
				location.getLongitude());
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(12).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		startSearch();
	}

	class MyPoiOverlay extends PoiOverlay {
		public MyPoiOverlay(BaiduMap arg0) {
			super(arg0);
		}

		@Override
		public boolean onPoiClick(int index) {
			PoiResult poiResult = getPoiResult();
			final PoiInfo poiInfo = poiResult.getAllPoi().get(index);
			dialogShowMessage(poiInfo.name + "-" + poiInfo.address, "导航", "取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface face,
								int paramAnonymous2Int) {
							showPopouWindow(poiInfo);

						}
					}, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface face,
								int paramAnonymous2Int) {

						}
					});

			return super.onPoiClick(index);
		}
	}

	private void showPopouWindow(final PoiInfo poiInfo) {
		final Dialog mCameraDialog = new Dialog(this, R.style.my_dialog);
		LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_camera_control, null);
		Button btn_3 = (Button) root.findViewById(R.id.btn_3);
		Button btn_2 = (Button) root.findViewById(R.id.btn_2);
		Button btn_1 = (Button) root.findViewById(R.id.btn_1);
		btn_3.setText("百度导航");
		btn_2.setText("高德导航");
		btn_1.setText("取消");
		btn_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openBaiduMap(location.getLongitude(),
						location.getLatitude(),
						location.getAddrStr(),
						poiInfo.location.longitude,
						poiInfo.location.latitude,
						poiInfo.address);
			}
		});
		btn_2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LatLng from = BD09ToGCJ02(new LatLng(location.getLongitude(),location.getLatitude()));
				LatLng to = BD09ToGCJ02(new LatLng(poiInfo.location.longitude,poiInfo.location.latitude));
				openGdMap(from.longitude,
						from.latitude,
						location.getAddrStr(),
						to.longitude,
						to.latitude,
						poiInfo.address);
				
			}
		});
		btn_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCameraDialog.dismiss();
				
			}
		});
		
		
		mCameraDialog.setContentView(root);
		Window dialogWindow = mCameraDialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		lp.x = 0; // 新位置X坐标
		lp.y = -20; // 新位置Y坐标
		lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
//		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//		lp.alpha = 9f; // 透明度
		root.measure(0, 0);
		lp.height = root.getMeasuredHeight();
		lp.alpha = 9f; // 透明度
		dialogWindow.setAttributes(lp);
		mCameraDialog.show();
		
//		String[] items = new String[] { "百度导航", "高德导航", "取消" };
//		final AlertDialog.Builder builder = new AlertDialog.Builder(
//				BaiduMapActivity.this);
//		builder.setIcon(R.drawable.btn_img_down)
//				.setTitle("请选择导航")
//				.setItems(items,
//						new android.content.DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								if (which == 0) {
//									openBaiduMap(location.getLongitude(),
//											location.getLatitude(),
//											location.getAddrStr(),
//											poiInfo.location.longitude,
//											poiInfo.location.latitude,
//											poiInfo.address);
//								} else if (which == 1) {
//									LatLng from = BD09ToGCJ02(new LatLng(location.getLongitude(),location.getLatitude()));
//									LatLng to = BD09ToGCJ02(new LatLng(poiInfo.location.longitude,poiInfo.location.latitude));
//									openGdMap(from.longitude,
//											from.latitude,
//											location.getAddrStr(),
//											to.longitude,
//											to.latitude,
//											poiInfo.address);
//								} else {
//									builder.create().dismiss();
//								}
//
//							}
//						});
//		builder.create().show();
	}

	private void openBaiduMap(double fromLon, double fromLat,String address, double toLon,
			double toLat, String describle) {
		try {
			StringBuilder loc = new StringBuilder();
			loc.append("intent://map/direction?origin=latlng:");
			loc.append(fromLat);
			loc.append(",");
			loc.append(fromLon);
			loc.append("|name:");
			loc.append(address);
			loc.append("&destination=latlng:");
			loc.append(toLat);
			loc.append(",");
			loc.append(toLon);
			loc.append("|name:");
			loc.append(describle);
			loc.append("&mode=driving");
			loc.append("&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
			Intent intent = Intent.getIntent(loc.toString());
			if (isInstallPackage("com.baidu.BaiduMap")) {
				startActivity(intent);
			} else {
				LatLng ptMine = new LatLng(fromLat, fromLon);
				LatLng ptPosition = new LatLng(toLat, toLon);

				NaviParaOption para = new NaviParaOption().startPoint(ptMine)
						.endPoint(ptPosition);
				BaiduMapNavigation.openWebBaiduMapNavi(para,
						getApplicationContext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openGdMap(double slat, double slon, String sname, double dlat,
			double dlon, String dname) {
		try {
			String uriString = null;
			StringBuilder builder = new StringBuilder(
					"amapuri://route/plan?sourceApplication=maxuslife");
			builder.append("&sname=").append(sname).append("&slat=")
					.append(slat).append("&slon=").append(slon)
					.append("&dlat=").append(dlat).append("&dlon=")
					.append(dlon).append("&dname=").append(dname)
					.append("&dev=0").append("&t=0");
			uriString = builder.toString();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setPackage("com.autonavi.minimap");
			intent.setData(Uri.parse(uriString));
			if (isInstallPackage("com.autonavi.minimap")) {
				startActivity(intent);
			}else{
				toastShowMessage("未安装高德导航");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换 即 百度 转 谷歌、高德
	 *
	 * @param latLng
	 * @returns
	 */
	public static LatLng BD09ToGCJ02(LatLng latLng) {
		double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
		double x = latLng.longitude - 0.0065;
		double y = latLng.latitude - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lat = z * Math.sin(theta);
		double gg_lng = z * Math.cos(theta);
		return new LatLng(gg_lat, gg_lng);
	}

	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换 即谷歌、高德 转 百度
	 *
	 * @param latLng
	 * @returns
	 */
	public static LatLng GCJ02ToBD09(LatLng latLng) {
		double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
		double z = Math.sqrt(latLng.longitude * latLng.longitude
				+ latLng.latitude * latLng.latitude)
				+ 0.00002 * Math.sin(latLng.latitude * x_pi);
		double theta = Math.atan2(latLng.latitude, latLng.longitude) + 0.000003
				* Math.cos(latLng.longitude * x_pi);
		double bd_lat = z * Math.sin(theta) + 0.006;
		double bd_lng = z * Math.cos(theta) + 0.0065;
		return new LatLng(bd_lat, bd_lng);
	}

	private boolean isInstallPackage(String packageName) {
		return new File("/data/data/" + packageName).exists();
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation locations) {
			if (locations == null) {
				return;
			} else {
				location = locations;
				Message msg = new Message();
				msg.what = Constant.NUM_7;
				handler.sendMessage(msg);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bmapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bmapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		bmapView.onPause();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(msgStr, null);
				break;
			case Constant.SUCCESS:
				dialogShowMessage_P(msgStr,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								onBackPressed();
							}
						});
				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			case Constant.NUM_7:
				setCenter();
				mLocClient.stop();
				break;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
}
