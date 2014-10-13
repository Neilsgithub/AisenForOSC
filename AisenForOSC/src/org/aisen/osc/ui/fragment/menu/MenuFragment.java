package org.aisen.osc.ui.fragment.menu;

import org.aisen.osc.R;
import org.aisen.osc.support.bean.MenuBean;
import org.aisen.osc.ui.activity.main.MainActivity;

import com.m.common.utils.Logger;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

/**
 * 抽屉菜单
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class MenuFragment extends PreferenceFragment 
								implements OnPreferenceClickListener {

	public static final String TAG = MenuFragment.class.getSimpleName();
	
	public static MenuFragment newInstance(MenuBean menu) {
		MenuFragment fragment = new MenuFragment();
		
		Bundle args = new Bundle();
		args.putSerializable("menu", menu);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private final String[][] typeArr = new String[][]{ {"11", "12", "13"},
														{"21", "22", "23"},
														{"31", "32", "33", "34", "35"} };
	
	private MenuCallback menuCallback;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		addPreferencesFromResource(R.xml.ui_menu);
		
		// 设置点击事件
		for (String[] type : typeArr) {
			for (String t : type)
				findPreference(t).setOnPreferenceClickListener(this);
		}
		
		if (savedInstanceState == null) {
			if (getArguments() != null) {
				MenuBean menu = (MenuBean) getArguments().getSerializable("menu");
				
				if (!menuCallback.onMenuSelected(menu, false))
					((MainActivity) getActivity()).closeDrawer();
			}
		}
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		String title = preference.getTitle().toString();
		String type = preference.getKey();
		
		MenuBean menu = new MenuBean();
		menu.setTitle(title);
		menu.setType(type);
		
		if (!menuCallback.onMenuSelected(menu, false))
			((MainActivity) getActivity()).closeDrawer();
		
		return true;
	}

	public void setMenuCallback(MenuCallback menuCallback) {
		this.menuCallback = menuCallback;
	}
	
	public interface MenuCallback {
		
		public boolean onMenuSelected(MenuBean menu, boolean replace);
		
	}
	
}
