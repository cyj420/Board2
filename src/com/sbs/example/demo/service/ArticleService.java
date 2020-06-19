package com.sbs.example.demo.service;
import java.util.List;

import com.sbs.example.demo.dao.ArticleDao;
import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;

public class ArticleService {
	private ArticleDao articleDao;

	public ArticleService() {
		articleDao = Factory.getArticleDao();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return articleDao.getArticlesByBoardCode(code);
	}

	public List<Board> getBoards() {
		return articleDao.getBoards();
	}

	public int makeBoard(String name, String code) {
		Board oldBoard = articleDao.getBoardByCode(code);

		if (oldBoard != null) {
			return -1;
		}

		Board board = new Board(name, code);
		return articleDao.saveBoard(board);
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public int write(int boardId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		return articleDao.save(article);
	}

	public List<Article> getArticles() {
		return articleDao.getArticles();
	}

	public void makeBoardIfNotExists(String name, String code) {
		Board board = articleDao.getBoardByCode(code);
		
		if ( board == null ) {
			makeBoard(name, code);
		}
	}

	public Board getBoardByCode(String boardCode) {
		return articleDao.getBoardByCode(boardCode);
	}

	public void modify(int modifyNum, String newTitle, String newBody) {
		articleDao.modify(modifyNum, newTitle, newBody);
	}

	public void delete(Article deleteArticle) {
		articleDao.deleteArticle(deleteArticle);
	}

	public Article detailArticle(int detailId) {
		return articleDao.detailArticle(detailId);
	}

	public void writeArticleReply(int articleId, String reply) {
		articleDao.writeArticleReply(articleId, reply);
	}

	public void getArticleReplies(int detailId) {
		articleDao.getArticleReplies(detailId);
	}

}