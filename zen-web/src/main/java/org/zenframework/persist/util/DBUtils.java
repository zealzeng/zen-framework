package org.zenframework.persist.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Zeal on 2018/12/18 0018.
 */
public class DBUtils {

    public static void close(ResultSet rs, Statement sm, Connection conn) {
		close(rs);
		close(sm);
		close(conn);
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Statement sm) {
		if (sm != null) {
			try {
				sm.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
