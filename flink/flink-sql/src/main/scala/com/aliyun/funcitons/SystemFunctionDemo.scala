package com.aliyun.funcitons

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-25 18:47:05
 * @version 1.0
 */
object SystemFunctionDemo {

  def main(args: Array[String]): Unit = {


    // create environments of both APIs
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val dataStream: DataStream[(String, Int)] = env.fromElements(
      ("张三", 35),
      ("姓张", 24),
      ("我可张", 18),
      ("我可张哈", 18),
      ("哈哈", 18)
    )
    val inputTable = tableEnv.fromDataStream(dataStream).as("name", "age")
    tableEnv.createTemporaryView("source_table", inputTable)
    val resultTable = tableEnv.sqlQuery(
      """
        |select * ,LOCATE('张',name),INSTR(name,'张') from source_table where LOCATE('张',name)>0
        |""".stripMargin)
    val resultStream = tableEnv.toChangelogStream(resultTable)

    resultStream.print()
    env.execute()

    // prints:
    // +I[Alice, 12]
    // +I[Bob, 10]
    // -U[Alice, 12]
    // +U[Alice, 112]

  }

}
