package com.tunning

import com.tunning.utils.InitUtil.initSparkSession
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @description ${description}
 * @author twan
 * @date 2024-01-13 12:22:07
 * @version 1.0
 */
object SparkTest {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("InitData")
      .setMaster("local[*]") //TODO 要打包提交集群执行，注释掉
    val sparkSession: SparkSession = initSparkSession(sparkConf)
    // 定义表结构
    val schema = StructType(Seq(
      StructField("userId", IntegerType, nullable = true),
      StructField("name", StringType, nullable = true),
      StructField("age", IntegerType, nullable = true),
      StructField("gender", StringType, nullable = true),
      StructField("email", StringType, nullable = true)
    ))

    // 模拟生成数据
    val data = Seq(
      Row(1, "Alice", 25, "Female", "alice@example.com"),
      Row(2, "Bob", 30, "Male", "bob@example.com"),
      Row(3, "Charlie", 22, "Male", "charlie@example.com"),
      // ... 添加更多的数据行
    )

    // 创建 DataFrame
    val df = sparkSession.createDataFrame(sparkSession.sparkContext.parallelize(data), schema)
    df.write.format("parquet")
      .mode(SaveMode.Overwrite)
      .saveAsTable("test.student")

    //df.join()
    // 打印模拟生成的数据
    println("Simulated Student Data:")
  }
}
