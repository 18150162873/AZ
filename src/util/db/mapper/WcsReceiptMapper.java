package util.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import util.db.receipt.ReceiptCount;
import util.db.receipt.WcsReceipt;

@Mapper
public interface WcsReceiptMapper {
	
	@Select("SELECT SERIALKEY, WHSEID, RECEIPTKEY, EXTERNRECEIPTKEY, STORERKEY , ADDDATE, TYPE, C_BZNO, C_SLOCNAME, RECEIPTLINENUMBER , SKU, SUSR5, QTYEXPECTED, LOTTABLE02, LPN FROM T_STOCKIN_RRETURN where editflag = 'P' and rownum < 500")
	List<ReceiptCount> querywcsCountData();
	
	@Update("update T_STOCKIN_RRETURN set editflag = #{status} where SERIALKEY = #{SERIALKEY}")
	int updSuccessStatus(@Param("SERIALKEY")String serialkey,@Param("status") String status);
	
	@Select("SELECT SERIALKEY, WHSEID, RECEIPTKEY, EXTERNRECEIPTKEY, STORERKEY , ADDDATE, TYPE, SUPPLIERCODE, SUPPLIERNAME, SHIPFROMADDRESSLINE1 , UDF1, C_SLOC, C_BZNO, C_SLOCNAME, RECEIPTLINENUMBER , SKU, QTYRCEIVED, LOTTABLE02, TOID, upper(TOLOC) as TOLOC, FLAG, SORTTIME, EDITFLAG, NOTES FROM T_STOCKIN_RETURN WHERE editflag = '0'")
	List<WcsReceipt> findWcsReturenData();
	
	@Update("update T_STOCKIN_RETURN set editflag ='9' where SERIALKEY = #{SERIALKEY}")
	int updWcsReturenDataStatus(@Param("SERIALKEY")String serialkey,@Param("status") String status);
	
	@Select("select count(*) from t_stockin where receiptkey =#{receiptkey} and sku =#{sku}")
	String isExist(@Param("receiptkey")String receiptkey,@Param("sku") String sku);
}
