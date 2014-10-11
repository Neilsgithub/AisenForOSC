package org.aisen.osc.support.cache;

import java.util.List;

import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.support.utils.OSCDb;

import com.m.common.params.Params;
import com.m.common.settings.Setting;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.support.bizlogic.ABaseBizlogic;
import com.m.support.cache.ICacheUtility;
import com.m.support.sqlite.property.Extra;
import com.m.support.sqlite.util.FieldUtils;

public class NewsCacheUtility implements ICacheUtility {

	public static String TAG = ABaseBizlogic.TAG;
	
	@Override
	public <T> Cache<T> findCacheData(Setting action, Params params, Class<T> responseCls) {
		int expired = 120;
		int pageIndex = Integer.parseInt(params.getParameter("pageIndex"));
		
		String selection = String.format(" %s = ? ", FieldUtils.KEY);
		String[] selectionArgs = new String[]{ String.valueOf(pageIndex) };
		List<NewsList> result = OSCDb.getSqlite().selectAll(NewsList.class, selection, selectionArgs);
		if (result.size() > 0) {
			NewsList bean = result.get(0);
			
			bean.setCache(true);
			long saveTime = Long.parseLong(ActivityHelper.getInstance().getShareData("NewsSaveTime", "0"));
			bean.setExpired(System.currentTimeMillis() / 1000 - saveTime >= expired);
			
			Logger.d(TAG, String.format("加载缓存，缓存有效性%s", String.valueOf(bean.expired())));
			
			return new Cache<T>((T) bean, false);
		}
				
		return null;
	}

	@Override
	public void addCacheData(Setting action, Params params, Object responseObj) {
		int pageIndex = Integer.parseInt(params.getParameter("pageIndex"));
		if (pageIndex == 1) {
			OSCDb.getSqlite().deleteAll(null, NewsList.class);
			
			ActivityHelper.getInstance().putShareData("NewsSaveTime", String.valueOf(System.currentTimeMillis() / 1000));
		}
		
		Logger.d(TAG, String.format("刷新第%d页缓存", pageIndex));
		
		OSCDb.getSqlite().insert(new Extra(null, String.valueOf(pageIndex)), responseObj);
	}

}
