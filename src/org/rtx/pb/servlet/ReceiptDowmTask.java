package org.rtx.pb.servlet;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.db.mapper.WcsReceiptMapper;
import util.db.mapper.WcsSqlSessionFactory;
import util.db.mapper.WmsReceiptMapper;
import util.db.mapper.WmsSqlSessionFactory;
import util.db.receipt.APIJDBConnection;
import util.db.receipt.ReceiptEntity;
@SuppressWarnings("all")
public class ReceiptDowmTask implements Serializable{
	Logger log = LoggerFactory.getLogger(ReceiptDowmTask.class);
	
	public void work() throws Exception {
		SqlSessionFactory wcsSqlSessionFactory = WcsSqlSessionFactory.getInstance();
		Connection wmsconn = APIJDBConnection.getConnection("wms");
		Connection wcsconn = APIJDBConnection.getConnection("wcs");
		SqlSession wcsSqlSession = wcsSqlSessionFactory.openSession();
		
		wcsconn.setAutoCommit(false);
		wmsconn.setAutoCommit(false);
		try {
			//获取要下发的数据
			WcsReceiptMapper wcsMapper = wcsSqlSession.getMapper(WcsReceiptMapper.class);
			String querySqlTemplate = "SELECT t2.SERIALKEY AS SERIALKEY, t1.WHSEID AS WHSEID, t1.RECEIPTKEY AS RECEIPTKEY, t1.EXTERNRECEIPTKEY AS EXTERNRECEIPTKEY, t1.STORERKEY AS STORERKEY , t1.ADDDATE AS ADDDATE, t1.TYPE AS type, t1.SUPPLIERCODE AS SUPPLIERCODE, t1.SUPPLIERNAME AS SUPPLIERNAME, t1.SHIPFROMADDRESSLINE1 AS SHIPFROMADDRESSLINE1 , t1.UDF1 AS UDF1, t1.C_SLOC AS C_SLOC, t1.C_BZNO AS C_BZNO, t1.C_SLOCNAME AS C_SLOCNAME, t1.WMSSTS AS WMSSTS , t3.udf2 AS SUSR2, t3.SUSR4 AS SUSR4, t3.SUSR5 AS SUSR5, t2.QTYEXPECTED AS QTYEXPECTED, t2.LOTTABLE02 AS LOTTABLE02 , t2.RECEIPTLINENUMBER AS RECEIPTLINENUMBER, t2.SKU AS SKU, t3.MODEL AS MODEL, SUBSTR(t3.sku,0,4) AS SUSR3NAME, SUBSTR(t3.sku,0,5) AS CATEGORY FROM receipt t1 LEFT JOIN receiptdetail t2 ON t1.receiptkey = t2.receiptkey LEFT JOIN sku t3 ON t2.sku = t3.sku AND t2.storerkey = t3.storerkey WHERE (t1.type IN ( '1', '16',  '18','9' ,'11') AND t1.status = '0' AND (t2.editflag = '0' OR t2.editflag IS NULL) AND t2.serialkey IS NOT NULL AND rownum < 50)";
			List<ReceiptEntity> list =  (List<ReceiptEntity>) queryAndResultMappingToBean(ReceiptEntity.class,wmsconn,querySqlTemplate);
			//将数据写到中间表
			String insertSqlTemplate = "INSERT INTO t_stockin (SERIALKEY, WHSEID, RECEIPTKEY, EXTERNRECEIPTKEY, STORERKEY ,  TYPE, SUPPLIERCODE, SUPPLIERNAME, SHIPFROMADDRESSLINE1 , UDF1, C_SLOC, C_BZNO, C_SLOCNAME, RECEIPTLINENUMBER , SKU, MODEL, SUSR2, SUSR4, SUSR5 , SUSR3NAME, CATEGORY, QTYEXPECTED, LOTTABLE02) VALUES (#{SERIALKEY}, #{WHSEID}, #{RECEIPTKEY}, #{EXTERNRECEIPTKEY}, #{STORERKEY} , #{TYPE}, #{SUPPLIERCODE}, #{SUPPLIERNAME}, #{SHIPFROMADDRESSLINE1} , #{UDF1}, #{C_SLOC}, #{C_BZNO}, #{C_SLOCNAME}, #{RECEIPTLINENUMBER} , #{SKU}, #{MODEL}, #{SUSR2}, #{SUSR4}, #{SUSR5} , #{SUSR3NAME}, #{CATEGORY}, #{QTYEXPECTED}, #{LOTTABLE02})";
			receiptDataToWCS(wcsMapper,list,wcsconn,insertSqlTemplate);
			//更新状态写成功数据的状态
			String updSqlTemplate = "update receiptdetail set editflag ='9' where serialkey =#{SERIALKEY}";
			updReceiptEditflag(list,wmsconn,updSqlTemplate);

			wmsconn.commit();
			wcsconn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			wcsconn.rollback();
			wmsconn.rollback();
		}finally {
			wmsconn.close();;
			wcsconn.close();;
			wcsSqlSession.close();
		}


	}
	
