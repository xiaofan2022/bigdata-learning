package com.xiaofan.constants;

/**
 * @author: twan
 * @date: 2023/8/1 18:00
 * @description:
 */
public class JdbcConstants {
    public static final String PGSQL_QUERY_UNION_INDEX = "SELECT indexname AS index_name,\n" +
            "       tablename AS table_name,\n" +
            "       indexdef AS index_definition\n" +
            "FROM pg_indexes\n" +
            "WHERE schemaname = 'public'\n" +
            "      AND tablename = '%s' ";

    public static final String PGSQL_QUERY_PRIMARY_KEY = "SELECT\n" +
            "    pg_attribute.attname AS column_name,\n" +
            "    format_type(pg_attribute.atttypid, pg_attribute.atttypmod) AS data_type\n" +
            "FROM\n" +
            "    pg_index,\n" +
            "    pg_class,\n" +
            "    pg_attribute\n" +
            "WHERE\n" +
            "    pg_class.oid = '%s'::regclass AND\n" +
            "    indrelid = pg_class.oid AND\n" +
            "    pg_attribute.attrelid = pg_class.oid AND\n" +
            "    pg_attribute.attnum = any(pg_index.indkey)\n" +
            "    AND indisprimary;";
    public static final String PGSQL_QUERY_ALL_COLUMN = "select * from information_schema.columns where table_schema='public' and table_name='%s'";
}
