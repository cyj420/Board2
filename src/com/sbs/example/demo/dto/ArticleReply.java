package com.sbs.example.demo.dto;

import java.util.Map;

public class ArticleReply extends Dto {
	private int id;
	private String regDate;
	private int articleId;
	private int memberId;
	private String body;

	ArticleReply() {

	}
	
	public ArticleReply(Map<String, Object> row) {
		super((int) row.get("id"), (String) row.get("regDate"));
		this.body = (String) row.get("body");
		this.memberId = (int) row.get("memberId");
		this.articleId = (int) row.get("articleId");
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}