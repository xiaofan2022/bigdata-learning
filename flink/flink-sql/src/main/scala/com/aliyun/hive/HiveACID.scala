package com.aliyun.hive

import com.aliyun.utils.FlinkUtils
import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.SqlDialect
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * TODO hive目前不支持acid
 *
 * @description ${description}
 * @author twan
 * @date 2024-05-23 11:51:26
 * @version 1.0
 */
object HiveACID {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("oracle_jdbc2_hudi", clusterName = "qidian")
    env.setRuntimeMode(RuntimeExecutionMode.BATCH)
    val tableEnv = StreamTableEnvironment.create(env)
    val hiveCatalogName = "hive_catalog"
    tableEnv.registerCatalog(hiveCatalogName, FlinkUtils.getHiveCatalog(hiveCatalogName, version = "3.1.3"))
    tableEnv.useCatalog(hiveCatalogName)
    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)
    tableEnv.executeSql("use test")
    tableEnv.executeSql(
      """
        |CREATE TABLE acidtbl (a INT, b STRING)TBLPROPERTIES
        |('transactional'='true')
        |""".stripMargin)
    //    tableEnv.executeSql(
    //      """
    //        |INSERT INTO acidtbl (a,b) VALUES (100, "oranges"), (200, "apples"), (300, "bananas")
    //        |""".stripMargin)
    //    tableEnv.toChangelogStream(tableEnv.sqlQuery("select * from acidtbl")).print("result1>>>")
    //    tableEnv.executeSql(
    //      """
    //        DELETE FROM acidTbl where a = 200
    //        |""".stripMargin)
    tableEnv.toChangelogStream(tableEnv.sqlQuery("select * from acidtbl")).print("resul2222>>>")

  }

}
