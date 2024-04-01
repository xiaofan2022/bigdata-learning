package com.aliyun.funcitons

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.types.Row

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-25 18:47:05
 * @version 1.0
 */
object OtherDemo {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    val config = new Configuration();
    //config.setString("table.local-time-zone", "Asia/Shanghai");
    //tableEnv.getConfig.set("table.local-time-zone", "Asia/Shanghai")
    tableEnv.getConfig.set("table.local-time-zone", "UTC")
    val table: Table = tableEnv.sqlQuery(" SELECT   CURRENT_TIMESTAMP, PROCTIME()")
    val dataStream: DataStream[Row] = tableEnv.toChangelogStream(table)
    dataStream.print("result>>>>")
    env.execute()
  }


}
