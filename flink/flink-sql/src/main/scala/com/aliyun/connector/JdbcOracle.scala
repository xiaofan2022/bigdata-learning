package com.aliyun.connector

import com.aliyun.utils.FlinkUtils
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.types.Row

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-10 11:06:49
 * @version 1.0
 */
object QueryJdbcOracle {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("kafka_query")
    env.disableOperatorChaining()
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE jdbc_customers (
        |    CUSTOMER_ID INT,
        |    CUSTOMER_NAME STRING,
        |    EMAIL STRING,
        |    PHONE STRING,
        |    TEST_TIME TIMESTAMP,
        |    INSERT_DATE TIMESTAMP
        |) WITH (
        |    'connector' = 'jdbc',
        |    'url' = 'jdbc:oracle:thin:@hdp05:1521:xe',
        |    'driver' = 'oracle.jdbc.driver.OracleDriver',
        |    'username' = 'flinkuser',
        |    'password' = 'flinkpw',
        |    'table-name' = 'CUSTOMERS'
        |);
        |""".stripMargin)

    val table: Table = tableEnv.sqlQuery("""select * from jdbc_customers""")
    val stream: DataStream[Row] = tableEnv.toChangelogStream(table)
    stream.print("result>>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
