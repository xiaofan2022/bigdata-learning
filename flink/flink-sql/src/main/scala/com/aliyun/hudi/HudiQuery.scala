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
        |CREATE TABLE hudi_constomer (
        |customer_id INT,
        |customer_name STRING,
        |email string,
        |phone string,
        |test_time timestamp,
        |insert_date timestamp
        |)
        |WITH (
        |  'connector' = 'hudi',
        |  'path' = 'file:///D://data//warehouse/hudi/hudi_constomer_no_partition_flink_new',
        |  'hoodie.datasource.write.keygenerator.class' = 'org.apache.hudi.keygen.ComplexAvroKeyGenerator',
        |  'hoodie.datasource.write.recordkey.field' = 'customer_id',
        |  'hoodie.datasource.write.hive_style_partitioning' = 'true'
        |)
        |""".stripMargin)
    val table: Table = tableEnv.sqlQuery("select * from hudi_constomer")
    tableEnv.toChangelogStream(table).print()
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
