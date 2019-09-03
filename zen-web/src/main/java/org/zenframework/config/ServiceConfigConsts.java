package org.zenframework.config;

/**
 * Created by Zeal on 2019/1/12 0012.
 * @deprecated
 */
public class ServiceConfigConsts {

    //===================================================================
    public static final String DATASOURCE_DRUID_URL = "datasource.druid.url";

    public static final String DATASOURCE_DRUID_USERNAME = "datasource.druid.username";

    public static final String DATASOURCE_DRUID_PASSWORD = "datasource.druid.password";

    public static final String DATASOURCE_DRUID_MAX_ACTIVE = "datasource.druid.max_active";

    public static final String DATASOURCE_DRUID_MAX_WAIT = "datasource.druid.max_wait";

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
