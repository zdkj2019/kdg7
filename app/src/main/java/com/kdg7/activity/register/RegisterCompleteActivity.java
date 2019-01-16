package com.kdg7.activity.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.activity.main.MainActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.po.User;
import com.kdg7.utils.Config;
import com.kdg7.utils.DateUtil;
import com.kdg7.webservice.WebService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterCompleteActivity extends FrameActivity {

	private SharedPreferences spf;
	private SharedPreferences.Editor spfe;
	private EditText et_yzm;
	private Button btn_yzm, btn_submit;
	private String retmsg, retcode, tel, yzm = "";
	private int lastnum = 60;
	private boolean canclick = true;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_register_complete);
		initView();
		initVariable();
		initListeners();
	}

	protected void initView() {
		et_yzm = (EditText) findViewById(R.id.et_yzm);

		btn_yzm = (Button) findViewById(R.id.btn_yzm);
		btn_submit = (Button) findViewById(R.id.btn_submit);
	};

	protected void initVariable() {
		tv_title.setText("注册");
		spf = getSharedPreferences("loginsp", LoginActivity.MODE_PRIVATE);
		tel = getIntent().getStringExtra("tel");
	}

	protected void initListeners() {
		topBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});

		btn_yzm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNull(et_yzm)) {
					toastShowMessage("请录入验证码");
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

	}

	private void sendMsg() {
		showProgressDialog();
		Config.getExecutorService().execute(new Runnable() {

			@Override
			public void run() {
				try {
					yzm = DateUtil.getRandomNum();
					HashMap<String, Object> param = new HashMap<String, Object>();
					param.put("in0", DateUtil.encode16(tel));
					param.put("in1", DateUtil.encode16("您正在登录系统，验证码：" + yzm));
					String result = (String) WebService.getinstance()
							.getWebService(
									Constant.WEBSERVICE_URL_MSG_NAMESPACE,
									Constant.WEBSERVICE_URL_MSG, "uf_fsdx_165",
									param);
					if ("1".equals(result)) {
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

	protected void getWebService(String s) {

		if (s.equals("submit")) {
			try {
				String typeStr = "";
				String str = tel + "$x$x$x";
				String ret = WebService.getinstance().setData("c#_WX_HYZC",
						str, typeStr, typeStr);
				JSONObject json = new JSONObject(ret);
				retcode = json.getString("flag");
				if (Integer.parseInt(retcode) > 0) {
					retmsg = "注册成功";
					getWebService("dl");
					// Message msg = new Message();
					// msg.what = Constant.SUCCESS;
					// handler.sendMessage(msg);
				} else {
					retmsg = json.getString("msg");
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

		if (s.equals("dl")) {
			try {
				String ret = WebService.getinstance().getData("_DL1", tel);
				JSONObject json = new JSONObject(ret);
				retcode = json.getString("flag");
				if (Integer.parseInt(retcode) > 0) {
					JSONArray array = json.getJSONArray("tableA");
					JSONObject obj = array.getJSONObject(0);
					obj.put("tel", tel);
					obj.put("loginTime",
							DateUtil.toDataString("yyyy-MM-dd hh:mm:ss"));
					User user = User.jsonToUser(obj.toString());
					DataCache.getinition().setUser(user);
					spfe = spf.edit();
					spfe.putString("userStr", obj.toString());
					spfe.commit();
					getWebService("querymenu");
				} else {
					retmsg = json.getString("msg");
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

				// String userid = DataCache.getinition().getUser().getUserid();
				// String ret = WebService.getinstance().getData("_PAD_GNQX",
				// userid+ "*" + userid);
				// JSONObject json = new JSONObject(ret);
				// retcode = json.getString("flag");
				// if (Integer.parseInt(retcode) > 0) {
				//
				// JSONArray jsonArray = json.getJSONArray("tableA");
				// List<String> menu_name = new ArrayList<String>();
				// for (int i = 0; i < jsonArray.length(); i++) {
				// JSONObject object = jsonArray.getJSONObject(i);
				// menu_name.add(object.getString("menu_name"));
				// }
				// DataCache.getinition().setMenu(menu_name);
				// Message msg = new Message();
				// msg.what = Constant.SUCCESS;
				// handler.sendMessage(msg);
				// } else {
				// retmsg = "û�в˵�Ȩ��";
				// Message msg = new Message();
				// msg.what = Constant.FAIL;
				// handler.sendMessage(msg);
				// }

			} catch (Exception e) {
				e.printStackTrace();
				retmsg = e.getMessage();
				Message msg = new Message();
				msg.what = Constant.FAIL;
				handler.sendMessage(msg);
			}
		}
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
				dialogShowMessage_P(retmsg,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								Intent intent = new Intent(
										getApplicationContext(),
										MainActivity.class);
								startActivity(intent);
							}
						});
				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			case Constant.NUM_6:
				if (lastnum > 0) {
					btn_yzm.setText(lastnum + "s");
				} else {
					canclick = true;
					btn_yzm.setBackgroundResource(R.drawable.btn_gree);
					btn_yzm.setText("获取验证码");
				}

				break;
			case Constant.NUM_7:
				dialogShowMessage_P("验证码已发送", null);
				break;
			case Constant.NUM_8:

				break;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};

	public void onBackPressed() {
		if (timer != null) {
			timer.cancel();
		}
		super.onBackPressed();
	};
}
