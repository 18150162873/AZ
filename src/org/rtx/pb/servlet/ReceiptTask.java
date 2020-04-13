package org.rtx.pb.servlet;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.rtx.pb.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agileitp.forte.framework.internal.ServiceObjectException;

import util.db.mapper.WcsSqlSessionFactory;
import util.db.mapper.WmsReceiptMapper;
import util.db.mapper.WmsSqlSessionFactory;
import util.db.receipt.APIJDBConnection;
import util.db.receipt.ReceiptDetail;
import util.db.receipt.ReceiptEntity;
import util.properties.PropertiesFactory;
import util.properties.PropertiesFile;

@SuppressWarnings("all")
public class ReceiptTask implements Serializable{
	Logger log = LoggerFactory.getLogger(ReceiptTask.class);
	
	static String provider = "";
	protected String whseid;
	private final String locale = Locale.getDefault().toString();
	private String ssaConfig = "";

	public ReceiptTask() {
		ssaConfig = PropertiesFactory
				.getPropertiesHelper(PropertiesFile.CONFIG).getValue("ssa");
		whseid = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG)
				.getValue("whseid");
		provider = PropertiesFactory.getPropertiesHelper(PropertiesFile.CONFIG)
				.getValue("provider");
	}
	
	
	public void work() throws Exception {
		SqlSessionFactory wmsSqlSessionFactory = WmsSqlSessionFactory.getInstance();
		SqlSession wmsSqlSession = wmsSqlSessionFactory.openSession();
		WmsReceiptMapper wmsMapper = wmsSqlSession.getMapper(WmsReceiptMapper.class);

		List<ReceiptEntity> receiptData = wmsMapper.queryReceiptData();
		//按照receiptkey分组
		Map<String,List<ReceiptEntity>> dataMap =  groupByReceiptKey(receiptData);
		//处理回传的每单的数据
		for(String receiptKey:dataMap.keySet()) {
			String editflag ="S";
			String notes =" ";
			try {
				for(ReceiptEntity entity:dataMap.get(receiptKey)) {
					try {
						Common.receipt(provider, ssaConfig, locale, whseid,entity);
					} catch (Exception e) {
						editflag = "E";
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						notes =sw.toString();
						log.info("异常:",e);
					}finally {
						//完成收货后更新数据状态
						wmsMapper.updReceiptStatus(editflag, receiptKey, notes,entity.getSKU());
					}
				}
			} catch (Exception e) {
				log.info("异常:",e);
			}
			wmsSqlSession.commit();
		}

	}
	
