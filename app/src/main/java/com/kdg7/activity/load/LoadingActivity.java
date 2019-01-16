package com.kdg7.activity.load;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.kdg7.R;
import com.kdg7.activity.BaseActivity;
import com.kdg7.activity.login.LoginActivity;
import com.kdg7.common.Constant;
import com.kdg7.update.DownLoadManager;
import com.kdg7.utils.Config;
import com.kdg7.webservice.WebService;

@SuppressLint("HandlerLeak")
public class LoadingActivity extends BaseActivity {
	
	private ProgressDialog pd;
	private String retcode, version, retmsg;
	private String downloadUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		View view = View.inflate(this, R.layout.activity_loading, null);
		setContentView(view);
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		
		downloadUrl = getResources().getString(R.string.updateurl_dx);
		
		showProgressDialog();
		Config.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				getBbh();
			}
		});
		
	}

	private void getBbh() {
		
		try {
			String sqlid = "_BBH";
			String ret = WebService.getinstance().getData(sqlid, "");
			JSONObject jsonObject = new JSONObject(ret);
			retcode = jsonObject.getString("flag");
			if ("1".equals(retcode)) {
				JSONArray jsonArray = jsonObject.getJSONArray("tableA");
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject temp = jsonArray.getJSONObject(i);
					version = temp.getString("bbh");
				}
				Message msg = new Message();
				msg.what = Constant.NUM_7;
				handler.sendMessage(msg);
			} else {
				retmsg = "查询版本失败";
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


	private String getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		return packInfo.versionName;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;

		}
		return true;
	}


	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage("检测到最新版本，请及时升级！");
		builer.setCancelable(false);
		builer.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				downLoadApk();
			}
		});
		builer.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
				finish();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	protected void downLoadApk() {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false);
		pd.setButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DownLoadManager.setCancelUpdate(true);
				retmsg = "下载失败";
				Message msg = new Message();
				msg.what = Constant.NUM_9;
				handler.sendMessage(msg);
			}
		});
		pd.setMessage("正在下载...");
		pd.show();

		Config.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				try {
					File file = DownLoadManager.getFileFromServer(downloadUrl, pd);
					Thread.sleep(3000);
					if (!DownLoadManager.isCancelUpdate()) {
						installApk(file);
					}
					pd.dismiss(); 
				} catch (Exception e) {
					e.printStackTrace();
					retmsg = "下载失败";
					Message msg = new Message();
					msg.what = Constant.NUM_9;
					handler.sendMessage(msg);
					
				}
			}
		});
	}

	protected void installApk(File file) {
		this.finish();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);

	}

	private void LoginMain() {
		Intent intent = new Intent(this, LoginActivity.class);
		
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		// ��������ǰ��activity
		this.finish();
	}

	private Handler handler = new Handler(Looper.myLooper()) {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.NETWORK_ERROR:
				Toast.makeText(LoadingActivity.this, retmsg, Toast.LENGTH_SHORT).show();
				LoginMain();
				finish();
				break;
			case Constant.FAIL:
				Toast.makeText(LoadingActivity.this, retmsg, Toast.LENGTH_SHORT).show();
				LoginMain();
				finish();
				break;
			case Constant.NUM_7:
				try {
					if (!version.equals("")&& Double.parseDouble(version) > Double.parseDouble(getVersionName())) {
						showUpdataDialog();
					} else {
						LoginMain();
					}
				} catch (Exception e) {
					e.printStackTrace();
					LoginMain();
				}
				break;
			}
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
		}
	};
	
}
