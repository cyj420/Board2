package com.sbs.example.demo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.ArticleReply;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.util.Util;

public class DBConnection {
	private Connection connection;
	public static String DB_NAME;
	public static String DB_USER;
	public static String DB_PASSWORD;
	public static int DB_PORT;

	public void connect() {
		String url = "jdbc:mysql://localhost:" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC&allowMultiQueries=true";
		//한번에 2개 이상의 쿼리를 이용할 경우 allowMultiQueries=true <= 이게 필요!
		//서치 키워드 : jdbc multiple query
		//이용 사이트 : https://stackoverflow.com/questions/10797794/multiple-queries-executed-in-java-in-single-statement
		String user = DB_USER;
		String password = DB_PASSWORD;
		String driverName = "com.mysql.cj.jdbc.Driver";

		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.err.printf("[SQL 예외] : %s\n", e.getMessage());
		}

		initDB();
		//table 자동 생성
	}

	private void initDB() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"CREATE TABLE IF NOT EXISTS `member` (" + "    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,"
						+ "    regDate DATETIME NOT NULL," + "    loginId CHAR(100) NOT NULL UNIQUE,"
						+ "    loginPw CHAR(100) NOT NULL," + "    `name` CHAR(100) NOT NULL);");
		sb.append("CREATE TABLE IF NOT EXISTS `board` ("
				+ "    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," + "    regDate DATETIME NOT NULL,"
				+ "    `code` CHAR(100) NOT NULL UNIQUE," + "    `name` CHAR(100) NOT NULL);");
		
		sb.append("CREATE TABLE IF NOT EXISTS `article` ("
				+ "    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," + "    regDate DATETIME NOT NULL,"
				+ "    title CHAR(100) NOT NULL," + "    `body` CHAR(100) NOT NULL,"
				+ "    memberId INT(10) UNSIGNED NOT NULL," + "    boardId INT(10) UNSIGNED NOT NULL,"
				+ "    INDEX boardId (`boardId`));");
		sb.append("CREATE TABLE IF NOT EXISTS `articleReply` ("
				+ "    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," + "    regDate DATETIME NOT NULL,"
				+ "    `body` CHAR(100) NOT NULL," + "    memberId INT(10) UNSIGNED NOT NULL,"
				+ "    articleId INT(10) UNSIGNED NOT NULL," + "    INDEX articleId (`articleId`));");
		// SQL을 적는 문서파일
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(sb.toString());
		} catch (SQLException e) {
			System.out.println(sb.toString());
			System.err.printf("[CREATE TABLE 쿼리 오류]\n" + e.getStackTrace() + "\n");
			e.printStackTrace();
		}

		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			System.err.println("[종료 오류]\n" + e.getStackTrace());
		}
	}

	public int selectRowIntValue(String sql) {
		Map<String, Object> row = selectRow(sql);

		for (String key : row.keySet()) {
			return (int) row.get(key);
		}

		return -1;
	}

	public String selectRowStringValue(String sql) {
		Map<String, Object> row = selectRow(sql);

		for (String key : row.keySet()) {
			return (String) row.get(key);
		}

		return "";
	}

	public Boolean selectRowBooleanValue(String sql) {
		Map<String, Object> row = selectRow(sql);
		System.out.println(row);

		for (String key : row.keySet()) {
			if (row.get(key) instanceof String) {
				return ((String) row.get(key)).equals("1");
			} else if (row.get(key) instanceof Integer) {
				return ((int) row.get(key)) == 1;
			} else if (row.get(key) instanceof Boolean) {
				return ((boolean) row.get(key));
			}
		}

		return false;
	}

	public Map<String, Object> selectRow(String sql) {
		List<Map<String, Object>> rows = selectRows(sql);

		if (rows.size() == 0) {
			return new HashMap<String, Object>();
		}

		return rows.get(0);
	}

	public List<Map<String, Object>> selectRows(String sql) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnSize = metaData.getColumnCount();

			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();

				for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
					String columnName = metaData.getColumnName(columnIndex + 1);
					Object value = rs.getObject(columnName);

					if (value instanceof Long) {
						int numValue = (int) (long) value;
						row.put(columnName, numValue);
					} else if (value instanceof Timestamp) {
						String dateValue = value.toString();
						dateValue = dateValue.substring(0, dateValue.length() - 2);
						row.put(columnName, dateValue);
					} else {
						row.put(columnName, value);
					}
				}

				rows.add(row);
			}
		} catch (SQLException e) {
			System.err.printf("[SQL 예외, SQL : %s] : %s\n", sql, e.getMessage());
		}

		return rows;
	}

	public int delete(String sql) {
		int affectedRows = 0;

		Statement stmt;
		try {
			stmt = connection.createStatement();
			affectedRows = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.printf("[SQL 예외, SQL : %s] : %s\n", sql, e.getMessage());
		}

		return affectedRows;
	}

	public int update(String sql) {
		int affectedRows = 0;

		Statement stmt;
		try {
			stmt = connection.createStatement();
			affectedRows = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.printf("[SQL 예외, SQL : %s] : %s\n", sql, e.getMessage());
		}

		return affectedRows;
	}

	public int insert(String sql) {
		int id = -1;

		try {
			Statement stmt = connection.createStatement();
			stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
			}

		} catch (SQLException e) {
			System.err.printf("[SQL 예외, SQL : %s] : %s\n", sql, e.getMessage());
		}

		return id;
	}

	public Article detail(String sql) {
		Article article = new Article(selectRow(sql));
		return article;
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.printf("[SQL 예외] : %s\n", e.getMessage());
			}
		}
	}
}
