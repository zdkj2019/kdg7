package com.kdg7.activity.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.main.MainActivity;
import com.kdg7.activity.register.RegisterActivity;
import com.kdg7.activity.w.SetParams;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.po.User;
import com.kdg7.utils.Config;
import com.kdg7.utils.DateUtil;
import com.kdg7.utils.PhoneNum;
import com.kdg7.webservice.WebService;

@SuppressLint("HandlerLeak")
public class LoginActivity extends FrameActivity {

	private SharedPreferences spf;
	private SharedPreferences.Editor spfe;
	private EditText et_tel, et_yzm;
	private Button btn_login, btn_yzm;
	private TextView tv_register, tv_sz;
	private String retmsg, retcode, yzm="";
	private int lastnum = 60;
	private boolean canclick = true;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		initVariable();
		initListeners();
		
		String userStr = spf.getString("userStr", "");
		if(!"".equals(userStr)){
			try {
				User user = User.jsonToUser(userStr);
				et_tel.setText(user.getTel());
				String loginTime = user.getLoginTime();
				Date loginDate = DateUtil.StringToDate(loginTime);
				Date nowDate = new Date();
				long time = 6*30;
				long num = nowDate.getTime()-loginDate.getTime();
				num = num/(24*60*60*1000);
				if(num<time){
					DataCache.getinition().setUser(user);
					Config.getExecutorService().execute(new Runnable() {

						@Override
						public void run() {
							getWebService("submit");
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void initView() {

		spf = getSharedPreferences("loginsp", LoginActivity.MODE_PRIVATE);
		
	}

	protected void initVariable() {
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_yzm = (Button) findViewById(R.id.btn_yzm);

		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_sz = (TextView) findViewById(R.id.tv_sz);
		tv_sz.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);

			TextView v = (TextView) findViewById(R.id.vers);
			v.setText("V: " + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void initListeners() {

		btn_yzm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNull(et_tel)) {
					toastShowMessage("请录入手机号码");
					return;
				}
				if (!PhoneNum.isMobile(et_tel.getText().toString().trim())) {
					toastShowMessage("请录入正确的手机号码");
					return;
				}
				if (canclick) {
					lastnum = 60;
					canclick = false;
					btn_yzm.setBackgroundResource(R.drawable.btn_gray);
					Config.getExecutorService().execute(new Runnable() {

						@Override
						public void run() {
							showDjs();
						}
					});
					sendMsg();
				}

			}
		});

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNull(et_tel)) {
					toastShowMessage("请录入手机号码");
					return;
				}
				if (isNull(et_yzm)) {
					toastShowMessage("请录入验证码");
					return;
				}
				if (!PhoneNum.isMobile(et_tel.getText().toString().trim())) {
					toastShowMessage("请录入正确的手机号码");
					return;
				}
				if (!yzm.equals(et_yzm.getText().toString().trim())) {
					toastShowMessage("请录入正确的验证码");
					return;
				}
				showProgressDialog();
				Config.getExecutorService().execute(new Runnable() {

					@Override
					public void run() {
						getWebService("submit");
					}
				});
			}
		});

