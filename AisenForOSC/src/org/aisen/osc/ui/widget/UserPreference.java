package org.aisen.osc.ui.widget;

import org.aisen.osc.R;
import org.aisen.osc.support.utils.AppContext;
import org.android.loader.BitmapLoader;
import org.android.loader.core.ImageConfig;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户信息
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class UserPreference extends Preference {

	private TextView txtCounter;
	
	public UserPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setInit();
	}

	public UserPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setInit();
	}

	public UserPreference(Context context) {
		super(context);
		
		setInit();
	}
	
	private void setInit() {
		setLayoutResource(R.layout.lay_menu_user);
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		ImageView imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
		ImageConfig config = new ImageConfig();
		config.setLoadfaildBitmapRes(R.drawable.user_placeholder);
		config.setLoadingBitmapRes(R.drawable.user_placeholder);
		BitmapLoader.getInstance().display(null, AppContext.getUser().getAvatar(), imgPhoto, config);
		
		TextView txtName = (TextView) view.findViewById(R.id.txtName);
		txtName.setText(AppContext.getUser().getName());
		
		txtCounter = (TextView) view.findViewById(R.id.txtCounter);
		txtCounter.setVisibility(View.GONE);
	}

}
