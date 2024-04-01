package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-30 21:10:32
 * @version 1.0
 */
object HudiQuery {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE source_hudi (
             customer_name STRING,
        |    email string,
        |    phone string,
        |    test_time timestamp,
        |        insert_date timestamp,
        |
        |   dt string
        |)
        |PARTITIONED BY (dt)
        |WITH (
        |'connector' = 'hudi',
        |'read.utc-timezone'='false',
        |'path' = 'hdfs://hadoop101:9000/warehouse/hudi/hudi_constomer'
        |);
        |""".stripMargin)
    val table: Table = tableEnv.sqlQuery("select * from source_hudi")
    tableEnv.toChangelogStream(table).print()
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
