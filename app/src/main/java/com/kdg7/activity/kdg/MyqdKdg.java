package com.kdg7.activity.kdg;

import java.util.Map;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kdg7.activity.util.BaiduMapActivity;
import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;

public class MyqdKdg extends FrameActivity {

	private Button confirm,cancel;
	private String retcode,retmsg,zbh,lxdh,keyStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_kdg_myqd);
		initVariable();
		initView();
		initListeners();
	}

	protected void initVariable() {

		confirm = (Button) findViewById(R.id.include_botto).findViewById(
				R.id.confirm);
		cancel = (Button) findViewById(R.id.include_botto).findViewById(
				R.id.cancel);
		confirm.setText("确定");
		cancel.setText("取消");

	}

	protected void initView() {

		tv_title.setText("我的抢单");

		
		final Map<String, Object> itemmap = DataCache.getinition().getData();

		zbh = itemmap.get("zbh").toString();
		lxdh = itemmap.get("lxdh").toString();
		keyStr = itemmap.get("wdmc").toString();
		((TextView) findViewById(R.id.tv_1)).setText(zbh);
		((TextView) findViewById(R.id.tv_2)).setText(itemmap.get("wdmc").toString());
		((TextView) findViewById(R.id.tv_3)).setText(itemmap.get("jddz").toString());
		((TextView) findViewById(R.id.tv_4)).setText(itemmap.get("lxdh").toString());
		((TextView) findViewById(R.id.tv_5)).setText(itemmap.get("bzsj").toString());
		((TextView) findViewById(R.id.tv_6)).setText("");
		
	}

	protected void initListeners() {
		//
		OnClickListener backonClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_topback:
					onBackPressed();
					break;
				case R.id.cancel:
					onBackPressed();
					break;
				case R.id.confirm:
					onBackPressed();
					break;
				default:
					break;
				}
				
			}
		};

		topBack.setOnClickListener(backonClickListener);
		cancel.setOnClickListener(backonClickListener);
		confirm.setOnClickListener(backonClickListener);
		
		findViewById(R.id.iv_telphone).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(lxdh)) {
					toastShowMessage("请选择联系电话！");
					return;
				}
				Call(lxdh);
			}
		});
		
//		findViewById(R.id.iv_baidumap).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(getApplicationContext(),
//						BaiduMapActivity.class);
//				intent.putExtra("keyStr", keyStr);
//				startActivity(intent);
//			}
//		});
	}

	protected void getWebService(String s) {
		
		
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(retmsg, null);
				break;
			case Constant.SUCCESS:
				dialogShowMessage_P(retmsg,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface face,int paramAnonymous2Int) {
						Intent intent  = getIntent();
						setResult(-1, intent);
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