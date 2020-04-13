package org.rtx.pb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.db.receipt.APIJDBConnection;
import util.db.receipt.ReceiptDetail;
import util.db.receipt.ReceiptEntity;

@SuppressWarnings("all")
public class Test {

	public static void main(String[] args) throws Exception {
		addReceiptDetail(null);
	}
	
	public static void  addReceiptDetail(List<ReceiptEntity> wcsReturnData) throws Exception {
		List resultList = new ArrayList();
		Connection conn = APIJDBConnection.getConnection("wms");
		String querySql = "select UDF1,UDF2,UDF3,UDF4,UDF5,WMSSTS,ERRMSG,PLANT,C_GP_QTY,C_PROD_QTY,C_COM_QTY,C_SUPPLIER,EDITFLAG,RECEIPTKEY,RECEIPTLINENUMBER,SUBLINENUMBER,EXTERNRECEIPTKEY,EXTERNLINENO,STORERKEY,POKEY,TARIFFKEY,SKU,ALTSKU,ID,STATUS,DATERECEIVED,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,UOM,PACKKEY,VESSELKEY,VOYAGEKEY,XDOCKKEY,CONTAINERKEY,TOLOC,TOLOT,TOID,CONDITIONCODE,LOTTABLE01,LOTTABLE02,LOTTABLE03,LOTTABLE04,LOTTABLE05,LOTTABLE06,LOTTABLE07,LOTTABLE08,LOTTABLE09,LOTTABLE10,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,EFFECTIVEDATE,FORTE_FLAG,SUSR1,SUSR2,SUSR3,SUSR4,SUSR5,NOTES,REASONCODE,PALLETID,QTYREJECTED,TYPE,RETURNTYPE,RETURNREASON,DISPOSITIONTYPE,DISPOSITIONCODE,RETURNCONDITION,QCREQUIRED,QCQTYINSPECTED,QCQTYREJECTED,QCREJREASON,QCSTATUS,QCUSER,QCAUTOADJUST,EXTERNALLOT,RMA,PACKINGSLIPQTY,IPSKEY,SUPPLIERNAME,SUPPLIERKEY,MATCHLOTTABLE,RECEIPTDETAILID,POLINENUMBER,SOURCELOCATION,SOURCEVERSION,REFERENCETYPE,REFERENCEDOCUMENT,REFERENCELOCATION,REFERENCEVERSION,REFERENCELINE,CUBICMETER,HUNDREDWEIGHT,TAREWGT,REFERENCEACCOUNTINGENTITY,REFERENCESCHEDULELINE,REQUISITIONDOCUMENT,REQUISITIONACCOUNTINGENTITY,REQUISITIONLOCATION,REQUISITIONVERSION,REQUISITIONLINE,REQUISITIONSCHEDULELINE,PURCHASEORDERDOCUMENT,PURCHASEORDERACCOUNTINGENTITY,PURCHASEORDERLOCATION,PURCHASEORDERVERSION,PURCHASEORDERLINE,PURCHASEORDERSCHEDULELINE,SALESORDERDOCUMENT,SALESORDERACCOUNTINGENTITY,SERIALKEY,WHSEID,SALESORDERLOCATION,SALESORDERVERSION,SALESORDERLINE,SALESORDERSCHEDULELINE,PRODUCTIONORDERDOCUMENT,PRODUCTIONORDERACCENTITY,PRODUCTIONORDERLOCATION,PRODUCTIONORDERVERSION,PRODUCTIONORDERLINE,PRODUCTIONORDERSCHEDULELINE,TEMPERATURE,ADDDATE,ADDWHO,EDITDATE,EDITWHO from receiptdetail where receiptkey ='"+"0000000119"+"'";
		List<ReceiptDetail> list = (List<ReceiptDetail>) queryAndResultMappingToList(ReceiptDetail.class,conn,querySql);
		int lineNumber = list.size()+1;
		
		for(ReceiptDetail detail:list) {
			String wmssku = detail.getSKU();
			String wmsstorekey = detail.getSTORERKEY();
			
			for(ReceiptEntity wcsData:wcsReturnData) {
				String wcssku = detail.getSKU();
				String wcsstorekey = detail.getSTORERKEY();
				if(wcssku.equals(wmssku) && wcsstorekey.equals(wcsstorekey)) {
					String serialkeySql = "SELECT REC_227_SEQ.NEXTVAL as serialkey from dual";
					String serialkey = ((List<String>)queryAndResultMappingToList(String.class,conn,serialkeySql)).get(0);
					detail.setRECEIPTLINENUMBER(String.format("%05d", lineNumber));
					detail.setTOID(wcsData.getTOID());
					detail.setTOLOC(wcsData.getTOLOC());
					detail.setQTYRECEIVED(wcsData.getQTYEXPECTED());
					detail.setSERIALKEY(serialkey);
					detail.setEDITFLAG("P");
					System.out.println(serialkey);
					nullToZero(detail);
					String inserSql = "insert into receiptdetail(UDF1,UDF2,UDF3,UDF4,WMSSTS,ERRMSG,PLANT,C_GP_QTY,C_PROD_QTY,C_COM_QTY,C_SUPPLIER,EDITFLAG,RECEIPTKEY,RECEIPTLINENUMBER,SUBLINENUMBER,EXTERNRECEIPTKEY,EXTERNLINENO,STORERKEY,POKEY,TARIFFKEY,SKU,ALTSKU,ID,STATUS,DATERECEIVED,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,UOM,PACKKEY,VESSELKEY,VOYAGEKEY,XDOCKKEY,CONTAINERKEY,TOLOC,TOLOT,TOID,CONDITIONCODE,LOTTABLE01,LOTTABLE02,LOTTABLE03,LOTTABLE06,LOTTABLE07,LOTTABLE08,LOTTABLE09,LOTTABLE10,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,FORTE_FLAG,SUSR1,SUSR2,SUSR3,SUSR4,SUSR5,NOTES,REASONCODE,PALLETID,QTYREJECTED,TYPE,RETURNTYPE,RETURNREASON,DISPOSITIONTYPE,DISPOSITIONCODE,RETURNCONDITION,QCREQUIRED,QCQTYINSPECTED,QCQTYREJECTED,QCREJREASON,QCSTATUS,QCUSER,QCAUTOADJUST,EXTERNALLOT,RMA,PACKINGSLIPQTY,IPSKEY,SUPPLIERNAME,SUPPLIERKEY,MATCHLOTTABLE,RECEIPTDETAILID,POLINENUMBER,SOURCELOCATION,SOURCEVERSION,REFERENCETYPE,REFERENCEDOCUMENT,REFERENCELOCATION,REFERENCEVERSION,REFERENCELINE,CUBICMETER,HUNDREDWEIGHT,TAREWGT,REFERENCEACCOUNTINGENTITY,REFERENCESCHEDULELINE,REQUISITIONDOCUMENT,REQUISITIONACCOUNTINGENTITY,REQUISITIONLOCATION,REQUISITIONVERSION,REQUISITIONLINE,REQUISITIONSCHEDULELINE,PURCHASEORDERDOCUMENT,PURCHASEORDERACCOUNTINGENTITY,PURCHASEORDERLOCATION,PURCHASEORDERVERSION,PURCHASEORDERLINE,PURCHASEORDERSCHEDULELINE,SALESORDERDOCUMENT,SALESORDERACCOUNTINGENTITY,SERIALKEY,WHSEID,SALESORDERLOCATION,SALESORDERVERSION,SALESORDERLINE,SALESORDERSCHEDULELINE,PRODUCTIONORDERDOCUMENT,PRODUCTIONORDERACCENTITY,PRODUCTIONORDERLOCATION,PRODUCTIONORDERVERSION,PRODUCTIONORDERLINE,PRODUCTIONORDERSCHEDULELINE,TEMPERATURE) VALUES(#{UDF1},#{UDF2},#{UDF3},#{UDF4},#{WMSSTS},#{ERRMSG},#{PLANT},#{C_GP_QTY},#{C_PROD_QTY},#{C_COM_QTY},#{C_SUPPLIER},#{EDITFLAG},#{RECEIPTKEY},#{RECEIPTLINENUMBER},#{SUBLINENUMBER},#{EXTERNRECEIPTKEY},#{EXTERNLINENO},#{STORERKEY},#{POKEY},#{TARIFFKEY},#{SKU},#{ALTSKU},#{ID},#{STATUS},sysdate,#{QTYEXPECTED},#{QTYADJUSTED},#{QTYRECEIVED},#{UOM},#{PACKKEY},#{VESSELKEY},#{VOYAGEKEY},#{XDOCKKEY},#{CONTAINERKEY},#{TOLOC},#{TOLOT},#{TOID},#{CONDITIONCODE},#{LOTTABLE01},#{LOTTABLE02},#{LOTTABLE03},#{LOTTABLE06},#{LOTTABLE07},#{LOTTABLE08},#{LOTTABLE09},#{LOTTABLE10},#{CASECNT},#{INNERPACK},#{PALLET},#{CUBE},#{GROSSWGT},#{NETWGT},#{OTHERUNIT1},#{OTHERUNIT2},#{UNITPRICE},#{EXTENDEDPRICE},#{FORTE_FLAG},#{SUSR1},#{SUSR2},#{SUSR3},#{SUSR4},#{SUSR5},#{NOTES},#{REASONCODE},#{PALLETID},#{QTYREJECTED},#{TYPE},#{RETURNTYPE},#{RETURNREASON},#{DISPOSITIONTYPE},#{DISPOSITIONCODE},#{RETURNCONDITION},#{QCREQUIRED},#{QCQTYINSPECTED},#{QCQTYREJECTED},#{QCREJREASON},#{QCSTATUS},#{QCUSER},#{QCAUTOADJUST},#{EXTERNALLOT},#{RMA},#{PACKINGSLIPQTY},#{IPSKEY},#{SUPPLIERNAME},#{SUPPLIERKEY},#{MATCHLOTTABLE},#{RECEIPTDETAILID},#{POLINENUMBER},#{SOURCELOCATION},#{SOURCEVERSION},#{REFERENCETYPE},#{REFERENCEDOCUMENT},#{REFERENCELOCATION},#{REFERENCEVERSION},#{REFERENCELINE},#{CUBICMETER},#{HUNDREDWEIGHT},#{TAREWGT},#{REFERENCEACCOUNTINGENTITY},#{REFERENCESCHEDULELINE},#{REQUISITIONDOCUMENT},#{REQUISITIONACCOUNTINGENTITY},#{REQUISITIONLOCATION},#{REQUISITIONVERSION},#{REQUISITIONLINE},#{REQUISITIONSCHEDULELINE},#{PURCHASEORDERDOCUMENT},#{PURCHASEORDERACCOUNTINGENTITY},#{PURCHASEORDERLOCATION},#{PURCHASEORDERVERSION},#{PURCHASEORDERLINE},#{PURCHASEORDERSCHEDULELINE},#{SALESORDERDOCUMENT},#{SALESORDERACCOUNTINGENTITY},#{SERIALKEY},#{WHSEID},#{SALESORDERLOCATION},#{SALESORDERVERSION},#{SALESORDERLINE},#{SALESORDERSCHEDULELINE},#{PRODUCTIONORDERDOCUMENT},#{PRODUCTIONORDERACCENTITY},#{PRODUCTIONORDERLOCATION},#{PRODUCTIONORDERVERSION},#{PRODUCTIONORDERLINE},#{PRODUCTIONORDERSCHEDULELINE},#{TEMPERATURE})";
					addReceiptDetail(detail,conn,inserSql);
					lineNumber++;
					break;
				}
			}
		}
	}
	
	
	private static void nullToZero(ReceiptDetail detail) throws Exception {
		List<String> numberField = Arrays.asList("UDF4,WMSSTS,QTYEXPECTED,QTYADJUSTED,QTYRECEIVED,CASECNT,INNERPACK,PALLET,CUBE,GROSSWGT,NETWGT,OTHERUNIT1,OTHERUNIT2,UNITPRICE,EXTENDEDPRICE,QTYREJECTED,QCQTYINSPECTED,QCQTYREJECTED,PACKINGSLIPQTY,CUBICMETER,HUNDREDWEIGHT,TAREWGT,SERIALKEY,TEMPERATURE".split(","));
		List<Field> fieldList = Arrays.asList(detail.getClass().getDeclaredFields());
		for(Field field:fieldList) {
			if(numberField.contains(field.getName().toUpperCase())) {
				field.setAccessible(true);
				Object value = field.get(detail);
				if(null == value ||"null".equals(value) || "".equals(value)) {
					field.set(detail, "0");
				}
			}
		}
	}
	/**
	 * 
	 * @param list
	 * @param wcsconn
	 * @param sqlTemplate
	 */
	private static void addReceiptDetail(ReceiptDetail detail,Connection wcsconn,String sqlTemplate) throws Exception {
		String insertSql = sqlTemplateValueMapping(detail,sqlTemplate);
		PreparedStatement  prepar = wcsconn.prepareStatement(insertSql);
		prepar.execute();
	}
	
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
	 * 查询
	 * @param resultType
	 * @param connection
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	public static Object queryAndResultMappingToList(Class resultType,Connection connection,String sql) throws Exception {
		List resultList = new ArrayList();
		try {
			Object result = resultType;
			PreparedStatement  prepar = connection.prepareStatement(sql);
			prepar.executeQuery();
			ResultSet re = prepar.getResultSet();
			while(re.next()) {
				ResultSetMetaData metaData =prepar.getMetaData();
				if(String.class.equals(resultType)) {
					String str = re.getString(metaData.getColumnName(1));
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
		}
		return resultList;
	}
}
