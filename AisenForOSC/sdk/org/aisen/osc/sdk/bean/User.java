package org.aisen.osc.sdk.bean;

import java.io.ByteArrayInputStream;

import org.aisen.osc.sdk.support.utils.StringUtils;
import org.aisen.osc.sdk.support.xml.IXmlToDto;
import org.aisen.osc.support.utils.AppSettings;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.m.R;
import com.m.common.context.GlobalContext;
import com.m.support.sqlite.annotation.Id;
import com.m.support.task.TaskException;

/**
 * /action/openapi/user接口的DTO
 * 
 * @author Jeff.Wang
 *
 * @date 2014年10月13日
 */
public class User extends BaseBean implements IXmlToDto {

	private static final long serialVersionUID = 3346649280877522173L;

	@Id(column = "userId")
	private String id;// 用户ID
	
	private String email;// 用户email
	
	private String name;// 用户名
	
	private String gender;// 性别
	
	private String avatar;// 头像
	
	private String location;// 地点
	
	private String url;// 主页
	
	private int followers;
	
	private int fans;
	
	private int score;
	
	private String jointime;
	
	private String devplatform;
	
	private String expertise;
	
	private int relation;
	
	private String latestonline;
	
	private String account;
	
	private String pwd;
	
	private Notice notice;

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

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getJointime() {
		return jointime;
	}

	public void setJointime(String jointime) {
		this.jointime = jointime;
	}

	public String getDevplatform() {
		return devplatform;
	}

	public void setDevplatform(String devplatform) {
		this.devplatform = devplatform;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public String getLatestonline() {
		return latestonline;
	}

	public void setLatestonline(String latestonline) {
		this.latestonline = latestonline;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	@Override
	public <T> T parse(String xml, Class<T> clazz) throws TaskException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		
		User user = new User();
		
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, AppSettings.UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {

				case XmlPullParser.START_TAG:
					// 如果是标签开始，则说明需要实例化对象了
					if (tag.equalsIgnoreCase("result")) {
					} else if (tag.equalsIgnoreCase("errorCode")) {
						user.setError(xmlParser.nextText());
					} else if (tag.equalsIgnoreCase("errorMessage")) {
						user.setError_description(xmlParser.nextText().trim());
					} else if(tag.equalsIgnoreCase("uid")){
						user.setId(xmlParser.nextText());
					}else if(tag.equalsIgnoreCase("location")){
						user.setLocation(xmlParser.nextText());
					}else if(tag.equalsIgnoreCase("name")){
						user.setName(xmlParser.nextText());
					}else if(tag.equalsIgnoreCase("followers")){
						user.followers = StringUtils.toInt(xmlParser.nextText(), 0);
					}else if(tag.equalsIgnoreCase("fans")){
						user.fans = StringUtils.toInt(xmlParser.nextText(), 0);
					}else if(tag.equalsIgnoreCase("score")){
						user.score = StringUtils.toInt(xmlParser.nextText(), 0);
					}else if(tag.equalsIgnoreCase("portrait")){
						user.avatar = xmlParser.nextText();
					}
		            //通知信息
		            else if(tag.equalsIgnoreCase("notice"))
		    		{
		            	user.notice = new Notice();
		    		}
		            else if(user.notice != null) {
		    			if(tag.equalsIgnoreCase("atmeCount")) {			      
		    				user.notice.setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
			            }
			            else if(tag.equalsIgnoreCase("msgCount")) {			            	
			            	user.notice.setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
			            }
			            else if(tag.equalsIgnoreCase("reviewCount")) {			            	
			            	user.notice.setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
			            }
			            else if(tag.equalsIgnoreCase("newFansCount")) {			            	
			            	user.notice.setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
			            }
		    		}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskException("", GlobalContext.getInstance().getResources().getString(R.string.resultIllegal));
		} finally {
			try {
				inputStream.close();
			} catch (Exception e2) {
			}
		}
		
		return (T) user;
	}
	
}
