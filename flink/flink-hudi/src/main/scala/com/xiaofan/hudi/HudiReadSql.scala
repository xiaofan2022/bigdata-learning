package com.xiaofan.hudi

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @author: twan
 * @date: 2023/9/14 18:18
 * @description:
 */
object HudiReadSql {

  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    //conf.setBoolean("execution.sorted-inputs.enabled", false)
    //conf.setBoolean("execution.batch-state-backend.enabled", false)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnvSettings = EnvironmentSettings
      .newInstance()
      .inStreamingMode()
      .build()
    val tableEnv: StreamTableEnvironment =
      StreamTableEnvironment.create(env, tableEnvSettings)
    tableEnv.executeSql(
      """
        |select * from hudi_test_table
        |""".stripMargin).print()
  }

}
