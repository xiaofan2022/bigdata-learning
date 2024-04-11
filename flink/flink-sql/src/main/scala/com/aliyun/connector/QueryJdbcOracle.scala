package com.aliyun.connector

import com.aliyun.common.CommonConstants
import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-06 21:19:01
 * @version 1.0
 */
object QueryKafka {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    env.setParallelism(1)
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE student_score (
         `partition` BIGINT METADATA VIRTUAL,
        |  `offset` BIGINT METADATA VIRTUAL,
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
    val table: Table = tableEnv.sqlQuery("select * from student_score ")
    tableEnv.toChangelogStream(table).print("result>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))


  }

}
