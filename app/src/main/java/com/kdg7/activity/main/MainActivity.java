package com.kdg7.activity.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.kdg.FwbgKdg;
import com.kdg7.activity.kdg.JdxyKdg;
import com.kdg7.activity.kdg.ListKdg;
import com.kdg7.activity.kdg.SmdwKdg;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.myview.MarqueeTextView;
import com.kdg7.po.User;
import com.kdg7.utils.AppUtils;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;
//import com.tencent.android.otherPush.StubAppUtils;
import com.tencent.android.tpush.XGCustomPushNotificationBuilder;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

@SuppressLint("ResourceAsColor")
public class MainActivity extends FrameActivity{

	private LinearLayout linear_sy_top, tab_bottom_esp, tab_bottom_kdg,
			tab_bottom_kc, tab_bottom_js, tab_bottom_w;
	private ImageView sy_top_1, sy_top_2;
	private int num = 1;
	private List<String> menu;
	private TextView tishi, tv_esp, tv_kdg, tv_kc, tv_js, tv_w, tv_ry;
	private ImageView img_esp, img_kdg, img_kc, img_js, img_w;
	private MarqueeTextView tv_marquee;
	private String ts_num_msg_kdg = "",token;
	private int currType = 1;

	private List<Map<String, Object>> esp_list, kdg_list, kc_list, js_list,
			w_list;
	private GridView gridview;
	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;
	private String[] from;
	private int[] to;

	private ListView listview;
	private SimpleAdapter adapter_listview;
	private List<Map<String, Object>> data_listview;
	private String[] from_listview;
	private int[] to_listview;

