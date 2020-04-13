/**
 * 
 */
package org.rtx.pb.servlet;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

//import org.apache.log4j.Logger;



import util.api.APIServer;
import util.db.JDBCConnectionFactory;
import util.properties.PropertiesFactory;
import util.properties.PropertiesFile;

import com.agileitp.forte.framework.Array;
import com.agileitp.forte.framework.TextData;
import com.agileitp.forte.framework.internal.ServiceObjectException;
import com.ssaglobal.SsaException;
import com.ssaglobal.scm.wms.service.baseobjects.EXEDataObject;
import com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemote;
import com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemoteHome;
import com.ssaglobal.util.EjbHelper;

/**
 * @author chenmx
 * 
 */

public class AsnAutoAcounts implements Serializable {
	static String provider = "";
	//Logger logger = Logger.getLogger(AsnAutoAcounts.class);
	// �ֿ�
	protected String whseid;

	public void setWhseid(String whseid) {
		this.whseid = whseid;
	}

	private final String receiptverifiedclose = "NSPSNIPPET";
	private final String locale = Locale.getDefault().toString();
	private String ssaConfig = "";

	public AsnAutoAcounts() {
		ssaConfig = PropertiesFactory
				.getPropertiesHelper(PropertiesFile.CONFIG).getValue("ssa");
		whseid = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG)
				.getValue("whseid");
		provider = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG)
				.getValue("provider");
		
	}

	public void work1() {
		Array parameters = new Array();
		parameters.add(new TextData("udf1"));
		parameters.add(new TextData("udf2"));
		parameters.add(new TextData("udf3"));
		parameters.add(new TextData("udf4"));
		try {
			APIServer.Property p = new APIServer.Property();
			p.setSsaConfig(ssaConfig);
			p.setDbResourceName("INFOR_SCPRD_wmwhse1");
			p.setProcName(receiptverifiedclose);
			p.setParams(parameters);
			p.setUserId("background");
			p.setCallerId("background");
			p.setLocale(locale);
			EXEDataObject edo = null;
			try {
				edo = getTransactionServiceRemote().executeProcedure(
						new TextData(p.getUserId()), p.getDbResourceName(),
						new TextData(p.getProcName()), p.getParams(), null,
						p.getCallerId(), p.getLocale());
			} catch (RemoteException e) {
				e.printStackTrace();
			//	logger.error(e);
			} catch (ServiceObjectException e) {
				e.printStackTrace();
			//	logger.error(e);
			} catch (SsaException e) {
				e.printStackTrace();
			//	logger.error(e);
			}
			if (edo == null) {
				// logger.info(ReceiptKey+"�ջ��� ����ʧ��");
			} else {
				// logger.info(ReceiptKey+"�ջ�������ɹ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		//	logger.info("��ּ����ʧ��");
		}
	}


	// ��ѯ�ֿ�
	public String scprdwm() {
		JDBCConnectionFactory jdbcConnectionFactory = JDBCConnectionFactory
				.getInstance();
		Connection connection = null;
		String scprdwm = null;
		PreparedStatement pre = null;
		ResultSet rs = null;
		try {
			connection = jdbcConnectionFactory.getConnection(this.whseid);
			pre = connection
					.prepareStatement("select db_name from wmsadmin.pl_db where nls_upper(db_logid) = ?");
			pre.setString(1, this.whseid.toUpperCase());
			rs = pre.executeQuery();
			if (rs.next()) {
				scprdwm = rs.getString("db_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			jdbcConnectionFactory.closeConnection(rs, pre, connection);
		}
		return scprdwm;
	}

	public TransactionServiceSORemote getTransactionServiceRemote()
			throws SsaException {
		TransactionServiceSORemote remote = (TransactionServiceSORemote) EjbHelper
				.getSession(provider, null, null,
						"org.jboss.as.naming.InitialContextFactory",
						"ejb:wmserver/wmserverclient_ejb//TransactionServiceSO!com.ssaglobal.scm.wms.service.exeprocessmanager.TransactionServiceSORemoteHome",
						TransactionServiceSORemoteHome.class);

		return remote;
		// return remote;
	}

}
