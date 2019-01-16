package com.kdg7.activity.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kdg7.utils.DateUtil;
import com.kdg7.R;
import com.kdg7.activity.BaseActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;


public class CallTel extends BaseActivity {

	private MediaPlayer mMediaPlayer;
	private AudioManager mgr;
	private int num;
	private TextView tv_time;
	private Button btn_gd, btn_bd;
	private String tel, retcode, retmsg;
	private Timer  timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wakeUpAndUnlock(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calltel);

		btn_gd = (Button) findViewById(R.id.btn_gd);
		btn_bd = (Button) findViewById(R.id.btn_bd);
		tv_time = (TextView) findViewById(R.id.tv_time);
		try {
			tel = "10086";

			btn_gd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						// 首先拿到TelephonyManager 
						TelephonyManager telMag = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE); 
						Class<TelephonyManager> c = TelephonyManager.class; 
						// 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象 
						Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null); 
						//允许访问私有方法
						mthEndCall.setAccessible(true); 
						final Object obj = mthEndCall.invoke(telMag, (Object[]) null); 
						// 再通过ITelephony对象去反射里面的endCall方法，挂断电话 
						Method mt = obj.getClass().getMethod("endCall"); 
						//允许访问私有方法
						mt.setAccessible(true); 
						mt.invoke(obj);
						Toast.makeText(getApplicationContext(), "挂断电话！"+num, Toast.LENGTH_SHORT).show();
						onBackPressed();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});

			btn_bd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDjs();
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "10086"));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					
					
				}
			});
		} catch (Exception e) {
			retmsg = e.getMessage();
			Message msg = new Message();
			msg.what = Constant.FAIL;
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	
	/**
	 * 倒计时
	 */
	private void showDjs(){
		try {
			num=0;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					num++;
                    Message msg = new Message();
					msg.what = Constant.NUM_6;
					handler.sendMessage(msg);
				}
			}, 0,1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Uri getSystemDefultRingtoneUri() {
		return RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_RINGTONE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null){
			timer.cancel();
		}
	}

	protected void getWebService(String s) {
		// if (s.equals("submit")) {
		// try {
		// String typeStr = "";
		// String str = zbh + "$x"+ DataCache.getinition().getUser().getHykh();
		// String ret = WebService.getinstance().setData("c#_PAD_YWGL_GZQD",
		// str, typeStr, typeStr);
		// JSONObject json = new JSONObject(ret);
		// retcode = json.getString("flag");
		// if (Integer.parseInt(retcode) > 0) {
		// retmsg = "抢单成功";
		// Message msg = new Message();
		// msg.what = Constant.SUCCESS;
		// handler.sendMessage(msg);
		// } else {
		// retmsg = json.getString("msg");
		// Message msg = new Message();
		// msg.what = Constant.FAIL;
		// handler.sendMessage(msg);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// retmsg = e.getMessage();
		// Message msg = new Message();
		// msg.what = Constant.FAIL;
		// handler.sendMessage(msg);
		// }
		// }

	}

	public void wakeUpAndUnlock(Activity activity) {
		try {
			PowerManager pm = (PowerManager) activity
					.getSystemService(activity.POWER_SERVICE);
			boolean screenOn = pm.isScreenOn();
			if (!screenOn) {
				KeyguardManager km = (KeyguardManager) activity
						.getSystemService(activity.KEYGUARD_SERVICE);
				KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
				kl.disableKeyguard();
				PowerManager.WakeLock wl = pm.newWakeLock(
						PowerManager.ACQUIRE_CAUSES_WAKEUP
								| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
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

			switch (msg.what) {
			case Constant.FAIL:
				dialogShowMessage_P(retmsg,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								onBackPressed();
							}
						});
				break;
			case Constant.SUCCESS:
				dialogShowMessage(retmsg + "，是否立刻联系？",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								Intent phoneIntent = new Intent(
										"android.intent.action.CALL", Uri
												.parse("tel:" + tel));
								startActivity(phoneIntent);
								finish();
							}
						}, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface face,
									int paramAnonymous2Int) {
								onBackPressed();
							}
						});
				break;
			case Constant.NETWORK_ERROR:
				dialogShowMessage_P(Constant.NETWORK_ERROR_STR, null);
				break;
			case Constant.NUM_6:
				tv_time.setText(num+"");
				break;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
}
