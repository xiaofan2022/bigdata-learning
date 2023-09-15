package com.xiaofan.hudi

import com.xiaofan.bean.Student901
import com.xiaofan.constants.DevelopModel
import com.xiaofan.utils._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.{EnvironmentSettings, SqlDialect}

import java.time.Duration

/**
 * @author: twan
 * @date: 2023/9/13 10:51
 * @description:
 */
object SampleSinkHive {

  def main(args: Array[String]): Unit = {
    val ckPath = "file://%s/flink2hive".format(CommonUtils.getCurrentCKPath())
    val env: StreamExecutionEnvironment =
      FlinkUtils.getSampleStreamTableEnvironment(ckPath, Duration.ofMinutes(10).toMillis)
    val sourceDataStream: DataStream[Student901] = env
      .addSource(
        FlinkUtils.getCustomSource[Student901](() =>
          1.to(1000)
            .map(index => {
              val student90 = new Student901(index, RandomNameUtils.fullName(), index + 3)
              student90.setEventTime(DateUtils.getCurrentTimeStamp)
              student90
            })
            .toList, Duration.ofSeconds(1).toMillis))
    val tableEnvSettings = EnvironmentSettings
      .newInstance()
      .inStreamingMode()
      .build()
    val tableEnv: StreamTableEnvironment =
      StreamTableEnvironment.create(env, tableEnvSettings)
    tableEnv.registerCatalog("myHive", FlinkTableUtils.getHiveCatalog(DevelopModel.COMPANY))
    tableEnv.useCatalog("myHive")
    tableEnv.useDatabase("test")
    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)

    tableEnv.executeSql(
      """
        |CREATE TABLE if not exists  student
        |(
        |    id        INT,
        |    name      STRING,
        |    age       INT,
        |    eventTime BIGINT
        |) PARTITIONED BY (dt STRING)  TBLPROPERTIES (
        |    'partition.time-extractor.timestamp-pattern' = '$dt',
        |    'auto-compaction' = 'true',
        |    'sink.semantic' = 'exactly-once',
        |    'sink.rolling-policy.file-size' = '256MB',
        |   'sink.partition-commit.trigger'='partition-time',
        |    'sink.partition-commit.delay'='1 h',
        |    'sink.partition-commit.watermark-time-zone' = 'Asia/Shanghai',
        |    'sink.shuffle-by-partition.enable' = 'true',
        |    'sink.partition-commit.policy.kind' = 'metastore,success-file')
        |""".stripMargin)
    tableEnv.getConfig.setSqlDialect(SqlDialect.DEFAULT)
    tableEnv.createTemporaryView("sourceTable", sourceDataStream)

    tableEnv.executeSql(
      """
        |insert into student  select id,name,age,eventTime eventtime,from_unixtime(eventTime, 'yyyy-MM-dd')  from sourceTable
        |""".stripMargin)

    //val resultDataStream = tableEnv.toAppendStream[Row](tableEnv.sqlQuery("select * from sourceTable"))
    //resultDataStream.print("result>>>")
    tableEnv
  }

}
