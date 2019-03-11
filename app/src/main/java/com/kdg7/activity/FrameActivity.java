package com.kdg7.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.util.ImgPz;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.DateUtil;
import com.kdg7.webservice.WebService;

@SuppressLint("SimpleDateFormat")
public abstract class FrameActivity extends BaseActivity{
	
	protected TextView tv_title;
	protected ImageView topBack;
	protected Calendar c = Calendar.getInstance();
	protected int m_year, m_month, m_day;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_frame);
		topBack = (ImageView) findViewById(R.id.bt_topback);
		tv_title = (TextView) findViewById(R.id.interfacename);
		m_year = c.get(Calendar.YEAR);
		m_month = c.get(Calendar.MONTH) + 1;
		m_day = c.get(Calendar.DAY_OF_MONTH);
	}

	protected int getScreenWidth() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		return width;
	}

	protected int getScreenHeight() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		return height;
	}

	protected String getVersion() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (Exception e) {
			
		}
		return null;
	}

	protected void appendMainBody(int resId) {
		LinearLayout mainBody = (LinearLayout) findViewById(R.id.layMainBody);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(resId, null);
		LinearLayout.LayoutParams layoutParams = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mainBody.addView(view, layoutParams);
	}
	
	protected void myDialog(String title, View view,
			OnClickListener confirmListener, OnClickListener cancelListener) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton("确认", confirmListener);
		builder.setNegativeButton("取消", cancelListener);
		builder.create().show();
	}

	protected void Call(String tel) {
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:" + tel));
		startActivity(phoneIntent);
	}

	protected void dateDialog(TextView textView) {
		final TextView time;
		time = textView;
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				time.setText(DateUtil.toDataString("yyyy-MM-dd HH:mm:ss"));
			}
		}, m_year, m_month, m_day).show();
	}

	protected void dateDialog(final EditText tv) {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				tv.setText(DateUtil.toDataString("yyyy-MM-dd HH:mm:ss"));
			}
		}, m_year, m_month, m_day).show();
	}

	protected void MonthDialog(final EditText tv) {
		DatePickerDialog dp = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						c.set(Calendar.YEAR, year);
						c.set(Calendar.MONTH, monthOfYear);
						c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						tv.setText(DateUtil.toDataString("yyyy-MM"));
					}
				}, m_year, m_month, m_day);
		dp.show();
	}

	@SuppressLint("NewApi")
	public void showDateSelector(final View v) {
		final Calendar calendar = Calendar.getInstance();
		final EditText et = (EditText) v;
		DatePickerDialog.OnDateSetListener DateSet = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				monthOfYear = monthOfYear + 1;
				String month = "";
				String day = "";
				if (monthOfYear < 10) {
					month = "0" + monthOfYear;
				} else {
					month = "" + monthOfYear;
				}
				if (dayOfMonth < 10) {
					day = "0" + dayOfMonth;
				} else {
					day = "" + dayOfMonth;
				}
				String str = year + "-" + month + "-" + day;
				// String str = year + month;
				et.setText(str);
			}
		};
		DatePickerDialog datePickerDialog = new DatePickerDialog(this, DateSet,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.getDatePicker().setCalendarViewShown(false);
		datePickerDialog.show();
	}

	protected boolean isNotNull(View v) {
		String temp = null;
		if (v instanceof EditText) {
			temp = ((EditText) v).getText().toString();
		} else if (v instanceof TextView) {
			temp = ((TextView) v).getText().toString();
		}
		if (temp != null && !"".equals(temp.trim())) {
			return true;
		} else {
			if (v != null && v.getTag() != null) {
				String left = v.getTag() + "";
			}
			return false;
		}
	}
	
	protected boolean isNull(View v) {
		String temp = null;
		if (v instanceof EditText) {
			temp = ((EditText) v).getText().toString();
		} else if (v instanceof TextView) {
			temp = ((TextView) v).getText().toString();
		}
		if (temp == null || "".equals(temp.trim())) {
			return true;
		} else {
			return false;
		}
	}

	protected Bitmap convertBitmap(File file, int REQUIRED_SIZE)
			throws Exception {
		Bitmap bitmap = null;
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			} else {
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
		}
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = scale;
		fis = new FileInputStream(file.getAbsolutePath());
		o.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeStream(fis, null, op);
		fis.close();
		return bitmap;
	}

	protected void camera(int requestCode, ArrayList<String> list) {
		Intent intent = new Intent(getApplicationContext(), ImgPz.class);
		intent.putStringArrayListExtra("imglist", list);
		startActivityForResult(intent, requestCode);
	}

	protected boolean uploadPic(String num, String mxh, final byte[] data1,
			final String methed, String zbh, String sqlid) throws Exception {

		if (data1 != null && mxh != null) {
			String ret = WebService.getinstance().setData2_p11(sqlid,num, zbh + "*" + mxh, "0001", data1);
			JSONObject json = new JSONObject(ret);
			String retcode = json.getString("flag");
			if ("1".equals(retcode)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static Uri getUriForFile(Context context, File file) {
		return FileProvider.getUriForFile(context, Constant.PackageName+".fileProvider", file);
	}

	protected byte[] readJpeg(File filename) {
		if (filename == null) {
			return null;
		}

		ByteArrayOutputStream baos = null;
		byte[] filebyteArray = null;
		try {
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(filename);
			baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = fis.read(buffer)) >= 0) {
				baos.write(buffer, 0, count);
			}

			filebyteArray = baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return filebyteArray;
	}

	protected int getListItemIcon1(String djzt) {
		if ("3".equals(djzt)) {
			return R.drawable.list_item_green;
		} else if ("4".equals(djzt)) {
			return R.drawable.list_item_blue;
		} else if ("4.5".equals(djzt)) {
			return R.drawable.list_item_brown;
		} else if ("5".equals(djzt)) {
			return R.drawable.list_item_red;
		} else if ("6".equals(djzt)) {
			return R.drawable.list_item_pink;
		} else if ("7".equals(djzt)) {
			return R.drawable.list_item_purple;
		}
		return R.drawable.list_item_yellow;
	}

	protected int getListItemIcon(int num) {
		if (num % 4 == 0) {
			return R.drawable.list_item_yellow;
		} else if (num % 4 == 1) {
			return R.drawable.list_item_red;
		} else if (num % 4 == 2) {
			return R.drawable.list_item_blue;
		} else if (num % 4 == 3) {
			return R.drawable.list_item_green;
		}
		return R.drawable.list_item_yellow;
	}
	
	protected String mdateformat(int format, String time) {

		String[] times = time.split(" ");
		String t = times[format];

		if (time.indexOf("1990") != -1) {
			t = "";
		}
		if (format == 1) {
			String[] strings = t.split(":");
			t = strings[0] + ":" + strings[1];
		}
		return t;
	}
}
