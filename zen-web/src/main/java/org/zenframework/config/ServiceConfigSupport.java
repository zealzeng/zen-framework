/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.config;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Service layer application beans
 *
 * @author Zeal 2016年4月27日
 * @deprecated spring boot默认都有了
 */
public abstract class ServiceConfigSupport {

    @Autowired
    protected Environment environment = null;

    /**
     * Use druid data source by default
     *
     * @return
     */
    @Bean(name = ServiceConfigConsts.DATA_SOURCE_NAME)
    public abstract DataSource dataSource();

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
    @Bean(name = ServiceConfigConsts.JDBC_TEMPLATE_NAME)
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


}
