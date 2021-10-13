package com.mobileleader.edoc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBTest {

	public static void main(String[] args) {
		String employeeNo = "A180202";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int idx = 1;
		
		try {
			String user = "credit_dbadm";
			String pw = "creditdb12";
			String url = "jdbc:oracle:thin:@192.168.12.10:1521:pcrs";
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			Properties props = new Properties();
			props.put("user", user);
			props.put("password", pw);
			//props.put("charSet", "utf-8");
			
			conn = DriverManager.getConnection(url, props);
			
			System.out.println("DB 연결 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("DB 드라이버 로딩 실패 : " + e.toString());
		} catch (SQLException e) {
			System.out.println("DB 접속 실패 : " + e.toString());
		} catch (Exception e) {
			System.out.println("Unkown Error : " + e.toString());
		}

		try {
			StringBuilder sb = new StringBuilder();

			sb.append(" SELECT                \n");
			sb.append("       EMPNO AS EMPNO, NM AS EMPNM,  MDF_DT   \n");
			sb.append(" FROM  CM_US_USER      \n");
			sb.append(" WHERE EMPNO = ?       \n");
			
			System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +
					"[QUERY]" + "\n" +
					sb.toString() + "\n" + 
					"[PARAM]"
					+ "\n" + employeeNo + "\n");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(idx++, employeeNo);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				
				idx = 1;
				
				String empNo = rs.getString("EMPNO");
				String empNm = rs.getString("EMPNM");
				
				System.out.println("EMPNO : " + empNo);
				System.out.println("EMPNM : " + empNm);
			}
		} catch (SQLException e) {
			System.out.println("SQL문 에러 : " + e.toString());
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println("DB 종료 에러  : " + e.toString());
			}	
		}

	}
}
