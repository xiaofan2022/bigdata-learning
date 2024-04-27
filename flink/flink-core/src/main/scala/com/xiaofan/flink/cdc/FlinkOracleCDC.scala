package com.xiaofan.flink.cdc

import com.ververica.cdc.connectors.base.options.StartupOptions
import com.ververica.cdc.connectors.oracle.OracleSource
import com.ververica.cdc.debezium.{DebeziumSourceFunction, JsonDebeziumDeserializationSchema}
import com.xiaofan.flink.utils.FlinkUtils
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import java.time.Duration
import java.util.Properties

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-22 21:45:43
 * @version 1.0
 */
object FlinkOracleCDC {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_cdc")
    env.enableCheckpointing(Duration.ofSeconds(30).toMillis)
    val properties = new Properties()
    //properties.put("oracle.cdc.auto-commit.enabled","false")
    properties.put("debezium.log.mining.strategy", "online_catalog")
    properties.put("debezium.log.mining.continuous.mine", "true")
    properties.put("database.history.store.only.captured.tables.ddl", String.valueOf(true))
    properties.put("debezium.lob.enabled", "true")
    properties.put("lob.enabled", "true")
    //properties.put("debezium.decimal.handling.mode","string")
    properties.put("decimal.handling.mode", "string")

    //properties.put("database.tablename.case.insensitive","false")
    val source: DebeziumSourceFunction[String] = OracleSource.builder[String]()
      .hostname("hdp05")
      .port(1521)
      //.url("jdbc:oracle:thin:@hdp05:1521/xe")
      .schemaList("FLINKUSER")
      .database("XE") //这个应该是sid
      .username("flinkuser")
      .password("flinkpw")
      .tableList("FLINKUSER.CUSTOMERS")
      .startupOptions(StartupOptions.initial())
      .deserializer(new JsonDebeziumDeserializationSchema())
      .debeziumProperties(properties)
      .build()
    env.addSource(source).print("result>>>>>>>>>>>>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
