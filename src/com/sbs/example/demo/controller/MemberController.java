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

	}

	private void actionWhoami(Request reqeust) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember == null) {
			System.out.println("나그네");
		} else {
			System.out.println(loginedMember.getName());
		}

	}

	private void actionLogin(Request reqeust) {
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

	private void actionLogout(Request reqeust) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember != null) {
			Session session = Factory.getSession();
			System.out.println("로그아웃 되었습니다.");
			session.setLoginedMember(null);
		}

	}
}