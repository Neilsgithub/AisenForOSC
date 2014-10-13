package org.aisen.osc.support.utils;

import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.User;

import com.m.support.sqlite.property.Extra;

public class AppContext {

	private static Token token;
	
	private static User user;

	/**
	 * 是否授权登录了
	 * 
	 * @return
	 */
	public static boolean isLogedin() {
		return token != null && user != null;
	}
	
	/**
	 * 应用用户登录
	 * 
	 * @param user
	 * @param token
	 * @param save 是否保存到本地
	 */
	public static void login(User user, Token token, boolean save) {
		AppContext.token = token;
		AppContext.user = user;
		
		if (save) {
			OSCDb.getSqlite().insert(null, token);
			OSCDb.getSqlite().insert(new Extra(null, "oauth"), user);
		}
	}
	
	public static Token getToken() {
		return token;
	}
	
	public static User getUser() {
		return user;
	}
	
}
