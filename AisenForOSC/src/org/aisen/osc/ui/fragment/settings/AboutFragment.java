package org.aisen.osc.ui.fragment.settings;

import org.aisen.osc.R;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.User;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.ui.activity.common.FragmentContainerActivity;
import org.aisen.osc.ui.activity.main.MainActivity;
import org.aisen.osc.ui.fragment.account.OauthFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.m.support.Inject.ViewInject;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.utils.FragmentArgs;

/**
 * 关于界面<br/>
 * 展示AisenForOSC的应用信息<br/>
 * 请求用户授权
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class AboutFragment extends ABaseFragment {

	/**
	 * 启动关于界面
	 * 
	 * @param from
	 * @param showLoginBtn 是否显示授权按钮
	 */
	public static void launch(Activity from, boolean showLoginBtn) {
		FragmentArgs args = new FragmentArgs();
		args.add("showLogin", showLoginBtn);
		
		FragmentContainerActivity.launch(from, AboutFragment.class, args);
	}
	
	@ViewInject(id = R.id.btnLogin, click = "launchLogin")
	View btnLogin;
	
	private boolean showLogin;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_about;
	}
	
	@Override
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		showLogin = savedInstanceSate == null ? getArguments().getBoolean("showLogin", false)
											  : savedInstanceSate.getBoolean("showLogin", false);
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setTitle("关于");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean("showLogin", showLogin);
	}
	
	void launchLogin(View v) {
		OauthFragment.launch(this, 1000);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
			Token token = (Token) data.getSerializableExtra("token");
			User user = (User) data.getSerializableExtra("user");
			
			// 保存授权信息
			AppContext.login(user, token, true);
			// 启动首页
			MainActivity.launch(getActivity());
		}
	}

}
