package com.aliyun.jdbc;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * @author twan
 * @version 1.0
 * @description
 * @date 2024-05-24 15:26:54
 */
public class KyuubiDemo {

    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";
    //private static String kyuubiJdbcUrl = "jdbc:kyuubi://hdp04:10009/default?auth=noSasl";
    private static final String kyuubiJdbcUrl = "jdbc:hive2://hdp02:2181,hdp03:2181,hdp04:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=kyuubi#kyuubi.engine.share.level=CONNECTION;hive.server2.proxy.user=anonymous;";

    public static void main(String[] args) throws SQLException, IOException {
        //System.setProperty("HADOOP_USER_NAME", "hdfs");
        Properties properties = new Properties();

        try (Connection conn = DriverManager.getConnection(kyuubiJdbcUrl, "hive", "")) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("select * from  test.hudi_customers1 limit 10")) {
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                    }
                }
            }
        }

    }
}
