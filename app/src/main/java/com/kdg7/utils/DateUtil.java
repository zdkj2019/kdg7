package com.kdg7.utils;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	/**
	 * 格式化系统时间
	 * 
	 * @param format
	 * @return
	 */
	public static String toDataString(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date now = new Date();
		return sdf.format(now);
	}

	/**
	 * 格式化系统时间
	 * 
	 * @param format
	 * @return
	 */
	public static String toDataString(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	/**
	 * 时间反格式化
	 * 
	 * @param s
	 * @return
	 */
	public static Date StringToDate(String s) {
		Date time = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			time = df.parse(s);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return time;
	}

	/**
	 * 把金额进行元、角、分的转换
	 * 
	 * @param recfee
	 * @return
	 */
	public String changeRecfee(String recfee) {
		int temp = Integer.parseInt(recfee);
		int y = temp / 100;
		int j = (temp % 100) / 10;
		int f = (temp % 100) % 10;
		return "" + y + "." + j + "" + f + " 元";
	}
	
	public static String getRandomNum(){
		String str = "";
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			int j = random.nextInt(9);
			str = str + j;
		}

		return str;
	} 
	
	/**
	 * 把传输的数据转换成16进制
	 *
	 * @param str
	 *            需要转换的字符串
	 * @return
	 * @throws Exception
	 */
	public static String encode16(String str) throws Exception {
		String hexString = "0123456789ABCDEF";
		// 根据默认编码获取字节数组
		byte[] bytes = null;
		bytes = str.getBytes("GBK");//
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		String s = sb.toString();
		return s;
	}
	
	public static String decode16(String bytes) {
        String hexString = "0123456789ABCDEF";
        bytes = bytes.toUpperCase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2){
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        }
        String bb = "";  
        try {  
            bb = new String(baos.toByteArray(), "GBK");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return bb;
    }
}
