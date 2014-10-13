package org.aisen.osc.support.bean;

import java.io.Serializable;

import org.aisen.osc.R;

import android.content.res.Resources;

import com.m.common.context.GlobalContext;

public class MenuBean implements Serializable {

	private static final long serialVersionUID = 3845112378360217028L;
	
	private String type;
	
	private String title;
	
	public MenuBean() {
		
	}
	
	public MenuBean(String type, String title) {
		this.type = type;
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCategory() {
		Resources res = GlobalContext.getInstance().getResources();
		
		int typeInt = Integer.parseInt(type);
		if (typeInt < 20)
			return res.getString(R.string.tweet_category);
		else if (typeInt < 30) 
			return res.getString(R.string.news_category);
		else if (typeInt < 40) 
			return res.getString(R.string.post_category);
		
		return "";
	}

}
