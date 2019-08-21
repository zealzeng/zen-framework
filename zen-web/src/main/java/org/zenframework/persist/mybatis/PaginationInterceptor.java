package org.zenframework.persist.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.zenframework.persist.util.DBUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import org.zenframework.persist.util.SQLUtils;
import org.zenframework.web.vo.Pagination;

/**
 * 通过拦截<code>StatementHandler</code>的<code>prepare</code>方法，重写sql语句实现物理分页。
 * 老规矩，签名里要拦截的类型只能是接口。
 * 
 * @author 湖畔微风,Zeal
 * 
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class PaginationInterceptor implements Interceptor {

    public static final String VAR_DIALECT = "dialect";
    public static final String VAR_PAGE_SQL_ID = "pageSqlId";
    private static final Log logger = LogFactory.getLog(PaginationInterceptor.class);
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private static String defaultDialect = "mysql"; // 数据库类型(默认为mysql)
    private static String defaultPageSqlId = ".*PageList$"; // 需要拦截的ID(正则匹配)

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String method = invocation.getMethod().getName();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
            DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
        String pageSqlId = configuration.getVariables().getProperty("pageSqlId");
        if (null == pageSqlId || "".equals(pageSqlId)) {
            logger.warn("Property pageSqlId is not setted,use default '.*Page$' ");
            pageSqlId = defaultPageSqlId;
        }
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        
        // 只重写需要分页的sql语句。通过MappedStatement的ID匹配，默认重写以Page结尾的MappedStatement的sql
        if (mappedStatement.getId().matches(pageSqlId)) {
        	
        	BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            Object parameterObject = boundSql.getParameterObject();
            Pagination<?> page = this.getPagination(parameterObject);
        	//拦截prepare
        	if ("prepare".equals(method)) {
                String dialect = configuration.getVariables().getProperty("dialect");
                if (null == dialect || "".equals(dialect)) {
                    logger.warn("Property dialect is not setted,use default 'mysql' ");
                    dialect = defaultDialect;
                }
        		return prepare(invocation, metaStatementHandler, dialect, mappedStatement, boundSql, page);
        	}
        	//拦截query
        	else {
        		return query(invocation, boundSql, page);
            }
        }
        else {
            // 将执行权交给下一个拦截器
            return invocation.proceed();
        }
    }
    
    /**
     * Get Pagination from parameter object
     * @param parameterObject
     * @return
     */
    @SuppressWarnings("unchecked")
	private Pagination<?> getPagination(Object parameterObject) {
    	 if (parameterObject == null) {
             throw new NullPointerException("parameterObject is null!");
         } 
    	 else if (parameterObject instanceof MapperMethod.ParamMap) {
    		 Map<String, Object> paramMap = (Map<String,Object>) parameterObject;
    		 Iterator<Object> iter = paramMap.values().iterator();
    		 while (iter.hasNext()) {
    			 Object value = iter.next();
    			 if (value instanceof Pagination) {
    				 return (Pagination<?>) value;
    			 }
    		 }
    		 throw new IllegalArgumentException("Cannot find Pagination parameter");
    	 }
    	 else if (parameterObject instanceof Pagination) {
    		 return (Pagination<?>) parameterObject;
    	 }
         else {
         	throw new IllegalArgumentException("paramterObject must be Pagination instance");
         }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Object query(Invocation invocation, BoundSql boundSql, Pagination<?> page) throws Throwable {
    	Object result = invocation.proceed();
    	if (result != null && result instanceof List) {
    		//Pagination page = (Pagination) boundSql.getParameterObject();
    		page.setRecords((List)result);
    	}
    	return result;
    }
    
    private Object prepare(Invocation invocation, MetaObject metaStatementHandler, String dialect, 
    	MappedStatement mappedStatement, BoundSql boundSql, Pagination<?> page) throws Throwable {
    	
        //Pagination page = (Pagination) boundSql.getParameterObject();
        String sql = boundSql.getSql();
        //String[] sqls = SQLUtils.parsePaginationSQLs(dialect, sql, page.getSortColumn(), page.isSortAsc());
        //String pageSql = buildPageSql(dialect, sqls[1], page);
        int offset = (page.getPageNum() - 1) * page.getNumPerPage();
        String[] sqls = SQLUtils.paginationSQLs(dialect, sql, offset, page.getNumPerPage());
        String countSQL = sqls[0];
        String limitSQL = sqls[1];

        metaStatementHandler.setValue("delegate.boundSql.sql", limitSQL);
        // 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        Connection connection = (Connection) invocation.getArgs()[0];
        // 重设分页参数里的总页数等
        setPageParameter(countSQL, connection, mappedStatement, boundSql, page);
        // 将执行权交给下一个拦截器
        //FIXME 如果查询总记录数<=0,怎么直接返回?
        return invocation.proceed();
    }

    /**
     * 从数据库里查询总的记录数并计算总页数，回写进分页参数<code>PageParameter</code>,这样调用者就可用通过 分页参数
     * <code>PageParameter</code>获得相关信息。
     * 
     * @param sql
     * @param connection
     * @param mappedStatement
     * @param boundSql
     * @param page
     */
    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement,
            BoundSql boundSql, Pagination<?> page) {
    	if(page != null) {  // 判断一下,未传page参数 ， 则不用分页查询 .
	        // 记录总记录数
	        //String countSql = "select count(0) from (" + sql + ") total";
	        String countSql = sql;
	        PreparedStatement countStmt = null;
	        ResultSet rs = null;
	        try {
	            countStmt = connection.prepareStatement(countSql);
	            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
	                    boundSql.getParameterMappings(), boundSql.getParameterObject());
	            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
	            rs = countStmt.executeQuery();
	            int totalCount = 0;
	            if (rs.next()) {
	                totalCount = rs.getInt(1);
	            }
	            page.setTotalCount(totalCount);
	        } 
	        catch (SQLException e) {
	            logger.error("Ignore this exception", e);
	        } 
	        catch (Exception e) {
	        	logger.error("Ignore this exception", e);
	        }
	        finally {
	            DBUtils.close(rs);
	            DBUtils.close(countStmt);
	        }
    	}

    }

    /**
     * 对SQL参数(?)设值
     * 
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new org.apache.ibatis.scripting.defaults.DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    /**
     * mysql的分页语句
     * 
     * @param sql
     * @param page
     * @return String
     */
    private String buildPageSqlForMysql(String sql, Pagination<?> page) {
        StringBuilder pageSql = new StringBuilder(sql.length() + 50);
        String beginrow = String.valueOf((page.getPageNum() - 1) * page.getNumPerPage());
        pageSql.append(sql);
        pageSql.append(" limit " + beginrow + "," + page.getNumPerPage());
        return pageSql.toString();
    }

    /**
     * 参考hibernate的实现完成oracle的分页
     * 
     * @param sql
     * @param page
     * @return String
     */
    private String buildPageSqlForOracle(String sql, Pagination<?> page) {
        StringBuilder pageSql = new StringBuilder(sql.length() + 100);
        String beginrow = String.valueOf((page.getPageNum() - 1) * page.getNumPerPage());
        String endrow = String.valueOf(page.getPageNum() * page.getNumPerPage());

        pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
        pageSql.append(sql);
        pageSql.append(" ) temp where rownum <= ").append(endrow);
        pageSql.append(") where row_id > ").append(beginrow);
        return pageSql.toString();
    }


    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }

}

