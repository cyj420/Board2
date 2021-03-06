package com.sbs.example.demo.service;
import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.ArticleReply;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.util.Util;

// Service
public class BuildService {
	ArticleService articleService;

	public BuildService() {
		articleService = Factory.getArticleService();
	}

	public void buildSite() {
		Util.makeDir("site");
		Util.makeDir("site/article");

		String head = Util.getFileContents("site_template/part/head.html");
		String foot = Util.getFileContents("site_template/part/foot.html");

		// 각 게시판 별 게시물리스트 페이지 생성
		List<Board> boards = articleService.getBoards();

		for (Board board : boards) {
			String fileName = board.getCode() + "-list-1.html";

			String html = "";

			List<Article> articles = articleService.getArticlesByBoardCode(board.getCode());

			String template = Util.getFileContents("site_template/article/list.html");

			for (Article article : articles) {
				MemberService ms = Factory.getMemberService();
				html += "<tr>";
				html += "<td>" + article.getId() + "</td>";
				html += "<td>" + article.getRegDate() + "</td>";
				html += "<td><a href=\"" + article.getId() + ".html\">" + article.getTitle() + "</a></td>";
				html += "<td>" + ms.getMember(article.getMemberId()).getName() + "</td>";
				html += "</tr>";
			}

			html = template.replace("${TR}", html);

			html = head + html + foot;

			Util.writeFileContents("site/article/" + fileName, html);
		}

		// 게시물 별 파일 생성
		List<Article> articles = articleService.getArticles();

		for (Article article : articles) {
			List<ArticleReply> ar = articleService.getArticleReplies(article.getId());
			String html = "";

			html += "<div>제목 : " + article.getTitle() + "</div>";
			html += "<div>내용 : " + article.getBody() + "</div>";
			
			//List는 null이 아닌 isEmpty()로 체크해야함
			if(!ar.isEmpty()) {
				html += "<div>댓글 ("+ar.size()+")</div>";
				for(int i=0; i<ar.size(); i++) {
					MemberService ms = Factory.getMemberService();
					html += "<div>"+ms.getMember(ar.get(i).getMemberId()).getName()+" : "
					+ar.get(i).getBody()+" - "+ar.get(i).getRegDate()+"</div>";
				}
			}
			if(article.getId()!=articles.get(articles.size()-1).getId()) {
				html += "<div><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></div>";
			}
			
			if(article.getId()!=articles.get(0).getId()) {
				html += "<div><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></div>";
			}

			html = head + html + foot;

			Util.writeFileContents("site/article/" + article.getId() + ".html", html);
		}
//		for(int i=0; i<articles.lastIndexOf(articles); i++) {
//		System.out.println(articles.indexOf(articles));
//			Util.deleteFile("site/article/" + i + ".html");
//		}
	}

}