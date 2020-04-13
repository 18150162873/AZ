package util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import util.properties.PropertiesFactory;
import util.properties.PropertiesFile;

/**
 * 数据库工具类
 * @author berr
 * 2013.08.08
 */
public class JDBCConnectionFactory {
	
	private DataSource dataSource = null; //berr 2013.08.07
	private String dataSourceName = null; //berr 2013.08.07


	private JDBCConnectionFactory() {
//		dataSourceName = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG).getValue("DataSource");
//		initConnection();
	}
	
	
	/**
	 * 初始化   berr 2013.08.07
	 */
	/*protected void initConnection() {
		if (dataSource == null) { //加载数据源
			InitialContext ic = null;
			try {
				ic = new InitialContext();
				dataSource = ((DataSource) ic.lookup(dataSourceName));
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	
	protected Connection initConnection(String whseid) {
		//加载数据源
		InitialContext ic = null;
		try {
			String dataSourceName = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG).getValue(whseid.toUpperCase());
			ic = new InitialContext();
			dataSource = ((DataSource) ic.lookup(dataSourceName));
			return dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JDBCConnectionFactory getInstance() {
		return JDBCConnectionFactoryInit.JDBCConnectionFactory;
	}

	/**
	 * 获取连接
	 * @return
	 */
	/*public  Connection getConnection() {
		try {
			initConnection();
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/**
	 * 根据仓库获取连接
	 * @return
	 */
	public  Connection getConnection(String whseid) {
		return initConnection(whseid);
	}

	/**
	 * 关闭连接
	 * @param rst
	 * @param stmt
	 * @param conn
	 */
	public  void closeConnection(ResultSet rst, Statement stmt,
			Connection conn) {
		if (rst != null) {
			try {
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class JDBCConnectionFactoryInit{ //单例模式初始化
		public static JDBCConnectionFactory JDBCConnectionFactory = new JDBCConnectionFactory();
	}

}
