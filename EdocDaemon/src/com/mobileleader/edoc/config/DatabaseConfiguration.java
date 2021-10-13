package com.mobileleader.edoc.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.mobileleader.edoc.data.mapper"})
@PropertySource(value = {"db.properties"})
public class DatabaseConfiguration {

	@Value("${jdbc.driverName:}")
	private String jdbcDriver;
	
	@Value("${jdbc.url:}")
	private String jdbcUrl;
	
	@Value("${jdbc.username:}")
	private String jdbcUsername;
	
	@Value("${jdbc.password:}")
	private String jdbcPassword;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		HikariDataSource dataSource = new HikariDataSource();

		// jdbc setting
		dataSource.setDriverClassName(jdbcDriver);
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUsername(jdbcUsername);
		dataSource.setPassword(jdbcPassword);
		
		// connection pool setting
		dataSource.setMaximumPoolSize(20);
		dataSource.setMinimumIdle(10);
		
		return dataSource;
	}
	
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		
		sqlSessionFactory.setDataSource(dataSource);
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		sqlSessionFactory.setMapperLocations(
				resolver.getResources("classpath*:com/mobileleader/edoc/data/mapper/*.xml"));
		
		sqlSessionFactory.setTypeAliasesPackage("com.mobileleader.edoc.data");
		
		return sqlSessionFactory;
	}
	
	@Bean(name = "sqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
