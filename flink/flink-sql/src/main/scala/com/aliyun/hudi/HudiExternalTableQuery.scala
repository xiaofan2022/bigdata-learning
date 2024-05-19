package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.hudi.table.catalog.HoodieCatalog

/**
 * @description ${description}
 * @author twan
 * @date 2024-05-12 21:34:13
 * @version 1.0
 */
object HudiExternalTableQuery {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("query_hudi_external")
    val tableEnv = StreamTableEnvironment.create(env)
    val hoodieCatalog: HoodieCatalog = FlinkUtils.getHudiCatalog()
    tableEnv.registerCatalog(hoodieCatalog.getName, hoodieCatalog)
    tableEnv.useCatalog(hoodieCatalog.getName)
    tableEnv.executeSql("create database  if not exists  test")
    tableEnv.executeSql("use test")
    val table: Table = tableEnv.sqlQuery(
      """
        |select * from  hudi_catalog.test.sink_hudi
        |""".stripMargin)
    table.printSchema()
    tableEnv.toChangelogStream(tableEnv.sqlQuery(
      """
        |select * from  hudi_catalog.test.sink_hudi
        |""".stripMargin)).print("result>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))


  }

}
