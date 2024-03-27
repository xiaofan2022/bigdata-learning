package com.aliyun.flinkcdc

import com.ververica.cdc.connectors.base.options.StartupOptions
import com.ververica.cdc.connectors.oracle.OracleSource
import com.ververica.cdc.debezium.{DebeziumSourceFunction, JsonDebeziumDeserializationSchema}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

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
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.enableCheckpointing(Duration.ofSeconds(30).toMillis)

    env.getCheckpointConfig.setCheckpointStorage(
      new Path("file:///{}/{}".format(this.getClass.getResource("").getPath, "oracle_cdc")))
    val properties = new Properties()
    //properties.put("oracle.cdc.auto-commit.enabled","false")
    properties.put("debezium.log.mining.strategy", "online_catalog")
    properties.put("debezium.log.mining.continuous.mine", "true")
    properties.put("database.history.store.only.captured.tables.ddl", String.valueOf(true))
    //properties.put("database.tablename.case.insensitive","false")
    val source: DebeziumSourceFunction[String] = OracleSource.builder[String]()
      .hostname("hdp05")
      .port(1521)
      .url("jdbc:oracle:thin:@hdp05:1521/xe")
      .schemaList("FLINKUSER")
      .database("xe") //这个应该是sid
      .username("flinkuser")
      .password("flinkpw")
      .tableList("CUSTOMERS")
      .startupOptions(StartupOptions.initial())
      .deserializer(new JsonDebeziumDeserializationSchema())
      .debeziumProperties(properties)
      .build()
    env.addSource(source).print()
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
