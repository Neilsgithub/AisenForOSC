package org.aisen.osc.sdk.bean;

/**
 * 实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Entity extends Base {

	public Entity() {
		super();
	}

	public Entity(boolean cache, boolean expired) {
		super(cache, expired);
	}

	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
