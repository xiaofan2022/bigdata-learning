package com.aliyun.join

import com.aliyun.common.CommonConstants
import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description 本文展示regular join效果以left join为例
 *              1.右表有数据，左边没有数据不会触发数据发送
 *              2.左边有数据，右表没有数据右表内容填充null 发送
 *              3.如果表数据发生变更（join key）首先发送会测流然后发送更新流
 * @author twan
 * @date 2024-04-06 13:20:42
 * @version 1.0
 */
object RegularJoin {

  /**
   * 准备数据
   * student_info
   * {"id": 1, "name": "小张", "age": 22, "gender": "Male"}
   *
   * student_score
   * {"student_id": 1, "course_id": 11, "score": 88.5,"is_delete":0}
   * {"student_id": 1, "course_id": 11, "score": 88.5,"is_delete":1}
   * {"student_id": 1, "course_id": 11, "score": 20.5,"is_delete":1}
   *
   * course_info
   * {"course_id": 11, "course_name": "数学", "teacher": "王老师","is_delete":0}
   * 设置单个topic过期时间1min
   * kafka-configs.sh --bootstrap-server  $servers  --alter --entity-name student_info --entity-type topics --add-config retention.ms=60000
   */


  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    env.setParallelism(1)
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """CREATE TABLE student_info (
        |    id INT,
        |    name STRING,
        |    age INT,
        |    gender STRING
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'student_info',
        |     'properties.group.id'='test',
        |    'properties.bootstrap.servers' = '%s',
        |     'scan.startup.mode' = 'earliest-offset',
        |    'format' = 'json'
        |);""".stripMargin.format(CommonConstants.BOOTSTRAP_SERVERS))
    tableEnv.executeSql(
      """
        |CREATE TABLE student_score (
        |    student_id INT,
        |    course_id INT,
        |    score FLOAT,
        |    is_delete int
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'student_score',
        |     'properties.group.id'='test2',
        |    'properties.bootstrap.servers' = '%s',
        |     'scan.startup.mode' = 'earliest-offset',
        |    'format' = 'json'
        |);
        |""".stripMargin.format(CommonConstants.BOOTSTRAP_SERVERS))

    tableEnv.executeSql(
      """
        |CREATE TABLE course_info (
        |    course_id INT,
        |    course_name STRING,
        |    teacher STRING,
        |     is_delete int
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'course_info',
        |    'properties.group.id'='test3',
        |    'properties.bootstrap.servers' = '%s',
        |     'scan.startup.mode' = 'earliest-offset',
        |    'format' = 'json'
        |);""".stripMargin.format(CommonConstants.BOOTSTRAP_SERVERS))
    val tableResult: Table = tableEnv.sqlQuery(
      """
        |SELECT s.*,sc.*,c.*
        |FROM student_info AS s
        |left JOIN student_score AS sc ON s.id = sc.student_id
        |left JOIN course_info AS c ON sc.course_id = c.course_id
        |""".stripMargin)
    tableEnv.toChangelogStream(tableResult).print("result>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))

  }

}