	/**
	 * 将数据写到中间表
	 * @param list
	 * @param wcsconn
	 * @param sqlTemplate
	 * @throws SQLException
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void receiptDataToWCS(WcsReceiptMapper wcsMapper,List<ReceiptEntity> list,Connection wcsconn,String sqlTemplate) throws SQLException, IllegalArgumentException, IllegalAccessException {
		for(ReceiptEntity entity:list) {
			String count = wcsMapper.isExist(entity.getRECEIPTKEY(), entity.getSKU());
			int existCount = Integer.parseInt(count);
			if(existCount > 0) {
				continue;
			}
			String insertSql = sqlTemplateValueMapping(entity,sqlTemplate);
			PreparedStatement  prepar = wcsconn.prepareStatement(insertSql);
			prepar.execute();
		}
	}
	
	/**
	 * 将javaBean的值映射到sql中
	 * @param obj
	 * @param sqlTemplate
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private String sqlTemplateValueMapping(Object obj,String sqlTemplate) throws IllegalArgumentException, IllegalAccessException {
		List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
		for(Field field:fields) {
			field.setAccessible(true);
			sqlTemplate = sqlTemplate.replaceAll("#\\{"+field.getName()+"\\}", "'"+String.valueOf(field.get(obj))+"'");
		}
		log.info(sqlTemplate);
		return sqlTemplate;
	}
	
	/**
	 * 更新下发之后的状态
	 * @param obj
	 * @param sqlTemplate
	 * @return
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void updReceiptEditflag(List<ReceiptEntity> list,Connection wcsconn,String sqlTemplate) throws SQLException, IllegalArgumentException, IllegalAccessException {
		for(ReceiptEntity entity:list) {
			String insertSql = sqlTemplateValueMapping(entity,sqlTemplate);
			PreparedStatement  prepar = wcsconn.prepareStatement(insertSql);
			prepar.execute();
		}
	}
	
	/**
	 * 执行查询并将查询结果设置到bean里面
	 * @param resultType
	 * @param connection
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	private Object queryAndResultMappingToBean(Class resultType,Connection conn,String sql) throws Exception {
		Object result = resultType;
		List resultList = new ArrayList();
		PreparedStatement  prepar = conn.prepareStatement(sql);
		prepar.execute();
		ResultSet re = prepar.getResultSet();
		while(re.next()) {
			ResultSetMetaData metaData =prepar.getMetaData();
			if(Map.class.equals(result)) {
				Map map = new HashMap();
				for(int i = 0;i<metaData.getColumnCount();i++) {
					map.put(metaData.getColumnName(i), re.getString(metaData.getColumnName(i)));
				}
				resultList.add(map);
			}else if(String.class.equals(result)) {
				String str = re.getString(metaData.getColumnName(0));
			}else{
				Object object = resultType.newInstance();
				List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
				Set columnNameIndex = new HashSet();
				for(int i = 1;i<=metaData.getColumnCount();i++) {
					columnNameIndex.add(metaData.getColumnName(i));
				}
				for(Field tempField:fields) {
					String fieldNam = tempField.getName().toUpperCase();
					if(columnNameIndex.contains(fieldNam)) {
						tempField.setAccessible(true);
						tempField.set(object,String.valueOf(re.getString(fieldNam)));
					}
				}
				resultList.add(object);
			};

		}

		return resultList;
	}
	
}
