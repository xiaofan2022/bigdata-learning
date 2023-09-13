package com.xiaofan.flink

import com.xiaofan.flink.bean.Student901
import com.xiaofan.flink.utils.{CommonUtils, FlinkUtils, SinkUtils}
import com.xiaofan.utils.RandomNameUtils
import org.apache.flink.connector.jdbc.JdbcStatementBuilder
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{
  DataStream,
  StreamExecutionEnvironment,
  createTypeInformation
}

import java.sql.PreparedStatement
import java.time.Duration
import java.util.Random

/**
 * @author: twan
 * @date: 2023/9/1 11:11
 * @description:
 */
object MysqlGenerator {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = FlinkUtils.getSampleStreamTableEnvironment(
      "file://" + CommonUtils.getCurrentCKPath() + "/cdctest",
      Duration.ofSeconds(10).toMillis)
    val random = new Random()
    val list1: List[Student901] = 1
      .to(100)
      .map(t => {
        new Student901(RandomNameUtils.fullName(), random.nextInt(100))
      })
      .toList

    val generateSourceFunction: SourceFunction[Student901] =
      FlinkUtils.getCustomSource[Student901](() =>
        1.to(100)
          .map(t => {
            new Student901(RandomNameUtils.fullName(), random.nextInt(100))
          })
          .toList)
    val sourceDataStream: DataStream[Student901] = env.addSource(generateSourceFunction)
    val sql =
      """INSERT INTO `test`.`student`(, `name`, `age`) VALUES (?, ?)
        |""".stripMargin
    sourceDataStream.addSink(
      SinkUtils.getJDBCSink(
        sql,
        "test",
        new JdbcStatementBuilder[Student901]() {
          override def accept(preparedStatement: PreparedStatement, u: Student901) = {
            SinkUtils.createStatement(sql, u, preparedStatement)
          }
        }))
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
