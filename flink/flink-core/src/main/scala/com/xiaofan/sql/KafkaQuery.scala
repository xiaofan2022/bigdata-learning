package com.xiaofan.sql

import com.xiaofan.flink.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 21:32:57
 * @version 1.0
 */
object KafkaQuery {


  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("kafka_query")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    //debezium cdc 查询
    tableEnv.executeSql(
      """
        |create table debezium_customers_kafka (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME STRING
        |) with (
        |    'connector' = 'kafka',
        |    'topic' = 'flink_cdc_table_customers',
        |    'properties.bootstrap.servers' = 'hadoop101:9092,hadoop102:9092,hadoop103:9092',
        |    'properties.group.id' = 'bigdata',
        |    'format' = 'debezium-json',
        |    'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    //普通json kafka
    tableEnv.executeSql(
      """
        |CREATE TABLE common_customers_kafka (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME STRING
        |) WITH (
        |'connector' = 'kafka',
        |'topic' = 'flink_jdbc_customers',
        |'properties.bootstrap.servers' = 'hadoop101:9092,hadoop102:9092,hadoop103:9092',
        |'properties.group.id' = 'bigdata1',
        |'format' = 'json',
        |'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    val resultTable: Table = tableEnv.sqlQuery(
      """
        |select * from debezium_customers_kafka
        | union
        | select * from common_customers_kafka
        |""".stripMargin)
    val resultStream = tableEnv.toChangelogStream(resultTable)

    resultStream.print()
    env.execute(this.getClass.getSimpleName.dropRight(1))


  }


}
