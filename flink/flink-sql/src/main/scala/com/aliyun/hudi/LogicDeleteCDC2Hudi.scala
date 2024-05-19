package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.hudi.table.catalog.HoodieCatalog

/**
 * @description ${description}
 * @author twan
 * @date 2024-05-11 16:58:37
 * @version 1.0
 */
object LogicDeleteCDC2Hudi {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE if not exists table_customers (
        |        CUSTOMER_ID INT,
        |        CUSTOMER_NAME STRING,
        |        EMAIL STRING,
        |        PHONE STRING,
        |        TEST_TIME TIMESTAMP,
        |        PRIMARY KEY(CUSTOMER_ID) NOT ENFORCED
        |        )
        |WITH (
        |        'connector' = 'oracle-cdc',
        |        'hostname' = 'hdp05',
        |        'port' = '1521',
        |        'username' = 'flinkuser',
        |        'password' = 'flinkpw',
        |        'database-name' = 'XE',
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'CUSTOMERS',
        |        'debezium.log.mining.strategy' = 'online_catalog',
        |        'debezium.log.mining.continuous.mine' = 'true'
        |)
        |""".stripMargin)
    val configuration = new Configuration()
    configuration.setString("type", "hudi")
    configuration.setString("mode", "hms")
    configuration.setString("default-database", "default")
    configuration.setString("hive.conf.dir", "D://develop//data//hive_conf")
    configuration.setString("catalog.path", "/hudi_catalog")

    /**
     * CREATE CATALOG hudi_catalog WITH (
     * 'type' = 'hudi',
     * 'mode' = 'hms',
     * 'default-database' = 'default',
     * 'hive.conf.dir' = 'D://develop//data//hive_conf'
     * );
     */
    val hudiCataLogName = "hudi_catalog"
    val hudiCatalog = new HoodieCatalog(hudiCataLogName, configuration)
    tableEnv.registerCatalog(hudiCataLogName, hudiCatalog)
    tableEnv.useCatalog(hudiCataLogName)
    tableEnv.executeSql("create database  if not exists  test")
    tableEnv.executeSql("use test")
    tableEnv.executeSql(
      """
        |CREATE TABLE if not exists  sink_hudi(
        |   CUSTOMER_ID INT  PRIMARY KEY NOT ENFORCED,
        |        CUSTOMER_NAME STRING,
        |        EMAIL STRING,
        |        PHONE STRING,
        |        TEST_TIME TIMESTAMP
        |)
        |with(
        |'connector'='hudi',
        |'path'= 'hdfs:///user/hive/warehouse/test/hudi_logic',
        |'hoodie.datasource.write.recordkey.field'= 'CUSTOMER_ID',-- 主键
        |'hoodie.allow.operation.metadata.field'='true',
        |'changelog.enabled'='true',
        |'table.type' = 'MERGE_ON_READ', --表类型
        |'compaction.async.enabled' = 'true', --开启在线压缩,数据量大的话，建议使用离线，否则会影响数据写入。
        |'compaction.trigger.strategy' = 'num_or_time',--可选择的策略有 num_commits：达到 N 个 delta commits 时触发 compaction; time_elapsed：距离上次 compaction 超过 N 秒触发 compaction ; num_and_time：NUM_COMMITS 和 TIME_ELAPSED 同时满足; num_or_time：NUM_COMMITS 或者 TIME_ELAPSED 中一个满足
        |'write.bucket_assign.tasks' = '1',--默认和write task保持一致，默认4
        |'write.tasks' = '1',--写入任务数量默认是4.
        |'hive_sync.skip_ro_suffix' = 'true',--ro表后缀
        |'compaction.delta_commits' = '2',--默认5次提交commits 触发一次压缩
        |'compaction.delta_seconds' = '60',--默认3600s触发一次压缩，1小时
        |'read.streaming.check-interval' = '4', -- 指定检查新的commit的周期，默认是60秒
        |'read.streaming.enabled'='true',--开启流读模式
        |'read.streaming.skip_compaction' = 'true',-- 避免重复消费问题
        |'read.tasks'='1',--读取任务的数量默认是4.
        |'hive_sync.enable'='true',
        |'hive_sync.table'='sink_hudi',
        |'hive_sync.db'='test',
        |'hive_sync.mode' = 'hms',
        |'hive_sync.metastore.uris' = 'thrift://hadoop2:9083',
        |'changelog.enabled'='true',
        |'hive_sync.support_timestamp'='true'
        |)""".stripMargin)
    tableEnv.executeSql("insert into hudi_catalog.test.sink_hudi select * from default_catalog.default_database.table_customers")

  }

}
