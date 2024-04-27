package com.xiaofan.flink.cdc

import com.ververica.cdc.connectors.mysql.source.MySqlSource
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;


/**
 * @description ${description}
 * @author twan
 * @date 2024-03-21 22:22:00
 * @version 1.0
 */
object FlinkMysqlCDC {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    val source: MySqlSource[String] = MySqlSource.builder[String]()
      .hostname("hadoop101")
      .port(3306)
      .databaseList("test")
      .tableList("test.student")
      .username("root")
      .password("root")
      .deserializer(new JsonDebeziumDeserializationSchema()) // 将SourceRecord转换为JSON字符串
      .build()
    env.fromSource(source, WatermarkStrategy.noWatermarks(), "MySQL Source")
      .print()
    env.execute(this.getClass.getSimpleName.dropRight(1))


  }

}
