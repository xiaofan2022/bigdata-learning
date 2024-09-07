package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-09-03 14:38:31
 * @version 1.0
 */
object OracleCDC2Hudi {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment(this.getClass.getSimpleName.dropRight(1), clusterName = "qidian")
    env.setParallelism(1)
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_source_oracle (
        |         ID INT,
        |         NAME STRING,
        |         AGE INT,
        |         CREATE_TIME TIMESTAMP,
        |         UPDATE_TIME TIMESTAMP
        |         )
        | WITH (
        | 'connector' = 'oracle-cdc',
        | 'hostname' = 'hdp01',
        | 'port' = '1521',
        | 'username' = 'FLINKUSER',
        | 'password' = 'flinkpw',
        | 'database-name' = 'P19C',
        | 'schema-name' = 'FLINKUSER',
        | 'table-name' = 'TEST01',
        | --'debezium.log.strategy' = 'online_catalog',
        | 'debezium.decimal.handling.mode'='STRING'
        | )
        |""".stripMargin)
    val hudiCataLogName = "hudi_catalog"
    tableEnv.registerCatalog(hudiCataLogName, FlinkUtils.getHudiCatalog())
    tableEnv.useCatalog(hudiCataLogName)
    tableEnv.executeSql("create database  if not exists  test")
    tableEnv.useDatabase("test")
    tableEnv.executeSql(
      """
        |create table  if not exists flink_mor_test1 (
        |   ID INT PRIMARY KEY NOT ENFORCED,
        |         NAME STRING,
        |         AGE INT,
        |         CREATE_TIME TIMESTAMP,
        |         UPDATE_TIME TIMESTAMP
        |)
        |with (
        |  'connector' = 'hudi',
        |  'path' = 'hdfs:///warehouse/tablespace/external/hive/test/flink_mor_test1',
        |'write.precombine' = 'true',
        |'hoodie.datasource.write.recordkey.field' ='ID',
        |'compaction.trigger.strategy'='num_commits',
        |'compaction.delta_commits'= '1',
        |  'write.tasks'='4',
        |
        |  'table.type' = 'MERGE_ON_READ'
        |)""".stripMargin)
    tableEnv.executeSql(
      """
        |insert into hudi_catalog.test.flink_mor_test1
        |select * from default_catalog.default_database.table_source_oracle
        |""".stripMargin)
  }

}
