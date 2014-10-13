package org.aisen.osc.sdk.bean;

import java.io.Serializable;

import com.m.support.iclass.IResult;

public class BaseBean implements Serializable, IResult {

	private boolean cache;
	
	private boolean _expired;
	
	private boolean _noMore;
	
	private String[] _pageIndex;
	
	private String error;
	
	private String error_description;
	
	public BaseBean() {
		
	}
	
	public BaseBean(boolean cache, boolean expired) {
		this.cache = cache;
		this._expired = expired;
	}
	
	private static final long serialVersionUID = -4148332841725594477L;

	@Override
	public boolean expired() {
		return _expired;
	}

	@Override
	public boolean isCache() {
		return cache;
	}

	public void setExpired(boolean expired) {
		this._expired = expired;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	@Override
	public boolean noMore() {
		return _noMore;
	}
	
	public void setNoMore(boolean noMore) {
		this._noMore = noMore;
	}

	@Override
	public String[] pagingIndex() {
		return _pageIndex;
	}
	
	public void setPagingIndex(String[] pageIndex) {
		this._pageIndex = pageIndex;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

}
