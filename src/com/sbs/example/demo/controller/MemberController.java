package com.sbs.example.demo.controller;

import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.MemberService;

public class MemberController extends Controller {
	private MemberService memberService;

	public MemberController() {
		memberService = Factory.getMemberService();
	}

	public void doAction(Request reqeust) {
		if (reqeust.getActionName().equals("logout")) {
			actionLogout(reqeust);
		} else if (reqeust.getActionName().equals("login")) {
			actionLogin(reqeust);
		} else if (reqeust.getActionName().equals("whoami")) {
			actionWhoami(reqeust);
		} else if (reqeust.getActionName().equals("join")) {
			actionJoin(reqeust);
		}
	}

	private void actionJoin(Request reqeust) {
		System.out.print("ID : ");
		String loginId = Factory.getScanner().next().trim();
		System.out.print("PW : ");
		String loginPw = Factory.getScanner().next().trim();
		System.out.print("name : ");
		String name = Factory.getScanner().next().trim();
		//아래 코드를 적지 않으면 명령어: 메세지가 두번 뜸.
		Factory.getScanner().nextLine();
		if(memberService.join(loginId, loginPw, name)<0) {
			System.out.println("회원가입 실패 사유 : 이미 존재하는 ID");
		}else {
			System.out.println("회원가입이 완료되었습니다.");
		}
	}

	private void actionWhoami(Request reqeust) {
		Member loginedMember = Factory.getSession().getLoginedMember();
		if (loginedMember == null) {
			System.out.println("로그인 후 이용 가능합니다.");
		} else {
			System.out.println(loginedMember.getName());
		}

	}

	private void actionLogin(Request reqeust) {
		if(Factory.getSession().getLoginedMember()==null) {
			System.out.printf("로그인 아이디 : ");
			String loginId = Factory.getScanner().nextLine().trim();
			
			System.out.printf("로그인 비번 : ");
			String loginPw = Factory.getScanner().nextLine().trim();
			
			Member member = memberService.getMemberByLoginIdAndLoginPw(loginId, loginPw);
			
			if (member == null) {
				System.out.println("일치하는 회원이 없습니다.");
			} else {
				System.out.println(member.getName() + "님 환영합니다.");
				Factory.getSession().setLoginedMember(member);
			}
		}
		else {
			System.out.println(Factory.getSession().getLoginedMember().getName()+"님은 현재 로그인 상태입니다.");
		}
	}

	private void actionLogout(Request reqeust) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember != null) {
			Session session = Factory.getSession();
			System.out.println(loginedMember.getName()+"님, 로그아웃 되었습니다.");
			session.setLoginedMember(null);
		}
		else {
			System.out.println("이미 로그아웃 상태입니다.");
		}

	}
}