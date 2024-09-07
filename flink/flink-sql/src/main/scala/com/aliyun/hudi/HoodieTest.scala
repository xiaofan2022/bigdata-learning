package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-06-09 23:16:41
 * @version 1.0
 */
object HoodieTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment(this.getClass.getSimpleName.dropRight(1), clusterName = "qidian")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE CATALOG myhive WITH (
        |    'type' = 'hive',
        |    'default-database' = 'default',
        |    'hive-conf-dir' = 'D:\\develop\\workspace\\bigdata-learning\\flink\\flink-sql\\target\\classes\\hive'
        |)
        |""".stripMargin)
    tableEnv.executeSql("USE CATALOG myhive")
    tableEnv.toChangelogStream(tableEnv.sqlQuery("show databases")).print("result>>>")

    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
