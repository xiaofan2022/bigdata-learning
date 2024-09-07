package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.{DataTypes, Schema, Table}

/**
 * @description ${description}
 * @author twan
 * @date 2024-05-23 18:26:15
 * @version 1.0
 */
object HudiCompactionTest {

  /**
   * 1,zs,11
   * 2,lisi,22
   *
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment(this.getClass.getSimpleName.dropRight(1), clusterName = "qidian")
    val tableEnv = StreamTableEnvironment.create(env)
    val sourceDataStream: DataStream[LogicDeleteStudent] = env.socketTextStream("hdp01", 1111).filter(t => {
      val strings: Array[String] = t.split(",")
      strings.length == 3 || strings.length == 4
    }).map(t => {
      val strings: Array[String] = t.split(",")
      LogicDeleteStudent(strings(0).toInt, strings(1), strings(2).toInt)
    })
    val schema: Schema = Schema.newBuilder()
      .column("id", DataTypes.INT())
      .column("name", DataTypes.STRING())
      .column("age", DataTypes.INT())
      .column("idDelete", DataTypes.INT())
      .build()
    val sourceTable: Table = tableEnv.fromDataStream(sourceDataStream, schema)
    tableEnv.createTemporaryView("sourceTable", sourceTable)
    val hudiCataLogName = "hudi_catalog"
    tableEnv.registerCatalog(hudiCataLogName, FlinkUtils.getHudiCatalog())
    tableEnv.useCatalog(hudiCataLogName)
    tableEnv.executeSql("create database  if not exists  test")
    tableEnv.useDatabase("test")
    tableEnv.executeSql("drop table  if exists hudi_compaction_test")
    tableEnv.executeSql(
      """
        |CREATE TABLE if not exists  hudi_compaction_test(
        |   id INT  PRIMARY KEY NOT ENFORCED,
        |        name STRING,
        |        age INT,
        |        idDelete INT
        |)
        |with(
        |'connector'='hudi',
        |'path'= 'hdfs:///warehouse/tablespace/managed/hive/test/hudi_compaction_test',
        |'hoodie.datasource.write.recordkey.field'= 'id',-- 主键
        |'table.type' = 'MERGE_ON_READ', --表类型
        |'compaction.async.enabled' = 'false', --开启在线压缩,数据量大的话，建议使用离线，否则会影响数据写入。
        |'compaction.trigger.strategy' = 'num_or_time',--可选择的策略有 num_commits：达到 N 个 delta commits 时触发 compaction; time_elapsed：距离上次 compaction 超过 N 秒触发 compaction ; num_and_time：NUM_COMMITS 和 TIME_ELAPSED 同时满足; num_or_time：NUM_COMMITS 或者 TIME_ELAPSED 中一个满足
        |'write.bucket_assign.tasks' = '1',--默认和write task保持一致，默认4
        |'write.tasks' = '1',--写入任务数量默认是4.
        |'hive_sync.skip_ro_suffix' = 'true',--ro表后缀
        |'compaction.delta_commits' = '2',--默认5次提交commits 触发一次压缩
        |'compaction.delta_seconds' = '60',--默认3600s触发一次压缩，1小时
        |'read.streaming.check-interval' = '4', -- 指定检查新的commit的周期，默认是60秒
        |'read.streaming.enabled'='true',--开启流读模式
        |'read.streaming.skip_compaction' = 'true',-- 避免重复消费问题
        |'read.tasks'='1'--读取任务的数量默认是4.
        |)""".stripMargin)
    tableEnv.executeSql("insert into hudi_catalog.test.hudi_compaction_test select * from default_catalog.default_database.sourceTable")


  }

}
