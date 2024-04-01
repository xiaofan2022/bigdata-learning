package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-28 22:33:09
 * @version 1.0
 */
object Jdbc2Hudi {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi")
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE jdbc_customers (
        |    customer_id int,
        |    customer_name string,
        |    email string,
        |    phone string,
        |    test_time timestamp,
        |    insert_date timestamp
        |) WITH (
        |    'connector' = 'jdbc',
        |    'url' = 'jdbc:oracle:thin:@hdp05:1521:xe',
        |    'driver' = 'oracle.jdbc.driver.OracleDriver',
        |    'username' = 'flinkuser',
        |    'password' = 'flinkpw',
        |    'table-name' = 'CUSTOMERS'
        |);
        |""".stripMargin)
    //
    //'table.type' = 'MERGE_ON_READ',
    tableEnv.executeSql(
      """
        |CREATE TABLE hudi_constomer (
        |customer_id INT,
        |    customer_name STRING,
        |    email string,
        |    phone string,
        |    test_time timestamp,
        |        insert_date timestamp,
        |
        |   dt string
        |)
        |PARTITIONED BY (dt)
        |WITH (
        |  'connector' = 'hudi',
        |  'path' = '/warehouse/hudi/hudi_constomer',
        |  'read.utc-timezone'='false',
        |  'hoodie.datasource.write.keygenerator.class' = 'org.apache.hudi.keygen.ComplexAvroKeyGenerator',
        |  'hoodie.datasource.write.recordkey.field' = 'customer_id',
        |  'hoodie.datasource.write.hive_style_partitioning' = 'true',
        |  'hive_sync.enable' = 'true',
        |  'hive_sync.mode' = 'hms',
        |  'hive_sync.metastore.uris' = 'thrift://hadoop103:9083',
        |  'hive_sync.conf.dir'='D:\\develop\\data\\hive_conf',
        |  'hive_sync.db' = 'hudi',
        |  'hive_sync.table' = 'hudi_constomer',
        |  'hive_sync.partition_fields' = 'dt',
        |  'hive_sync.partition_extractor_class' = 'org.apache.hudi.hive.HiveStylePartitionValueExtractor'
        |)
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |insert into hudi_constomer
        |select *, DATE_FORMAT(test_time, 'yyyy-MM-dd') dt from  jdbc_customers
        |""".stripMargin)
    //env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
