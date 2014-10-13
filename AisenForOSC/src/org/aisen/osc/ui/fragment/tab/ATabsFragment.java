package org.aisen.osc.ui.fragment.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aisen.osc.R;
import org.aisen.osc.support.bean.MenuBean;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;

import com.m.support.Inject.ViewInject;
import com.m.support.adapter.FragmentPagerAdapter;
import com.m.ui.activity.BaseActivity;
import com.m.ui.fragment.ABaseFragment;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public abstract class ATabsFragment extends ABaseFragment 
           								implements OnPageChangeListener {

	@ViewInject(idStr = "pager")
	ViewPager viewPager;
	@ViewInject(idStr = "indicator")
	TabPageIndicator indicator;
    
	MyViewPagerAdapter mViewPagerAdapter;
	
	private Map<String, ABaseFragment> fragments;
	private int selectedIndex = 0;
	
	private ArrayList<MenuBean> menuList;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_tabs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void layoutInit(LayoutInflater inflater, final Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		if (savedInstanceSate == null) {
			menuList = generateMenus();
			
			selectedIndex = 0;
			if (getArguments() != null) {
				selectedIndex = getArguments().getInt("index", 0);
			}
			
		} else {
			menuList = (ArrayList<MenuBean>) savedInstanceSate.getSerializable("menus");
			selectedIndex = savedInstanceSate.getInt("selectedIndex");
		}
		
		fragments = new HashMap<String, ABaseFragment>();
		
		if (menuList == null)
			return;
		
		for (int i = 0; i < menuList.size(); i++) {
			ABaseFragment fragment = (ABaseFragment) getActivity().getFragmentManager().findFragmentByTag(menuList.get(i).getTitle());
			if (fragment != null)
				fragments.put(menuList.get(i).getTitle(), fragment);
		}
		
		mViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());
		viewPager.setAdapter(mViewPagerAdapter);
		if (selectedIndex >= mViewPagerAdapter.getCount())
			selectedIndex = 0;
//		viewPager.setOnPageChangeListener(this);
		indicator.setViewPager(viewPager);
//		viewPager.setCurrentItem(selectedIndex);
		
		indicator.setOnPageChangeListener(this);
		indicator.setCurrentItem(selectedIndex);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("menus", menuList);
		outState.putInt("selectedIndex", viewPager.getCurrentItem());
	}
	
	class MyViewPagerAdapter extends FragmentPagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			ABaseFragment fragment = fragments.get(makeFragmentName(position));
			if (fragment == null) {
				fragment = newFragment(menuList.get(position));
				
				fragments.put(makeFragmentName(position), fragment);
			}
			
			return fragment;
		}
		
		@Override
		protected void freshUI(Fragment fragment) {
		}
		
		@Override
		public int getCount() {
			return menuList.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return menuList.get(position).getTitle();
		}

		@Override
		protected String makeFragmentName(int position) {
			return menuList.get(position).getTitle();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		try {
			destoryFragments();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void destoryFragments() {
		if (getActivity() != null) {
			if (getActivity() instanceof BaseActivity) {
				BaseActivity mainActivity = (BaseActivity) getActivity();
				if (mainActivity.mIsDestoryed())
					return;
			}
			
			try {
				FragmentTransaction trs = getFragmentManager().beginTransaction();
				Set<String> keySet = fragments.keySet();
				for (String key : keySet) {
					if (fragments.get(key) != null)
						trs.remove(fragments.get(key));
				}
				trs.commit();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		if (getActivity().getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
			getActivity().getActionBar().setSelectedNavigationItem(position);
	}

	protected abstract ABaseFragment newFragment(MenuBean menu);
	
	public abstract ArrayList<MenuBean> generateMenus();

}
