package com.kdg7.activity.w;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kdg7.R;
import com.kdg7.activity.FrameActivity;
import com.kdg7.common.Constant;

public class SetParams extends FrameActivity {

	private TextView tv_val;
	private EditText et_val;
	private Button confirm, cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appendMainBody(R.layout.activity_w_params);
		initVariable();
		initView();
		initListeners();
	}

	protected void initVariable() {
		tv_val = (TextView) findViewById(R.id.tv_val);
		et_val = (EditText) findViewById(R.id.et_val);
		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);
		tv_title.setText("路径配置");
	}

	protected void initView() {
		tv_val.setText(Constant.STM_WEBSERVICE_URL_dx);
	}

	protected void initListeners() {
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.confirm:
					if("".equals(et_val.getText().toString())){
						toastShowMessage("请录入新的wenservice地址");
					}else{
						Constant.STM_WEBSERVICE_URL_dx = "http://"+et_val.getText().toString()+":8000/ws/services/zdgl_kdg_x1_u_getdata_web?wsdl";
						Constant.ImgPath = "http://"+et_val.getText().toString()+"/";
						dialogShowMessage("修改成功", null, null);
					}
					break;
				case R.id.cancel:
					onBackPressed();
					break;
				case R.id.bt_topback:
					onBackPressed();
					break;
				}

			}
		};
		confirm.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
		topBack.setOnClickListener(onClickListener);
	}

}
