package util.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import util.db.receipt.ReceiptCount;
import util.db.receipt.ReceiptEntity;
import util.db.receipt.WcsReceipt;

@Mapper
public interface WmsReceiptMapper {
	
	@Insert("insert into T_STOCKIN_RRETURN(SERIALKEY, WHSEID, RECEIPTKEY, EXTERNRECEIPTKEY, STORERKEY , ADDDATE, TYPE, C_BZNO, C_SLOCNAME, RECEIPTLINENUMBER , SKU, SUSR5, QTYEXPECTED, LOTTABLE02, LPN)values(#{SERIALKEY},#{WHSEID},#{RECEIPTKEY},#{EXTERNRECEIPTKEY},#{STORERKEY},sysdate,#{TYPE},#{C_BZNO},#{C_SLOCNAME},#{RECEIPTLINENUMBER},#{SKU},#{SUSR5},#{QTYEXPECTED},#{LOTTABLE02},#{LPN})")
	int addcountData(ReceiptCount count);
	
	@Insert("insert into T_STOCKIN_RETURN(SERIALKEY,WHSEID,RECEIPTKEY,EXTERNRECEIPTKEY,STORERKEY,ADDDATE,TYPE,SUPPLIERCODE,SUPPLIERNAME,SHIPFROMADDRESSLINE1,UDF1,C_SLOC,C_BZNO,C_SLOCNAME,RECEIPTLINENUMBER,SKU,QTYRCEIVED,LOTTABLE02,TOID,TOLOC,FLAG,SORTTIME,EDITFLAG) values (#{SERIALKEY},#{WHSEID},#{RECEIPTKEY},#{EXTERNRECEIPTKEY},#{STORERKEY},sysdate,#{TYPE},#{SUPPLIERCODE},#{SUPPLIERNAME},#{SHIPFROMADDRESSLINE1},#{UDF1},#{C_SLOC},#{C_BZNO},#{C_SLOCNAME},#{RECEIPTLINENUMBER},#{SKU},#{QTYRCEIVED},#{LOTTABLE02},#{TOID},#{TOLOC},#{FLAG},#{SORTTIME},0)")
	int addWcsReceiptData(WcsReceipt data);
	
	@Select("SELECT SERIALKEY, WHSEID, RECEIPTKEY, EXTERNRECEIPTKEY, STORERKEY , ADDDATE, TYPE, SUPPLIERCODE, SUPPLIERNAME, SHIPFROMADDRESSLINE1 , UDF1, C_SLOC, C_BZNO, C_SLOCNAME, RECEIPTLINENUMBER , SKU,QTYRCEIVED as QTYEXPECTED, LOTTABLE02, TOID, TOLOC FROM T_STOCKIN_RETURN where editflag = '0' and serialkey is not null")
	List<ReceiptEntity> queryReceiptData();
	
	@Update("update T_STOCKIN_RETURN set editflag =#{editflag},notes=#{notes} where receiptkey = #{receiptkey} and sku =#{sku}")
	int updReceiptStatus(@Param("editflag") String editflag,@Param("receiptkey") String receiptkey,@Param("notes") String notes,@Param("sku") String sku);
}
