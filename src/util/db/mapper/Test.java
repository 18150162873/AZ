package util.db.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.ibatis.common.resources.Resources;

@SuppressWarnings("all")
public class Test {

	public static void main(String[] args) throws IOException {
		InputStream inwms = Resources.getResourceAsStream("mybatis.xml");
		SqlSessionFactory wmsFactory = new SqlSessionFactoryBuilder().build(inwms);
		InputStream inwcs = Resources.getResourceAsStream("mybatis.xml");
		SqlSessionFactory wcsFactory = new SqlSessionFactoryBuilder().build(inwcs,"wcs");
		
		SqlSession wmssqlsession =  wmsFactory.openSession();
		SqlSession wcssqlsession =  wcsFactory.openSession();
		
		WmsReceiptMapper wms =  wmssqlsession.getMapper(WmsReceiptMapper.class);
		WcsReceiptMapper wcs =  wcssqlsession.getMapper(WcsReceiptMapper.class);
//		
//		List<Map> list1 = wms.test();
//		List<Map> list2 = wcs.test();
		
//		System.out.println(list1);
//		System.out.println(list2);
	}
}
