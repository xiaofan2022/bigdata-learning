package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.time.Duration

/**
 * @description ${description}
 * @author twan
 * @date 2024-07-10 10:42:53
 * @version 1.0
 */
object MockHudi {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment(this.getClass.getSimpleName.dropRight(1), checkPointDuration = Duration.ofSeconds(10), clusterName = "qidian")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE MockOrders (
        |              id INT,
        |              user_id        BIGINT,
        |              total_amount   DOUBLE,
        |              create_time   TIMESTAMP(3)
        |          ) WITH (
        |            'connector' = 'datagen',
        |            'rows-per-second'='200000',
        |            'fields.id.kind'='random' ,
        |            'fields.id.min'='1',
        |            'fields.id.max'='1000000',
        |            'fields.user_id.kind'='random',
        |            'fields.user_id.min'='1',
        |            'fields.user_id.max'='100000000',
        |            'fields.total_amount.kind'='random',
        |            'fields.total_amount.min'='1',
        |            'fields.total_amount.max'='100000000'
        |          )""".stripMargin)

    val hudiCataLogName = "hudi_catalog"
    tableEnv.registerCatalog(hudiCataLogName, FlinkUtils.getHudiCatalog())
    tableEnv.useCatalog(hudiCataLogName)
    tableEnv.executeSql("create database  if not exists  test")
    tableEnv.useDatabase("test")
    tableEnv.executeSql("drop table  if exists hudi_mock_orders")
    tableEnv.executeSql(
      """
        |CREATE TABLE if not exists  hudi_mock_orders(
                    id INT,
        |              user_id        BIGINT,
        |              total_amount   DOUBLE,
        |              create_time   TIMESTAMP(3)
        |)
        |with(
        |'connector'='hudi',
        |'path'= 'hdfs://nameservice1/warehouse/tablespace/external/hive/hudi_mock_orders',
        |'hoodie.datasource.write.recordkey.field'= 'id',-- 主键
        |'table.type' = 'MERGE_ON_READ', --表类型
        |'compaction.async.enabled' = 'true', --开启在线压缩,数据量大的话，建议使用离线，否则会影响数据写入。
        |'compaction.trigger.strategy' = 'num_or_time',--可选择的策略有 num_commits：达到 N 个 delta commits 时触发 compaction; time_elapsed：距离上次 compaction 超过 N 秒触发 compaction ; num_and_time：NUM_COMMITS 和 TIME_ELAPSED 同时满足; num_or_time：NUM_COMMITS 或者 TIME_ELAPSED 中一个满足
        |'write.bucket_assign.tasks' = '1',--默认和write task保持一致，默认4
        |'write.tasks' = '1',--写入任务数量默认是4
        |'read.utc-timezone'='false',
        |'compaction.delta_commits' = '2',--默认5次提交commits 触发一次压缩
        |'compaction.delta_seconds' = '60',--默认3600s触发一次压缩，1小时
        |'read.streaming.check-interval' = '4', -- 指定检查新的commit的周期，默认是60秒
        |'read.streaming.enabled'='true',--开启流读模式
        |'read.streaming.skip_compaction' = 'true',-- 避免重复消费问题
        |'read.tasks'='1'--读取任务的数量默认是4.
        |)""".stripMargin)
    tableEnv.executeSql("insert into hudi_catalog.test.hudi_mock_orders select * from default_catalog.default_database.MockOrders")

  }

}
