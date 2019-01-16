package com.kdg7.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;

import com.kdg7.activity.login.LoginActivity;
import com.kdg7.po.User;

public class DataCache implements Serializable {

	private static final long serialVersionUID = 1L;
	private static DataCache dataCache;
	
	private User user;
	private byte[] imageByte;
	private List<String> menu;
	private Map<String, Object> data;
	
	private DataCache(){
		
	}
	public static DataCache getinition() {
		if (dataCache == null) {
			dataCache = new DataCache();
		}
		return dataCache;

	}

	
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public List<String> getMenu() {
		return menu;
	}
	public void setMenu(List<String> menu) {
		this.menu = menu;
	}
	public byte[] getImageByte() {
		return imageByte;
	}

	public void setImageByte(byte[] imageByte) {
		this.imageByte = imageByte;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
