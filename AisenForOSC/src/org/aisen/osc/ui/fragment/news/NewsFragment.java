package org.aisen.osc.ui.fragment.news;

import java.util.List;

import org.aisen.osc.R;
import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.bean.News;
import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.support.utils.AppContext;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.m.support.Inject.ViewInject;
import com.m.support.adapter.ABaseAdapter.AbstractItemView;
import com.m.support.bizlogic.ABaseBizlogic.CacheMode;
import com.m.support.paging.IPaging;
import com.m.support.paging.PageIndexPaging;
import com.m.support.task.TaskException;
import com.m.ui.fragment.ARefreshListFragment;

public class NewsFragment extends ARefreshListFragment<News, NewsList> {

	@Override
	protected int inflateContentView() {
		return R.layout.ui_news;
	}
	
	@Override
	public String getLastReadKey() {
		return "NewsList";
	}
	
	@Override
	protected IPaging<News, NewsList> configPaging() {
		return new PageIndexPaging<News, NewsList>();
	}

	@Override
	protected AbstractItemView<News> newItemView() {
		return new NewsItemView();
	}

	@Override
	protected void requestData(RefreshMode mode) {
		new NewsTask(mode == RefreshMode.refresh ? RefreshMode.reset : mode).execute();
	}
	
	class NewsItemView extends AbstractItemView<News> {

		@ViewInject(id = R.id.txtTitle)
		TextView txtTitle;
		
		@Override
		public int inflateViewId() {
			return R.layout.item_news;
		}

		@Override
		public void bindingData(View convertView, News data) {
			txtTitle.setText(data.getTitle());
		}
		
	}
	
	class NewsTask extends PagingTask<Void, Void, NewsList> {

		public NewsTask(RefreshMode mode) {
			super("NewsTask", mode);
		}

		@Override
		protected List<News> parseResult(NewsList result) {
			return result.getNewslist();
		}

		@Override
		protected NewsList workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... params)
				throws TaskException {
			int page = 1;
			if (mode == RefreshMode.update && !TextUtils.isEmpty(nextPage))
				page = Integer.parseInt(nextPage);
			
			// 优先加载缓存数据
			CacheMode cacheMode = CacheMode.cachePriority;
			// 重置数据时，拉取网络，注意，第一次重置数据优先拉取缓存
			if (getTaskCount(getTaskId()) > 1 && mode == RefreshMode.reset)
				cacheMode = CacheMode.disable;
			
			return OSCSdk.newInstance(cacheMode, AppContext.getToken()).getNews(1, page, 20);
		}
		
		@Override
		protected void onFailure(TaskException exception) {
			super.onFailure(exception);
			
			showMessage(exception.getMessage());
		}
		
	}

}
