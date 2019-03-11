package com.kdg7.common;

import android.os.Environment;

public class Constant {
	
//	public static  String ImgPath = "http://192.168.10.86/";
	public static  String ImgPath = "http://115.28.55.47/";
	
//    public static  String STM_WEBSERVICE_URL_dx = "http://192.168.10.86:8000/ws/services/zd_crm_u_getdata_web?wsdl";
    public static  String STM_NAMESPACE = "http://zd_crm";
//    public static  String ImgPath = "http://121.40.206.151/";
    public static  String STM_WEBSERVICE_URL_dx = "http://115.28.55.47:7001/ws/services/zd_crm_u_getdata_web?wsdl";
    
    
//    public static  String STM_WEBSERVICE_URL_dx = "http://esp.zdetech.com:7001/ws/services/zdgl_kdg_x1_u_getdata_web?wsdl";
//    public static  String ImgPath = "http://esp.zdetech.com";
    
//    public static String WEBSERVICE_URL_MSG = "http://192.168.10.30:8000/ws/services/dxs_u_getdata?wsdl";
    public static String WEBSERVICE_URL_MSG = "http://115.28.55.47:8000/ws/services/dxs_u_getdata?wsdl";
    public static String WEBSERVICE_URL_MSG_NAMESPACE = "http://dxs";
    
    public static String AppName = "kdg7.apk";
	public static String PackageName = "com.kdg7";

	public static final int NETWORK_ERROR = 1;
	public static final int SUCCESS = 2;
	public static final int FAIL = 3;
	public static final int DOWNLOAD_FAIL = 4;
	public static final int SUBMIT_SUCCESS = 5;
	public static final int NUM_6 = 6;
	public static final int NUM_7 = 7;
	public static final int NUM_8 = 8;
	public static final int NUM_9 = 9;
	public static final int NUM_10 = 10;
	public static final int NUM_11 = 11;
	public static final int NUM_12 = 12;
	public static final int NUM_13 = 13;
	public static final int NUM_14 = 14;
	
	public static final String NETWORK_ERROR_STR = "网络连接失败！";
	public static final String SUCCESS_STR = "操作成功！";
	public static final String SUBMIT_SUCCESS_STR = "提交成功！";
	public static final String FAIL_STR = "操作失败！";
	public static final String TIME_OUT = "操作超时！";
	public static final String DOWNLOAD_FAIL_STR = "下载失败！";
	
	public static String SAVE_PIC_PATH = Environment.getExternalStorageState()
			.equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment
			.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
}
