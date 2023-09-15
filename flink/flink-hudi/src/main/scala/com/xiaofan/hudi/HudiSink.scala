package com.xiaofan.hudi

import com.xiaofan.utils.{CommonUtils, DateUtils, RandomNameUtils}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.table.data.{GenericRowData, RowData}
import org.apache.hudi.common.model.HoodieTableType
import org.apache.hudi.configuration.FlinkOptions
import org.apache.hudi.util.HoodiePipeline

import java.time.Duration
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

/**
 * @author: twan
 * @date: 2023/9/14 12:32
 * @description:
 */
object HudiSink {

  def main(args: Array[String]): Unit = {
    val ckPath = "file://%s/flink2hudi".format(CommonUtils.getCurrentCKPath())
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.getCheckpointConfig.setCheckpointInterval(Duration.ofMinutes(10).toMillis)
    val sourceDataStream: DataStreamSource[RowData] = env.addSource(new SourceFunction[RowData]() {
      var flag = true

      override def run(context: SourceFunction.SourceContext[RowData]) = {
        while (flag) {
          1.to(100000).foreach(index => {
            val row = new GenericRowData(4)
            row.setField(0, index)
            row.setField(1, RandomNameUtils.fullName())
            row.setField(2, index + 3)
            row.setField(3, System.currentTimeMillis())
            row.setField(4, DateUtils.getCurrentDateStr)
            context.collect(row)
          })
          TimeUnit.SECONDS.sleep(1)
        }
      }

      override def cancel() = flag = false
    })

    val targetTable = "student"
    val basePath = "hdfs:///data//hudi/student"
    val map = Map(FlinkOptions.PATH.key -> basePath, FlinkOptions.TABLE_TYPE.key -> HoodieTableType.MERGE_ON_READ.name,
      FlinkOptions.PRECOMBINE_FIELD.key -> "ts")
    val builder: HoodiePipeline.Builder = HoodiePipeline.builder(targetTable)
      .column("id INT")
      .column("name VARCHAR(10)")
      .column("age INT")
      .column("ts TIMESTAMP")
      .column("`partition` VARCHAR(20)")
      .pk("id")
      .partition("partition")
      .options(map.asJava)
    builder.sink(sourceDataStream, false); // The second parameter indicating whether the input data stream is bounded
    env.execute("Api_Sink");
  }

}
