package com.aliyun.flinkcdc

import com.aliyun.utils.FlinkUtils
import com.ververica.cdc.connectors.base.options.StartupOptions
import com.ververica.cdc.connectors.oracle.OracleSource
import com.ververica.cdc.debezium.{DebeziumSourceFunction, JsonDebeziumDeserializationSchema}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.time.Duration
import java.util.Properties


/**
 * @description ${description}
 * @author twan
 * @date 2024-03-28 13:03:16
 * @version 1.0
 */
object OracleTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_cdc")
    env.enableCheckpointing(Duration.ofSeconds(30).toMillis)
    val properties = new Properties()
    //properties.put("oracle.cdc.auto-commit.enabled","false")
    properties.setProperty("debezium.database.tablename.case.insensitive", "false")
    properties.setProperty("debezium.log.mining.strategy", "online_catalog")
    properties.setProperty("debezium.log.mining.continuous.mine", "true")
    properties.put("database.history.store.only.captured.tables.ddl", String.valueOf(true))

    properties.setProperty("scan.startup.mode", "latest-offset")
    //properties.put("database.tablename.case.insensitive","false")
    val source: DebeziumSourceFunction[String] = OracleSource.builder[String]()
      .hostname("hdp05")
      .port(1521)
      //.url("jdbc:oracle:thin:@hdp05:1521/xe")
      .schemaList("FLINKUSER")
      .database("xe") //这个应该是sid
      .username("flinkuser")
      .password("flinkpw")
      //.tableList("*")如果没有写表明那么同步所有
      .startupOptions(StartupOptions.initial())
      .deserializer(new JsonDebeziumDeserializationSchema())
      .debeziumProperties(properties)
      .build()
    env.addSource(source).print()
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
