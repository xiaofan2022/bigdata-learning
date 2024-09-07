package com.aliyun.cdc

import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.slf4j.LoggerFactory

import java.time.Duration

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 11:54:45
 * @version 1.0
 */
object OracleCDCDemo {

  def main(args: Array[String]): Unit = {
    val LOG = LoggerFactory.getLogger(OracleCDCDemo.getClass) // 这个类填自己的类名
    LOG.info("---------------------------")
    val configuration = new Configuration
    configuration.setInteger("rest.port", 8083)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    //val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("flink_sql_cde")
    env.enableCheckpointing(Duration.ofMinutes(1).toMillis)
    env.disableOperatorChaining()
    env.getCheckpointConfig.setCheckpointStorage(new Path("file:///D:\\qidian\\data\\checkpoint"))
    val tableEnv = StreamTableEnvironment.create(env)
    // 'debezium.log.mining.continuous.mine' = 'true'
    //  'debezium.log.mining.strategy' = 'online_catalog',
    tableEnv.executeSql(
      """
        |CREATE TABLE table_source_oracle (
        |        ID INT,
        |       NAME STRING,
        |       AGE INT
        |        )
        |WITH (
        |        'connector' = 'oracle-cdc',
        |        'hostname' = 'hdp01',
        |        'port' = '1521',
        |        'username' = 'FLINKUSER',
        |        'password' = 'flinkpw',
        |        'database-name' = 'P19C',
        |        'schema-name' = 'FLINKUSER',
        |        'table-name' = 'TEST01',
        |        'debezium.decimal.handling.mode'='STRING'
        |
        |)
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery("select * from table_source_oracle")
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print("result>>>>>")
    env.execute()


  }

}
