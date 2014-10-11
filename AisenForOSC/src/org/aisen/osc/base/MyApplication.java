package org.aisen.osc.base;

import org.aisen.osc.sdk.OSCApi;
import org.aisen.osc.sdk.OSCApi.ApiType;

import com.m.common.context.GlobalContext;
import com.m.ui.utils.MToast;

public class MyApplication extends GlobalContext {

	@Override
	public void onCreate() {
		super.onCreate();
		
		OSCApi.config(ApiType.api);
		OSCApi.init();
		
		MToast.type = 2;
	}
	
}
