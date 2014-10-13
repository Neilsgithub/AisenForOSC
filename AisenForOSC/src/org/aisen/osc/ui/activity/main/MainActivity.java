package org.aisen.osc.ui.activity.main;

import org.aisen.osc.R;
import org.aisen.osc.support.bean.MenuBean;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.ui.fragment.menu.MenuFragment;
import org.aisen.osc.ui.fragment.menu.MenuFragment.MenuCallback;
import org.aisen.osc.ui.fragment.tweet.TweetTabsFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.support.Inject.ViewInject;
import com.m.ui.activity.BaseActivity;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.fragment.CacheClearFragment;

/**
 * 应用首页
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class MainActivity extends BaseActivity implements MenuCallback {

	public static void launch(Activity from) {
		Intent intent = new Intent(from, MainActivity.class);
		from.startActivity(intent);
	}
	
	@ViewInject(id = R.id.drawer)
	private DrawerLayout mDrawerLayout;
	
	private ActionBarDrawerToggle mDrawerToggle;
	private MenuBean lastSelectedMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_light, 
				R.string.draw_open, R.string.draw_close) {
			
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				
				if (lastSelectedMenu != null) {
                    getActionBar().setTitle(lastSelectedMenu.getCategory());
                    invalidateOptionsMenu();
                }
			}
			
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
				
				getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
			
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
			}
			
			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
			}
			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		
		lastSelectedMenu = savedInstanceState == null ? null : (MenuBean) savedInstanceState.getSerializable("menu");
		
		if (ActivityHelper.getInstance().getBooleanShareData("isFirstLaunch", true)) {
			ActivityHelper.getInstance().putBooleanShareData("isFirstLaunch", false);
			
			mDrawerLayout.openDrawer(Gravity.LEFT);
		} else {
			if (lastSelectedMenu != null)
				getActionBar().setTitle(lastSelectedMenu.getCategory());
			else
				getActionBar().setTitle(R.string.tweet_title);
		}
		
		if (savedInstanceState == null) {
			// 默认显示动弹
			MenuBean menu = new MenuBean();
			menu.setTitle(getString(R.string.tweet_title));
			menu.setType("11");
			
			MenuFragment menuFragment = MenuFragment.newInstance(menu);
			menuFragment.setMenuCallback(this);
			getFragmentManager().beginTransaction().add(R.id.menu_frame, menuFragment, "MenuFragment").commit();
		}
		else {
			MenuFragment menuFragment = (MenuFragment) getFragmentManager().findFragmentByTag("MenuFragment");
			menuFragment.setMenuCallback(this);

			// 2014-8-30 解决因为状态保存而导致的耗时阻塞
			if (lastSelectedMenu.getType().equals("1"))
				onMenuSelected(lastSelectedMenu, true);
		}
	}

	@Override
	public boolean onMenuSelected(MenuBean menu, boolean replace) {
		if (!replace && lastSelectedMenu != null && lastSelectedMenu.getType().equals(menu.getType())) {
			closeDrawer();
			return true;
		}
		
		Logger.d(MenuFragment.TAG, String.format("category = %s, title = %s, type = %s", 
														menu.getCategory(), menu.getTitle(), menu.getType()));
		
		ABaseFragment fragment = null;
		
		int type = Integer.parseInt(menu.getType());
		
		// 动弹
		if (type < 20) {
			fragment = TweetTabsFragment.newInstance();
		}
		// 资讯
		else if (type < 30) {
			
		}
		// 问答
		else if (type < 40) {
			
		}
		
		if (fragment == null)
			return true;
		
		getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "MainFragment").commit();
		
		lastSelectedMenu = menu;
		return false;
	}
	
	public void closeDrawer() {
		mDrawerLayout.closeDrawers();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		mDrawerToggle.syncState();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		resetAcUnused();
		
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;
		
		return super.onOptionsItemSelected(item);
	}
	
	public boolean isDrawerOpened() {
		return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
	}
	
	private boolean canFinish = false;
	
	@Override
	public boolean onBackClick() {
		if (!canFinish) {
			canFinish = true;

			showMessage(R.string.main_exit);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					canFinish = false;
				}
				
			}, 1500);
			
			return true;
		}
		
		setMDestory(true);
		return super.onBackClick();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!AppContext.isLogedin())
			finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// 清理压缩缓存
		CacheClearFragment.clearCompress();
	}
	
}
