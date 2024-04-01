package com.aliyun.cdc

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-30 17:45:45
 * @version 1.0
 */
object OracleOnLineUnionOffLine {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("flink_sql_cde")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE offline_table (
        |        CUSTOMER_ID INT,
        |        CUSTOMER_NAME STRING,
        |        EMAIL STRING,
        |        PHONE STRING,
        |        TEST_TIME TIMESTAMP
        |        )
        |WITH (
        |  'connector' = 'jdbc',
        |'url' = 'jdbc:oracle:thin:@hdp05:1521:xe',
        |'table-name' = 'CUSTOMERS',
        |'driver' = 'oracle.jdbc.driver.OracleDriver',
        |'username' = 'flinkuser',
        |'password' = 'flinkpw'
        |);
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |CREATE TABLE online_table (
        |        CUSTOMER_ID INT,
        |        CUSTOMER_NAME STRING,
        |        EMAIL STRING,
        |        PHONE STRING,
        |        TEST_TIME TIMESTAMP
        |        )
        |WITH (
        |        'connector' = 'oracle-cdc',
        |        'hostname' = 'hdp05',
        |        'port' = '1521',
        |        'username' = 'flinkuser',
        |        'password' = 'flinkpw',
        |        'database-name' = 'XE',
        |        'scan.startup.mode' = 'latest-offset',
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'CUSTOMERS',
        |        'debezium.log.mining.strategy' = 'online_catalog',
        |        'debezium.log.mining.continuous.mine' = 'true'
        |)
        |""".stripMargin)

    /** *
     * CREATE TABLE customers (
     * customer_id INT NOT NULL AUTO_INCREMENT,
     * customer_name VARCHAR(255) NOT NULL,
     * email VARCHAR(255) NOT NULL,
     * phone VARCHAR(20),
     * test_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     * PRIMARY KEY (customer_id)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
     */
    tableEnv.executeSql(
      """
        |CREATE TABLE sink_table (
        |        customer_id int,
        |        customer_name string,
        |        email string,
        |        phone string,
        |        test_time timestamp,
        |          PRIMARY KEY (customer_id) NOT ENFORCED
        |
        |        )
        |WITH (
        |  'connector' = 'jdbc',
        |'url' = 'jdbc:mysql://hadoop101:3306/test',
        |'table-name' = 'customers',
        |'driver' = 'com.mysql.cj.jdbc.Driver',
        |'username' = 'root',
        |'password' = 'root'
        |);
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |insert into sink_table
        |select * from offline_table union
        |select * from online_table
        |""".stripMargin)
    /*   val table: Table = tableEnv.sqlQuery(
         """
           |select * from offline_table union all
           |select * from online_table
           |""".stripMargin)
       val resultStream = tableEnv.toChangelogStream(table)*/
    //resultStream.print()
    //env.execute()
  }

}
