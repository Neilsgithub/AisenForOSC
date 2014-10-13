package org.aisen.osc.sdk.bean;

import com.m.support.sqlite.annotation.Id;

/**
 * /action/openapi/user接口的DTO
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class User extends BaseBean {

	private static final long serialVersionUID = 3346649280877522173L;

	@Id(column = "userId")
	private String id;// 用户ID
	
	private String email;// 用户email
	
	private String name;// 用户名
	
	private String gender;// 性别
	
	private String avatar;// 头像
	
	private String location;// 地点
	
	private String url;// 主页

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
