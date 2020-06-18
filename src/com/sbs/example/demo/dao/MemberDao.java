package com.sbs.example.demo.dao;
import java.util.Map;

import com.sbs.example.demo.db.DBConnection;
import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;

public class MemberDao {
	DBConnection dbConnection;

	public MemberDao() {
		dbConnection = Factory.getDBConnection();
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `loginId` = '%s' ", loginId));
		sb.append(String.format("AND `loginPw` = '%s' ", loginPw));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		
		if ( row.isEmpty() ) {
			return null;
		}
		
		return new Member(row);
	}

	public Member getMemberByLoginId(String loginId) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND loginId = '%s' ", loginId));

		Map<String, Object> memberRow = dbConnection.selectRow(sb.toString());

		if (memberRow.isEmpty()) {
			return null;
		}

		return new Member(memberRow);
	}

	public Member getMember(int id) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `id` = '%d' ", id));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		
		if ( row.isEmpty() ) {
			return null;
		}
		
		return new Member(row);
	}

	public int save(Member member) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("INSERT INTO member "));
		sb.append(String.format("SET regDate = '%s' ", member.getRegDate()));
		sb.append(String.format(", loginId = '%s' ", member.getLoginId()));
		sb.append(String.format(", loginPw = '%s' ", member.getLoginPw()));
		sb.append(String.format(", `name` = '%s' ", member.getName()));

		return dbConnection.insert(sb.toString());
	}
}