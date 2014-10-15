package org.aisen.osc.sdk.bean;

public class TweetBean extends BaseBean {

	private static final long serialVersionUID = 6793419625519149009L;

	private String id;// 动弹ID
	
	private String pubDate;// 动弹时间
	
	private String body;// 动弹内容
	
	private String author;// 发帖人
	
	private String authorid;// 发帖人ID
	
	private int commentCount;// 评论数
	
	private String portrait;// 发帖人用户头像地址
	
	private String imgSmall;// 缩略图
	
	private String imgBig;// 大图
	
	private int appClient;// 来自

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorid() {
		return authorid;
	}

	public void setAuthorid(String authorid) {
		this.authorid = authorid;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getImgSmall() {
		return imgSmall;
	}

	public void setImgSmall(String imgSmall) {
		this.imgSmall = imgSmall;
	}

	public String getImgBig() {
		return imgBig;
	}

	public void setImgBig(String imgBig) {
		this.imgBig = imgBig;
	}

	public int getAppClient() {
		return appClient;
	}

	public void setAppClient(int appClient) {
		this.appClient = appClient;
	}
	
}
