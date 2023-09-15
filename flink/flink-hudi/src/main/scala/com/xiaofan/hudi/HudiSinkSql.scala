package com.xiaofan.hudi

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @author: twan
 * @date: 2023/9/14 16:28
 * @description:
 */
object HudiSinkSql {

  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    //conf.setBoolean("execution.sorted-inputs.enabled", false)
    //conf.setBoolean("execution.batch-state-backend.enabled", false)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // datagen source 随机生成数据
    val sourceDDL =
      """
        |create table if not exists datagen_source (
        | id int,
        | data string,
        | ts as localtimestamp,
        | watermark for ts as ts
        |) with (
        | 'connector' = 'datagen',
        | 'rows-per-second'='10',
        | 'fields.id.kind'='sequence',
        | 'fields.id.start'='1',
        | 'fields.id.end'='100000',
        | 'fields.data.length'='5'
        |)
        |""".stripMargin

    val tableEnvSettings = EnvironmentSettings
      .newInstance()
      .inStreamingMode()
      .build()
    val tableEnv: StreamTableEnvironment =
      StreamTableEnvironment.create(env, tableEnvSettings)
    tableEnv.executeSql(sourceDDL)
    tableEnv.executeSql(sourceDDL)
    //1. 建表，写表
    val writeTableDDL =
      """
        |create table if not exists hudi_test_table (
        | id int,
        | data string,
        | ts timestamp(3),
        | `time` string,
        | `date` string
        |) partitioned by (`date`)
        | with (
        |  'connector' = 'hudi',
        |  'table.type' = 'MERGE_ON_READ',
        |  'path' = 'hdfs:///data//hudi/hudi_test_table',
        |  'write.tasks' = '1',
        |  'hoodie.datasource.write.recordkey.field' = 'id',
        |  'write.precombine.field' = 'ts',
        |  'compaction.tasks' = '1',
        |  'compaction.trigger.strategy' = 'num_or_time',
        |  'compaction.delta_commits' = '2',
        |  'compaction.delta_seconds' = '300'
        |)
        |""".stripMargin
    tableEnv.executeSql(writeTableDDL)
    //3. source 数据写入 hudi
    val insertDML =
      """
        |insert into hudi_test_table
        |select
        | id,
        | data,
        | ts,
        | date_format(ts, 'yyyy-MM-dd HH:mm:ss') as `time`,
        | date_format(ts, 'yyyy-MM-dd') as `date`
        |from datagen_source
        |""".stripMargin
    tableEnv.executeSql(insertDML)

  }

}
