package com.kdg7.activity.register;

import org.json.JSONObject;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.common.Constant;
import com.kdg7.utils.Config;
import com.kdg7.utils.PhoneNum;
import com.kdg7.webservice.WebService;

import android.annotation.SuppressLint;
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

public class RegisterActivity extends FrameActivity {


	private EditText et_tel;
	private Button btn_submit;
	private String retmsg,retcode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_register);
		initView();
		initVariable();
		initListeners();
	}
	
	protected void initView(){
		et_tel = (EditText) findViewById(R.id.et_tel);
		btn_submit = (Button) findViewById(R.id.btn_submit);
	};

	protected void initVariable() {
		tv_title.setText("注册");
	}

	protected void initListeners() {
		topBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isNull(et_tel)){
					toastShowMessage("请录入手机号码");
					return;
				}
				if(!PhoneNum.isMobile(et_tel.getText().toString().trim())){
					toastShowMessage("请录入正确的手机号码");
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

	protected void getWebService(String s) {

		
		if (s.equals("submit")) {
			try {
				Message msg = new Message();
				msg.what = Constant.SUCCESS;
				handler.sendMessage(msg);
//				String typeStr = "";
//				String str = et_tel.getText().toString()+"$x02";
//				String ret = WebService.getinstance().setData("c#_WX_HYZC", str, typeStr, typeStr);
//				JSONObject json =  new JSONObject(ret);
//				retcode =json.getString("flag");
//				if (Integer.parseInt(retcode) > 0) {
//					retmsg = "ע��ɹ�";
//					Message msg = new Message();
//					msg.what = Constant.SUCCESS;
//					handler.sendMessage(msg);
//				}else{
//					retmsg = json.getString("msg");
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
	
	private Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(retmsg, null);
				break;
			case Constant.SUCCESS:
				Intent intent = new Intent(getApplicationContext(),RegisterCompleteActivity.class);
				intent.putExtra("tel", et_tel.getText().toString().trim());
				startActivity(intent);
//				dialogShowMessage_P(retmsg,new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface face,int paramAnonymous2Int) {
//						Intent intent = new Intent(getApplicationContext(),RegisterCompleteActivity.class);
//						intent.putExtra("tel", et_tel.getText().toString().trim());
//						startActivity(intent);
//					}
//				});
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
