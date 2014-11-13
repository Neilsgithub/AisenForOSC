package org.aisen.osc.support.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.aisen.osc.R;
import org.aisen.osc.sdk.bean.TweetBean;
import org.android.loader.core.ImageConfig;

import android.content.Context;
import android.content.res.Resources;

import com.m.common.context.GlobalContext;
import com.m.common.utils.DateUtils;
import com.m.common.utils.SystemUtility;
import com.m.common.utils.SystemUtility.NetWorkType;

public class OSCHelper {

	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;
	public final static int CLIENT_WECHAT=6;
	
	public static ImageConfig getPhotoImageConfig() {
		ImageConfig config = new ImageConfig();
		
		config.setLoadingBitmapRes(R.drawable.user_placeholder);
		config.setLoadfaildBitmapRes(R.drawable.user_placeholder);
		
		return config;
	}
	
	public static ImageConfig getTweetImageConfig() {
		ImageConfig config = new ImageConfig();
		
		config.setId("TweetList");
		config.setLoadfaildBitmapRes(R.drawable.bg_timeline_loading);
		config.setLoadingBitmapRes(R.drawable.bg_timeline_loading);
		config.setMaxWidth(Math.round(SystemUtility.getScreenWidth() / 2.0f * 3 / 4));
		
		return config;
	}
	
	public static String getAppClient(int appClient) {
		String client = "";
		
		switch (appClient) {
		case CLIENT_MOBILE:
			client = "来自:手机";
			break;
		case CLIENT_ANDROID:
			client = "来自:Android";
			break;
		case CLIENT_IPHONE:
			client = "来自:iPhone";
			break;
		case CLIENT_WINDOWS_PHONE:
			client = "来自:Windows Phone";
			break;
		case CLIENT_WECHAT:
			client = "来自:微信";
			break;
		default:
			break;
		}
		
		return client;
	}
	
	public static String getTweetImage(TweetBean tweet) {
		if (SystemUtility.getNetworkType() == NetWorkType.wifi)
			return tweet.getImgBig();
		else 
			return tweet.getImgSmall();
	}
	
	public static String convDate(String time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Context context = GlobalContext.getInstance();
			Resources res = context.getResources();
			
			StringBuffer buffer = new StringBuffer();

			Calendar createCal = Calendar.getInstance();
			createCal.setTimeInMillis(dateFormat.parse(time).getTime());
			Calendar currentcal = Calendar.getInstance();
			currentcal.setTimeInMillis(System.currentTimeMillis());

			long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

			// 同一月
			if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) {
				// 同一天
				if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) {
					if (diffTime < 3600 && diffTime >= 60) {
						buffer.append((diffTime / 60) + res.getString(R.string.msg_few_minutes_ago));
					} else if (diffTime < 60) {
						buffer.append(res.getString(R.string.msg_now));
					} else {
						buffer.append(res.getString(R.string.msg_today)).append(" ").append(DateUtils.formatDate(createCal.getTimeInMillis(), "HH:mm"));
					}
				}
				// 前一天
				else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) {
					buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(DateUtils.formatDate(createCal.getTimeInMillis(), "HH:mm"));
				}
			}

			if (buffer.length() == 0) {
				buffer.append(DateUtils.formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
			}

			String timeStr = buffer.toString();
			if (currentcal.get(Calendar.YEAR) != createCal.get(Calendar.YEAR)) {
				timeStr = createCal.get(Calendar.YEAR) + " " + timeStr;
			}
			return timeStr;
		} catch (Exception e) {
		}

		return "";
	}
	
}
