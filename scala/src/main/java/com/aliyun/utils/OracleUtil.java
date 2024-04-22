package com.aliyun.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleUtil {
    private static final String username = "ggs";
    private static final String password = "sz7rYmv_6v";
    //数据库连接地址
    private static final String url = "jdbc:oracle:thin:@192.168.1.32:1521/emr";
    //private static final String url = "jdbc:oracle:thin:@hadoop101:1521/EE.oracle.docker";
//用户名
//    private static final String username = "sys";
//    //密码
//    private static final String password = "oracle";

    //驱动名称
    private static final String jdbcName = "oracle.jdbc.OracleDriver";

    /*获取数据库连接 */
    public static Connection getCon() {
        try {
            Class.forName(jdbcName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /*关闭数据库连接*/
    public static void closeCon(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }

    /*
    public static void main(String[] args){
        try {
            getCon();
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
        }
    }
    */

}