/*
 * Copyright (c) 2015, All rights reserved.
 */
package org.zenframework.persist.util;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.zenframework.util.MethodUtils;
import org.zenframework.util.StringUtils;

/**
 * The utility to process SQL
 *
 * @author Zeal 2015年12月18日
 */
public class SQLUtils {

    /**
     * Generate pagination sql
     * @param dbType
     * @param sql
     * @param offset
     * @param count
     * @return First one is count sql, second is limit sql
     */
    public static String[] paginationSQLs(String dbType, String sql, int offset, int count) {
        SQLSelectStatement countStmt = getSQLSelectStatement(dbType, sql);
        SQLSelectStatement limitStmt = countStmt.clone();
        Object[] args = new Object[] { countStmt.getSelect(), dbType };
        Class[] paramTypes = new Class[] { SQLSelect.class, String.class };
        String countSQL = "";
        try {
            //FIXME If there's performance issue, copy count source codes from PagerUtils directly
            countSQL = (String) MethodUtils.invokeStaticMethod(true, PagerUtils.class, "count", args, paramTypes);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        String limitSQL = PagerUtils.limit(limitStmt.getSelect(), dbType, offset, count);
        return new String[] { countSQL, limitSQL };
    }

    /**
     * Get SQLStatement
     * @param dbType
     * @param sql
     * @return
     */
    private static SQLStatement getSQLStatement(String dbType, String sql) {
        List<SQLStatement> stmtList = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, dbType);
        if (stmtList == null || stmtList.size() <= 0) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }
        return stmtList.get(0);
    }

    /**
     * Get SQLSelectStatement
     * @param dbType
     * @param sql
     * @return
     */
    private static SQLSelectStatement getSQLSelectStatement(String dbType, String sql) {
        SQLStatement stmt = getSQLStatement(dbType, sql);
        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }
        return (SQLSelectStatement) stmt;
    }

    /**
     * PagerUtils has better compatible, cancel supportting sortColumn, sortAsc
     * @param dbType
     * @param sql
     * @param sortColumn
     * @param sortAsc
     * @return
     * @deprecated
     */
    public static String[] parsePaginationSQLs(String dbType, String sql, String sortColumn, boolean sortAsc) {
        return parseMysqlPaginationSQLs(sql, sortColumn, sortAsc);
    }

    /**@deprecated  PagerUtils has better compatible, cancel supportting sortColumn, sortAsc */
    private static void mergeOrderBy(SQLOrderBy orderBy, String sortColumn, boolean sortAsc) {

        if (StringUtils.isBlank(sortColumn)) {
            return;
        }
        List<SQLSelectOrderByItem> items = orderBy.getItems();
        LinkedList<SQLSelectOrderByItem> newItems = new LinkedList<>();

        boolean found = false;
        for (SQLSelectOrderByItem item : items) {
            SQLExpr expr = item.getExpr();
            //Normal order by item, column_name desc/asc
            if ((expr instanceof SQLPropertyExpr) || (expr instanceof SQLIdentifierExpr)) {
                //SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                //Full name with owner
                if (sortColumn.equalsIgnoreCase(expr.toString())) {
                    if (isOrderByAsc(item.getType()) != sortAsc) {
                        item.setType(sortAsc ? null : SQLOrderingSpecification.DESC);
                    }
                    found = true;
                    //Place it at the beginning
                    newItems.addFirst(item);
                    continue;
                }
            }
            newItems.add(item);
        }
        if (!found) {
            String ownerName = "";
            String name = sortColumn;
            int index = sortColumn.indexOf('.');
            if (index > 0) {
                ownerName = sortColumn.substring(0, index);
                name = sortColumn.substring(index + 1);
            }
            SQLSelectOrderByItem item = new SQLSelectOrderByItem();
            item.setParent(orderBy);
            item.setType(sortAsc ? null : SQLOrderingSpecification.DESC);
            //No owner space
            if (StringUtils.isBlank(ownerName)) {
                SQLIdentifierExpr idExpr = new SQLIdentifierExpr(name);
                idExpr.setParent(item);
                item.setExpr(idExpr);
            } else {
                SQLPropertyExpr propertyExpr = new SQLPropertyExpr();
                propertyExpr.setName(name);
                propertyExpr.setParent(item);
                SQLIdentifierExpr ownerExpr = new SQLIdentifierExpr(ownerName);
                propertyExpr.setOwner(ownerExpr);
                item.setExpr(propertyExpr);
            }
            newItems.addFirst(item);
        }
        orderBy.getItems().clear();
        orderBy.getItems().addAll(newItems);
    }

    /**@deprecated  PagerUtils has better compatible, cancel supportting sortColumn, sortAsc */
    private static boolean isOrderByAsc(SQLOrderingSpecification orderSpec) {
        return (orderSpec == null || orderSpec == SQLOrderingSpecification.ASC);
    }

    /**
     * PagerUtils has better compatible, cancel supportting sortColumn, sortAsc
     * @param sql
     * @param sortColumn
     * @param sortAsc
     * @return sql arrary, first sql is select count, second one is merged sql with sort column
     * @deprecated
     */
    public static String[] parseMysqlPaginationSQLs(String sql, String sortColumn, boolean sortAsc) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        //Only support select statement
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseSelect();
        SQLSelect select = stmt.getSelect();
        SQLSelectQuery selectQuery = select.getQuery();

        //Normal select
        if (selectQuery instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) selectQuery;
            SQLOrderBy orderBy = query.getOrderBy();
            SQLSelectGroupByClause groupBy = query.getGroupBy();
            //String orderBySQL = "";
            String queryWithoutOrderBySQL = "";
            String mergeSql = sql;
            //Has order by clause
            if (orderBy != null) {

                mergeOrderBy(orderBy, sortColumn, sortAsc);
                mergeSql = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
                //Trim order by
                query.setOrderBy(null);
            }
            //Query without order by SQL
            queryWithoutOrderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            String selectCountSQL = null;

            //Group by clause, the select field might contain aggregate expr and we had better to prepand select count(1) directly
            if (groupBy != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT COUNT(1) FROM(").append(queryWithoutOrderBySQL).append(") AS t");
                selectCountSQL = sb.toString();
            }
            //No group by
            else {
                //Generate select count SQL
                SQLAggregateExpr count = new SQLAggregateExpr("COUNT");
                SQLIntegerExpr countOne = new SQLIntegerExpr(new Integer(1));
                count.getArguments().add(countOne);
                SQLSelectItem selectItem = new SQLSelectItem(count);
                selectItem.setParent(query);
                List<SQLSelectItem> selectItems = query.getSelectList();
                selectItems.clear();
                selectItems.add(selectItem);
                if (query.getDistionOption() > 0) {
                    query.setDistionOption(0);
                }
                selectCountSQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            }
            //SQL array
            return new String[]{selectCountSQL, mergeSql};
        }
        //Union select
        else if (selectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery query = (SQLUnionQuery) selectQuery;
            MySqlSelectQueryBlock rightQuery = (MySqlSelectQueryBlock) getUnionRightQuery(query);
            SQLOrderBy orderBy = rightQuery.getOrderBy();
            //Order by SQL
            //String orderBySQL = "";
            String queryWithoutOrderBySQL = "";
            String mergeSql = sql;
            //Has order by
            if (orderBy != null) {
                mergeOrderBy(orderBy, sortColumn, sortAsc);
                mergeSql = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
                //Trim order by
                query.setOrderBy(null);
            }
            //Union query without order by SQL
            queryWithoutOrderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            //Generate select count SQL
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(1) FROM(").append(queryWithoutOrderBySQL).append(") AS t");
            String selectCountSQL = sb.toString();
            //SQL array
            return new String[]{selectCountSQL, mergeSql};
        } else {
            throw new UnsupportedOperationException("SQLSelectQuery type " + selectQuery.getClass() + " is not supported");
        }
    }

    /**
     * Retrieve right query from union
     * PagerUtils has better compatible, cancel supportting sortColumn, sortAsc
     * @param unionQuery
     * @return
     * @deprecated
     */
    private static SQLSelectQuery getUnionRightQuery(SQLUnionQuery unionQuery) {
        SQLSelectQuery query = unionQuery.getRight();
        if (query instanceof SQLUnionQuery) {
            return getUnionRightQuery((SQLUnionQuery) query);
        } else {
            return query;
        }
    }

    /**
     * Parse pagination sql, actully it only support mysql dialect and qyby GenericDAO
     *
     * @param sql
     * @return String[] first one is selecting count SQL, second one is queryWithoutOrderBySQL, the third one is orderBySQL
     * @deprecated PagerUtils has better compatible, cancel supporting sortColumn, sortAsc
     */
    public static String[] parsePaginationSQLs(String sql) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseSelect();
        SQLSelect select = stmt.getSelect();
        SQLSelectQuery selectQuery = select.getQuery();
        //Normal select
        if (selectQuery instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) selectQuery;
            SQLOrderBy orderBy = query.getOrderBy();
            SQLSelectGroupByClause groupBy = query.getGroupBy();
            String orderBySQL = "";
            String queryWithoutOrderBySQL = "";
            //Has order by clause
            if (orderBy != null) {
                //Order by SQL
                orderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(orderBy);
                //Trim order by
                query.setOrderBy(null);
            }
            //Query without order by SQL
            queryWithoutOrderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            String selectCountSQL = null;

            //Group by clause, the select field might contain aggregate expr and we had better to prepand select count(1) directly
            if (groupBy != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT COUNT(1) FROM(").append(queryWithoutOrderBySQL).append(") AS t");
                selectCountSQL = sb.toString();
            }
            //No group by
            else {
                //Generate select count SQL
                SQLAggregateExpr count = new SQLAggregateExpr("COUNT");
                SQLIntegerExpr countOne = new SQLIntegerExpr(new Integer(1));
                count.getArguments().add(countOne);
                SQLSelectItem selectItem = new SQLSelectItem(count);
                selectItem.setParent(query);
                List<SQLSelectItem> selectItems = query.getSelectList();
                selectItems.clear();
                selectItems.add(selectItem);
                if (query.getDistionOption() > 0) {
                    query.setDistionOption(0);
                }
                selectCountSQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            }
            //SQL array
            return new String[]{selectCountSQL, queryWithoutOrderBySQL, orderBySQL};
        }
        //Union select
        else if (selectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery query = (SQLUnionQuery) selectQuery;
            MySqlSelectQueryBlock rightQuery = (MySqlSelectQueryBlock) query.getRight();
            SQLOrderBy orderBy = rightQuery.getOrderBy();
            //Order by SQL
            String orderBySQL = "";
            String queryWithoutOrderBySQL = "";
            //Has order by
            if (orderBy != null) {
                //Order by SQL
                orderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(orderBy);
                //Trim order by
                query.setOrderBy(null);
            }
            //Union query without order by SQL
            queryWithoutOrderBySQL = com.alibaba.druid.sql.SQLUtils.toMySqlString(query);
            //Generate select count SQL
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(1) FROM(").append(queryWithoutOrderBySQL).append(") AS t");
            String selectCountSQL = sb.toString();
            //SQL array
            return new String[]{selectCountSQL, queryWithoutOrderBySQL, orderBySQL};
        } else {
            throw new UnsupportedOperationException("SQLSelectQuery type " + selectQuery.getClass() + " is not supported");
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String sql = "SELECT DISTINCT " +
                "o.order_id," +
                "o.order_no, " +
                "buyer.mbr_name buy_name, " +
                "seller.mbr_display_name sell_name, " +
                "o.order_status, " +
                "o.create_time, " +
                "o.modify_time, " +
                "o.amount_money, " +
                "o.amount_quantity, " +
                "o.amount_weight " +
                "FROM " +
                "p_tde_order o, " +
                "s_scu_mbr buyer, " +
                "s_scu_mbr seller, " +
                "p_tde_order_spot_list sp, " +
                "p_tde_contract ct " +
                "WHERE " +
                "o.deleted = 0 " +
                "AND sp.deleted = 0 " +
                "AND o.order_type = 1 " +
                "AND o.order_id = sp.order_id " +
                "AND o.sell_id = seller.mbr_id " +
                "AND o.buy_id = buyer.mbr_id " +
                "AND o.order_id = ct.order_id " +
                "AND o.order_no LIKE '%Q1512081628368905%' " +
                "ORDER BY " +
                "	o.modify_time DESC";

        //sql = "SELECT a.role_id,a.`name`,p.person_name modifier_name,a.modify_time,c.mbr_type,c.mbr_name,GROUP_CONCAT(DISTINCT sr.display_name SEPARATOR '、') privilege FROM s_scu_role a, c_scu_role_mbrtype b, s_scu_mbrtype_info c, c_scu_role_res rr, s_scu_res sr,s_scu_user p WHERE a.role_id = b.role_id  and a.role_id=rr.role_id and a.modifier=p.user_id and rr.res_id=sr.res_id AND b.mbr_type = c.mbr_type AND a.deleted = 0 AND b.deleted = 0 and rr.deleted=0 and sr.deleted=0 and sr.type=1 and LENGTH(sr.path)-LENGTH( REPLACE(sr.path,'_',''))=2  GROUP BY a.`name`,a.modifier,a.modify_time,c.`mbr_name`  order by a.modify_time desc,b.mbr_type,a.name,rand() ";
        sql = "select * from T1 union all select * from T2 ";

        String[] sqls = paginationSQLs("oracle", sql, 10, 100);
        System.out.println(sqls[0]);
        System.out.println("==========================");
        System.out.println(sqls[1]);


//        System.out.println("++++++++++++++++++++++++++++++++++++");

        //String[] values = SQLUtils.parsePaginationSQLs(sql);
//        String[] values = SQLUtils.parseMysqlPaginationSQLs(sql, "b.mbr_type", false);
//        for (String value : values) {
//            System.out.println(value);
//            System.out.println("====================================");
//        }
    }

}
