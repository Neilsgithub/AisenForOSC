package org.aisen.osc.ui.fragment.tweet;

import java.util.List;

import org.aisen.osc.R;
import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.bean.TweetBean;
import org.aisen.osc.sdk.bean.TweetBeans;
import org.aisen.osc.support.bean.MenuBean;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.support.utils.AppSettings;
import org.aisen.osc.support.utils.OSCHelper;
import org.android.loader.BitmapLoader;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.support.Inject.ViewInject;
import com.m.support.adapter.ABaseAdapter.AbstractItemView;
import com.m.support.paging.IPaging;
import com.m.support.paging.PageIndexPaging;
import com.m.support.task.TaskException;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.fragment.ACombinationRefreshListFragment;

/**
 * 动弹列表<br/>
 * 最新动弹、热门动弹、我的动弹
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class TweetListFragment extends ACombinationRefreshListFragment<TweetBean, TweetBeans> {

	public static ABaseFragment newInstance(MenuBean menu) {
		ABaseFragment fragment = new TweetListFragment();
		
		Bundle args = new Bundle();
		args.putSerializable("menu", menu);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private MenuBean menu;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_tweetlist;
	}
	
	@Override
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		menu = savedInstanceSate == null ? (MenuBean) getArguments().getSerializable("menu")
										 : (MenuBean) savedInstanceSate.getSerializable("menu");
	}
	
	@Override
	protected IPaging<TweetBean, TweetBeans> configPaging() {
		return new PageIndexPaging<TweetBean, TweetBeans>();
	}
	
	@Override
	protected AbstractItemView<TweetBean> newItemView() {
		return new TweetItemView();
	}

	@Override
	protected void requestData(RefreshMode mode) {
		// 如果是下拉，就重置数据
		new TweetListTask(mode == RefreshMode.refresh ? RefreshMode.reset : mode).execute();
	}
	
	class TweetListTask extends PagingTask<Void, Void, TweetBeans> {

		public TweetListTask(RefreshMode mode) {
			super("TweetListTask", mode);
		}

		@Override
		protected List<TweetBean> parseResult(TweetBeans result) {
			return result.getTweetlist();
		}

		@Override
		protected TweetBeans workInBackground(RefreshMode mode, String previousPage, String nextPage,
				Void... params) throws TaskException {
			String type = "0";
			// 最新动弹
			if ("11".equals(menu.getType()))
				type = "0";
			// 热门动弹
			else if ("12".equals(menu.getType()))
				type = "-1";
			// 我的动弹
			else if ("13".equals(menu.getType()))
				type = AppContext.getUser().getId();
			int pageIndex = mode == RefreshMode.reset ? 1 : Integer.parseInt(nextPage);
			int pageSize = AppSettings.getTweetListSize();

			TweetBeans result = OSCSdk.newInstance(AppContext.getToken()).getTweetList(type, pageIndex, pageSize); 
			// 加载数目少于3个，就默认全部加载完了
			if (result != null && result.getTweetlist().size() < AppSettings.minSize())
				result.setNoMore(true);
			
			return result;
		}
		
	}
	
	class TweetItemView extends AbstractItemView<TweetBean> {
		
		@ViewInject(id = R.id.imgPhoto)
		ImageView imgPhoto;
		@ViewInject(id = R.id.txtName)
		TextView txtName;
		@ViewInject(id = R.id.txtDesc)
		TextView txtDesc;
		
		@ViewInject(id = R.id.txtBody)
		TextView txtBody;
		
		@ViewInject(id = R.id.txtComment) 
		TextView txtComment;
		
		@ViewInject(id = R.id.btnMenus) 
		View btnMenus;
		
		@Override
		public int inflateViewId() {
			return R.layout.item_tweet;
		}

		@Override
		public void bindingData(View convertView, TweetBean data) {
			// 作者
			txtName.setText(data.getAuthor());
			// 头像
			if (!TextUtils.isEmpty(data.getPortrait())) {
				BitmapLoader.getInstance().display(TweetListFragment.this, 
														data.getPortrait(), 
														imgPhoto, 
														OSCHelper.getPhotoImageConfig());
			}
			else {
				imgPhoto.setImageResource(R.drawable.user_placeholder);
			}
			
			// 描述
			String client = OSCHelper.getAppClient(data.getAppClient());
			txtDesc.setText(String.format("%s %s", OSCHelper.convDate(data.getPubDate()), client));
			
			// 正文
			txtBody.setText(data.getBody());
			
			// 评论数
			txtComment.setVisibility(data.getCommentCount() > 0 ? View.VISIBLE : View.GONE);
			txtComment.setText(String.valueOf(data.getCommentCount()));
		}
		
	}
	
}
