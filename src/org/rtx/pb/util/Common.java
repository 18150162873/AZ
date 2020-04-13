package org.rtx.pb.util;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.agileitp.forte.framework.Array;
import com.agileitp.forte.framework.TextData;
import com.agileitp.forte.framework.internal.ServiceObjectException;
import com.ssaglobal.SsaException;
import com.ssaglobal.scm.wms.service.baseobjects.EXEDataObject;
import com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemote;
import com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemoteHome;
import com.ssaglobal.util.EjbHelper;

import util.api.APIServer;
import util.db.JDBCConnectionFactory;
import util.db.receipt.APIJDBConnection;
import util.db.receipt.ReceiptEntity;
public class Common {
	
	public static void receipt(String provider,String ssaConfig,String locale,String whseid,ReceiptEntity entity){
		JDBCConnectionFactory jdbcConnectionFactory = JDBCConnectionFactory
				.getInstance();
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet rs = null;
		String dataSource = Common.scprdwm(whseid.toUpperCase());
		try {
			connection = APIJDBConnection.getConnection("wms");
			String sql = "SELECT RECEIPTKEY,STORERKEY,SKU,nvl(TRIM(POKEY),'NOPO') as POKEY,qtyreceived QTYEXPECTED,UOM,PACKKEY,TOLOC,TOID,"
			            +"LOTTABLE02,LOTTABLE03,LOTTABLE04,LOTTABLE06,STATUS FROM receiptdetail "
			            +"where status = '0' AND receiptkey = '"+entity.getRECEIPTKEY()+"' and sku ='"+entity.getSKU()+"' order by RECEIPTKEY,STORERKEY,SKU";
			pre = connection.prepareStatement(sql);
			rs = pre.executeQuery();
			while (rs.next()) {
				
				String qtyexpectedStr = entity.getQTYEXPECTED();
				String toId = entity.getTOID();
				String toLoc = entity.getTOLOC();
				
				int QTYEXPECTED = Integer.parseInt(qtyexpectedStr);
				Array parameters = new Array();
				String userid = "wcs";
				parameters.add(new TextData("`"));
				parameters.add(new TextData("1"));
				parameters.add(new TextData(userid));
				parameters.add(new TextData("29358"));
				parameters.add(new TextData(dataSource));
				parameters.add(new TextData("RC"));
				parameters.add(new TextData("01"));
				parameters.add(new TextData("cddsis001"));
				
				parameters.add(new TextData(rs.getString("RECEIPTKEY")));
				parameters.add(new TextData(rs.getString("STORERKEY")));
				parameters.add(new TextData(""));
				parameters.add(new TextData(rs.getString("RECEIPTKEY")));
				parameters.add(new TextData(rs.getString("SKU")));
				parameters.add(new TextData(rs.getString("POKEY")));
				parameters.add(new TextData(QTYEXPECTED));
				parameters.add(new TextData(rs.getString("UOM")));
				parameters.add(new TextData(rs.getString("PACKKEY")));
				parameters.add(new TextData(toLoc));
				parameters.add(new TextData(toId));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(rs.getString("LOTTABLE02")));
				parameters.add(new TextData(rs.getString("LOTTABLE03")));
				parameters.add(new TextData(rs.getString("LOTTABLE04")));
				parameters.add(new TextData(""));
				parameters.add(new TextData(rs.getString("LOTTABLE06")));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData("01"));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(0));
				parameters.add(new TextData(0));
				parameters.add(new TextData(""));
				parameters.add(new TextData(0));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				parameters.add(new TextData(""));
				String procName = "NSPRFRC01";
				System.out.println("sku执行收货开始时间："+System.currentTimeMillis());
				execApp(procName, provider, ssaConfig, locale, parameters, whseid, userid);
				System.out.println("sku执行收货开始时间："+System.currentTimeMillis());
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			jdbcConnectionFactory.closeConnection(rs, pre, connection);
		}
	}
	//调用APP
	public static void execApp(String procName,String provider,String ssaConfig,String locale,Array parameters,String whseid,String userid){
		try {
			APIServer.Property p = new APIServer.Property();
			p.setSsaConfig(ssaConfig);
			String scprdWhseid = Common.scprdwm(whseid.toUpperCase());
			p.setDbResourceName(scprdWhseid);
			p.setProcName(procName);
			p.setParams(parameters);
			p.setUserId(userid);
			p.setCallerId("background");
			p.setLocale(locale);
			EXEDataObject edo = null;
			try {
				edo = Common.getTransactionServiceRemote(provider).executeProcedure(
						new TextData(p.getUserId()), p.getDbResourceName(),
						new TextData(p.getProcName()), p.getParams(), null,
						p.getCallerId(), p.getLocale());
			} catch (RemoteException e) {
				e.printStackTrace();
				throw e;
			} catch (ServiceObjectException e) {
				e.printStackTrace();
				throw e;
			} catch (SsaException e) {
				e.printStackTrace();
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	public static TransactionServiceSORemote getTransactionServiceRemote(String provider)
			throws SsaException {
		TransactionServiceSORemote remote = (TransactionServiceSORemote) EjbHelper
				.getSession(provider, null, null,
						"org.jboss.as.naming.InitialContextFactory",
						"ejb:wmserver/wmserverclient_ejb//TransactionServiceSO!com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemoteHome",
						TransactionServiceSORemoteHome.class);

		return remote;
	}
	//获取数据源
	public static String scprdwm(String whseid) {
		JDBCConnectionFactory jdbcConnectionFactory = JDBCConnectionFactory
				.getInstance();
		Connection connection = null;
		String scprdwm = null;
		PreparedStatement pre = null;
		ResultSet rs = null;
		try {
			connection = jdbcConnectionFactory.getConnection(whseid);
			pre = connection
					.prepareStatement("select db_name from wmsadmin.pl_db where nls_upper(db_logid) = ?");
			pre.setString(1, whseid);
			rs = pre.executeQuery();
			if (rs.next()) {
				scprdwm = rs.getString("db_name");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			jdbcConnectionFactory.closeConnection(rs, pre, connection);
		}
		return scprdwm;
	}
}
