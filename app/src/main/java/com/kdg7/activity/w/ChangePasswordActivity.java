package com.kdg7.activity.w;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;

public class ChangePasswordActivity extends FrameActivity {

	private EditText et_ops, et_nps, et_snps;
	private TextView et_users;
	private Button confirm, cancel;
	private String loginps, nps, ops,retcode,retmsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_w_changepassword);
		initVariable();
		initView();
		initListeners();

	}

	protected void initVariable() {

		et_users = (TextView) findViewById(R.id.et_users);
		et_ops = (EditText) findViewById(R.id.et_ops);
		et_nps = (EditText) findViewById(R.id.et_nps);
		et_snps = (EditText) findViewById(R.id.et_snps);
		confirm = ((Button) findViewById(R.id.confirm).findViewById(
				R.id.confirm));
		cancel = ((Button) findViewById(R.id.cancel).findViewById(R.id.cancel));
	}

	protected void initView() {
		tv_title.setText("修改密码");
		loginps = getSharedPreferences("loginsp",LoginActivity.MODE_PRIVATE).getString("userPs", "");
		String userstext = "(" + DataCache.getinition().getUser().getHyid() + ")"+ DataCache.getinition().getUser().getHykh();
		et_users.setText(userstext);

	}

	protected void initListeners() {
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ops = et_ops.getText().toString();
				nps = et_nps.getText().toString();
				String snps = et_snps.getText().toString();
				if (!ops.equals(loginps)) {
					// 旧密码错
					toastShowMessage("原密码输入错误");

				} else if (nps.equals("") || nps == null) {
					// 新密码为空
					toastShowMessage("请输入新密码");

				} else if (snps.equals("") || snps == null) {
					// 新密码确认为空
					toastShowMessage("请再次输入新密码");

				} else if (!nps.equals(snps)) {
					// 两次密码不同
					toastShowMessage("两次输入的密码不同");

				} else {
					// toastShowMessage("submit");
					showProgressDialog();
					Config.getExecutorService().execute(new Runnable() {

						@Override
						public void run() {

							getWebService("submit");
						}
					});

				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
		topBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
	}

	protected void getWebService(String s) {
		try {
			String ret = WebService.getinstance().setData2("_PAD_XGMM", "ok", DataCache.getinition().getUser().getHykh(), ops + "*" + nps);
			JSONObject json = new JSONObject(ret);
			retcode = json.getString("flag");
			if (Integer.parseInt(retcode) > 0) {
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P("密码修改失败", null);
				break;
			case Constant.SUCCESS:
				dialogShowMessage_P("修改密码成功，请重新登陆",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
								startActivity(intent);
								finish();
							}
						});

				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;

			}
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			
		}
	};
}
