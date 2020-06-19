package com.sbs.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.demo.db.DBConnection;
import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;

// Dao
public class ArticleDao {
	DBConnection dbConnection;

	public ArticleDao() {
		dbConnection = Factory.getDBConnection();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT A.* "));
		sb.append(String.format("FROM `article` AS A "));
		sb.append(String.format("INNER JOIN `board` AS B "));
		sb.append(String.format("ON A.boardId = B.id "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND B.`code` = '%s' ", code));
		sb.append(String.format("ORDER BY A.id DESC "));

		List<Article> articles = new ArrayList<>();
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString());

		for (Map<String, Object> row : rows) {
			articles.add(new Article(row));
		}

		return articles;
	}

	public List<Board> getBoards() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("ORDER BY id DESC "));

		List<Board> boards = new ArrayList<>();
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString());

		for (Map<String, Object> row : rows) {
			boards.add(new Board(row));
		}

		return boards;
	}

	public Board getBoardByCode(String code) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `code` = '%s' ", code));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());

		if (row.isEmpty()) {
			return null;
		}

		return new Board(row);
	}

	public int saveBoard(Board board) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("INSERT INTO board "));
		sb.append(String.format("SET regDate = '%s' ", board.getRegDate()));
		sb.append(String.format(", `code` = '%s' ", board.getCode()));
		sb.append(String.format(", `name` = '%s' ", board.getName()));

		return dbConnection.insert(sb.toString());
	}

	public int save(Article article) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("INSERT INTO article "));
		sb.append(String.format("SET regDate = '%s' ", article.getRegDate()));
		sb.append(String.format(", `title` = '%s' ", article.getTitle()));
		sb.append(String.format(", `body` = '%s' ", article.getBody()));
		sb.append(String.format(", `memberId` = '%d' ", article.getMemberId()));
		sb.append(String.format(", `boardId` = '%d' ", article.getBoardId()));

		return dbConnection.insert(sb.toString());
	}

	public Board getBoard(int id) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `id` = '%d' ", id));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());

		if (row.isEmpty()) {
			return null;
		}

		return new Board(row);
	}

	public List<Article> getArticles() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `article` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("ORDER BY id DESC "));

		List<Article> articles = new ArrayList<>();
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString());

		for (Map<String, Object> row : rows) {
			articles.add(new Article(row));
		}

		return articles;
	}

	public void modify(int modifyId, String newTitle, String newBody) {
		Article a = getArticleById(modifyId);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("UPDATE article "));
		sb.append(String.format("SET title = '%s' ", newTitle));
		sb.append(String.format(", `body` = '%s' ", newBody));
		sb.append(String.format("where id = '%d' ", modifyId));

		dbConnection.update(sb.toString());
		System.out.println(a.getId() + "번 게시물 변경이 완료되었습니다.");
	}

	public Article getArticleById(int id) {
		List<Article> articles = getArticles();
		for (Article a : articles) {
			if (a.getId() == id) {
				return a;
			}
		}
		return null;
	}

	public boolean isArticleExists(int id) {
		List<Article> articles = getArticles();
		for (Article a : articles) {
			if (a.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public void deleteArticle(Article deleteArticle) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM article WHERE id=" + deleteArticle.getId());
		dbConnection.delete(sb.toString());
	}

	public Article detailArticle(int detailId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM article WHERE id=" + detailId);
		return dbConnection.detail(sb.toString());
	}

	public void writeArticleReply(int articleId, String reply) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT articleReply ");
		sb.append("SET regDate=NOW(),");
		sb.append(String.format("`body`='%s', ", reply));
		sb.append(String.format("memberId='%d', ", Factory.getSession().getLoginedMember().getId()));
		sb.append(String.format("articleId='%d'", articleId));
		dbConnection.insert(sb.toString());
	}

	public void getArticleReplies(int detailId) {
		
	}

}