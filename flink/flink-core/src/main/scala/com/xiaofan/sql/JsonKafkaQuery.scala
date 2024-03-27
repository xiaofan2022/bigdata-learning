package com.xiaofan.sql

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 21:32:57
 * @version 1.0
 */
object JsonKafkaQuery {


  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    queryCDC(env, tableEnv)
    env.execute(this.getClass.getSimpleName.dropRight(1))
    //queryCommon(env,tableEnv)

  }

  def queryCDC(env: StreamExecutionEnvironment, tableEnv: StreamTableEnvironment): Unit = {
    tableEnv.executeSql(
      """
        CREATE TABLE table_customers_kafka (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME STRING
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'flink_cdc_table_customers',
        |    'properties.bootstrap.servers' = 'hadoop101:9092,hadoop102:9092,hadoop103:9092',
        |    'properties.group.id' = 'bigdata',
        |    'format' = 'debezium-json',
        |    'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery(
      """
        |select * from table_customers_kafka
        |""".stripMargin)
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print()
  }


}
