package com.aliyun.test

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-15 10:06:03
 * @version 1.0
 */
object SqlTest {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("test")

    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.getConfig.set("table.local-time-zone", "UTC")
    val tableResult: Table = tableEnv.sqlQuery("SELECT current_timestamp")
    tableEnv.toChangelogStream(tableResult).print("result>>>")
    env.execute(this.getClass.getSimpleName.drop(1))
  }

}
