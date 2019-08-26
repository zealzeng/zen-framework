package org.zenframework.config;

/**
 * Created by Zeal on 2019/1/12 0012.
 */
public class ServiceConfigConsts {

    //===================================================================
    public static final String DATASOURCE_URL = "datasource.url";

    public static final String DATASOURCE_USERNAME = "datasource.username";

    public static final String DATASOURCE_PASSWORD = "datasource.password";

    public static final String DATASOURCE_MAX_ACTIVE = "datasource.max_active";

    //ms
    public static final String DATASOURCE_MAX_WAIT = "datasource.max_wait";

    //===================================================================
    public static final String CACHE_MANAGER_NAME = "zenCacheManager";

    public static final String DATA_SOURCE_NAME = "zenDataSource";

    public static final String TRANSACTION_MANAGER_NAME = "zenTransactionManager";

    public static final String JDBC_TEMPLATE_NAME = "zenJdbcTemplate";

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE_NAME = "zenNamedParameterJdbcTemplate";

    public static final String SQL_SESSION_FACTORY_NAME = "zenSqlSessionFactory";

    public static final String SQL_SESSION_NAME = "zenSqlSession";

    //===================================================================
    //Application shares one system cache
	public static final String SYSTEM_CACHE = "systemCache";

}
