package com.xiaofan.flink.hudi

import com.xiaofan.flink.utils.FlinkUtils
import com.xiaofan.utils.DateUtils
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.data.{GenericRowData, RowData, StringData, TimestampData}
import org.apache.hudi.common.model.HoodieTableType
import org.apache.hudi.configuration.FlinkOptions
import org.apache.hudi.util.HoodiePipeline

import java.sql.Timestamp
import scala.collection.JavaConverters._

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-27 18:30:13
 * @version 1.0
 */
object FlinkHudiDemo {
  val targetTable = "flink_hudi_table"
  val basePath = "file:///D://data//warehouse//hudi//%s".format(targetTable)

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment = FlinkUtils.getStreamEnvironment("hudi")

    insertHudiData(env)

    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

  def insertHudiData(env: StreamExecutionEnvironment) = {
    val options = Map(FlinkOptions.PATH.key() -> basePath,
      FlinkOptions.PRECOMBINE_FIELD.key() -> "ts",
      FlinkOptions.TABLE_TYPE.key() -> HoodieTableType.MERGE_ON_READ.name())
    val sourceDataStream: DataStream[RowData] = env.socketTextStream("hadoop101", 9999).map(t => {
      val strings: Array[String] = t.split(",")
      val rowData = new GenericRowData(4)
      rowData.setField(0, strings(0).toInt) //id
      rowData.setField(1, StringData.fromString(strings(1))) //name
      rowData.setField(2, strings(2).toInt) //age
      rowData.setField(3, TimestampData.fromTimestamp(new Timestamp(DateUtils.strToDate(strings(3)).getTime))) //eventTime
      rowData
    })
    val builder: HoodiePipeline.Builder = HoodiePipeline.builder(targetTable)
      .column("id int")
      .column("name VARCHAR(10)")
      .column("age INT")
      .column("ts TIMESTAMP(3)")
      .pk("id")
      .options(options.asJava)
    builder.sink(sourceDataStream, false)
  }

}
