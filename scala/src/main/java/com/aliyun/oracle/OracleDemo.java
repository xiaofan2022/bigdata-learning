package com.aliyun.oracle;

import com.aliyun.utils.OracleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleDemo {

    //连接对象
    Connection connection = null;
    //创建预编译对象
    PreparedStatement ps = null;
    //创建结果集
    ResultSet rs = null;

    public static void main(String[] args) {
        OracleDemo od = new OracleDemo();
        //int add = od.add();
        od.select();
//        for (int i = 0; i < 1000; i++) {
//            int num = new Random(100000).nextInt();
//            Student student = new Student(i, i + "");
//            od.insert(student);
//        }
//        System.out.println(od.delete());
    }

    /*插入*/
    public int insert(Student student) {
        int result = 0;
        connection = OracleUtil.getCon();
        String sql = "insert into customers ";
        sql = sql + " (customer_id,customer_name,email,phone) ";
        sql = sql + " values(?,?,?,? )";
        try {
            ps = connection.prepareStatement(sql);
//            ps.setInt(1,customer.customerId);
//            ps.setString(2,customer.customerName);
//            ps.setString(3,customer.email);
//            ps.setString(4,customer.phone);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                OracleUtil.closeCon(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*查询*/
    public void select() {
        connection = OracleUtil.getCon();
        String sql = "select * from student";
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                System.out.println("id:" + id + " name:" + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                OracleUtil.closeCon(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Student {
        private final Integer id;
        private final String name;

        public Student(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}