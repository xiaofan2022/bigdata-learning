package com.aliyun.funcitons

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


/**
 * @description ${description}
 * @author twan
 * @date 2024-04-11 20:56:27
 * @version 1.0
 */
object RowNumber {

  //张三,dept1,login,首次登录,2023-10-01
  //李四,dept2,login,首次登录,2023-10-11
  //张三,dept1,login,修改密码,2023-10-12
  //张三,dept1,buy,buy apple,2023-10-12
  //张三,dept1,buy,buy iphone,2023-10-17
  //张三,dept1,login,重新登录,2024-01-12
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    val config = new Configuration()
    val sourceDataStream: DataStream[String] = env.socketTextStream("hadoop101", 8080)
    val eventDataStream: DataStream[MyEvent] = sourceDataStream.map(t => {
      val strings: Array[String] = t.split(",")
      MyEvent(strings(0), strings(1), strings(2), strings(3), strings(4))
    })
    val inputTable = tableEnv.fromDataStream(eventDataStream).as("name", "dept", "type_name", "desc", "create_time")
    tableEnv.createTemporaryView("source_table", inputTable)
    tableEnv.executeSql(
      """
        |create view temp_view as
        |select *,row_number() over(partition by dept,type_name order by create_time desc) rn  from source_table
        |""".stripMargin)
    val resultTable: Table = tableEnv.sqlQuery(
      """
        |select * from temp_view  where rn<=1
        |""".stripMargin)
    tableEnv.toChangelogStream(resultTable).print("result>>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

  case class MyEvent(name: String, dept: String, typeName: String, desc: String, createTime: String)
}
