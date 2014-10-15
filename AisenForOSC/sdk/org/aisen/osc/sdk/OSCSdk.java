package org.aisen.osc.sdk;

import org.aisen.osc.sdk.bean.BaseBean;
import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.bean.TweetBeans;
import org.aisen.osc.sdk.bean.User;
import org.aisen.osc.sdk.http.OSCHttpConfig;
import org.aisen.osc.sdk.http.OSCHttpUtility;
import org.aisen.osc.support.utils.AppContext;

import android.text.TextUtils;
import android.webkit.WebView;

import com.m.common.context.GlobalContext;
import com.m.common.params.Params;
import com.m.common.params.ParamsUtil;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Consts;
import com.m.common.utils.Logger;
import com.m.common.utils.SystemUtility;
import com.m.support.bizlogic.ABaseBizlogic;
import com.m.support.network.HttpConfig;
import com.m.support.network.HttpUtility;
import com.m.support.task.TaskException;

/**
 * 开源中国OpenApi
 * 
 * @author Jeff.Wang
 *
 * @date 2014年9月19日
 */
public class OSCSdk extends ABaseBizlogic {

	public enum ApiType {
		api, openapi
	}
	
	private static ApiType apiType;
	
	private Token token;
	
	@Override
	protected HttpUtility configHttpUtility() {
		return new OSCHttpUtility();
	}
	
	private static String getUserAgent() {
		StringBuilder sb = new StringBuilder("OSChina.NET");
		sb.append('/'+SystemUtility.getVersionName(GlobalContext.getInstance())+'_'+SystemUtility.getVersionCode(GlobalContext.getInstance()));// App版本
		sb.append("/Aisen");// 手机系统平台
		sb.append("/"+android.os.Build.VERSION.RELEASE);// 手机系统版本
		sb.append("/"+android.os.Build.MODEL); // 手机型号
		sb.append("/"+"Aisen");// 客户端唯一标识
		return sb.toString();
	}
	
	@Override
	protected HttpConfig configHttpConfig() {
		OSCHttpConfig httpConfig = new OSCHttpConfig();
		httpConfig.baseUrl = getSetting(Consts.Setting.BASE_URL).getValue();
		httpConfig.contentType = "text/xml";
		httpConfig.userAgent = getUserAgent();
		
		if (!TextUtils.isEmpty(ActivityHelper.getInstance().getShareData("cookie", ""))) {
			httpConfig.cookie = ActivityHelper.getInstance().getShareData("cookie", "");
			Logger.w(httpConfig.cookie);
		}
		return httpConfig;
	}
	
	private String getAppKey() {
		return getSetting("app_key").getValue();
	}
	
	private String getAppSecret() {
		return getSetting("app_secret").getValue();
	}
	
	private String getAppCallback() {
		return getSetting("callback_url").getValue();
	}
	
	private OSCSdk() {
		
	}

	private OSCSdk(CacheMode cacheMode, Token token) {
		super(cacheMode);
		
		this.token = token;
	}
	
	public static OSCSdk newInstance() {
		return new OSCSdk(CacheMode.disable, AppContext.getToken());
	}
	
	public static OSCSdk newInstance(Token token) {
		return new OSCSdk(CacheMode.disable, token);
	}

	public static OSCSdk newInstance(CacheMode cacheMode, Token token) {
		return new OSCSdk(cacheMode, token);
	}
	
	public static void config(ApiType apiType) {
		OSCSdk.apiType = apiType;
		
		SettingUtility.setPermanentSetting("apitype", apiType.toString());
	}
	
	public static void init() {
		if (OSCSdk.apiType == ApiType.api)
			SettingUtility.addSettings("actions_api");
		else if (OSCSdk.apiType == ApiType.openapi)
			SettingUtility.addSettings("actions_openapi");
	}
	
