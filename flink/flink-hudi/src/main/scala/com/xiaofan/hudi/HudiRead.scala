package com.xiaofan.hudi

import com.xiaofan.hudi.HudiSink.BASE_PATH
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.data.RowData
import org.apache.hudi.common.model.HoodieTableType
import org.apache.hudi.configuration.FlinkOptions
import org.apache.hudi.util.HoodiePipeline

import scala.collection.JavaConverters._

/**
 * @author: twan
 * @date: 2023/10/9 15:50
 * @description:
 */
object HudiRead {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val targetTable = "t1"

    val map = Map(FlinkOptions.PATH.key -> BASE_PATH,
      FlinkOptions.TABLE_TYPE.key -> HoodieTableType.MERGE_ON_READ.name,
      FlinkOptions.READ_AS_STREAMING.key -> "true", // this option enable the streaming read
      //FlinkOptions.READ_START_COMMIT.key -> "'20210316134557'" // specifies the start commit instant time
    )
    val builder: HoodiePipeline.Builder = HoodiePipeline.builder(targetTable)
      .column("id INT")
      .column("name VARCHAR(10)")
      .column("age INT")
      .column("ts TIMESTAMP")
      .column("`partition` VARCHAR(20)")
      .pk("id")
      .partition("partition")
      .options(map.asJava)
    val dataStream: DataStream[RowData] = builder.source(env)
    dataStream.print("result>>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))

  }

}
