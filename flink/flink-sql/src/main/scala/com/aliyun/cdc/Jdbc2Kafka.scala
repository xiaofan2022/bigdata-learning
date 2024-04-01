package com.aliyun.cdc

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 21:43:05
 * @version 1.0
 */
object Jdbc2Kafka {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("jdbc_kafka")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE jdbc_customers (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME TIMESTAMP
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
        |CREATE TABLE sink_customers_kafka (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME TIMESTAMP
        |) WITH (
        |'connector' = 'kafka',
        |'topic' = 'flink_jdbc_customers',
        |'properties.bootstrap.servers' = 'hadoop101:9092,hadoop102:9092,hadoop103:9092',
        |'format' = 'json',
        |'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |insert into  sink_customers_kafka  select  * from jdbc_customers
        |""".stripMargin)

    //env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