	/**
	 * 配置请求参数<br/>
	 * 1、添加dataType<br/>
	 * 2、添加accessToken
	 * 
	 * @param params
	 * @return
	 */
	private Params configParams(Params params) {
		if (params == null) {
			params = new Params();
		}

		if (token != null) {
			params.addParameter("dataType", "json");
			params.addParameter("access_token", token.getAccess_token());
		}

		return params;
	}
	
	/**
	 * 验证结果是否有错误
	 * 
	 * @param t
	 * @return
	 * @throws TaskException
	 */
	private <T> T checkResult(T t) throws TaskException {
		if (t instanceof BaseBean) {
			BaseBean baseBean = (BaseBean) t;
			
			if (!"1".equals(baseBean.getError()) && !TextUtils.isEmpty(baseBean.getError_description()))
				throw new TaskException(baseBean.getError(), baseBean.getError_description());
		}
			
		return t;
	}
	
	/**
	 * 浏览器请求授权
	 * 
	 * @param webView
	 */
	public void doWebRequest(WebView webView) {
		Params params = new Params();
		params.addParameter("client_id", getAppKey());
		params.addParameter("redirect_uri", getAppCallback());
		params.addParameter("response_type", "code");
		
		HttpConfig config = configHttpConfig();
		String url = config.baseUrl + getSetting("requestOauch").getValue() + "?" + ParamsUtil.encodeToURLParams(params);
		webView.loadUrl(url);
	}
	
	/**
	 * 根据授权获取AccessToken
	 * 
	 * @param code
	 * @return
	 * @throws TaskException
	 */
	public Token getToken(String code) throws TaskException {
		Params params = new Params();
		params.addParameter("client_id", getAppKey());
		params.addParameter("client_secret", getAppSecret());
		params.addParameter("redirect_uri", getAppCallback());
		params.addParameter("grant_type", "authorization_code");
		params.addParameter("code", code);
		
		return doGet(getSetting("actionToken"), configParams(params), Token.class);
	}
	
	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param pwd
	 * @return
	 * @throws TaskException
	 */
	public User doLogin(String username, String pwd) throws TaskException {
		Params params = new Params();
		params.addParameter("username", username);
		params.addParameter("pwd", pwd);
		params.addParameter("keep_login", "1");
		
		return checkResult(doPost(configHttpConfig(), getSetting("actionLogin"), configParams(params), User.class, null));
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @return
	 * @throws TaskException
	 */
	public User getUser() throws TaskException {
		return doPost(configHttpConfig(), getSetting("actionUser"), configParams(null), User.class, null);
	}
	
	/**
	 * 获取动弹列表 （最新动弹列表 我的动弹）
	 * 注意，接口定义分页是从1开始的，但是实际调用WebService接口是从0开始的
	 * 
	 * @param type
	 * @return
	 * @throws TaskException
	 */
	public TweetBeans getTweetList(String type, int pageIndex, int pageSize) throws TaskException {
		Params params = new Params();
		params.addParameter("uid", type);
		// 分页是从0开始的
		params.addParameter("pageIndex", String.valueOf(pageIndex - 1));
		params.addParameter("pageSize", String.valueOf(pageSize));
		
		return doGet(getSetting("actionTweetList"), configParams(params), TweetBeans.class);
	}
	
	/**
	 * 获取新闻列表
	 * 
	 * @param catalog 
	 * 				1-所有|2-综合新闻|3-软件更新
	 * @param page 
	 * 				页数
	 * @param pageSize 
	 * 				每页条数
	 * 
	 * @return
	 * @throws TaskException
	 */
	public NewsList getNews(int catalog, int page, int pageSize) throws TaskException {
		Params params = new Params();
		params.addParameter("catalog", String.valueOf(catalog));
		params.addParameter("pageIndex", String.valueOf(page));
		params.addParameter("pageSize", String.valueOf(pageSize));
		
		return doGet(getSetting("actionNewss"), configParams(params), NewsList.class);
	}

}
