package com.aliyun.sql

import com.aliyun.df.HudiDemo
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-27 11:49:30
 * @version 1.0
 */
object HudiSqlDemo {
  private val logger: Logger = LoggerFactory.getLogger(HudiDemo.getClass)

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("readParqut")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //加入该配置后spark建表制定path不会报file not found 报错
    sparkConf.set("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    sparkConf.setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    createHudiTable("manager", spark)
    spark.stop()
  }

  def createHudiTable(managerType: String, spark: SparkSession) = {
    val createManagerTableDDL =
      """
        |create table hudi_test (
        |  id bigint,
        |  name string,
        |  ts bigint
        |) using hudi
        |location  'file:///D://data//warehouse//hudi//spark_hudi'
        |tblproperties (
        |  type = 'cow',
        |  primaryKey = 'id',
        |  preCombineField = 'ts'
        |);""".stripMargin
    spark.sql(createManagerTableDDL)
    spark.sql("show tables").show()
  }
}
