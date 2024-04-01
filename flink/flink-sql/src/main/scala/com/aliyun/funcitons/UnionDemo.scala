package com.aliyun.funcitons

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-25 18:47:05
 * @version 1.0
 */
object UnionDemo {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    //union(env,tableEnv)
    unionALl(env, tableEnv)
    env.execute()
  }

  def unionALl(env: StreamExecutionEnvironment, tableEnv: StreamTableEnvironment) = {
    // 模拟数据流1（包含 Date 类型的时间字段）
    val dataStream1: DataStream[(String, String, java.sql.Date)] = env.fromCollection(Seq(
      ("1", "张三", java.sql.Date.valueOf("2022-03-25")),
      ("2", "李四", java.sql.Date.valueOf("2020-03-10")),
      ("4", "赵六", java.sql.Date.valueOf("2024-03-25"))
    ))

    // 模拟数据流2（包含 Timestamp 类型的时间字段）
    val dataStream2: DataStream[(String, String, java.sql.Timestamp)] = env.fromCollection(Seq(
      ("1", "张三", java.sql.Timestamp.valueOf("2022-03-25 10:30:00")),
      ("2", "李四", java.sql.Timestamp.valueOf("2020-03-10 11:45:00")),
      ("4", "赵六", java.sql.Timestamp.valueOf("2024-03-25 21:45:00"))
    ))
    val inputTable1 = tableEnv.fromDataStream(dataStream1).as("id", "name", "date_column")
    val inputTable2 = tableEnv.fromDataStream(dataStream2).as("id", "name", "timestamp_column")
    tableEnv.createTemporaryView("source_table1", inputTable1)
    tableEnv.createTemporaryView("source_table2", inputTable2)
    val resultTable: Table = tableEnv.sqlQuery(
      """
        |select * from source_table1 union all select * from source_table2
        |""".stripMargin)
    val resultStream = tableEnv.toChangelogStream(resultTable)
    resultStream.print()
  }

  def union(env: StreamExecutionEnvironment, tableEnv: StreamTableEnvironment): Unit = {
    val dataStream1: DataStream[(String, String, String)] = env.fromElements(
      ("1", "张三", "2022-03-25 10:30:00"),
      ("2", "李四", "2021-03-25 11:45:00"),
      ("3", "王五", "2024-03-25 11:45:00")
    )
    val dataStream2: DataStream[(String, String, String)] = env.fromElements(
      ("1", "张三", "2022-03-25 10:30:00"),
      ("2", "李四", "2020-03-10 11:45:00"),
      ("4", "赵六", "2024-03-25 21:45:00")
    )
    val inputTable1 = tableEnv.fromDataStream(dataStream1).as("id", "time_str")
    val inputTable2 = tableEnv.fromDataStream(dataStream2).as("id", "time_str")
    tableEnv.createTemporaryView("source_table1", inputTable1)
    tableEnv.createTemporaryView("source_table2", inputTable2)
    val resultTable = tableEnv.sqlQuery(
      """
      select * from source_table1
        | union
      select * from source_table2
        |
        |""".stripMargin)
    val resultStream = tableEnv.toChangelogStream(resultTable)
    resultStream.print()
  }

}
