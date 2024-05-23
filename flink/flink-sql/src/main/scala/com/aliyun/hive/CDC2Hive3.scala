package com.aliyun.hive

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.SqlDialect
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * TODO cdc直接写hive不通,原因hive不支持update,delete
 *
 * @description ${description}
 * @author twan
 * @date 2024-05-23 11:03:14
 * @version 1.0
 */
object CDC2Hive3 {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi", clusterName = "qidian")
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
        |);
        |""".stripMargin)
    val hiveCatalogName = "hive_catalog"
    tableEnv.registerCatalog(hiveCatalogName, FlinkUtils.getHiveCatalog(hiveCatalogName, version = "3.1.3"))
    tableEnv.useCatalog(hiveCatalogName)
    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)
    tableEnv.executeSql("use test")
    tableEnv.executeSql(
      """
        |CREATE TABLE  IF NOT EXISTS customers_hive_cdc (
        |CUSTOMER_ID INT,
        |CUSTOMER_NAME STRING,
        |EMAIL STRING,
        |PHONE STRING,
        |TEST_TIME TIMESTAMP
        |) STORED AS parquet TBLPROPERTIES (
        |  'sink.partition-commit.trigger'='partition-time',
        |  'sink.partition-commit.delay'='0S',
        |  'sink.partition-commit.policy.kind'='metastore,success-file',
        |  'auto-compaction'='true',
        |  'compaction.file-size'='128MB'
        |)
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |insert into hive_catalog.test.customers_hive_cdc select  *   from default_catalog.default_database.table_customers
        |""".stripMargin)
  }

}
