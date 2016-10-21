package com.mgear.wuhanparking.common.interceptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.alibaba.fastjson.JSONObject;



/**
 * Mybatis - 分页拦截器
 * 
 * @author c.c.
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }),
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class PageInterceptor implements Interceptor {
	//private static final Logger logger = Logger.getLogger(PageInterceptor.class);
	
	public static final ThreadLocal<Page> localPage = new ThreadLocal<Page>();
    private static Page mypage;
     
    /**
     * 开始分页
     * @param pageNum 当前页
     * @param pageSize 每记录数
     * @param type 数据库类型
     */
    public static void startPage(int pageNum, int pageSize, DBTYPE type) {
        localPage.set(new Page(pageNum, pageSize, type));
    }

    /**
     * 结束分页并返回结果，该方法必须被调用，否则localPage会一直保存下去，直到下一次startPage
     * @return
     */
    public static Page endPage() {
        return mypage;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (localPage.get() == null) {
            return invocation.proceed();
        }
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环
            // 可以分离出最原始的的目标类)
            while (metaStatementHandler.hasGetter("h")) {
                Object object = metaStatementHandler.getValue("h");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            // 分离最后一个代理对象的目标类
            while (metaStatementHandler.hasGetter("target")) {
                Object object = metaStatementHandler.getValue("target");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            //分页信息if (localPage.get() != null) {
            Page page = localPage.get();
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            // 分页参数作为参数对象parameterObject的一个属性
            String sql = boundSql.getSql();
            // 重写sql
            String pageSql = buildPageSql(sql, page);
            //重写分页sql
            metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
            Connection connection = (Connection) invocation.getArgs()[0];
            // 重设分页参数里的总页数等
            setPageParameter(sql, connection, mappedStatement, boundSql, page);
            // 将执行权交给下一个拦截器
            return invocation.proceed();
        } else if (invocation.getTarget() instanceof ResultSetHandler) {
            Object result = invocation.proceed();
            Page page = localPage.get();
            page.setResult((List) result);
            //添加序号
            List l = (List) result;
            for(int i=0; i<l.size(); i++){
            	((Map)l.get(i)).put("SerialNumber", i+1);
            }
            mypage=page;
            localPage.remove();
            return result;
        }
        return null;
    }

    /**
     * 只拦截这两种类型的
     * <br>StatementHandler
     * <br>ResultSetHandler
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 修改原SQL为分页SQL
     * @author chuwc 2016.4.13 
     * @handle update
     * @param sql
     * @param page
     * @return
     */
    private String buildPageSql(String sql, Page page) {
       StringBuilder pageSql = new StringBuilder(200);
       switch (page.getDBType()) {
		case SQLSERVER:
			/**SQLServer分页*/
	        String order = "(select 0)";
	        sql = sql.replace("ORDER BY", "order by");
	        String[] str = sql.split("order by");
	        if(str.length == 2){
	        	order = str[1];
	        }
	        pageSql.append("select rn.* from (select top ").append(page.getEndRow());
	        pageSql.append(" temp.*,row_number() over(order by").append(order).append(") SerialNumber from (");
	        pageSql.append(str[0]).append(") temp ) rn where rn.SerialNumber > ").append(page.getStartRow());
			break;
		case ORACLE:
			/**oracle分页*/
            pageSql.append("select rn.* from ( select temp.*, rownum XH from ( ").append(sql);
            pageSql.append(" ) temp where rownum <= ").append(page.getEndRow());
            pageSql.append(") rn where XH > ").append(page.getStartRow());
			break;
		case MYSQL:
			/**mysql分页*/
            pageSql.append(sql);
            pageSql.append(" limit ").append(page.getStartRow()).append(" , ").append(page.getPageRecord());
			break;	
		default:
			break;
		}
//        System.out.println("分页sql:"+pageSql);
        return pageSql.toString();
    }

    /**
     * 获取总记录数
     * @author chuwc 2016.8.15 
     * @update 优化countSql
     */
    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement,
                                  BoundSql boundSql, Page page) {
    	// 统计总记录数
    	String countSql = "";
    	sql = sql.replace("ORDER BY", "order by");
    	if(sql.indexOf("order by") > -1) {
    		String str[] = sql.split("order by");
    		countSql = "select count(0) from (" + str[0] + ") t";
    	} else {
    		countSql = "select count(0) from (" + sql + ") t";
    	}
//        System.out.println("统计SQL:"+countSql);
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            
            //使用反射注入参数
            Field metaParamsField = ReflectUtil.getFieldByFieldName(boundSql, "metaParameters");
            if (metaParamsField != null) {
                MetaObject mo = (MetaObject)ReflectUtil.getValueByFieldName(boundSql, "metaParameters");
                ReflectUtil.setValueByFieldName(countBS, "metaParameters", mo);
            }
            
            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            page.setRecordCount(totalCount);
            int totalPage = totalCount==0?0:(totalCount-1)/page.getPageRecord()+1;
            page.setPageCount(totalPage);
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
               e.printStackTrace();
            }
            try {
                countStmt.close();
            } catch (SQLException e) {
            
            }
        }
    }

    /**
     * 代入参数值
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
                               Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    /**
     * Description: 分页
     * Author: liuzh
     * Update: liuzh(2014-04-16 10:56)
     */
    public static class Page<E> {
        private int CurrentPage;
        private int PageRecord;
        private int startRow;
        private int endRow;
        private long RecordCount;
        private int PageCount;
        private DBTYPE DBType; // 0:SqlServer 1:oracle  2:mysql
        private List<E> result;

        public Page(int pageNum, int pageSize, DBTYPE type) {
            this.CurrentPage = pageNum;
            this.PageRecord = pageSize;
            this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
            this.endRow = pageNum * pageSize;
            this.DBType = type;
        }

		public  String toString() {
            JSONObject  count = new JSONObject();
            count.put("RecordCount", RecordCount);
            count.put("PageCount", PageCount);
            count.put("CurrentPage",CurrentPage);
            count.put("PageRecord", PageRecord);
            
            return  count.toString();
        }

		public int getCurrentPage() {
			return CurrentPage;
		}

		public void setCurrentPage(int currentPage) {
			CurrentPage = currentPage;
		}

		public int getPageRecord() {
			return PageRecord;
		}

		public void setPageRecord(int pageRecord) {
			PageRecord = pageRecord;
		}

		public int getStartRow() {
			return startRow;
		}

		public void setStartRow(int startRow) {
			this.startRow = startRow;
		}

		public int getEndRow() {
			return endRow;
		}

		public void setEndRow(int endRow) {
			this.endRow = endRow;
		}

		public long getRecordCount() {
			return RecordCount;
		}

		public void setRecordCount(long recordCount) {
			RecordCount = recordCount;
		}

		public int getPageCount() {
			return PageCount;
		}

		public void setPageCount(int pageCount) {
			PageCount = pageCount;
		}

		public List<E> getResult() {
			return result;
		}

		public void setResult(List<E> result) {
			this.result = result;
		}

		public DBTYPE getDBType() {
			return DBType;
		}

		public void setDBType(DBTYPE dBType) {
			DBType = dBType;
		}
		
    }
}
