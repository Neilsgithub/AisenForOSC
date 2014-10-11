package org.aisen.osc.sdk;

import org.aisen.osc.sdk.bean.NewsList;
import org.aisen.osc.sdk.bean.Token;
import org.aisen.osc.sdk.http.OSCHttpUtility;

import android.webkit.WebView;

import com.m.common.params.Params;
import com.m.common.params.ParamsUtil;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.Consts;
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
public class OSCApi extends ABaseBizlogic {

	public enum ApiType {
		api, openapi
	}
	
	private static ApiType apiType;
	
	private Token token;
	
	@Override
	protected HttpUtility configHttpUtility() {
		return new OSCHttpUtility();
	}
	
	@Override
	protected HttpConfig configHttpConfig() {
		HttpConfig httpConfig = new HttpConfig();
		httpConfig.baseUrl = getSetting(Consts.Setting.BASE_URL).getValue();
		if (token != null)
			httpConfig.authrization = "OAuth2 " + token.getAccess_token();
		
		httpConfig.contentType = "application/json";
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
	
	private OSCApi() {
		
	}

	private OSCApi(CacheMode cacheMode, Token token) {
		super(cacheMode);
		
		this.token = token;
	}
	
	public static OSCApi newInstance(Token token) {
		return new OSCApi(CacheMode.disable, token);
	}

	public static OSCApi newInstance(CacheMode cacheMode, Token token) {
		return new OSCApi(cacheMode, token);
	}
	
	public static void config(ApiType apiType) {
		OSCApi.apiType = apiType;
		
		SettingUtility.setPermanentSetting("apitype", apiType.toString());
	}
	
	public static void init() {
		if (OSCApi.apiType == ApiType.api)
			SettingUtility.addSettings("actions_api");
		else if (OSCApi.apiType == ApiType.openapi)
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

		params.addParameter("dataType", "json");
		if (token != null)
			params.addParameter("access_token", token.getAccess_token());

		return params;
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
