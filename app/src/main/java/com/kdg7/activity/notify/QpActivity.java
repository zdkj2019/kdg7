package com.kdg7.activity.notify;

import java.io.IOException;

import org.json.JSONObject;

import com.kdg7.R;
import com.kdg7.activity.BaseActivity;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.po.User;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class QpActivity extends BaseActivity {

	private MediaPlayer mMediaPlayer;
	private AudioManager mgr;
	private int max,current;
	private TextView tv_jd, tv_xq;
	private Button btn_bq, btn_qd;
	private String tel, zbh, retcode, retmsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wakeUpAndUnlock(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qp);

		tv_jd = (TextView) findViewById(R.id.tv_jd);
		tv_xq = (TextView) findViewById(R.id.tv_xq);
		btn_bq = (Button) findViewById(R.id.btn_bq);
		btn_qd = (Button) findViewById(R.id.btn_qd);

		String ret = getIntent().getStringExtra("message");
		try {
			JSONObject json = new JSONObject(ret);
			tv_jd.setText("街道地址："+json.getString("jddz"));
			tv_xq.setText("小区名称："+json.getString("wdmc"));
			tel = json.getString("tel");
			zbh = json.getString("zbh");
			

			btn_qd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mgr!=null){
						mgr.setStreamVolume(AudioManager.STREAM_MUSIC,current, 0); 
					}
					if(mMediaPlayer!=null){
						mMediaPlayer.stop();
						mMediaPlayer.release();
						mMediaPlayer = null;
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
		} catch (Exception e) {
			retmsg = e.getMessage();
			Message msg = new Message();
			msg.what = Constant.FAIL;
			handler.sendMessage(msg);
		}

		btn_bq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mgr!=null){
					mgr.setStreamVolume(AudioManager.STREAM_MUSIC,current, 0); 
				}
				if(mMediaPlayer!=null){
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}
				onBackPressed();
			}
		});


	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mMediaPlayer!=null){
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
//		mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
		mMediaPlayer = MediaPlayer.create(this, R.raw.sound);
		mMediaPlayer.setLooping(true);
		try {
			mgr = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
			max = mgr.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
			max = (int) (max*0.8);
			current = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
			System.out.println("json:"+max+"-"+current);
			mgr.setStreamVolume(AudioManager.STREAM_MUSIC,max, 0);
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.start();
	}
	
	private Uri getSystemDefultRingtoneUri() {
		return RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void getWebService(String s) {
		if (s.equals("submit")) {
			try {
				String typeStr = "";
				String str = zbh + "$x"+ getUser().getHykh();
				String ret = WebService.getinstance().setData("c#_PAD_YWGL_GZQD", str, typeStr, typeStr);
				JSONObject json = new JSONObject(ret);
				retcode = json.getString("flag");
				if (Integer.parseInt(retcode) > 0) {
					retmsg = "抢单成功";
					Message msg = new Message();
					msg.what = Constant.SUCCESS;
					handler.sendMessage(msg);
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

	}

	public void wakeUpAndUnlock(Activity activity) {
		try {
			PowerManager pm = (PowerManager) activity.getSystemService(activity.POWER_SERVICE);
			boolean screenOn = pm.isScreenOn();
			if (!screenOn) {
				KeyguardManager km= (KeyguardManager) activity.getSystemService(activity.KEYGUARD_SERVICE);  
		        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");  
		        kl.disableKeyguard();  
		        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");  
		        wl.acquire();  
		        wl.release();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			retmsg = e.getMessage();
			Message msg = new Message();
			msg.what = Constant.FAIL;
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(mgr!=null){
				mgr.setStreamVolume(AudioManager.STREAM_MUSIC,current, 0); 
			}
			if(mMediaPlayer!=null){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(retmsg,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface face,int paramAnonymous2Int) {
						onBackPressed();
					}
				});
				break;
			case Constant.SUCCESS:
				dialogShowMessage(retmsg+"，是否立刻联系？",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface face,int paramAnonymous2Int) {
						Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tel));
						startActivity(phoneIntent);
						finish();
					}
				},new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface face,int paramAnonymous2Int) {
						onBackPressed();
					}
				});
				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
}
