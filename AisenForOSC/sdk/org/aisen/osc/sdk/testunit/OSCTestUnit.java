package org.aisen.osc.sdk.testunit;

import java.io.Serializable;

import org.aisen.osc.sdk.OSCApi;
import org.aisen.osc.sdk.OSCApi.ApiType;
import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.sdk.bean.Token;

import android.test.AndroidTestCase;

import com.alibaba.fastjson.JSON;
import com.m.common.settings.SettingUtility;

public class OSCTestUnit extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		SettingUtility.setSettingUtility();
		
//		OSCApi.config(ApiType.api);
		OSCApi.config(ApiType.openapi);
		OSCApi.init();
		
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception e) {
		}
	}
	
	private Token getToken() {
		Token token = new Token();
		
		token.setAccess_token("870a4dc8-96a1-45b3-83f2-12de3dafaec1");
		
		return token;
	}
	
	private void log(Serializable s) {
		System.out.println(JSON.toJSONString(s));
	}
	
	public void testGetNewss() throws Throwable {
		try {
			int catalog = 1;// 1-所有|2-综合新闻|3-软件更新
			NewsList bean = OSCApi.newInstance(null).getNews(catalog, 1, 5);
			log(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
