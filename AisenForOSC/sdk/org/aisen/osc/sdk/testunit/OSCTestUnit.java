package org.aisen.osc.sdk.testunit;

import java.io.Serializable;

import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.OSCSdk.ApiType;
import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.TweetBeans;
import org.aisen.osc.sdk.bean.User;

import android.test.AndroidTestCase;

import com.alibaba.fastjson.JSON;
import com.m.common.settings.SettingUtility;

public class OSCTestUnit extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		SettingUtility.setSettingUtility();
		
		OSCSdk.config(ApiType.api);
//		OSCApi.config(ApiType.openapi);
		OSCSdk.init();
		
		try {
			Thread.sleep(5 * 1000);
		} catch (Exception e) {
		}
	}
	
	private Token getToken() {
		Token token = new Token();
		
		token.setAccess_token("ed0a5d2c-3f55-49cb-af8a-4184f0726ccf");
		
		return token;
	}
	
	private void log(Serializable s) {
		System.out.println(JSON.toJSONString(s));
	}
	
	public void testLogin() throws Throwable {
		try {
			OSCSdk.newInstance(null).doLogin("wangd.info@gmail.com", "wangdan7560");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetNews() throws Throwable {
		try {
			int catalog = 1;// 1-所有|2-综合新闻|3-软件更新
			NewsList bean = OSCSdk.newInstance(null).getNews(catalog, 1, 5);
			log(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetUser() throws Throwable {
		try {
			User user = OSCSdk.newInstance(getToken()).getUser();
			log(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetTweetList() throws Throwable {
		try {
			// 723627
			TweetBeans beans = OSCSdk.newInstance(getToken()).getTweetList("723627", 0, 5);
			log(beans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
