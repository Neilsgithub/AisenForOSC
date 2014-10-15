package org.aisen.osc.ui.fragment.account;

import java.io.Serializable;

import org.aisen.osc.R;
import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.User;
import org.aisen.osc.ui.activity.common.FragmentContainerActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.m.common.params.Params;
import com.m.common.params.ParamsUtil;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.Logger;
import com.m.support.Inject.ViewInject;
import com.m.support.task.TaskException;
import com.m.support.task.WorkTask;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.utils.ViewUtils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * 用户授权登录
 * 
 * @author Jeff.Wang
 *
 * @date 2014年9月19日
 */
public class OauthFragment extends ABaseFragment {

	public static void launch(ABaseFragment from, int requestCode) {
		FragmentContainerActivity.launchForResult(from, OauthFragment.class, null, requestCode);
	}
	
	private static final String TAG = "Login";
	
	@ViewInject(id = R.id.webview)
	WebView webView;
	@ViewInject(id = R.id.progress)
	SmoothProgressBar progressBar;
	
	private LoginTask loginTask;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_oauth;
	}
	
	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.ui_login_title);
		
		progressBar.setIndeterminate(true);
		
		WebSettings setting = webView.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true); 
		setting.setAppCacheEnabled(true);
		setting.setDefaultTextEncodingName("utf-8") ;
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
				view.loadUrl(url);
				
				return true;
			}

		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress < 100) {
					progressBar.setVisibility(View.VISIBLE);
				} else if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				}
				
				if (newProgress < 100) {
					
				} else if (newProgress == 100) {
					Logger.d(TAG, String.format("progress = 100 , url = %s", view.getUrl()));
					
					// 是否授权成功
					if (loginTask == null && webView.getUrl() != null && webView.getUrl().startsWith(SettingUtility.getStringSetting("callback_url"))) {
						Params params = ParamsUtil.deCodeUrl(webView.getUrl());
						String code = params.getParameter("code");
						
						Logger.d(TAG, "授权成功, code = " + code);
						
						loginTask = new LoginTask();
						loginTask.execute(code);
					}
				}
				
				if (webView.getUrl() != null && webView.getUrl().startsWith(SettingUtility.getStringSetting("callback_url"))) {
					webView.setVisibility(View.GONE);
				}
				super.onProgressChanged(view, newProgress);
			}

		});
	}
	
	@Override
	public void requestData() {
		super.requestData();
		
		webView.setVisibility(View.VISIBLE);
		// 请求授权
		OSCSdk.newInstance(null).doWebRequest(webView);
	}
	
	class LoginTask extends WorkTask<String, Integer, Serializable[]> {

		@Override
		protected void onPrepare() {
			super.onPrepare();
			
			ViewUtils.createNormalProgressDialog(getActivity(), getString(R.string.login_loading)).show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			if (getActivity() != null)
				ViewUtils.updateNormalProgressDialog(getString(values[0]));
		}
		
		@Override
		public Serializable[] workInBackground(String... params) throws TaskException {
			Token token =  OSCSdk.newInstance(null).getToken(params[0]);
			
			publishProgress(R.string.login_user_loading);
			User user = OSCSdk.newInstance(token).getUser();
			
			return new Serializable[]{ token, user };
		}
		
		@Override
		protected void onFailure(TaskException exception) {
			super.onFailure(exception);
			
			showMessage(exception.getErrorMsg());
			
			loginTask = null;
		}
		
		@Override
		protected void onSuccess(Serializable[] result) {
			super.onSuccess(result);
			
			showMessage(R.string.login_success);
			
			if (getActivity() != null) {
				Token token = (Token) result[0];
				User user = (User) result[1];
				
				Intent data = new Intent();
				data.putExtra("token", token);
				data.putExtra("user", user);
				getActivity().setResult(Activity.RESULT_OK, data);
				getActivity().finish();
			}
		}
		
		@Override
		protected void onFinished() {
			super.onFinished();
			
			ViewUtils.dismissNormalProgressDialog();
			
		}
		
	}
	
}
