package com.aliyun.cdc

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.slf4j.LoggerFactory

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 11:54:45
 * @version 1.0
 */
object OracleCDCDemo {

  def main(args: Array[String]): Unit = {
    val LOG = LoggerFactory.getLogger(OracleCDCDemo.getClass) // 这个类填自己的类名
    LOG.info("---------------------------")

    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("flink_sql_cde")
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
        |        'database-name' = 'XE',
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'CUSTOMERS',
        |         'debezium.log.mining.strategy' = 'online_catalog',
        |        'debezium.log.mining.continuous.mine' = 'true'
        |)
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery("select * from table_source_oracle")
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print()
    env.execute()


  }

}
