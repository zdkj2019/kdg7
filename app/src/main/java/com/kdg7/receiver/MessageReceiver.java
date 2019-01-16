package com.kdg7.receiver;

import android.content.Context;
import android.content.Intent;

import com.kdg7.activity.notify.QpActivity;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONObject;

public class MessageReceiver extends XGPushBaseReceiver {

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult arg1) {
	}

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult arg1) {
		

	}

	@Override
	public void onRegisterResult(Context arg0, int arg1,
			XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		// TODO Auto-generated method stub
		try{
			String brand = android.os.Build.BRAND;
			String text = "json:" +brand+"---"+ message.getContent();
			System.out.println(text);
			String msgStr = "";
			msgStr = message.getContent();
			JSONObject json = new JSONObject(msgStr);
			if("HUAWEI".equals(brand)){
				msgStr = json.getString("content");
			}
			Intent it = new Intent(context, QpActivity.class);
			it.putExtra("message", msgStr);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
