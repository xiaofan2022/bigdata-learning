package com.xiaofan.utils

import com.xiaofan.constants.DevelopModel
import com.xiaofan.utils.CommonUtils.getCurrentModelPath
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.{SqlDialect, TableResult}
import org.apache.flink.table.catalog.hive.HiveCatalog
import org.slf4j.{Logger, LoggerFactory}

import java.io.InputStream
import scala.io.Source

/**
 * @author: twan
 * @date: 2023/9/13 17:23
 * @description:
 */
object FlinkTableUtils {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)


  def batchExecuteSql(tableEnv: StreamTableEnvironment, filName: String, isLocalEnv: Boolean = false): Boolean = {
    val inputStream: InputStream = FlinkTableUtils.getClass.getClassLoader.getResourceAsStream(filName)
    var flag = false
    tableEnv.useCatalog("myHive")
    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)
    try {
      val sqlList: List[String] = Source.fromInputStream(inputStream).getLines.reduceLeft(_ + _).trim.split(";").map(t => t.replaceAll(" +", " ")).toList
      sqlList.foreach(sql => {
        try {
          val tableResult: TableResult = tableEnv.executeSql(sql)
          println(s"sql execute ${tableResult.getResultKind.name()}")
        } catch {
          case e: Exception => {
            logger.error(s"batchExecSql sql ${sql} error message:${e.getMessage}")
            false
          }
        }
      })
      flag = true
    } catch {
      case e: Exception => {
        logger.error(s"batchExecSql sql   error message:${e.getMessage}")
      }
    }
    tableEnv.getConfig.setSqlDialect(SqlDialect.DEFAULT)
    flag
  }

  def getHiveCatalog(model: DevelopModel): HiveCatalog = {
    val modelPath = getCurrentModelPath(this.getClass.getClassLoader)
    val hiveConfDir = model match {
      case DevelopModel.COMPANY => modelPath + "/src/main/resources/company_test"
      case DevelopModel.DEV => modelPath + "/src/main/resources/company_test"
    }
    new HiveCatalog(
      "myHive", // catalog name
      "default",
      hiveConfDir,
      "2.1.1" // Hive version
    )
  }


}
