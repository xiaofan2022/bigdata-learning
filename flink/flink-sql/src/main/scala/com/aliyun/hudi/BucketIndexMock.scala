package com.aliyun.hudi

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.time.Duration

/**
 * @description ${description}
 * @author twan
 * @date 2024-09-07 22:30:16
 * @version 1.0
 */
object BucketIndexMock {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment(this.getClass.getSimpleName.dropRight(1), checkPointDuration = Duration.ofSeconds(10), clusterName = "qidian")
    val tableEnv = StreamTableEnvironment.create(env)
    val hudiCataLogName = "hudi_catalog"
    tableEnv.registerCatalog(hudiCataLogName, FlinkUtils.getHudiCatalog())
    tableEnv.useCatalog(hudiCataLogName)
    tableEnv.executeSql(
      """
        |CREATE TABLE MockData (
        |              id INT,
        |              name        string,
        |              age   INT
        |          ) WITH (
        |            'connector' = 'datagen',
        |            'rows-per-second'='200000',
        |            'fields.id.kind'='random' ,
        |            'fields.id.min'='1',
        |            'fields.id.max'='10000',
        |            'fields.name.kind'='random',
        |            'fields.age.min'='1',
        |            'fields.age.max'='100'
        |          )""".stripMargin)
    tableEnv.executeSql("insert into hudi_catalog.test.hudi_bucket_index_test select * from default_catalog.default_database.MockData")

  }

}
