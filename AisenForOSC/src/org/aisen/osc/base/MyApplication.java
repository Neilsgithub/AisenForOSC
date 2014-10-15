package org.aisen.osc.base;

import java.util.List;

import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.OSCSdk.ApiType;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.User;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.support.utils.OSCDb;

import com.m.common.context.GlobalContext;
import com.m.support.sqlite.util.FieldUtils;
import com.m.ui.utils.MToast;

public class MyApplication extends GlobalContext {

	@Override
	public void onCreate() {
		super.onCreate();
		
		OSCSdk.config(ApiType.api);
		OSCSdk.init();
		
		MToast.type = 2;
		
		// 判断是否有用户登录授权了
		List<Token> tokenList = OSCDb.getSqlite().selectAll(Token.class);
		List<User> userList = OSCDb.getSqlite().selectAll(
													User.class, 
													String.format(" %s = ? ", FieldUtils.KEY), 
													new String[] {"oauth"});
		if (tokenList.size() > 0 && userList.size() > 0) {
			// 将授权的用户信息刷新到内存中
			AppContext.login(userList.get(0), tokenList.get(0), false);
		}
	}
	
}
