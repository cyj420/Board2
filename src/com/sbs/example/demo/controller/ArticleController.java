package com.sbs.example.demo.controller;

import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.ArticleService;

public class ArticleController extends Controller {
	private ArticleService articleService;

	public ArticleController() {
		articleService = Factory.getArticleService();
	}

	public void doAction(Request request) {
		if (request.getActionName().equals("list")) {
			actionList(request);
		} else if (request.getActionName().equals("write")) {
			actionWrite(request);
		} else if (request.getActionName().equals("changeBoard")) {
			actionChangeBoard(request);
		} else if (request.getActionName().equals("currentBoard")) {
			actionCurrentBoard();
		} else if (request.getActionName().equals("modify")) {
			actionModify(request);
		} else if (request.getActionName().equals("delete")) {
			actionDelete(request);
		} else if (request.getActionName().equals("detail")) {
			actionDetail(request);
		}
	}

	private void actionDetail(Request request) {
		try{
			int detailId = Integer.parseInt(request.getArg1());
			articleService.detailArticle(detailId);
		}catch (Exception e) {
			 System.out.println("게시물 상세보기 실패 사유 : 번호 미입력");
		}
	}

	private void actionDelete(Request request) {
		try{
			int deleteId = Integer.parseInt(request.getArg1());
			articleService.delete(deleteId);
		}catch (Exception e) {
			 System.out.println("게시물 삭제 실패 사유 : 번호 미입력");
		}
	}

	private void actionModify(Request request) {
		try{
			int modifyId = Integer.parseInt(request.getArg1());
			if(Factory.getArticleDao().isArticleExists(modifyId)) {
				System.out.print("새로운 제목 : ");
				String newTitle = Factory.getScanner().nextLine().trim();
				System.out.print("새로운 내용 : ");
				String newBody = Factory.getScanner().nextLine().trim();
				articleService.modify(modifyId, newTitle, newBody);
			}
			else {
				System.out.println("게시물 수정 실패 사유 : 존재하지 않는 게시물");
			}
		}catch (Exception e) {
			System.out.println("게시물 수정 실패 사유 : 번호 미입력");
		}
	}

	private void actionCurrentBoard() {
		Board board = Factory.getSession().getCurrentBoard();
		System.out.printf("현재 게시판 : %s\n", board.getName());
	}

	private void actionChangeBoard(Request reqeust) {
		String boardCode = reqeust.getArg1();

		Board board = articleService.getBoardByCode(boardCode);

		if (board == null) {
			System.out.println("해당 게시판이 존재하지 않습니다.");
		} else {
			System.out.printf("%s 게시판으로 변경되었습니다.\n", board.getName());
			Factory.getSession().setCurrentBoard(board);
		}
	}

	private void actionList(Request reqeust) {
		Board currentBoard = Factory.getSession().getCurrentBoard();
		List<Article> articles = articleService.getArticlesByBoardCode(currentBoard.getCode());

		System.out.printf("== %s 게시물 리스트 시작 ==\n", currentBoard.getName());
		for (Article article : articles) {
			System.out.printf("%d, %s, %s\n", article.getId(), article.getRegDate(), article.getTitle());
		}
		System.out.printf("== %s 게시물 리스트 끝 ==\n", currentBoard.getName());
	}

	private void actionWrite(Request reqeust) {
		System.out.printf("제목 : ");
		String title = Factory.getScanner().nextLine();
		System.out.printf("내용 : ");
		String body = Factory.getScanner().nextLine();

		// 현재 게시판 id 가져오기
		int boardId = Factory.getSession().getCurrentBoard().getId();

		// 현재 로그인한 회원의 id 가져오기
		int memberId = Factory.getSession().getLoginedMember().getId();
		int newId = articleService.write(boardId, memberId, title, body);

		System.out.printf("%d번 글이 생성되었습니다.\n", newId);
	}
}