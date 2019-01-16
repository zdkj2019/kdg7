package com.kdg7.po;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private String hyid;
	private String hykh;
	private String tokenid;
	private String openid;
	private String loginTime;
	private String tel;
	private String brand;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getHyid() {
		return hyid;
	}

	public void setHyid(String hyid) {
		this.hyid = hyid;
	}

	public String getHykh() {
		return hykh;
	}

	public void setHykh(String hykh) {
		this.hykh = hykh;
	}

	public String getTokenid() {
		return tokenid;
	}

	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public static User jsonToUser(String jsonStr) {

		try {
			JSONObject json = new JSONObject(jsonStr);
			User user = new User();
			user.setHyid(json.getInt("hyid") + "");
			user.setHykh(json.getString("hykh"));
			user.setOpenid(json.getString("openid"));
			user.setTokenid(json.getString("tokenid"));
			user.setTel(json.getString("tel"));
			user.setBrand(json.getString("brand"));
			user.setLoginTime(json.getString("loginTime"));
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
