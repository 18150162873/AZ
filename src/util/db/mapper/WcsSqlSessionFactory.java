package util.db.mapper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.ibatis.common.resources.Resources;

public class WcsSqlSessionFactory {
	
	public volatile static SqlSessionFactory factory;

	private WcsSqlSessionFactory() {
	}
	
	public static SqlSessionFactory getInstance() throws IOException {
		if(factory == null ) {
			synchronized (WmsSqlSessionFactory.class) {
				if (factory == null) {
					InputStream inwms = Resources.getResourceAsStream("mybatis.xml");
					factory = new SqlSessionFactoryBuilder().build(inwms,"wcs");
				}
			}
		}
		return factory;
	}
}
