package org.aisen.osc.sdk.bean;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.aisen.osc.sdk.support.utils.StringUtils;
import org.aisen.osc.sdk.support.xml.IXmlToDto;
import org.aisen.osc.support.utils.AppSettings;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.m.R;
import com.m.common.context.GlobalContext;
import com.m.support.task.TaskException;

public class TweetBeans extends BaseBean implements IXmlToDto {

	private static final long serialVersionUID = -9090232368890048974L;

	private List<TweetBean> tweetlist;
	
	private int pageSize;
	
	private int tweetCount;
	
	private Notice notice;

	public List<TweetBean> getTweetlist() {
		return tweetlist;
	}

	public void setTweetlist(List<TweetBean> tweetlist) {
		this.tweetlist = tweetlist;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTweetCount() {
		return tweetCount;
	}

	public void setTweetCount(int tweetCount) {
		this.tweetCount = tweetCount;
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(String xml, Class<T> clazz) throws TaskException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		
		TweetBeans tweetlist = new TweetBeans();
		tweetlist.setTweetlist(new ArrayList<TweetBean>());
		TweetBean tweet = null;
		
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
					if (tag.equalsIgnoreCase("tweetCount")) {
						tweetlist.tweetCount = StringUtils.toInt(
								xmlParser.nextText(), 0);
					} else if (tag.equalsIgnoreCase("pageSize")) {
						tweetlist.pageSize = StringUtils.toInt(
								xmlParser.nextText(), 0);
					} else if (tag.equalsIgnoreCase("tweet")) {
						tweet = new TweetBean();
					} else if (tweet != null) {
						if (tag.equalsIgnoreCase("id")) {
							tweet.setId(xmlParser.nextText());;
						} else if (tag.equalsIgnoreCase("portrait")) {
							tweet.setPortrait(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("body")) {
							tweet.setBody(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("author")) {
							tweet.setAuthor(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("authorid")) {
							tweet.setAuthorid(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("commentCount")) {
							tweet.setCommentCount(StringUtils.toInt(xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("pubDate")) {
							tweet.setPubDate(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("imgSmall")) {
							tweet.setImgSmall(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("imgBig")) {
							tweet.setImgBig(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("appclient")) {
							tweet.setAppClient(StringUtils.toInt(xmlParser.nextText(), 0));
						}
					}
					// 通知信息
					else if (tag.equalsIgnoreCase("notice")) {
						tweetlist.setNotice(new Notice());
					} else if (tweetlist.getNotice() != null) {
						if (tag.equalsIgnoreCase("atmeCount")) {
							tweetlist.getNotice().setAtmeCount(
									StringUtils.toInt(xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("msgCount")) {
							tweetlist.getNotice().setMsgCount(
									StringUtils.toInt(xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("reviewCount")) {
							tweetlist.getNotice().setReviewCount(
									StringUtils.toInt(xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("newFansCount")) {
							tweetlist.getNotice().setNewFansCount(
									StringUtils.toInt(xmlParser.nextText(), 0));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// 如果遇到标签结束，则把对象添加进集合中
					if (tag.equalsIgnoreCase("tweet") && tweet != null) {
						tweetlist.getTweetlist().add(tweet);
						tweet = null;
					}
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
		
		return (T) tweetlist;
	}

}
