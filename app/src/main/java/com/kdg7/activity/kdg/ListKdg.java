package com.kdg7.activity.kdg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.po.User;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;

public class ListKdg extends FrameActivity {

	private String flag,title;
	private ListView listView;
	private EditText et_search;
	private SimpleAdapter adapter;
	private ArrayList<Map<String, Object>> datalist,data;
	private String[] from;
	private int[] to;
	private int queryType;
	private String cs,name;
	private SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appendMainBody(R.layout.activity_kdg_list);
		initVariable();
		initView();
		initListeners();
		

	}

	protected void initVariable() {

		listView = (ListView) findViewById(R.id.listView);
		from = new String[] { "textView1", "faultuser", "zbh", "timemy",
				"datemy", "ztzt" };
		to = new int[] { R.id.textView1, R.id.yytmy, R.id.pgdhmy, R.id.timemy,
				R.id.datemy, R.id.ztzt };
		spf = getSharedPreferences("loginsp", LoginActivity.MODE_PRIVATE);
		findViewById(R.id.ll_filter).setVisibility(View.VISIBLE);
		et_search = (EditText) findViewById(R.id.et_search);
	}

	protected void initView() {
		title = getIntent().getStringExtra("title");
		queryType = getIntent().getIntExtra("queryType", 0);
		tv_title.setText(title);
	}

	protected void initListeners() {
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_topback:
					onBackPressed();
					finish();
					break;

				}
			}
		};

		topBack.setOnClickListener(onClickListener);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= 0) {
					DataCache.getinition().setData(data.get(position));
					Intent intent = null;
					if(queryType==3001){
						intent = new Intent(getApplicationContext(),MyqdKdg.class);
					}
					startActivity(intent);
				}

			}
		});

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, int start,
					int before, int count) {
				name = s.toString();
				Config.getExecutorService().execute(new Runnable() {

					@Override
					public void run() {

						getWebService("getdata");
					}
				});
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showProgressDialog();
		Config.getExecutorService().execute(new Runnable() {

			@Override
			public void run() {

				getWebService("query");
			}
		});
	};

	protected void getWebService(String s) {

		if ("query".equals(s)) {
			try {
				String sqlid = "";
				datalist = new ArrayList<Map<String, Object>>();
				String userStr = spf.getString("userStr", "");
				User user = User.jsonToUser(userStr);
				cs = user.getHykh();
				if (queryType == 3001) {
					sqlid = "_PAD_BZGL_GDCX";
				}
				
				String ret = WebService.getinstance().getData(sqlid, cs);
				JSONObject json =  new JSONObject(ret);
				flag = json.getString("flag");
				JSONArray jsonArray = json.getJSONArray("tableA");
				if (Integer.parseInt(flag) > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = jsonArray.getJSONObject(i);
						Map<String, Object> item = new HashMap<String, Object>();
						String timeff = "";
						item.put("textView1", getListItemIcon(i));
						if (queryType == 3001) {
							timeff = temp.getString("bzsj");
							item.put("bzsj", timeff);
							item.put("faultuser", temp.getString("wdmc"));
							item.put("zbh", temp.getString("zbh"));
							item.put("wdmc", temp.getString("wdmc"));
							item.put("lxdh", temp.getString("lxdh"));
							item.put("jddz", temp.getString("jddz"));
						}
						
						timeff = timeff.substring(2);
						item.put("timemy", mdateformat(1, timeff));
						item.put("datemy", mdateformat(0, timeff));

						datalist.add(item);
					}
					data = datalist;
					Message msg = new Message();
					msg.what = Constant.SUCCESS;
					handler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = Constant.FAIL;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = Constant.NETWORK_ERROR;
				handler.sendMessage(msg);
			}
		} 
		
		if ("getdata".equals(s)) {
			data = new ArrayList<Map<String, Object>>();
			try {
				for (int i = 0; i < datalist.size(); i++) {
					Map<String, Object> map = datalist.get(i);
					if (map.get("zbh").toString().indexOf(name) != -1||map.get("xqmc").toString().indexOf(name) != -1) {
						data.add(map);
					}
				}
				Message msg = new Message();
				msg.what = Constant.SUCCESS;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = Constant.SUCCESS;
				handler.sendMessage(msg);
			}
		}

	}

	private class CurrAdapter extends SimpleAdapter {

		public CurrAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);

		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			final View view = super.getView(position, convertView, parent);
			try {
				Map<String, Object> item = datalist.get(position);
				String red = (String) item.get("red");
				if ("1".equals(red)) {
					view.setBackgroundResource(R.color.red);
				} else {
					view.setBackgroundResource(R.color.white);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return view;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			case Constant.SUCCESS:
				adapter = new CurrAdapter(ListKdg.this,
						data,
						R.layout.listview_dispatchinginformationreceiving_item,
						from, to);
				listView.setAdapter(adapter);
				break;
			case Constant.FAIL:
				dialogShowMessage_P("没有查询数据",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						onBackPressed();
					}
				});
				break;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}

	};

}
