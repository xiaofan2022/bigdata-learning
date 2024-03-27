package com.tunning

import com.atguigu.sparktuning.utils.InitUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @description ${description}
 * @author twan
 * @date 2024-01-16 15:31:04
 * @version 1.0
 */
object SparkSizeEstimator {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("AqeDynamicSwitchJoin")
      .set("spark.sql.adaptive.enabled", "true")
      .setMaster("local[*]")
      .set("spark.sql.adaptive.localShuffleReader.enabled", "true") //在不需要进行shuffle重分区时，尝试使用本地shuffle读取器。将sort-meger join 转换为广播join
    val sparkSession: SparkSession = InitUtil.initSparkSession(sparkConf)
    val dataFrame: DataFrame = sparkSession.sql("select * from sparktuning.course_pay ")
    val plan: LogicalPlan = dataFrame.queryExecution.logical
    val bytes: BigInt = sparkSession.sessionState.executePlan(plan).optimizedPlan.stats.sizeInBytes
  }

}
