package org.aisen.osc.ui.activity.splash;

import org.aisen.osc.R;
import org.aisen.osc.ui.activity.common.BaseOSCActivity;
import org.aisen.osc.ui.activity.common.FragmentContainerActivity;
import org.aisen.osc.ui.fragment.news.NewsFragment;
import org.aisen.osc.ui.fragment.news.NewsSwipeFragment;

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
		
//		FragmentContainerActivity.launch(this, NewsFragment.class, null);
		FragmentContainerActivity.launch(this, NewsSwipeFragment.class, null);
		
		finish();
	}
	
}
