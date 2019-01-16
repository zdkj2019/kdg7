package com.kdg7.activity.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.utils.Config;

public class ChooseActivity extends FrameActivity {

	private ListView listView1;
	private EditText et_search;
	private List<Map<String, String>> mdata;
	private ArrayList<Map<String, String>> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		appendMainBody(R.layout.activity_yytbz);

		initVariable();
		initView();
		initListeners();
	}

	@SuppressWarnings("unchecked")
	protected void initVariable() {

		listView1 = (ListView) findViewById(R.id.listView1);
		et_search = (EditText) findViewById(R.id.et_search);
		mdata = new ArrayList<Map<String, String>>();

		Intent intent = getIntent();

		data = (ArrayList<Map<String, String>>) intent
				.getSerializableExtra("data");
	}

	protected void initView() {

		textchange("", "name");
	}

	protected void initListeners() {

		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position >= 0) {
					Intent intent = getIntent();
					intent.putExtra("name", mdata.get(position).get("name"));
					intent.putExtra("id", mdata.get(position).get("id"));
					setResult(1, intent);
					finish();
				}
			}
		});

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, int start,
					int before, int count) {
				textchange(s.toString(), "name");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		topBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	public List<Map<String, String>> querycache(String pipei, String str,
			List<Map<String, String>> data2) {
		List<Map<String, String>> matchered;
		if (str == null || "".equals(str)) {
			return data2;
		}
		String regex = ".*" + str + ".*";
		Pattern pattern = Pattern.compile(regex);
		matchered = new LinkedList<Map<String, String>>();

		for (int i = 0; i < data2.size(); i++) {
			Map<String, String> map2 = data2.get(i);
			Matcher matcher = pattern.matcher(map2.get(pipei));
			if (matcher.find()) {

				matchered.add(map2);
			}
		}
		return matchered;
	}

	private void textchange(final String s, final String string2) {

		Config.getExecutorService().execute(new Runnable() {

			@Override
			public void run() {

				mdata = querycache(string2, s, data);
			
				if (mdata != null) {
					Message message = new Message();
					message.what = 1;
					hander.sendMessage(message);
				}
			}
		});
	}

	private Handler hander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			SimpleAdapter adapter2 = new SimpleAdapter(getApplicationContext(),
					mdata, R.layout.listview_item_filterdata1, new String[] {
							"id", "name" }, new int[] { R.id.tv_id,
							R.id.tv_name });
			listView1.setAdapter(adapter2);

		}

	};

	@Override
	public void onBackPressed() {
		finish();
	}
}