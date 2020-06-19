package com.sbs.example.demo.controller;

import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.ArticleReply;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.ArticleService;
import com.sbs.example.demo.service.MemberService;

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
	// write, modify, delete 로그인 필요
	// modify, delete 접근 권한 필요(작성자 본인 / admin)

	private void actionDetail(Request request) {
		try {
			int detailId = Integer.parseInt(request.getArg1());
			if (Factory.getArticleDao().isArticleExists(detailId)) {
				Article a = articleService.detailArticle(detailId);
				if(a.getBoardId()==Factory.getSession().getCurrentBoard().getId()) {
					MemberService memberService = Factory.getMemberService();
					System.out.printf("== %d번 게시물 상세보기 ==\n", a.getId());
					System.out.println("제목 : "+a.getTitle());
					System.out.println("내용 : "+a.getBody());
					System.out.println("작성자 : "+memberService.getMember(a.getMemberId()).getName());
					System.out.println("작성게시판 : "+articleService.getBoard(a.getBoardId()).getName());
					articleService.getArticleReplies(detailId);
					System.out.println("댓글 ("+);
					//여기서 댓글 노출 시키기
					
					//회원만 댓글 작성 가능
					if(Factory.getSession().getLoginedMember()!=null) {
						System.out.print("댓글을 다시겠습니까?(yes) : ");
						String yesOrNo = Factory.getScanner().nextLine().trim();
						if(yesOrNo.equals("yes")) {
							System.out.print("댓글 : ");
							String reply = Factory.getScanner().nextLine().trim();
							articleService.writeArticleReply(a.getId(), reply);
						}
					}
					System.out.printf("== %d번 게시물 상세보기 끝==\n", a.getId());
				}
				else {
					System.out.println("게시물 상세보기 실패 사유 : 현재 게시판의 게시물이 아님");
				}
			} else {
				System.out.println("게시물 상세보기 실패 사유 : 존재하지 않는 게시물");
			}
		} catch (Exception e) {
			System.out.println("게시물 상세보기 실패 사유 : 번호 미입력");
		}
	}

	private void actionDelete(Request request) {
		if (Factory.getSession().getLoginedMember() != null) {
			try {
				int deleteId = Integer.parseInt(request.getArg1());
				if (Factory.getArticleDao().isArticleExists(deleteId)) {
					Article a = Factory.getArticleDao().getArticleById(deleteId);
					if (a.getBoardId() == Factory.getSession().getCurrentBoard().getId()) {
						if (a.getMemberId() == Factory.getSession().getLoginedMember().getId()
								|| Factory.getSession().getLoginedMember().getId() == 1) {
							articleService.delete(a);
							System.out.println(deleteId + "번 게시물이 삭제되었습니다.");
						} else {
							System.out.println("게시물 삭제 실패 사유 : 작성자 본인만 가능");
						}
					} else {
						System.out.println("게시물 삭제 실패 사유 : 현재 게시판의 게시물이 아님");
					}
				} else {
					System.out.println("게시물 삭제 실패 사유 : 존재하지 않는 게시물");
				}
			} catch (Exception e) {
				System.out.println("게시물 삭제 실패 사유 : 번호 미입력");
			}
		} else {
			System.out.println("게시물 삭제 실패 사유 : 비회원 접근 불가");
		}
	}

	private void actionModify(Request request) {
		if (Factory.getSession().getLoginedMember() != null) {
			try {
				int modifyId = Integer.parseInt(request.getArg1());
				Article a = Factory.getArticleDao().getArticleById(modifyId);
				if (a != null) {
					// 게시물 존재 위치가 현재 게시판이 아니라면 찾을 수 없어야 함.
					if (a.getBoardId() == Factory.getSession().getCurrentBoard().getId()) {
						if (Factory.getSession().getLoginedMember().getId() == a.getMemberId()
								|| Factory.getSession().getLoginedMember().getId() == 1) {
							System.out.print("새로운 제목 : ");
							String newTitle = Factory.getScanner().nextLine().trim();
							System.out.print("새로운 내용 : ");
							String newBody = Factory.getScanner().nextLine().trim();
							articleService.modify(modifyId, newTitle, newBody);
						} else {
							System.out.println("게시물 수정 실패 사유 : 작성자 본인만 가능");
						}
					} else {
						System.out.println("게시물 수정 실패 사유 : 현재 게시판의 게시물이 아님");
					}
				} else {
					System.out.println("게시물 수정 실패 사유 : 존재하지 않는 게시물");
				}
			} catch (Exception e) {
				System.out.println("게시물 수정 실패 사유 : 번호 미입력");
			}
		} else {
			System.out.println("게시물 수정 실패 사유 : 비회원 접근 불가");
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
		if (Factory.getSession().getLoginedMember() != null) {
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
		} else {
			System.out.println("게시물 작성은 로그인 후 이용 가능합니다.");
		}
	}
}