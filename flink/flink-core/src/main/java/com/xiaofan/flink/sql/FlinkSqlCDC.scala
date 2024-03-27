package com.xiaofan.flink.sql


import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 11:54:45
 * @version 1.0
 */
object FlinkSqlCDC {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_source_oracle (
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
        |        'database-name' = 'xe',
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'CUSTOMERS'
        |)
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery("select * from table_source_oracle")
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print()
    env.execute()


  }

}
