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
object TimeDemo {

  def main(args: Array[String]): Unit = {


    // create environments of both APIs
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    // create a DataStream
    val dataStream: DataStream[(String, String)] = env.fromElements(
      ("1", "2022-03-25 10:30:00"),
      ("2", "2021-03-25 11:45:00"),
      ("3", "2024-03-25 11:45:00")
    )
    // interpret the insert-only DataStream as a Table
    val inputTable = tableEnv.fromDataStream(dataStream).as("id", "time_str")

    // register the Table object as a view and query it
    // the query contains an aggregation that produces updates
    tableEnv.createTemporaryView("source_table", inputTable)
    //  ---CAST(time_str AS TIMESTAMP) AS timestamp1,
    /*    val resultTable = tableEnv.sqlQuery(
      """
        |select id,
        | TO_TIMESTAMP(time_str, 'yyyy-MM-dd hh:mm:ss') timestamp2,
        | time_str,
        | CHARACTER_LENGTH(time_str),
        |  CASE
        |           WHEN CHARACTER_LENGTH(time_str)>0  THEN TO_TIMESTAMP(time_str, 'yyyy-MM-dd hh:mm:ss')
        |           ELSE CURRENT_TIMESTAMP END

        | from source_table where time_str>CURRENT_DATE - INTERVAL '2' YEAR;
        |
        |""".stripMargin)*/

    val resultTable = tableEnv.sqlQuery(
      """
        |select * from source_table where id>1
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
