package org.aisen.osc.ui.fragment.tweet;

import java.util.ArrayList;

import org.aisen.osc.R;
import org.aisen.osc.support.bean.MenuBean;
import org.aisen.osc.ui.fragment.tab.ATabsFragment;

import android.content.res.Resources;

import com.m.common.context.GlobalContext;
import com.m.ui.fragment.ABaseFragment;

/**
 * 最新动弹、热门动弹、我的动弹
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class TweetTabsFragment extends ATabsFragment {

	public static ABaseFragment newInstance() {
		return new TweetTabsFragment();
	}
	
	@Override
	protected ABaseFragment newFragment(MenuBean menu) {
		return TweetListFragment.newInstance(menu);
	}

	@Override
	public ArrayList<MenuBean> generateMenus() {
		Resources res = GlobalContext.getInstance().getResources();
		
		ArrayList<MenuBean> menuList = new ArrayList<MenuBean>();
		
		menuList.add(new MenuBean("11", res.getString(R.string.tweet_01)));
		menuList.add(new MenuBean("12", res.getString(R.string.tweet_02)));
		menuList.add(new MenuBean("13", res.getString(R.string.tweet_03)));
		
		return menuList;
	}

}
