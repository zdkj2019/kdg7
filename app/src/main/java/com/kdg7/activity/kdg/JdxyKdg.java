package com.kdg7.activity.kdg;

import java.util.ArrayList;
import java.util.Map;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.activity.main.MainActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;

public class JdxyKdg extends FrameActivity {

	private Button confirm,cancel;
	private String flag,zbh,type="1",message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_kdg_jdxy);
		initVariable();
		initView();
		initListeners();
	}

	protected void initVariable() {

		confirm = (Button) findViewById(R.id.include_botto).findViewById(
				R.id.confirm);
		cancel = (Button) findViewById(R.id.include_botto).findViewById(
				R.id.cancel);
		confirm.setText("接单");
		cancel.setText("拒绝");

	}

	protected void initView() {

		tv_title.setText("接单响应");

		
		final Map<String, Object> itemmap = DataCache.getinition().getData();

		zbh = itemmap.get("zbh").toString();
		((TextView) findViewById(R.id.tv_1)).setText(zbh);
		((TextView) findViewById(R.id.tv_2)).setText(itemmap.get("sx").toString());
		((TextView) findViewById(R.id.tv_3)).setText(itemmap.get("qy").toString());
		((TextView) findViewById(R.id.tv_4)).setText(itemmap.get("xqmc").toString());
		((TextView) findViewById(R.id.tv_5)).setText(itemmap.get("xxdz").toString());
		((TextView) findViewById(R.id.tv_6)).setText(itemmap.get("gzxx").toString());
		((TextView) findViewById(R.id.tv_7)).setText(itemmap.get("ywlx").toString());
		((TextView) findViewById(R.id.tv_8)).setText(itemmap.get("bz").toString());
		
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
					dialogShowMessage("是否确认拒绝接单？",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface face,int paramAnonymous2Int) {
							showProgressDialog();
							Config.getExecutorService().execute(new Runnable() {
								
								@Override
								public void run() {
									type = "2";
									getWebService("submit");
								}
							});
						}
					} ,null);
					break;
				case R.id.confirm:
					showProgressDialog();
					Config.getExecutorService().execute(new Runnable() {
						
						@Override
						public void run() {
							type = "1";
							getWebService("submit");
						}
					});
					break;
				default:
					break;
				}
				
			}
		};

		topBack.setOnClickListener(backonClickListener);
		cancel.setOnClickListener(backonClickListener);
		confirm.setOnClickListener(backonClickListener);
	}

	protected void getWebService(String s) {
		
		if (s.equals("submit")) {
			try {
				String typeStr = "1".equals(type)?"jdxy":"jjgd";
				message = "1".equals(type)?"接交成功":"拒单成功";
				String str = zbh+"*PAM*"+DataCache.getinition().getUser().getHykh();
				String ret = WebService.getinstance().setData2("c#_PAD_KDG_ALL", str,typeStr,typeStr);
				JSONObject json =  new JSONObject(ret);
				flag =json.getString("flag");
				if (Integer.parseInt(flag) > 0) {
					Message msg = new Message();
					msg.what = Constant.SUCCESS;
					handler.sendMessage(msg);
				}else{
					flag = json.getString("msg");
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
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(flag, null);
				break;
			case Constant.SUCCESS:
				dialogShowMessage_P(message,new DialogInterface.OnClickListener() {
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