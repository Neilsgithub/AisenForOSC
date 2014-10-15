package org.aisen.osc.ui.activity.splash;

import org.aisen.osc.R;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.ui.activity.common.BaseOSCActivity;
import org.aisen.osc.ui.activity.main.MainActivity;
import org.aisen.osc.ui.fragment.account.LoginFragment;

import android.os.Bundle;

/**
 * 启动页
 * 
 * @author Jeff.Wang
 *
 * @date 2014年9月19日
 */
public class SplashActivity extends BaseOSCActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_splash);
		
		if (AppContext.isLogedin()) {
			MainActivity.launch(this);
		}
		else {
			LoginFragment.launch(this);
		}
		
		finish();
	}
	
}