//	/**
//	 * 新增一条数据 接收wcs返回的数据
//	 * @param wcsReturnData
//	 * @return
//	 * @throws Exception
//	 */
//	public List<ReceiptDetail> addReceiptDetail(List<ReceiptEntity> wcsReturnData) throws Exception {
//		List<ReceiptDetail> resultList = new ArrayList();
//		
//		Connection conn = APIJDBConnection.getConnection("wms");
//		String querySql = "select UDF1,UDF2,UDF3,UDF4,UDF5,WMSSTS,ERRMSG,PLANT,C_GP_QTY,C_PROD_QTY,C_COM_QTY,C_SUPPLIER,EDITFLAG,RECEIPTKEY,RECEIPTLINENUMBER,SUBLINENUMBER,EXTERNRECEIPTKEY,EXTERNLINENO,STORERKEY,POKEY,TARIFFKEY,SKU,ALTSKU,ID,STATUS,DATERECEIVED,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,UOM,PACKKEY,VESSELKEY,VOYAGEKEY,XDOCKKEY,CONTAINERKEY,TOLOC,TOLOT,TOID,CONDITIONCODE,LOTTABLE01,LOTTABLE02,LOTTABLE03,LOTTABLE04,LOTTABLE05,LOTTABLE06,LOTTABLE07,LOTTABLE08,LOTTABLE09,LOTTABLE10,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,EFFECTIVEDATE,FORTE_FLAG,SUSR1,SUSR2,SUSR3,SUSR4,SUSR5,NOTES,REASONCODE,PALLETID,QTYREJECTED,TYPE,RETURNTYPE,RETURNREASON,DISPOSITIONTYPE,DISPOSITIONCODE,RETURNCONDITION,QCREQUIRED,QCQTYINSPECTED,QCQTYREJECTED,QCREJREASON,QCSTATUS,QCUSER,QCAUTOADJUST,EXTERNALLOT,RMA,PACKINGSLIPQTY,IPSKEY,SUPPLIERNAME,SUPPLIERKEY,MATCHLOTTABLE,RECEIPTDETAILID,POLINENUMBER,SOURCELOCATION,SOURCEVERSION,REFERENCETYPE,REFERENCEDOCUMENT,REFERENCELOCATION,REFERENCEVERSION,REFERENCELINE,CUBICMETER,HUNDREDWEIGHT,TAREWGT,REFERENCEACCOUNTINGENTITY,REFERENCESCHEDULELINE,REQUISITIONDOCUMENT,REQUISITIONACCOUNTINGENTITY,REQUISITIONLOCATION,REQUISITIONVERSION,REQUISITIONLINE,REQUISITIONSCHEDULELINE,PURCHASEORDERDOCUMENT,PURCHASEORDERACCOUNTINGENTITY,PURCHASEORDERLOCATION,PURCHASEORDERVERSION,PURCHASEORDERLINE,PURCHASEORDERSCHEDULELINE,SALESORDERDOCUMENT,SALESORDERACCOUNTINGENTITY,SERIALKEY,WHSEID,SALESORDERLOCATION,SALESORDERVERSION,SALESORDERLINE,SALESORDERSCHEDULELINE,PRODUCTIONORDERDOCUMENT,PRODUCTIONORDERACCENTITY,PRODUCTIONORDERLOCATION,PRODUCTIONORDERVERSION,PRODUCTIONORDERLINE,PRODUCTIONORDERSCHEDULELINE,TEMPERATURE,ADDDATE,ADDWHO,EDITDATE,EDITWHO from receiptdetail where receiptkey ='"+"0000000119"+"'";
//		List<ReceiptDetail> list = (List<ReceiptDetail>) queryAndResultMappingToList(ReceiptDetail.class,conn,querySql);
//		int lineNumber = list.size()+1;
//		
//		for(ReceiptDetail detail:list) {
//			String wmssku = detail.getSKU();
//			String wmsstorekey = detail.getSTORERKEY();
//			
//			for(ReceiptEntity wcsData:wcsReturnData) {
//				String wcssku = detail.getSKU();
//				String wcsstorekey = detail.getSTORERKEY();
//				if(wcssku.equals(wmssku) && wcsstorekey.equals(wcsstorekey)) {
//					String serialkeySql = "SELECT REC_227_SEQ.NEXTVAL as serialkey from dual";
//					String serialkey = ((List<String>)queryAndResultMappingToList(String.class,conn,serialkeySql)).get(0);
//					detail.setRECEIPTLINENUMBER(String.format("%05d", lineNumber));
//					detail.setTOID(wcsData.getTOID());
//					detail.setTOLOC("");
//					detail.setQTYRECEIVED(wcsData.getQTYEXPECTED());
//					detail.setSERIALKEY(serialkey);
//					detail.setEDITFLAG("P");
//					System.out.println(serialkey);
//					nullToZero(detail);
//					String inserSql = "insert into receiptdetail(UDF1,UDF2,UDF3,UDF4,WMSSTS,ERRMSG,PLANT,C_GP_QTY,C_PROD_QTY,C_COM_QTY,C_SUPPLIER,EDITFLAG,RECEIPTKEY,RECEIPTLINENUMBER,SUBLINENUMBER,EXTERNRECEIPTKEY,EXTERNLINENO,STORERKEY,POKEY,TARIFFKEY,SKU,ALTSKU,ID,STATUS,DATERECEIVED,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,UOM,PACKKEY,VESSELKEY,VOYAGEKEY,XDOCKKEY,CONTAINERKEY,TOLOC,TOLOT,TOID,CONDITIONCODE,LOTTABLE01,LOTTABLE02,LOTTABLE03,LOTTABLE06,LOTTABLE07,LOTTABLE08,LOTTABLE09,LOTTABLE10,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,FORTE_FLAG,SUSR1,SUSR2,SUSR3,SUSR4,SUSR5,NOTES,REASONCODE,PALLETID,QTYREJECTED,TYPE,RETURNTYPE,RETURNREASON,DISPOSITIONTYPE,DISPOSITIONCODE,RETURNCONDITION,QCREQUIRED,QCQTYINSPECTED,QCQTYREJECTED,QCREJREASON,QCSTATUS,QCUSER,QCAUTOADJUST,EXTERNALLOT,RMA,PACKINGSLIPQTY,IPSKEY,SUPPLIERNAME,SUPPLIERKEY,MATCHLOTTABLE,RECEIPTDETAILID,POLINENUMBER,SOURCELOCATION,SOURCEVERSION,REFERENCETYPE,REFERENCEDOCUMENT,REFERENCELOCATION,REFERENCEVERSION,REFERENCELINE,CUBICMETER,HUNDREDWEIGHT,TAREWGT,REFERENCEACCOUNTINGENTITY,REFERENCESCHEDULELINE,REQUISITIONDOCUMENT,REQUISITIONACCOUNTINGENTITY,REQUISITIONLOCATION,REQUISITIONVERSION,REQUISITIONLINE,REQUISITIONSCHEDULELINE,PURCHASEORDERDOCUMENT,PURCHASEORDERACCOUNTINGENTITY,PURCHASEORDERLOCATION,PURCHASEORDERVERSION,PURCHASEORDERLINE,PURCHASEORDERSCHEDULELINE,SALESORDERDOCUMENT,SALESORDERACCOUNTINGENTITY,SERIALKEY,WHSEID,SALESORDERLOCATION,SALESORDERVERSION,SALESORDERLINE,SALESORDERSCHEDULELINE,PRODUCTIONORDERDOCUMENT,PRODUCTIONORDERACCENTITY,PRODUCTIONORDERLOCATION,PRODUCTIONORDERVERSION,PRODUCTIONORDERLINE,PRODUCTIONORDERSCHEDULELINE,TEMPERATURE) VALUES(#{UDF1},#{UDF2},#{UDF3},#{UDF4},#{WMSSTS},#{ERRMSG},#{PLANT},#{C_GP_QTY},#{C_PROD_QTY},#{C_COM_QTY},#{C_SUPPLIER},#{EDITFLAG},#{RECEIPTKEY},#{RECEIPTLINENUMBER},#{SUBLINENUMBER},#{EXTERNRECEIPTKEY},#{EXTERNLINENO},#{STORERKEY},#{POKEY},#{TARIFFKEY},#{SKU},#{ALTSKU},#{ID},#{STATUS},sysdate,#{QTYEXPECTED},#{QTYADJUSTED},#{QTYRECEIVED},#{UOM},#{PACKKEY},#{VESSELKEY},#{VOYAGEKEY},#{XDOCKKEY},#{CONTAINERKEY},#{TOLOC},#{TOLOT},#{TOID},#{CONDITIONCODE},#{LOTTABLE01},#{LOTTABLE02},#{LOTTABLE03},#{LOTTABLE06},#{LOTTABLE07},#{LOTTABLE08},#{LOTTABLE09},#{LOTTABLE10},#{CASECNT},#{INNERPACK},#{PALLET},#{CUBE},#{GROSSWGT},#{NETWGT},#{OTHERUNIT1},#{OTHERUNIT2},#{UNITPRICE},#{EXTENDEDPRICE},#{FORTE_FLAG},#{SUSR1},#{SUSR2},#{SUSR3},#{SUSR4},#{SUSR5},#{NOTES},#{REASONCODE},#{PALLETID},#{QTYREJECTED},#{TYPE},#{RETURNTYPE},#{RETURNREASON},#{DISPOSITIONTYPE},#{DISPOSITIONCODE},#{RETURNCONDITION},#{QCREQUIRED},#{QCQTYINSPECTED},#{QCQTYREJECTED},#{QCREJREASON},#{QCSTATUS},#{QCUSER},#{QCAUTOADJUST},#{EXTERNALLOT},#{RMA},#{PACKINGSLIPQTY},#{IPSKEY},#{SUPPLIERNAME},#{SUPPLIERKEY},#{MATCHLOTTABLE},#{RECEIPTDETAILID},#{POLINENUMBER},#{SOURCELOCATION},#{SOURCEVERSION},#{REFERENCETYPE},#{REFERENCEDOCUMENT},#{REFERENCELOCATION},#{REFERENCEVERSION},#{REFERENCELINE},#{CUBICMETER},#{HUNDREDWEIGHT},#{TAREWGT},#{REFERENCEACCOUNTINGENTITY},#{REFERENCESCHEDULELINE},#{REQUISITIONDOCUMENT},#{REQUISITIONACCOUNTINGENTITY},#{REQUISITIONLOCATION},#{REQUISITIONVERSION},#{REQUISITIONLINE},#{REQUISITIONSCHEDULELINE},#{PURCHASEORDERDOCUMENT},#{PURCHASEORDERACCOUNTINGENTITY},#{PURCHASEORDERLOCATION},#{PURCHASEORDERVERSION},#{PURCHASEORDERLINE},#{PURCHASEORDERSCHEDULELINE},#{SALESORDERDOCUMENT},#{SALESORDERACCOUNTINGENTITY},#{SERIALKEY},#{WHSEID},#{SALESORDERLOCATION},#{SALESORDERVERSION},#{SALESORDERLINE},#{SALESORDERSCHEDULELINE},#{PRODUCTIONORDERDOCUMENT},#{PRODUCTIONORDERACCENTITY},#{PRODUCTIONORDERLOCATION},#{PRODUCTIONORDERVERSION},#{PRODUCTIONORDERLINE},#{PRODUCTIONORDERSCHEDULELINE},#{TEMPERATURE})";
//					addReceiptDetail(detail,conn,inserSql);
//					lineNumber++;
//					resultList.add(detail);
//					break;
//				}
//			}
//		}
//		
//		return resultList;
//	}
//	
//	
//	private void nullToZero(ReceiptDetail detail) throws Exception {
//		List<String> numberField = Arrays.asList("UDF4,WMSSTS,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,QTYREJECTED,QCQTYINSPECTED,QCQTYREJECTED,PACKINGSLIPQTY,CUBICMETER,HUNDREDWEIGHT,TAREWGT,SERIALKEY,TEMPERATURE".split(","));
//		List<Field> fieldList = Arrays.asList(detail.getClass().getDeclaredFields());
//		for(Field field:fieldList) {
//			if(numberField.contains(field.getName().toUpperCase())) {
//				field.setAccessible(true);
//				Object value = field.get(detail);
//				if(null == value ||"null".equals(value) || "".equals(value)) {
//					field.set(detail, "0");
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 
//	 * @param list
//	 * @param wcsconn
//	 * @param sqlTemplate
//	 */
//	private void addReceiptDetail(ReceiptDetail detail,Connection wcsconn,String sqlTemplate) throws Exception {
//		String insertSql = sqlTemplateValueMapping(detail,sqlTemplate);
//		PreparedStatement  prepar = wcsconn.prepareStatement(insertSql);
//		prepar.execute();
//	}
	
	/**
	 * 将javaBean的值映射到sql中
	 * @param obj
	 * @param sqlTemplate
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static String sqlTemplateValueMapping(Object obj,String sqlTemplate) throws IllegalArgumentException, IllegalAccessException {
		List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
		for(Field field:fields) {
			field.setAccessible(true);
			sqlTemplate = sqlTemplate.replaceAll("#\\{"+field.getName()+"\\}", "'"+String.valueOf(field.get(obj))+"'");
		}
		System.out.println(sqlTemplate);
		return sqlTemplate;
	}
	
	/**
	 * 更新wcs数据的状态
	 * @param conn
	 * @param sqlTemplate
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private boolean updWcsDataStatus(Connection conn,String sqlTemplate,List list) throws Exception {
		boolean success = false;
		int count = 0;
		for(Object wcsreturnDetail:list) {
			String updSql = sqlTemplateValueReplace(sqlTemplate,wcsreturnDetail);
			PreparedStatement  prepar = conn.prepareStatement(updSql);
			count = prepar.executeUpdate();
			if(count < 1) {
				throw new RuntimeErrorException(new Error(),"");
			}
		}
		return success;
	}
	
	/**
	 * 更新入库单数据的状态
	 * @param conn
	 * @param sqlTemplate
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private boolean updReceiptStatus(Connection conn,String sqlTemplate) throws Exception {
		boolean success = false;
		int count = 0;
		PreparedStatement  prepar = conn.prepareStatement(sqlTemplate);
		count = prepar.executeUpdate();
		if(count < 1) {
			throw new RuntimeErrorException(new Error(),"");
		}
		return success;
	}
	
	/**
	 * 执行update
	 * @param conn
	 * @param sqlTemplate
	 * @param obj
	 * @throws Exception 
	 */
	private void updWmsReceiptDetailTolocAndToid(Connection conn,String sqlTemplate,List list) throws Exception {
		boolean success = false;
			for(Object wcsreturnDetail:list) {
				String updSql = sqlTemplateValueReplace(sqlTemplate,wcsreturnDetail);
				PreparedStatement  prepar = conn.prepareStatement(updSql);
				int count  = prepar.executeUpdate();
				if(count < 1 ) {
					throw new RuntimeErrorException(new Error(),"wcs回传数据,执行更新"+updSql+"失败;");
				}
			}
	}
	
	/**
	 * 将javabean赋值到sql
	 * @param sqlTemplate
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private String sqlTemplateValueReplace(String sqlTemplate,Object obj) throws Exception {
		List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
		for(Field field:fields) {
			field.setAccessible(true);
			sqlTemplate = sqlTemplate.replaceAll("#\\{"+field.getName()+"\\}", "'"+String.valueOf(field.get(obj))+"'");
		}
		log.info(sqlTemplate);
		return sqlTemplate;
	}
	
	
	/**
	 * 按照receiptkey分组
	 * @param list
	 * @return
	 */
	private Map groupByReceiptKey(List<ReceiptEntity> list) {
		Map<String,List> map = new HashMap();
		for(ReceiptEntity wcsData:list) {
			List tempList = new ArrayList();
			if(map.containsKey(wcsData.getRECEIPTKEY())) {
				List receiptkeyList = map.get(wcsData.getRECEIPTKEY());
				receiptkeyList.add(wcsData);
			}else{
				tempList.add(wcsData);
				map.put(wcsData.getRECEIPTKEY(), tempList);
			};
		}
		return map;
	}
	
	/**
	 * 填充收货明细数据至ASN单明细内（RECEIPTDETAIL表），当SUM(QTYRECEIVED)（收货总数量）= SUM(QTYEXPECTED)（应收总数量）时，单据状态更新为“已结”（11）状态；
	 * @param receiptket
	 * @param list
	 * @throws Exception 
	 */
	private void updReceiptAndDetailStatus(String receiptkey,Connection connection) throws Exception {
		String queryQtyreceivedSql = "select sum(QTYRECEIVED) as QTYRECEIVED from rceiptdetail where receiptkey = '"+receiptkey+"'";
		String qtyreceivedStr = (String) ((List) queryAndResultMappingToList(String.class,connection,queryQtyreceivedSql)).get(0);
		
		String queryQtyexpectedSql = "select sum(QTYEXPECTED) as QTYRECEIVED from rceiptdetail where receiptkey = '"+receiptkey+"'";
		String qtyexpectedStr = (String) ((List) queryAndResultMappingToList(String.class,connection,queryQtyreceivedSql)).get(0);
		
		int qtyreceived = Integer.parseInt(qtyreceivedStr);
		int qtyexpected = Integer.parseInt(qtyexpectedStr);
		if(qtyreceived == qtyexpected) {
				String updDetailStatusSql = "update rceiptdetail set status = '11' where receiptkey = '"+receiptkey+"'";
				updReceiptStatus(connection,updDetailStatusSql);
				String updStatusSql = "update rceiptdetail set status = '11' where receiptkey = '"+receiptkey+"'";
				updReceiptStatus(connection,updStatusSql);
		}
	} 
	/**
	 * 查询
	 * @param resultType
	 * @param connection
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	private Object queryAndResultMappingToList(Class resultType,Connection connection,String sql) throws Exception {
		List resultList = new ArrayList();
		try {
			Object result = resultType;
			PreparedStatement  prepar = connection.prepareStatement(sql);
			prepar.executeQuery();
			ResultSet re = prepar.getResultSet();
			while(re.next()) {
				ResultSetMetaData metaData =prepar.getMetaData();
				if(String.class.equals(resultType)) {
					String str = re.getString(metaData.getColumnName(0));
					resultList.add(str);
				}else {
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
				}
			}
		} catch (Exception e) {
//			log.info("{}",e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.err.println("查询报错："+sw.toString());
		}finally {
			connection.close();
		}
		return resultList;
	}
}