		tv_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		tv_sz.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SetParams.class);
				startActivity(intent);
			}
		});

	}

	private void showDjs() {
		try {
			if(timer!=null){
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (!canclick) {
						lastnum--;
						Message msg = new Message();
						msg.what = Constant.NUM_6;
						handler.sendMessage(msg);
					}
				}
			}, 0, 1000);
		} catch (Exception e) {
		}

	}
	
	private void sendMsg(){
		showProgressDialog();
		Config.getExecutorService().execute(new Runnable() {

			@Override
			public void run() {
				try {
					String tel = et_tel.getText().toString().trim();
					yzm = DateUtil.getRandomNum();
					HashMap<String, Object> param = new HashMap<String, Object>();
					param.put("in0", DateUtil.encode16(tel));
					param.put("in1", DateUtil.encode16("您正在登录系统，验证码："+yzm));
					String result = (String) WebService.getinstance().getWebService(Constant.WEBSERVICE_URL_MSG_NAMESPACE, Constant.WEBSERVICE_URL_MSG, "uf_fsdx_165",param);
					if("1".equals(result)){
						Message msg = new Message();
						msg.what = Constant.NUM_7;
						handler.sendMessage(msg);
					}
					Message msg = new Message();
					msg.what = Constant.NUM_8;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = Constant.NUM_8;
					handler.sendMessage(msg);
				}
			}
		});
		
	}

	protected void getWebService(String s) {

		if (s.equals("submit")) {
			try {
				String tel = et_tel.getText().toString();
				String ret = WebService.getinstance().getData("_DL1", tel);
				JSONObject json =  new JSONObject(ret);
				retcode =json.getString("flag");
				if (Integer.parseInt(retcode) > 0) {
					JSONArray array = json.getJSONArray("tableA");
					JSONObject obj = array.getJSONObject(0);
					obj.put("tel", tel);
					obj.put("loginTime", DateUtil.toDataString("yyyy-MM-dd hh:mm:ss"));
					User user = User.jsonToUser(obj.toString());
					DataCache.getinition().setUser(user);
					spfe = spf.edit();
					spfe.putString("userStr", obj.toString());
					spfe.commit();
					getWebService("querymenu");
				}else{
					retmsg = "账号不存在，请注册";//json.getString("msg");
					Message msg = new Message();
					msg.what = Constant.FAIL;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				retmsg = e.getMessage();
				Message msg = new Message();
				msg.what = Constant.FAIL;
				handler.sendMessage(msg);
			}
		}

		if (s.equals("querymenu")) {
			try {
				List<String> menuList = new ArrayList<String>();
				menuList.add("m_pad_help");
				menuList.add("m_pad_myqd");
				DataCache.getinition().setMenu(menuList);
				Message msg = new Message();
				msg.what = Constant.SUCCESS;
				handler.sendMessage(msg);
				
//				String userid = DataCache.getinition().getUser().getUserid();
//				String ret = WebService.getinstance().getData("_PAD_GNQX", userid+ "*" + userid);
//				JSONObject json =  new JSONObject(ret);
//				retcode = json.getString("flag");
//				if (Integer.parseInt(retcode) > 0) {
//
//					JSONArray jsonArray = json.getJSONArray("tableA");
//					List<String> menu_name = new ArrayList<String>();
//					for (int i = 0; i < jsonArray.length(); i++) {
//						JSONObject object = jsonArray.getJSONObject(i);
//						menu_name.add(object.getString("menu_name"));
//					}
//					DataCache.getinition().setMenu(menu_name);
//					Message msg = new Message();
//					msg.what = Constant.SUCCESS;
//					handler.sendMessage(msg);
//				} else {
//					retmsg = "û�в˵�Ȩ��";
//					Message msg = new Message();
//					msg.what = Constant.FAIL;
//					handler.sendMessage(msg);
//				}

			} catch (Exception e) {
				e.printStackTrace();
				retmsg = e.getMessage();
				Message msg = new Message();
				msg.what = Constant.FAIL;
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onBackPressed() {
		dialogShowMessage("是否确认退出？", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(timer != null){
					timer.cancel();
				}
				finish();
				System.exit(0);
				// AppManager.getAppManager().AppExit(getApplicationContext());
			}
		}, null);

	}

	private Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(retmsg, null);
				break;
			case Constant.SUCCESS:
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			case Constant.NUM_6:
				if(lastnum>0){
					btn_yzm.setText(lastnum+"s");
				}else{
					canclick = true;
					btn_yzm.setBackgroundResource(R.drawable.bt_login);
					btn_yzm.setText("获取验证码");
				}
				
				break;
			case Constant.NUM_7:
				dialogShowMessage_P("验证码已发送", null);
				break;
			case Constant.NUM_8:
				
				break;
			}
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
		}
	};
}
