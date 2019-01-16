package com.kdg7.activity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.cache.DataCache;
import com.kdg7.common.Constant;
import com.kdg7.utils.Config;

public class AddParts extends FrameActivity{
//
//	private Button confirma, cancela, add;
//	private String flag, number, bz, machineName, machineId, pgdh,xlbm;
//	private List<Map<String, String>> data, data_listView;
//	private String[] from, from2;
//	private int[] to, to2;
//	private Spinner bjmc;
//	private ListView listView;
//	private ArrayList<String> sqlList;
//	private JSONObject jsonObject;
//
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		appendMainBody(R.layout.activity_addparts);
//		initVariable();
//		initView();
//		initListeners();
//		showProgressDialog();
//		Config.getExecutorService().execute(new Runnable() {
//
//			@Override
//			public void run() {
//
//				getWebService("xl");
//			}
//		});
//	}
//
//	
//	protected void initVariable() {
//		
//		from = new String[] { "id", "name" };
//		to = new int[] { R.id.bm, R.id.name };
//		from2 = new String[] { "machine", "number","sfhs" };
//		to2 = new int[] { R.id.machine, R.id.number, R.id.sfhs };
//
//		add = (Button) findViewById(R.id.add);
//		cancela = (Button) findViewById(R.id.cancela);
//		confirma = (Button) findViewById(R.id.confirma);
//
//		data = new ArrayList<Map<String, String>>();
//		data_listView = new ArrayList<Map<String, String>>();
//
//		listView = (ListView) findViewById(R.id.listView);
//
//		sqlList = new ArrayList<String>();
//		
//		tv_title.setText("��������б�");
//
//	}
//
//	protected void initView() {
//		
//		Intent intent = this.getIntent();
//		pgdh = intent.getStringExtra("oddnumber");
//		ArrayList<String> hpdata = intent.getStringArrayListExtra("hpdata");
//		HashMap<String, String> title = new HashMap<String, String>();
//		title.put("machine", "�������");
//		title.put("number", "����");
//		title.put("sfhs", "�Ƿ����");
//		
//
//		data_listView.add(title);
//		if(hpdata!=null){
//			for(int i=0;i<hpdata.size();i++){
//				String[] hps = hpdata.get(i).split(",");
//				HashMap<String, String> temp = new HashMap<String, String>();
//				temp.put("id",hps[0]);
//				temp.put("machine", hps[1]);
//				temp.put("number", hps[2]);
//				temp.put("sfhs", hps[3]);
//				
//				data_listView.add(temp);
//			}
//		}
//		SimpleAdapter adapter = new SimpleAdapter(
//				AddParts.this, data_listView,
//				R.layout.machine_listview_item, from2, to2);
//
//		listView.setAdapter(adapter);
//	}
//
//	protected void initListeners() {
//		OnClickListener backonClickListener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		};
//
//		topBack.setOnClickListener(backonClickListener);
//		
//		OnClickListener onClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				switch (v.getId()) {
//
//				case R.id.confirma:
//
//					Intent intent = new Intent();
//					intent.putStringArrayListExtra("hpsql", generateSql());
//					intent.putStringArrayListExtra("hpdata", getHpdata());
//					setResult(RESULT_OK, intent);
//					finish();
//					break;
//				case R.id.add:
//					Intent intent1 = new Intent(AddParts.this,AddPartsAdd.class);
//					startActivityForResult(intent1,21);
//					//showDialog();
//					break;
//
//				case R.id.cancela:
//					Intent intent2 = new Intent();
//					intent2.putStringArrayListExtra("hpsql", null);
//					intent2.putStringArrayListExtra("hpdata", null);
//					setResult(RESULT_OK, intent2);
//					finish();
//					break;
//
//				}
//			}
//		};
//
//		add.setOnClickListener(onClickListener);
//		cancela.setOnClickListener(onClickListener);
//		confirma.setOnClickListener(onClickListener);
//
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == 21 && resultCode == 21 && data != null) {
//			HashMap<String, String> temp = new HashMap<String, String>();
//			temp.put("machine", data.getStringExtra("name"));
//			temp.put("number", data.getStringExtra("num"));
//			temp.put("sfhs", data.getStringExtra("sfhs"));
//			temp.put("id", data.getStringExtra("id"));
//			data_listView.add(temp);
//			SimpleAdapter adapter = new SimpleAdapter(
//					AddParts.this, data_listView,
//					R.layout.machine_listview_item, from2, to2);
//
//			listView.setAdapter(adapter);
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	protected void getWebService(String s) {
//
//		
////		if ("xl".equals(s)) {
////			try {
////				String cs = DataCache.getinition().getUserId()+"*"+DataCache.getinition().getUserId();
////				jsonObject = callWebserviceImp.getWebServerInfo("_PAD_SBPJXL", cs, "uf_json_getdata",getApplicationContext());
////				//{"tableA":[{"hplbbm":"001001001","hpmc":"���ǵ�����ʾ��","hpbm":"0010010010001"},{"hplbbm":"001001001","hpmc":"������ʾ��","hpbm":"0010010010002"},{"hplbbm":"001001001","hpmc":"������ʾ��","hpbm":"0010010010003"},{"hplbbm":"001001001","hpmc":"HP��ʾ��","hpbm":"0010010010004"},{"hplbbm":"001001002","hpmc":"������","hpbm":"0010010020001"},{"hplbbm":"001001001","hpmc":"������","hpbm":"0010010010005"}],"flag":"6","costTime":342}
////				flag = jsonObject.getString("flag");
////				if (Integer.parseInt(flag) > 0) {
////					Config.getExecutorService().execute(new Runnable() {
////						@Override
////						public void run() {
////							Config.writeFile("_PAD_SBPJXL",jsonObject.toString(), getApplicationContext());
////						}
////					});
////					Message msg = new Message();
////					msg.what = 2;// �ɹ�
////					handler.sendMessage(msg);
////				} else {
////					Message msg = new Message();
////					msg.what = -2;// ʧ��
////					handler.sendMessage(msg);
////				}
////
////			} catch (Exception e) {
////				e.printStackTrace();
////				Message msg = new Message();
////				msg.what = Constant.NETWORK_ERROR;// ���粻ͨ
////				handler.sendMessage(msg);
////			}
//
////		}
//	}
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case Constant.NETWORK_ERROR:
//				dialogShowMessage_P("�������ӳ������������������", null);
//				break;
//
//			case Constant.SUCCESS:
//				dialogShowMessage_P("�豸��Ϣ��ӳɹ�", null);
//				break;
//			case Constant.FAIL:
////				SimpleAdapter adapter = new SimpleAdapter(
////						AddParts.this, data, R.layout.spinner_item,
////						from, to);
////				bjmc.setAdapter(adapter);
//				break;
//
//			}
//			if(progressDialog!=null){
//				progressDialog.dismiss();
//			}
//			
//		}
//
//	};
//
//
//	private ArrayList<String> generateSql() {
//
//		for (int i = 1; i < data_listView.size(); i++) {
//
//			String sql = "insert into shgl_ywgl_fwbgsbjb (mxh, sxh, zbh, bjmc, sl, bz) values"
//					+ "("
//					+ "'"
//					+ "%s"
//					+ "'"
//					+ ","
//					+ 1
//					+ ","
//					+ "'"
//					+ pgdh
//					+ "'"
//					+ ",'"
//					+ data_listView.get(i).get("id")
//					+ "',"
//					+ data_listView.get(i).get("number")
//					+ ",'"
//					+ data_listView.get(i).get("bz") + "')" + "*sql*";
//			sqlList.add(sql);
//
//		}
//		
//		return sqlList;
//
//	}
//	
//	private ArrayList<String> getHpdata() {
//		ArrayList<String> hpdata = new ArrayList<String>();
//		for (int i = 1; i < data_listView.size(); i++) {
//
//			String sql = data_listView.get(i).get("id")+","
//						+data_listView.get(i).get("machine")+","
//						+ data_listView.get(i).get("number")+","
//						+ data_listView.get(i).get("sfhs");
//			hpdata.add(sql);
//
//		}
//		return hpdata;
//
//	}
//
}
