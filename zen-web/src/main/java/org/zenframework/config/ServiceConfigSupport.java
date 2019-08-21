/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import org.zenframework.persist.mybatis.PaginationInterceptor;
import org.zenframework.web.vo.Pagination;
import org.apache.ibatis.session.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Service layer application beans
 *
 * @author Zeal 2016年4月27日
 */
public abstract class ServiceConfigSupport {

    @Autowired
    protected org.springframework.core.env.Environment environment = null;

    /**
     * Use druid data source by default
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.DATA_SOURCE_NAME)
    public DataSource dataSource() {
        DruidDataSource ds = new DruidDataSource();
        org.springframework.core.env.Environment env = this.environment;
        ds.setUrl(env.getProperty(ServiceConfigConsts.PROPERTY_JBBC_URL));
        ds.setUsername(env.getProperty(ServiceConfigConsts.PROPERTY_JDBC_USERNAME));
        ds.setPassword(env.getProperty(ServiceConfigConsts.PROPERTY_JDBC_PASSWORD));
        ds.setMaxActive(200);
        ds.setMinIdle(2);
        ds.setMaxWait(120000L);
        ds.setRemoveAbandoned(false);
//		ds.setRemoveAbandonedTimeout(180);
        String dbType = ds.getDbType();
        ds.setTestOnBorrow(true);
        if (JdbcConstants.MYSQL.equals(dbType) || JdbcConstants.MARIADB.equals(dbType)) {
            ds.setValidationQuery("select 1");
        } else if (JdbcConstants.ORACLE.equals(dbType) || JdbcConstants.ALI_ORACLE.equals(dbType)) {
            ds.setValidationQuery("select 1 from dual");
        } else if (JdbcConstants.DB2.equals(dbType)) {
            ds.setValidationQuery("select 1 from sysibm.sysdummy1");
        } else if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            ds.setValidationQuery("select version()");
        } else if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            ds.setValidationQuery("select 1");
        }
        initializeDataSource(ds);
        return ds;
    }

    /**
     * Override data source settings callback
     *
     * @param dataSource
     */
    protected void initializeDataSource(DruidDataSource dataSource) {
    }

    /**
     * Default data source transaction manager
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.TRANSACTION_MANAGER_NAME)
    public DataSourceTransactionManager datasourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * Predefine jdbc template
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.JDBC_TEAMPLATE_NAME)
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    /**
     * Predefine named jdbc template
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.NAMED_PARAMETER_JDBC_TEMPLATE_NAME)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    /**
     * @return
     */
    @Bean(name = ServiceConfigConsts.SQL_SESSION_FACTORY_NAME)
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        bean.setTypeAliases(new Class[]{Pagination.class});
        ClassPathResource mybatisConfigFile = new ClassPathResource("mybatis-config.xml");
        if (mybatisConfigFile.exists()) {
            bean.setConfigLocation(mybatisConfigFile);
            initializeSqlSessionFactory(bean, null);
        } else {
            Configuration config = new Configuration();
            config.setCacheEnabled(true);
            config.setLazyLoadingEnabled(false);
            config.setAggressiveLazyLoading(false);
            config.setMultipleResultSetsEnabled(true);
            config.setUseColumnLabel(true);
            config.setUseGeneratedKeys(true);
            config.setAutoMappingBehavior(AutoMappingBehavior.FULL);
            config.setDefaultExecutorType(ExecutorType.SIMPLE);
            config.setSafeRowBoundsEnabled(false);
            config.setMapUnderscoreToCamelCase(true);
            config.setLocalCacheScope(LocalCacheScope.SESSION);
            config.setJdbcTypeForNull(JdbcType.NULL);
            config.addInterceptor(new PaginationInterceptor());
            Properties variables = new Properties();
            variables.put(PaginationInterceptor.VAR_DIALECT, "mysql");
            variables.put(PaginationInterceptor.VAR_PAGE_SQL_ID, ".*PageList$");
            config.setVariables(variables);
            bean.setConfiguration(config);
            initializeSqlSessionFactory(bean, config);
        }
        return bean;
    }

    /**
     * Set SqlSessionFactoryBean setTypeAliasesPackage or config location
     *
     * @param bean   SqlSessionFactoryBean
     * @param config It can be null when it's configured by mybatis-config.xml
     */
    protected void initializeSqlSessionFactory(SqlSessionFactoryBean bean, Configuration config) {
    }

    /**
     * Default mybatis sql session template
     *
     * @param factory
     * @return
     * @throws Exception
     */
    @Bean(name = ServiceConfigConsts.SQL_SESSION_NAME)
    public SqlSession sqlSession(SqlSessionFactory factory) throws Exception {
        return new SqlSessionTemplate(factory);
    }

    /**
     * One cache manager must create system cache
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.CACHE_MANAGER_NAME)
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        ConcurrentMapCache cache = new ConcurrentMapCache(ServiceConfigConsts.SYSTEM_CACHE);
        caches.add(cache);
        manager.setCaches(caches);
        return manager;
    }


}
