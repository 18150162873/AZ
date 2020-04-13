package util.db.receipt;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.properties.PropertiesFactory;
import util.properties.PropertiesFile;

@SuppressWarnings("all")
public class APIJDBConnection {

	private String url;
	private String user;
	private String password;

	public static Connection getConnection(String type) throws ClassNotFoundException {
		String url = "";
		String user = "";
		String password = "";
		
		if("wms".equals(type)) {
//			url = "jdbc:oracle:thin:@10.2.16.114:1521:scprd";
			//测试环境
//			url = "jdbc:oracle:thin:@10.2.16.244:1521:scprd";
			url = "jdbc:oracle:thin:@10.2.16.133:1521:scprd";
			user = "wmwhse1";
			password = "WMwhSql1";
		}else {
//			url = "jdbc:oracle:thin:@10.2.16.114:1521:scprd";
//			user = "interface";
//			password = "Pass1234";
			url = "jdbc:oracle:thin:@10.2.16.147:1521:sorter";
			user = "sorter";
			password = "sort12#";
		}
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
}
