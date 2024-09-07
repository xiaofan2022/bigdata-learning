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
 * @date 2024-07-22 18:21:37
 * @version 1.0
 */
object MysqlCDCDemo {

  def main(args: Array[String]): Unit = {
    val LOG = LoggerFactory.getLogger(OracleCDCDemo.getClass) // 这个类填自己的类名
    LOG.info("---------------------------")
    val configuration = new Configuration
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.enableCheckpointing(Duration.ofMinutes(1).toMillis)
    env.disableOperatorChaining()
    env.getCheckpointConfig.setCheckpointStorage(new Path("file:///D:\\qidian\\data\\checkpoint"))
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
       CREATE TABLE table_source_pg (
        |  id INT,
        |  name STRING,
        |  age  INT,
        | gender STRING,
        |    class STRING,
        |         PRIMARY KEY(id) NOT ENFORCED
        |
        |)WITH (
        |'connector' = 'mysql-cdc',
        |   'server-time-zone' = 'Asia/Shanghai',
        |    'hostname' = 'hdp03',
        |    'port' = '3341',
        |    'username' = 'root',
        |    'password' = '123456',
        |    'database-name' = 'test',
        |    'table-name' = 'students',
        |    'debezium.binary.handling.mode' = 'base64'
        |)
        |""".stripMargin)
    val tableResult: Table = tableEnv.sqlQuery("select * from table_source_pg")
    val resultStream = tableEnv.toChangelogStream(tableResult)
    resultStream.print("result>>>>>")
    env.execute()
  }

}
