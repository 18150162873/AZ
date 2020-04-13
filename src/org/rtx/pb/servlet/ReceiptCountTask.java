package org.rtx.pb.servlet;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.db.mapper.WcsReceiptMapper;
import util.db.mapper.WcsSqlSessionFactory;
import util.db.mapper.WmsReceiptMapper;
import util.db.mapper.WmsSqlSessionFactory;
import util.db.receipt.ReceiptCount;

@SuppressWarnings("all")
public class ReceiptCountTask {
	
	Logger log = LoggerFactory.getLogger(ReceiptCountTask.class);
	
	
	public void work() throws Exception {
		SqlSessionFactory wmsSqlSessionFactory = WmsSqlSessionFactory.getInstance();
		SqlSessionFactory wcsSqlSessionFactory = WcsSqlSessionFactory.getInstance();
		
		SqlSession wmsSqlSession = wmsSqlSessionFactory.openSession();
		SqlSession wcsSqlSession = wcsSqlSessionFactory.openSession();
		try {
			WmsReceiptMapper wmsMapper = wmsSqlSession.getMapper(WmsReceiptMapper.class);
			WcsReceiptMapper wcsMapper = wcsSqlSession.getMapper(WcsReceiptMapper.class);
			
			//获取中间表计件的数据
			List<ReceiptCount> wcsCountData = wcsMapper.querywcsCountData();
			
			for(ReceiptCount temp:wcsCountData) {
				int status = wmsMapper.addcountData(temp);
				if(status > 0) {
					wcsMapper.updSuccessStatus(temp.getSERIALKEY(),"S");
				}
			}
			
			wcsSqlSession.commit();
			wmsSqlSession.commit();
		} catch (Exception e) {
			wmsSqlSession.rollback();
			wcsSqlSession.rollback();
			e.printStackTrace();
		}


	}
}
