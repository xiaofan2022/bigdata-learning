package com.aliyun

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
object MockDemo {

  def main(args: Array[String]): Unit = {
    val LOG = LoggerFactory.getLogger(MockDemo.getClass) // 这个类填自己的类名
    LOG.info("---------------------------")

    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("mock_test")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    // 'debezium.log.mining.continuous.mine' = 'true'
    //  'debezium.log.mining.strategy' = 'online_catalog',
    tableEnv.executeSql(
      """
        |CREATE TABLE dataGenSourceTable
        | (
        | order_number BIGINT,
        | price DECIMAL(10, 2),
        | buyer STRING,
        | order_time TIMESTAMP(3)
        | )
        |WITH
        | ( 'connector'='datagen',
        | 'number-of-rows'='100000000',
        | 'rows-per-second' = '100000'
        | )
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery("select * from dataGenSourceTable")
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print("result>>>>>")
    env.execute()


  }

}
