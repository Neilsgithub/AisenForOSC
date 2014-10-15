package org.aisen.osc.sdk.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.aisen.osc.sdk.OSCSdk.ApiType;
import org.aisen.osc.sdk.support.xml.IXmlToDto;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.m.common.params.Params;
import com.m.common.params.ParamsUtil;
import com.m.common.settings.Setting;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.common.utils.SystemUtility;
import com.m.common.utils.SystemUtility.NetWorkType;
import com.m.support.bizlogic.ABaseBizlogic;
import com.m.support.network.HttpConfig;
import com.m.support.network.HttpUtility;
import com.m.support.network.http.DefHttpUtility;
import com.m.support.task.TaskException;

public class OSCHttpUtility implements HttpUtility {

	private static final String TAG = DefHttpUtility.class.getSimpleName();
	
	static final int TIMEOUT_SOCKET = 8 * 1000;

	@Override
	public <T> T doGet(HttpConfig config, Setting action, Params params, Class<T> responseCls) throws TaskException {
		// 是否有网络连接
		if (SystemUtility.getNetworkType() == NetWorkType.none)
			throw new TaskException(TaskException.TaskError.noneNetwork.toString());

		String url = (config.baseUrl + action.getValue() + (params == null ? "" : "?" + ParamsUtil.encodeToURLParams(params))).replaceAll(" ", "");
		Logger.v(TAG, url);

		return executeClient(action, getHttpGet(url, config), responseCls);
	}

	@Override
	public <T> T doPost(HttpConfig config, Setting action, Params params, Class<T> responseCls, Object requestObj) throws TaskException {
		// 是否有网络连接
		if (SystemUtility.getNetworkType() == NetWorkType.none)
			throw new TaskException(TaskException.TaskError.noneNetwork.toString());

		String url = (config.baseUrl + action.getValue() + (params == null ? "" : "?" + ParamsUtil.encodeToURLParams(params))).replaceAll(" ", "");
		Logger.v(TAG, url);

		PostMethod httpPost = getHttpPost(url, config);
		
		int length = params == null ? 0 : params.size();
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.getKeys()){
        	parts[i++] = new StringPart(name, String.valueOf(params.getParameter(name)), "UTF-8");
        }
        
		return executeClient(action, httpPost, responseCls);
	}

	public <T> T uploadFile(HttpConfig config, Setting action, Params params, File file, Params headers, Class<T> responseClazz) throws TaskException {
		PostMethod postMethod = new PostMethod((config.baseUrl + action.getValue() + (params == null ? "" : "?"
				+ ParamsUtil.encodeToURLParams(params))).replaceAll(" ", ""));

		StringPart sp = new StringPart(" TEXT ", " testValue ");
		FilePart fp = null;
		try {
			fp = new FilePart("file", file.getName(), file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		MultipartRequestEntity mrp = new MultipartRequestEntity(new Part[] { sp, fp }, postMethod.getParams());
		postMethod.setRequestEntity(mrp);
		postMethod.addRequestHeader("cookie", config.cookie);

		if (headers != null)
			for (String key : headers.getKeys())
				postMethod.addRequestHeader(key, headers.getParameter(key));

		// 执行postMethod
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		try {
			httpClient.executeMethod(postMethod);
			Logger.v(ABaseBizlogic.TAG, String.format("upload file's response body = %s", postMethod.getResponseBodyAsString()));
//			T result = new ObjectMapper().readValue(postMethod.getResponseBodyAsString(), responseClazz);
			T result = parseStrToDTO(postMethod.getResponseBodyAsString(), responseClazz);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private <T> T parseStrToDTO(String responseStr, Class<T> responseCls) throws TaskException {
		if (ApiType.valueOf(SettingUtility.getPermanentSettingAsStr("apitype", "openapi")) == ApiType.api) {
			Object t = null;
			try {
				t = responseCls.newInstance();
			} catch (Exception e) {
			}
			if (t != null && t instanceof IXmlToDto) {
				return ((IXmlToDto) t).parse(responseStr, responseCls);
			}
		}
		else if (ApiType.valueOf(SettingUtility.getPermanentSettingAsStr("apitype", "openapi")) == ApiType.openapi) {
			return JSON.parseObject(responseStr, responseCls);
		}
		
		throw new TaskException(TaskException.TaskError.resultIllegal.toString());
	}

	@SuppressWarnings("unchecked")
	private <T> T executeClient(Setting action, HttpMethod request, Class<T> responseCls) throws TaskException {
		try {
			HttpClient httpClient = generateHttpClient();

			int statusCode = httpClient.executeMethod(request);
			if (statusCode == HttpStatus.SC_OK) {
				if (action != null && action.getType().equals("actionLogin")) {
					Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(!TextUtils.isEmpty(tmpcookies)){
	        			Logger.w(tmpcookies);
	        			ActivityHelper.getInstance().putShareData("cookie", tmpcookies);
	        		}
				}
				
				String responseStr = readResponse(request.getResponseBodyAsStream());
				try {
					if (responseCls.getSimpleName().equals("String"))
						return (T) responseStr;
					
					return parseStrToDTO(responseStr, responseCls);
				} catch (Exception e) {
					e.printStackTrace();
					throw new TaskException(TaskException.TaskError.resultIllegal.toString());
				}
			} else {
				Logger.e(ABaseBizlogic.TAG,
						String.format("Access to the server error, statusCode = %d", statusCode));
				Logger.w(ABaseBizlogic.TAG, request.getResponseBodyAsString());
				throw new TaskException(TaskException.TaskError.timeout.toString());
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskException(TaskException.TaskError.timeout.toString());
		}
	}

	private static GetMethod getHttpGet(String url, HttpConfig config) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", "www.oschina.net");
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", config.cookie);
		if (config instanceof OSCHttpConfig)
			httpGet.setRequestHeader("User-Agent", ((OSCHttpConfig) config).userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, HttpConfig config) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", "www.oschina.net");
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", config.cookie);
		if (config.cookie != null)
			Logger.d(ABaseBizlogic.TAG, config.cookie);
		if (config instanceof OSCHttpConfig) {
			String userAgent = ((OSCHttpConfig) config).userAgent;
			if (!TextUtils.isEmpty(userAgent)) {
				httpPost.setRequestHeader("User-Agent", userAgent);
				Logger.d(ABaseBizlogic.TAG, userAgent);
			}
		}
		return httpPost;
	}

	private HttpClient generateHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_SOCKET);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset("UTF-8");

		return httpClient;
	}

	private String readResponse(InputStream inputStream) throws IllegalStateException, IOException {
		String result = "";

		ByteArrayOutputStream content = new ByteArrayOutputStream();

		int readBytes = 0;
		byte[] sBuffer = new byte[1024 * 8];
		while ((readBytes = inputStream.read(sBuffer)) != -1) {
			content.write(sBuffer, 0, readBytes);
		}
		result = new String(content.toByteArray());
		
		Logger.d(ABaseBizlogic.TAG, String.format("response = %s", result));
		
		return result;
	}
	
}
