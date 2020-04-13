package org.rtx.pb.servlet;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import util.db.mapper.WcsReceiptMapper;
import util.db.mapper.WcsSqlSessionFactory;
import util.db.mapper.WmsReceiptMapper;
import util.db.mapper.WmsSqlSessionFactory;
import util.db.receipt.WcsReceipt;

public class ReceiptUploadTask {

	public void work() throws Exception {
		SqlSessionFactory wmsSqlSessionFactory = WmsSqlSessionFactory.getInstance();
		SqlSessionFactory wcsSqlSessionFactory = WcsSqlSessionFactory.getInstance();

		SqlSession wmsSqlSession = wmsSqlSessionFactory.openSession();
		SqlSession wcsSqlSession = wcsSqlSessionFactory.openSession();
		try {
			WmsReceiptMapper wmsMapper = wmsSqlSession.getMapper(WmsReceiptMapper.class);
			WcsReceiptMapper wcsMapper = wcsSqlSession.getMapper(WcsReceiptMapper.class);

			List<WcsReceipt> wcsData = wcsMapper.findWcsReturenData();

			for(WcsReceipt temp:wcsData) {
				temp.setTOLOC(temp.getTOLOC().toUpperCase());
				int status = wmsMapper.addWcsReceiptData(temp);
				if(status > 0) {
					wcsMapper.updWcsReturenDataStatus(temp.getSERIALKEY(),"S");
				}
			}

			wcsSqlSession.commit();
			wmsSqlSession.commit();
		} catch (Exception e) {
			wmsSqlSession.rollback();
			wcsSqlSession.rollback();
			e.printStackTrace();
		}finally {
			wcsSqlSession.close();
			wmsSqlSession.close();
		}
	}
}
