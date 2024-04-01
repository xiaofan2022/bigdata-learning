package com.aliyun.cdc

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 17:49:16
 * @version 1.0
 */
object OracleCDC2Kafka {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_cdc")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_customers (
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
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'CUSTOMERS',
        |        'debezium.log.mining.strategy' = 'online_catalog',
        |        'debezium.log.mining.continuous.mine' = 'true'
        |)
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_customers_kafka (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME TIMESTAMP
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'flink_cdc_table_customers',
        |    'properties.bootstrap.servers' = 'hadoop101:9092,hadoop102:9092,hadoop103:9092',
        |    'format' = 'debezium-json',
        |    'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    tableEnv.executeSql("insert into table_customers_kafka select * from table_customers  ")

    //env.execute()
  }

}
