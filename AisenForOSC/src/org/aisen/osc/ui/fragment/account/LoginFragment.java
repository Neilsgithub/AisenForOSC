package org.aisen.osc.ui.fragment.account;

import org.aisen.osc.R;
import org.aisen.osc.sdk.OSCSdk;
import org.aisen.osc.sdk.bean.User;
import org.aisen.osc.support.utils.AppContext;
import org.aisen.osc.ui.activity.common.FragmentContainerActivity;
import org.aisen.osc.ui.activity.main.MainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.m.common.utils.ActivityHelper;
import com.m.support.Inject.ViewInject;
import com.m.support.task.TaskException;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.utils.ViewUtils;

/**
 * 用户登录<br/>
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月15日
 */
public class LoginFragment extends ABaseFragment implements TextWatcher {

	public static void launch(Activity from) {
		FragmentContainerActivity.launch(from, LoginFragment.class, null);
	}
	
	@ViewInject(id = R.id.btnLogin, click = "doLogin")
	View btnLogin;
	@ViewInject(id = R.id.txtError)
	TextView txtError;
	@ViewInject(id = R.id.editUsername)
	EditText editUsername;
	@ViewInject(id = R.id.editPwd)
	EditText editPwd;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_login;
	}
	
	@Override
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		getActivity().getActionBar().setTitle(R.string.login_btn);
		
		if (!TextUtils.isEmpty(ActivityHelper.getInstance().getShareData("lastLogedinUseranme", "")))
			editUsername.setText(ActivityHelper.getInstance().getShareData("lastLogedinUseranme", ""));
		editUsername.addTextChangedListener(this);
		editPwd.addTextChangedListener(this);
	}
	
	/**
	 * 用户登录
	 * 
	 * @param v
	 */
	void doLogin(View v) {
		String username = editUsername.getText().toString();
		String pwd = editPwd.getText().toString();
		
		// 验证用户名密码
		if (TextUtils.isEmpty(username))
			showMessage(R.string.login_error_username_empty);
		else if (TextUtils.isEmpty(pwd))
			showMessage(R.string.login_error_pwd_empty);
		else 
			new LoginTask().execute(username, pwd);
	}
	
	// 登录线程
	class LoginTask extends ABaseTask<String, Void, User> {

		public LoginTask() {
			super("LoginTask");
		}
		
		@Override
		protected void onPrepare() {
			super.onPrepare();
			
			ViewUtils.createNormalProgressDialog(getActivity(), getString(R.string.login_loading_1)).show();
		}

		@Override
		public User workInBackground(String... params) throws TaskException {
			return OSCSdk.newInstance().doLogin(params[0], params[1]);
		}
		
		@Override
		protected void onFailure(TaskException exception) {
			super.onFailure(exception);

			txtError.setText(exception.getMessage());
			txtError.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onSuccess(User result) {
			super.onSuccess(result);
			
			if (getActivity() == null)
				return;
			
			// 记录用户登录信息，以后每次启动应用的时候，都做一次有效验证刷新cookie
			result.setAccount(getParams()[0]);
			result.setPwd(getParams()[1]);
			
			// 记录最后一次登录成功的用户名
			ActivityHelper.getInstance().putShareData("lastLogedinUseranme", getParams()[0]);
			
			AppContext.login(result, null, true);
			
			MainActivity.launch(getActivity());
			
			getActivity().finish();
		}
		
		@Override
		protected void onFinished() {
			super.onFinished();
			
			ViewUtils.dismissNormalProgressDialog();
		}
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		txtError.setVisibility(View.GONE);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

}
