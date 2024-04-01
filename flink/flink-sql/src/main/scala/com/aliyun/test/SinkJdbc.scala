package com.aliyun.test

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-01 16:33:26
 * @version 1.0
 */
object SinkJdbc {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    env.getCheckpointConfig.setCheckpointStorage("file:///D://data//checkpoint//flink")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE jdbc_customers (
        |    customer_id int,
        |    customer_name string,
        |    email string,
        |    phone string,
        |    test_time timestamp,
        |    insert_date timestamp
        |) WITH (
        |    'connector' = 'jdbc',
        |    'url' = 'jdbc:oracle:thin:@hdp05:1521:xe',
        |    'driver' = 'oracle.jdbc.driver.OracleDriver',
        |    'username' = 'flinkuser',
        |    'password' = 'flinkpw',
        |    'table-name' = 'CUSTOMERS'
        |);
        |""".stripMargin)
    tableEnv.executeSql(
      """
       INSERT INTO jdbc_customers
        |(customer_id, customer_name, email, phone, insert_date)
        |VALUES
        |(7, 'John Doe', 'john@example.com', '1234567890', TIMESTAMP '2024-03-28 12:00:00');
        |
        |""".stripMargin)
  }

}