    private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListeners();
		changeBackground();
		runChangeService();
		runServices();
		
	}

	private void runServices() {
	
		try {
			
//			StubAppUtils.attachBaseContext(this);
			XGPushConfig.enableOtherPush(getApplicationContext(), true);
			XGPushConfig.setHuaweiDebug(true);
			XGPushConfig.setMzPushAppId(this, "1003519");
			XGPushConfig.setMzPushAppKey(this, "5ce6cf6b3daa4aa4a142ce468b5878bf");
			XGPushConfig.setMiPushAppId(getApplicationContext(), "2882303761517925208");
			XGPushConfig.setMiPushAppKey(getApplicationContext(), "5481792559208");
			
			
			XGCustomPushNotificationBuilder build = new XGCustomPushNotificationBuilder();
			build.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.strum));
			build.setDefaults(Notification.DEFAULT_LIGHTS); 
			build.setFlags(Notification.FLAG_AUTO_CANCEL); 
			build.setLayoutId(R.layout.notification);
			build.setLayoutTextId(R.id.content);
			build.setLayoutTitleId(R.id.title);
			build.setLayoutIconDrawableId(R.drawable.logo);
			build.setLayoutTimeId(R.id.time);

			XGPushManager.setDefaultNotificationBuilder(this, build);
			XGPushManager.registerPush(this, new XGIOperateCallback() {
				@Override
				public void onSuccess(Object data, int flag) {
					token = (String) data;
					User user = DataCache.getinition().getUser();
					if(!token.equals(user.getTokenid())){
						Config.getExecutorService().execute(new Runnable() {

							@Override
							public void run() {
								getWebService("updateToken");
							}
						});
					}
					
				}

				@Override
				public void onFail(Object data, int errCode, String msg) {
					token = "";
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected void initView() {

		tishi = (TextView) findViewById(R.id.tishi);
		tv_ry = (TextView) findViewById(R.id.tv_ry);
		linear_sy_top = (LinearLayout) findViewById(R.id.linear_sy_top);
		tab_bottom_esp = (LinearLayout) findViewById(R.id.tab_bottom_esp);
		tab_bottom_kdg = (LinearLayout) findViewById(R.id.tab_bottom_kdg);
		tab_bottom_kc = (LinearLayout) findViewById(R.id.tab_bottom_kc);
		tab_bottom_js = (LinearLayout) findViewById(R.id.tab_bottom_js);
		tab_bottom_w = (LinearLayout) findViewById(R.id.tab_bottom_w);
		tv_marquee = (MarqueeTextView) findViewById(R.id.tv_marquee);
		sy_top_1 = (ImageView) findViewById(R.id.sy_top_1);
		sy_top_2 = (ImageView) findViewById(R.id.sy_top_2);
		img_esp = (ImageView) tab_bottom_esp.findViewById(R.id.img_esp);
		tv_esp = (TextView) tab_bottom_esp.findViewById(R.id.tv_esp);
		img_kdg = (ImageView) tab_bottom_kdg.findViewById(R.id.img_kdg);
		tv_kdg = (TextView) tab_bottom_kdg.findViewById(R.id.tv_kdg);
		img_kc = (ImageView) tab_bottom_kc.findViewById(R.id.img_kc);
		tv_kc = (TextView) tab_bottom_kc.findViewById(R.id.tv_kc);
		img_js = (ImageView) tab_bottom_js.findViewById(R.id.img_js);
		tv_js = (TextView) tab_bottom_js.findViewById(R.id.tv_js);
		img_w = (ImageView) tab_bottom_w.findViewById(R.id.img_w);
		tv_w = (TextView) tab_bottom_w.findViewById(R.id.tv_w);

		gridview = (GridView) findViewById(R.id.gridview);
		from = new String[] { "id", "img", "name", "type" };
		to = new int[] { R.id.menu_id, R.id.menu_img, R.id.menu_name,
				R.id.menu_type };

		listview = (ListView) findViewById(R.id.listview);

		from_listview = new String[] { "textView1", "faultuser", "zbh",
				"timemy", "datemy", "ztzt" };
		to_listview = new int[] { R.id.textView1, R.id.yytmy, R.id.pgdhmy,
				R.id.timemy, R.id.datemy, R.id.ztzt };

		initMenus();

		tv_ry.setText(DataCache.getinition().getUser().getHykh());
		Intent intent = getIntent();
		currType = intent.getIntExtra("currType", 5);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listview = (ListView) findViewById(R.id.listview);
		loadMenus();
	}

	protected void initListeners() {
		
		OnClickListener onClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.sy_top_1:
					num = 1;
					changeBackground();
					break;
				case R.id.sy_top_2:
					num = 2;
					changeBackground();
					break;
				case R.id.linear_sy_top:
					gotoOtherPage();
					break;
				case R.id.tv_marquee:
					dialogShowMessage_P(tv_marquee.getText().toString(), null);
					break;
				case R.id.tab_bottom_esp:
					currType = 1;
					setMenus();
					break;
				case R.id.tab_bottom_kdg:
					currType = 2;
					setMenus();
					break;
				case R.id.tab_bottom_js:
					currType = 3;
					setMenus();
					break;
				case R.id.tab_bottom_kc:
					currType = 4;
					setMenus();
					break;
				case R.id.tab_bottom_w:
					currType = 5;
					setMenus();
					break;
				default:
					break;
			}
				
		}};
		
		
		
		sy_top_1.setOnClickListener(onClickListener);
		sy_top_2.setOnClickListener(onClickListener);
		linear_sy_top.setOnClickListener(onClickListener);
		tv_marquee.setOnClickListener(onClickListener);
		tab_bottom_kdg.setOnClickListener(onClickListener);
		tab_bottom_esp.setOnClickListener(onClickListener);
		tab_bottom_kc.setOnClickListener(onClickListener);
		tab_bottom_js.setOnClickListener(onClickListener);
		tab_bottom_w.setOnClickListener(onClickListener);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.menu_id);
				String menu_type = tv.getText().toString();
				turnTo(menu_type);
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					Map<String, Object> map = data_listview.get(position);
					String djzt = (String) map.get("djzt");
					String ywlx = (String) map.get("ywlx");
					Intent intent = null;
					if ("1".equals(ywlx)) {
						if ("2".equals(djzt)) {
							intent = new Intent(MainActivity.this,JdxyKdg.class);
						} else if ("3".equals(djzt)) {
							intent = new Intent(MainActivity.this,SmdwKdg.class);
						} else if ("4".equals(djzt)) {
							intent = new Intent(MainActivity.this,FwbgKdg.class);
						} 
					}
					DataCache.getinition().setData(data_listview.get(position));
					startActivity(intent);
				} catch (Exception e) {
				}

			}
		});
	}

	private void runChangeService() {

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (num ==1) {
                    num = 2;
                } else {
                    num = 1;
                }
                Message msg = new Message();
                msg.what = Constant.NUM_8;
                handler.sendMessage(msg);
            }
        }, 0,15000);
	}

	private void initMenus() {
		
		
		Map<String, Object> w_map = new HashMap<String, Object>();
		w_list = new ArrayList<Map<String, Object>>();
		w_map = new HashMap<String, Object>();
		w_map.put("id", "m_pad_myqd");
		w_map.put("name", "我的抢单");
		w_map.put("img", R.drawable.menu_kdg_jdxy);
		w_list.add(w_map);
		w_map = new HashMap<String, Object>();
		w_map.put("id", "m_pad_xgmm");
		w_map.put("name", "修改密码");
		w_map.put("img", R.drawable.menu_w_xgmm);
		w_list.add(w_map);
		
		w_map = new HashMap<String, Object>();
		w_map.put("id", "m_pad_help");
		w_map.put("name", "退出");
		w_map.put("img", R.drawable.menu_w_help);
		w_list.add(w_map);

	}

	private void loadMenus() {

		menu = DataCache.getinition().getMenu();
		if (menu == null || menu.isEmpty()) {
			skipActivity(LoginActivity.class);
			finish();
			return;
		}
		setMenus();
	}

	private void setMenus() {

		if (currType == 1) {
//			Config.getExecutorService().execute(new Runnable() {
//
//				@Override
//				public void run() {
//
//					getWebService("query");
//				}
//			});
//			Config.getExecutorService().execute(new Runnable() {
//
//				@Override
//				public void run() {
//
//					getWebService("wwgd");
//				}
//			});
			tv_esp.setTextColor(getResources().getColor(R.color.blue));
			tv_kdg.setTextColor(getResources().getColor(R.color.qianhui));
			tv_js.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kc.setTextColor(getResources().getColor(R.color.qianhui));
			tv_w.setTextColor(getResources().getColor(R.color.qianhui));
			img_esp.setBackgroundResource(R.drawable.btn_esp_up);
			img_kdg.setBackgroundResource(R.drawable.btn_kdg_down);
			img_js.setBackgroundResource(R.drawable.btn_js_down);
			img_kc.setBackgroundResource(R.drawable.btn_kc_down);
			img_w.setBackgroundResource(R.drawable.btn_w_down);
			loadEspMenus();
		} else if (currType == 2) {
			tv_kdg.setTextColor(getResources().getColor(R.color.blue));
			tv_esp.setTextColor(getResources().getColor(R.color.qianhui));
			tv_js.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kc.setTextColor(getResources().getColor(R.color.qianhui));
			tv_w.setTextColor(getResources().getColor(R.color.qianhui));
			img_esp.setBackgroundResource(R.drawable.btn_esp_down);
			img_kdg.setBackgroundResource(R.drawable.btn_kdg_up);
			img_js.setBackgroundResource(R.drawable.btn_js_down);
			img_kc.setBackgroundResource(R.drawable.btn_kc_down);
			img_w.setBackgroundResource(R.drawable.btn_w_down);
			loadKdgMenus();
		} else if (currType == 3) {
			tv_js.setTextColor(getResources().getColor(R.color.blue));
			tv_kc.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kdg.setTextColor(getResources().getColor(R.color.qianhui));
			tv_esp.setTextColor(getResources().getColor(R.color.qianhui));
			tv_w.setTextColor(getResources().getColor(R.color.qianhui));
			img_js.setBackgroundResource(R.drawable.btn_js_up);
			img_kc.setBackgroundResource(R.drawable.btn_kc_down);
			img_esp.setBackgroundResource(R.drawable.btn_esp_down);
			img_kdg.setBackgroundResource(R.drawable.btn_kdg_down);
			img_w.setBackgroundResource(R.drawable.btn_w_down);
			loadJsMenus();
		} else if (currType == 4) {
			tv_js.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kc.setTextColor(getResources().getColor(R.color.blue));
			tv_kdg.setTextColor(getResources().getColor(R.color.qianhui));
			tv_esp.setTextColor(getResources().getColor(R.color.qianhui));
			tv_w.setTextColor(getResources().getColor(R.color.qianhui));
			img_js.setBackgroundResource(R.drawable.btn_js_down);
			img_kc.setBackgroundResource(R.drawable.btn_kc_up);
			img_esp.setBackgroundResource(R.drawable.btn_esp_down);
			img_kdg.setBackgroundResource(R.drawable.btn_kdg_down);
			img_w.setBackgroundResource(R.drawable.btn_w_down);
			loadKcMenus();
		} else if (currType == 5) {
			tv_js.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kc.setTextColor(getResources().getColor(R.color.qianhui));
			tv_kdg.setTextColor(getResources().getColor(R.color.qianhui));
			tv_esp.setTextColor(getResources().getColor(R.color.qianhui));
			tv_w.setTextColor(getResources().getColor(R.color.blue));
			img_js.setBackgroundResource(R.drawable.btn_js_down);
			img_kc.setBackgroundResource(R.drawable.btn_kc_down);
			img_esp.setBackgroundResource(R.drawable.btn_esp_down);
			img_kdg.setBackgroundResource(R.drawable.btn_kdg_down);
			img_w.setBackgroundResource(R.drawable.btn_w_up);
			loadWMenus();
		}
	}

	private void loadEspMenus() {
		gridview.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
	}

	private void loadKdgMenus() {
		gridview.setVisibility(View.VISIBLE);
		listview.setVisibility(View.GONE);

		// menu.add("m_pad_kdg_fwbgxg_az");

		data_list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < kdg_list.size(); i++) {
			Map<String, Object> map = kdg_list.get(i);
			for (int k = 0; k < menu.size(); k++) {
				if (map.get("id").equals(menu.get(k))) {
					// map.put("type", "kdg");
					data_list.add(map);
					break;
				}
			}
		}

		sim_adapter = new CurrKdgAdapter(this, data_list,
				R.layout.include_menu, from, to);
		gridview.setAdapter(sim_adapter);
	}

	private void loadKcMenus() {

		gridview.setVisibility(View.VISIBLE);
		listview.setVisibility(View.GONE);

		data_list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < kc_list.size(); i++) {
			Map<String, Object> map = kc_list.get(i);
			for (int k = 0; k < menu.size(); k++) {
				if (map.get("id").equals(menu.get(k))) {
					// map.put("type", "kc");
					data_list.add(map);
					break;
				}
			}
		}

		sim_adapter = new CurrKdgAdapter(this, data_list,
				R.layout.include_menu, from, to);
		gridview.setAdapter(sim_adapter);

	}

	private void loadJsMenus() {
		gridview.setVisibility(View.VISIBLE);
		listview.setVisibility(View.GONE);

		data_list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < js_list.size(); i++) {
			Map<String, Object> map = js_list.get(i);
			for (int k = 0; k < menu.size(); k++) {
				if (map.get("id").equals(menu.get(k))) {
					// map.put("type", "js");
					data_list.add(map);
					break;
				}
			}
		}

		sim_adapter = new CurrKdgAdapter(this, data_list,
				R.layout.include_menu, from, to);
		gridview.setAdapter(sim_adapter);
	}

	private void loadWMenus() {
		gridview.setVisibility(View.VISIBLE);
		listview.setVisibility(View.GONE);

//		menu.add("m_pad_xgmm");
//		menu.add("m_pad_kdg_scsbb");
//		menu.add("m_pad_ckdqkc");
//		menu.add("m_pad_wbdbrk");
//		menu.add("m_pad_wbdbck");
		// menu.add("m_pad_kdg_pqzgjk");
		// menu.add("m_pad_kdg_pqzgjsl");
		// menu.add("m_pad_ryxxlr");

		// menu.add("m_pad_help");
		// menu.add("m_pad_jszl");

		data_list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < w_list.size(); i++) {
			Map<String, Object> map = w_list.get(i);
			for (int k = 0; k < menu.size(); k++) {
				if (map.get("id").equals(menu.get(k))) {
					// map.put("type", "w");
					data_list.add(map);
					break;
				}
			}
		}

		sim_adapter = new CurrKdgAdapter(this, data_list,
				R.layout.include_menu, from, to);
		gridview.setAdapter(sim_adapter);
	}

	private void turnTo(String menu_type) {

		if ("m_pad_myqd".equals(menu_type)) {
			Intent intent = new Intent(this, ListKdg.class);
			intent.putExtra("title", "我的抢单");
			intent.putExtra("queryType", 3001);
			startActivity(intent);
			return;
		} else if ("m_pad_help".equals(menu_type)) {
//			skipActivity(ZskActivity.class);
//			DataCache.getinition().setTitle("֪ʶ��");
			
			dialogShowMessage("是否确认退出登陆？", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface face,
						int paramAnonymous2Int) {
					SharedPreferences spf = getSharedPreferences("loginsp", LoginActivity.MODE_PRIVATE);
					SharedPreferences.Editor spfe = spf.edit();
					spfe.putString("userStr", "");
					spfe.commit();
					Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface face,
							int paramAnonymous2Int) {
						face.dismiss();
					}
				});
			
			return;
		}

	}

	private void gotoOtherPage() {
		// String url = "http://www.baidu.com";
		// switch (num) {
		// case 1:
		// url = "http://www.baidu.com";
		// break;
		// case 2:
		// url = "http://www.sina.com";
		// break;
		// case 3:
		// url = "http://china.nba.com/boxscore/";
		// break;
		// default:
		// break;
		// }
		// Uri uri = Uri.parse(url);
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// startActivity(intent);
	}

	private void changeBackground() {
		sy_top_1.setBackgroundResource(R.drawable.btn_sy_point_up);
		sy_top_2.setBackgroundResource(R.drawable.btn_sy_point_up);
		switch (num) {
		case 1:
			sy_top_1.setBackgroundResource(R.drawable.btn_sy_point_down);
			linear_sy_top.setBackgroundResource(R.drawable.a);
			break;
		case 2:
			sy_top_2.setBackgroundResource(R.drawable.btn_sy_point_down);
			linear_sy_top.setBackgroundResource(R.drawable.b);
			break;
		default:
			break;
		}

	}

	protected void initVariable() {

	}

	protected void getWebService(String s) {
		if ("query".equals(s)) {
			try {
				String cs = DataCache.getinition().getUser().getHykh() + "*"+ DataCache.getinition().getUser().getHykh();
				String ret = WebService.getinstance().getData("_PAD_SHGL_KDG_GDALL_WWG", cs);
				JSONObject json =  new JSONObject(ret);
				String flag = json.getString("flag");
				data_listview = new ArrayList<Map<String, Object>>();
				JSONArray jsonArray = json.getJSONArray("tableA");
				if (Integer.parseInt(flag) > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = jsonArray.getJSONObject(i);
						Map<String, Object> item = new HashMap<String, Object>();
						String timeff = "";
						item.put("textView1", getListItemIcon1(temp.getString("djzt")));
						timeff = temp.getString("bzsj");
						item.put("bzsj", timeff);
						item.put("faultuser", temp.getString("xqmc") + "   "
								+ temp.getString("xxdz"));
						item.put("zbh", temp.getString("zbh"));
						item.put("sx", temp.getString("sx"));
						item.put("qy", temp.getString("qy"));
						item.put("djzt", temp.getString("djzt"));
						item.put("xqmc", temp.getString("xqmc"));
						item.put("xxdz", temp.getString("xxdz"));
						item.put("gzxx", temp.getString("gzxx"));
						item.put("ywlx", temp.getString("ywlx"));
						item.put("ywlx2", temp.getString("ywlx2"));
						item.put("bz", temp.getString("bz"));
						item.put("ds", temp.getString("ds"));
						item.put("fwnr", temp.getString("fwnr"));
						item.put("sfhf", temp.getString("sfhf"));
						item.put("sfecgd", temp.getString("sfecgd"));
						item.put("sfecsm", temp.getString("sfecsm"));
						item.put("ecsmyy", temp.getString("ecsmyy"));
						item.put("kzzf1", temp.getString("kzzf1"));
						
						item.put("dygdh1_mc", temp.getString("dygdh1_mc"));
						item.put("dygdh2_mc", temp.getString("dygdh2_mc"));
						item.put("axdh", temp.getString("axdh"));
						item.put("ddh", temp.getString("ddh"));
						item.put("ddh_mc", temp.getString("ddh_mc"));
						item.put("yqsx", temp.getString("yqsx"));
						item.put("lxdh", temp.getString("lxdh"));
						
						item.put("kzsz4", temp.getString("kzsz4"));
						item.put("jsxh", temp.getString("jsxh"));
						item.put("jddz", temp.getString("jddz"));
						item.put("cssj", temp.getString("cssj"));
						
						item.put("kzzf3_bm", temp.getString("kzzf3_bm"));
						item.put("kzzf4_bm", temp.getString("kzzf4_bm"));
						item.put("kzzf5_bm", temp.getString("kzzf5_bm"));
						
						item.put("kzzf3", temp.getString("kzzf3"));
						item.put("kzzf4", temp.getString("kzzf4"));
						item.put("kzzf5", temp.getString("kzzf5"));
						
						item.put("timemy", "");
						item.put("datemy", temp.getString("djzt2"));
						data_listview.add(item);
					}
					
				} 
				Message msg = new Message();
				msg.what = Constant.SUCCESS;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = Constant.NETWORK_ERROR;
				handler.sendMessage(msg);
			}
		}
		if ("wwgd".equals(s)) {
			try {
				String cs = DataCache.getinition().getUser().getHykh()+"*"+DataCache.getinition().getUser().getHykh();
				String ret = WebService.getinstance().getData("_PAD_SHGL_KDG_WWGDS", cs);
				JSONObject json =  new JSONObject(ret);
				String flag = json.getString("flag");
				if (Integer.parseInt(flag) > 0) {
					String txzd = "";
					JSONArray jsonArray = json.getJSONArray("tableA");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = jsonArray.getJSONObject(i);
						txzd = temp.getString("wwgd");

					}
					AppUtils.sendBadgeNumber(this, "");
					AppUtils.sendBadgeNumber(this, txzd);
					ts_num_msg_kdg = "";
					Message msg = new Message();
					msg.what = Constant.NUM_7;
					handler.sendMessage(msg);
				} else {
					ts_num_msg_kdg = "";
					Message msg = new Message();
					msg.what = Constant.NUM_7;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = Constant.NETWORK_ERROR;
				handler.sendMessage(msg);
			}
		}
		
		if ("updateToken".equals(s)) {
			try {
//				String bbh = getVersion();
				String brand = android.os.Build.BRAND;
				String cs = DataCache.getinition().getUser().getHykh() + "$x"+ token + "$x" +"" + "$x" +brand;
				String ret = WebService.getinstance().setData("c#_PAD_IOS_TOKEN", cs, "", "");
				JSONObject json = new JSONObject(ret);
				if (Integer.parseInt(json.getString("flag")) > 0) {
					SharedPreferences spf = getSharedPreferences("loginsp", LoginActivity.MODE_PRIVATE);
					SharedPreferences.Editor spfe = spf.edit();
					String userStr = spf.getString("userStr", "");
					JSONObject obj = new JSONObject(userStr);
					obj.put("brand", brand);
					obj.put("tokenid", token);
					spfe.putString("userStr", obj.toString());
					spfe.commit();
					Message msg = new Message();
					msg.what = Constant.NUM_6;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
        if(timer != null){
            timer.cancel();
        }
		super.onBackPressed();

	}

	private class CurrKdgAdapter extends SimpleAdapter {

		public CurrKdgAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			final View view = super.getView(position, convertView, parent);
			LinearLayout l = (LinearLayout) view.findViewById(R.id.ll_menu);
			if (position % 4 == 0) {
				l.setBackgroundResource(R.drawable.menu_kdg_yellow);
			} else if (position % 4 == 1) {
				l.setBackgroundResource(R.drawable.menu_kdg_red);
			} else if (position % 4 == 2) {
				l.setBackgroundResource(R.drawable.menu_kdg_green);
			} else if (position % 4 == 3) {
				l.setBackgroundResource(R.drawable.menu_kdg_blue);
			}
			
			return view;
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			switch (msg.what) {
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;

			case Constant.SUCCESS:
				adapter_listview = new SimpleAdapter(MainActivity.this,
						data_listview,
						R.layout.listview_dispatchinginformationreceiving_item,
						from_listview, to_listview);
				listview.setAdapter(adapter_listview);
				break;
			case Constant.FAIL:
				break;
			case Constant.NUM_6:
//				dialogShowMessage_P(token, null);
				break;
			case Constant.NUM_7:
				tishi.setText(ts_num_msg_kdg);
				break;
			case Constant.NUM_8:
				changeBackground();
				break;
			}

		};
	};

}