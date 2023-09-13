package com.xiaofan.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.xiaofan.constants.JdbcConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author: twan
 * @date: 2023/8/1 12:42
 * @description: 使用完务必回收资源！！
 */
public class JdbcUtil {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    private static final Properties properties = new Properties();
    private final PreparedStatement pstmt = null;
    private String dbPrefixKey = "";
    private Connection connection = null;

    public JdbcUtil(String dbPrefixKey) {
        this.dbPrefixKey = dbPrefixKey;
        InputStream inputStream = JdbcUtil.class.getClassLoader().getResourceAsStream("sql/db.properties");
        try {
            properties.load(inputStream);
            Class.forName(properties.getProperty(dbPrefixKey + ".driver"));
            connection = DriverManager.getConnection(properties.getProperty(dbPrefixKey + ".url"), properties.getProperty(dbPrefixKey + ".user"), properties.getProperty(dbPrefixKey + ".pwd"));
        } catch (Exception e) {
            logger.error("get getConnection error message:{}", e.getMessage());
        }

    }

    public static void close(Connection con, Statement stat, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                logger.error("close error :{}", ex.getMessage());
            }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException ex) {
                logger.error("close error :{}", ex.getMessage());
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                logger.error("close error :{}", ex.getMessage());
            }
        }
    }

    public static List<String> getSqlParameterRelate(String sql) {
        String lowerSql = sql.toLowerCase();
        if (lowerSql.contains("insert")) {
            return Arrays.stream(lowerSql.substring(lowerSql.indexOf("(") + 1, lowerSql.indexOf(")")).split(",")).map(t -> t.trim()).collect(Collectors.toList());
        } else {
            return Arrays.stream(lowerSql.substring(lowerSql.indexOf("set") + 3, lowerSql.indexOf("where")).split(",")).filter(t -> t.contains("=")).map(t -> t.split("=")[0]).collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws Exception {
        JdbcUtil jdbcUtil = new JdbcUtil("xinxiaofei");

    }

    public Connection getConnection() {
        return connection;
    }

    //用类名调用方法关闭相关的连接或是其他
    //PreparedStatement的超级接口是Statement
    //下面的参数是Statement
    public void close() {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                logger.error("close error :{}", ex.getMessage());
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("close error :{}", ex.getMessage());

            }
        }
    }

    public Integer upsert(JSONArray jsonArray, Triple<String, List<String>, List<String>> upsetTriple) {
        Integer count = 0;
        try {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareStatement(upsetTriple.getLeft());
            List<String> middle = upsetTriple.getMiddle();
            for (int index = 0; index < jsonArray.size(); index++) {
                JSONObject jsonObject = ((JSONObject) jsonArray.get(index));
                for (int i = 0; i < middle.size(); i++) {
                    setStatementValue(prepareStatement, i + 1, jsonObject.get(middle.get(i)));
                }
                List<String> right = upsetTriple.getRight();
                for (int i = 0; i < right.size(); i++) {
                    setStatementValue(prepareStatement, middle.size() + i + 1, jsonObject.get(right.get(i)));
                }
                prepareStatement.addBatch();
                if ((index + 1) % 100 == 0) {
                    int[] results = prepareStatement.executeBatch();
                    count += Long.valueOf(Arrays.stream(results).mapToLong(Integer::valueOf).sum()).intValue();
                    prepareStatement.clearBatch();
                }
            }
            int[] results = prepareStatement.executeBatch();
            count += Long.valueOf(Arrays.stream(results).mapToLong(Integer::valueOf).sum()).intValue();
            prepareStatement.clearBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("upsert error message: {}", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                logger.error("Rollback error: {}", rollbackException);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error setting auto commit to true: {}", e);
            }
        }
        return count;
    }

    private void setStatementValue(PreparedStatement prepareStatement, int index, Object value) throws SQLException {
        if (value instanceof Date) {
            prepareStatement.setObject(index, value, Types.TIMESTAMP);
        } else if (value instanceof JSONArray) {
            prepareStatement.setObject(index, JSONObject.toJSONString(value));
        } else if (value instanceof JSONObject) {
            prepareStatement.setObject(index, JSONObject.toJSONString(value));
        } else {
            prepareStatement.setObject(index, value);
        }
    }

    /**
     * @param
     * @param jsonArray
     * @param tableName
     * @return
     * @return java.lang.Integer
     * @author twan
     * @date 13:51 2023/8/11
     **/
    public Integer insert(JSONArray jsonArray, String tableName) {
        Integer count = 0;
        Pair<String, List<String>> insertSql = getInsertSql(tableName);
        try {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareStatement(insertSql.getLeft());
            for (int i = 0; i < jsonArray.size(); i++) {
                List<String> columnList = insertSql.getValue();
                for (int j = 0; j < columnList.size(); j++) {
                    setStatementValue(prepareStatement, j + 1, jsonArray.getJSONObject(i).get(columnList.get(j)));
                }
                prepareStatement.addBatch();
                if ((i + 1) % 50 == 0) {
                    int[] results = prepareStatement.executeBatch();
                    count += Long.valueOf(Arrays.stream(results).mapToLong(Integer::valueOf).sum()).intValue();
                    prepareStatement.clearBatch();
                }
            }
            int[] results = prepareStatement.executeBatch();
            count += Long.valueOf(Arrays.stream(results).mapToLong(Integer::valueOf).sum()).intValue();

            prepareStatement.clearBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("upsert error message: {}", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                logger.error("Rollback error: {}", rollbackException);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error setting auto commit to true: {}", e);
            }
        }
        return count;

    }

    private Pair<String, List<String>> getInsertSql(String tableName) {
        StringBuffer sqlBuffer = new StringBuffer();
        List<String> columnList = getTableColumn(tableName).stream().filter(t -> {
            return !t.contains("ims");
        }).collect(Collectors.toList());
        sqlBuffer.append("INSERT INTO ").append(tableName).append(" ( ");
        IntStream.range(0, columnList.size()).forEach(t -> {
            sqlBuffer.append(columnList.get(t));
            if (t != columnList.size() - 1) {
                sqlBuffer.append(",");
            }
        });
        sqlBuffer.append(String.format(") VALUES(%s)", StringUtils.join(Stream.generate(() -> "?").limit(columnList.size()).collect(Collectors.toList()), ",")));
        return Pair.of(sqlBuffer.toString(), columnList);
    }

    public Integer upsert(JSONArray jsonArray, String tableName) {
        Triple<String, List<String>, List<String>> upsertSql = getUpsertSql(tableName);
        return upsert(jsonArray, upsertSql);
    }

    public <T> Integer upsert(List<T> list, String tableName) {
        Integer updateCount = 0;
        if (list.isEmpty()) {
            logger.error("list 不能为空");
            return updateCount;
        }
        JSONArray jsonArray = new JSONArray();
        //驼峰转下划线
        for (T t : list) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(t);
            JSONObject newObj = new JSONObject();
            for (String key : jsonObject.keySet()) {
                newObj.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key), jsonObject.get(key));
            }
            jsonArray.add(newObj);
        }
        return upsert(jsonArray, tableName);
    }


    public List<String> getTableColumn(String tableName) {
        //column_default -> nextval('token_id_seq'::regclass)
        return query(String.format(JdbcConstants.PGSQL_QUERY_ALL_COLUMN, tableName)).stream()
                .filter(t -> {
                    //过滤自增主键
                    return !(t.get("column_default") != null && t.get("column_default").toString().contains("nextval"));
                })
                .map(t -> t.get("column_name").toString()).collect(Collectors.toList());
    }

    public Triple<String, List<String>, List<String>> getUpsertSql(String tableName) {
        //查表主键
        List<Map<String, Object>> primaryKey = query(String.format(JdbcConstants.PGSQL_QUERY_PRIMARY_KEY, tableName));
        List<String> primaryKeyList = primaryKey.stream().map(t -> t.get("column_name").toString()).collect(Collectors.toList());
        if (primaryKeyList.isEmpty()) {
            List<String> indexDefinitionList = query(String.format(JdbcConstants.PGSQL_QUERY_UNION_INDEX, tableName)).stream().map(t -> t.get("index_definition").toString()).collect(Collectors.toList());
            if (!indexDefinitionList.isEmpty()) {
                String indexDescribe = indexDefinitionList.get(0);
                primaryKeyList = Arrays.asList(indexDescribe.substring(indexDescribe.indexOf("(") + 1, indexDescribe.indexOf(")")).split(","));
            }
        }
        if (primaryKeyList.isEmpty()) {
            assert primaryKeyList.isEmpty();
            logger.error("tableName:{} primaryKeyList is empty", tableName);
        }
        StringBuffer sqlBuffer = new StringBuffer();
        List<String> columnList = getTableColumn(tableName);
        sqlBuffer.append("INSERT INTO ").append(tableName).append(" ( ");
        IntStream.range(0, columnList.size()).forEach(t -> {
            sqlBuffer.append(columnList.get(t));
            if (t != columnList.size() - 1) {
                sqlBuffer.append(",");
            }
        });
        sqlBuffer.append(String.format(") VALUES(%s)", StringUtils.join(Stream.generate(() -> "?").limit(columnList.size()).collect(Collectors.toList()), ",")));
        sqlBuffer.append(String.format(" ON CONFLICT (%s) DO UPDATE  SET ", StringUtils.join(primaryKeyList, ",")));
        List<String> finalPrimaryKeyList = primaryKeyList;
        List<String> updateColumnList = columnList.stream().filter(t -> !finalPrimaryKeyList.contains(t) && !"ims_create_time".equals(t)).collect(Collectors.toList());
        IntStream.range(0, updateColumnList.size()).forEach(t -> {
            sqlBuffer.append(String.format("%s = ?", updateColumnList.get(t)));
            if (t != updateColumnList.size() - 1) {
                sqlBuffer.append(",");
            }
        });
        return Triple.of(sqlBuffer.toString(), columnList, updateColumnList);
    }

    /**
     * @param
     * @param tableName
     * @return <upsertSql,全部列，更新列>
     * @return org.apache.commons.lang3.tuple.Triple<java.lang.String, java.util.List < java.lang.String>,java.util.List<java.lang.String>>
     * @author twan
     * @date 9:56 2023/8/2
     **/
    public Triple<String, List<String>, List<String>> getUpsertSql(String tableName, Class clazz) {
        //优先按照unique index 查找
        //是否存在唯一索引如果存在请在对应的实体对象加UniqueIndex注解
        List<String> primaryKeyList = Lists.newArrayList();
        if (primaryKeyList.isEmpty()) {
            //查表主键
            List<Map<String, Object>> primaryKey = query(String.format(JdbcConstants.PGSQL_QUERY_PRIMARY_KEY, tableName));
            primaryKeyList = primaryKey.stream().map(t -> t.get("column_name").toString()).collect(Collectors.toList());
        }
        if (primaryKeyList.isEmpty()) {
            logger.error("不存在 主键|唯一索引 无法更新");
            return null;
        }
        StringBuffer sqlBuffer = new StringBuffer();
        List<String> columnList = getTableColumn(tableName);
        sqlBuffer.append("INSERT INTO ").append(tableName).append(" ( ");
        IntStream.range(0, columnList.size()).forEach(t -> {
            sqlBuffer.append(columnList.get(t));
            if (t != columnList.size() - 1) {
                sqlBuffer.append(",");
            }
        });
        sqlBuffer.append(String.format(") VALUES(%s)", StringUtils.join(Stream.generate(() -> "?").limit(columnList.size()).collect(Collectors.toList()), ",")));
        sqlBuffer.append(String.format(" ON CONFLICT (%s) DO UPDATE  SET ", StringUtils.join(primaryKeyList, ",")));
        List<String> finalPrimaryKeyList = primaryKeyList;
        List<String> updateColumnList = columnList.stream().filter(t -> !finalPrimaryKeyList.contains(t)).collect(Collectors.toList());
        IntStream.range(0, updateColumnList.size()).forEach(t -> {
            sqlBuffer.append(String.format("%s = ?", updateColumnList.get(t)));
            if (t != updateColumnList.size() - 1) {
                sqlBuffer.append(",");
            }
        });
        return Triple.of(sqlBuffer.toString(), columnList, updateColumnList);

    }

    public List<Map<String, Object>> query(String sql) {
        PreparedStatement pstmt = null;
        List<Map<String, Object>> resultList = Lists.newArrayList();
        try {
            pstmt = connection.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    //String newFieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, metaData.getColumnName(i + 1));
                    map.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
                }
                resultList.add(map);
            }
        } catch (Exception e) {
            logger.error("query error message:{}", e.getMessage());
        }
        return resultList;
    }

    public <T> List<T> queryBean(String sql, T bean) throws Exception {
        List<Map<String, Object>> listMap = query(sql);
        //如果包含下划线转驼峰
        List<Map<String, Object>> newList = Lists.newArrayList();
        for (Map<String, Object> map : listMap) {
            HashMap<String, Object> newMap = new HashMap<>();
            for (String key : map.keySet()) {
                if (key.contains("_")) {
                    newMap.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key), map.get(key));
                } else {
                    newMap.put(key, map.get(key));
                }
            }
            newList.add(newMap);
        }
        List<T> list = Lists.newArrayList();
        for (Map<String, Object> map : newList) {
            list.add(ReflectUtil.setFieldsValue((Class<T>) bean.getClass(), map));
        }
        return list;
    }

    public Integer upsertJsonArray(JSONObject data, String tableName) {
        Integer insertedCount = 0;
        List<String> columnList = getTableColumn(tableName);
        StringBuilder stringBuilder = new StringBuilder();

        List<String> valueColumnList = data.keySet().stream().map(t -> {
            String newKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, t);
            if (columnList.contains(newKey)) {
                return newKey;
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        stringBuilder.append(String.format("insert into %s (%s)", tableName, StringUtils.join(valueColumnList, ","), StringUtils.join(Stream.generate(() -> "?").collect(Collectors.toList()), ",")));
        try {
            PreparedStatement pstmt = connection.prepareStatement(stringBuilder.toString());
            for (int i = 1; i <= valueColumnList.size(); i++) {
                String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, valueColumnList.get(i - 1));
                pstmt.setObject(i, data.get(key));
            }
            insertedCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedCount;
    }

    /**
     * @param
     * @param sql
     * @param paramList 注意参数add应保持顺序
     * @return
     * @return java.lang.Integer
     * @author twan
     * @date 10:51 2023/8/10
     **/
    public Integer update(String sql, List<Object> paramList) {
        Integer count = 0;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < paramList.size(); i++) {
                setStatementValue(pstmt, i + 1, paramList.get(i));
            }
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public Integer update(String sql) {
        Integer count = 0;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}