package com.xiaofan.hudi

import com.xiaofan.utils.{DateUtils, RandomNameUtils}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.table.data.{GenericRowData, RowData, StringData, TimestampData}
import org.apache.hudi.common.model.HoodieTableType
import org.apache.hudi.configuration.FlinkOptions
import org.apache.hudi.util.HoodiePipeline
import org.slf4j.{Logger, LoggerFactory}

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.JavaConverters._

/**
 * @author: twan
 * @date: 2023/9/14 12:32
 * @description:
 */
object HudiSink {
  val BASE_PATH = "hdfs:///data//hudi/student"
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    logger.debug("testttttttttttttttttttt")
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    import org.apache.flink.streaming.api.CheckpointingMode
    import org.apache.flink.streaming.api.environment.CheckpointConfig
    // 2.必须开启checkpoint 默认有5个checkpoint后，hudi目录下才会有数据；不然只有一个.hoodie目录
    val checkPointPath = "hdfs:///data/check_point/hudi_sink_test"
    // 任务流取消和故障应保留检查点
    env.getCheckpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.enableCheckpointing(Duration.ofSeconds(10).toMillis)

    //env.getCheckpointConfig.setCheckpointTimeout(10 * 1000)
    //env.getCheckpointConfig.setMinPauseBetweenCheckpoints(2 * 1000) //相邻两次checkpoint之间的时间间隔

    env.getCheckpointConfig.setCheckpointStorage(checkPointPath)

    val sourceDataStream: DataStreamSource[RowData] = env.addSource(new SourceFunction[RowData]() {
      var flag = true
      private val atomicLong = new AtomicInteger()

      override def run(context: SourceFunction.SourceContext[RowData]) = {
        while (flag) {
          1.to(100000).foreach(index => {
            val row = new GenericRowData(5)
            row.setField(0, atomicLong.incrementAndGet())
            row.setField(1, StringData.fromString(RandomNameUtils.fullName()))
            row.setField(2, index + 3)
            row.setField(3, TimestampData.fromEpochMillis(System.currentTimeMillis()))
            row.setField(4, StringData.fromString(DateUtils.getCurrentDateStr)) //否则报错java.lang.String cannot be cast to org.apache.flink.table.data.StringData

            context.collect(row)
          })
          TimeUnit.SECONDS.sleep(1)
        }
      }

      override def cancel() = flag = false
    })

    val targetTable = "student"

    val map = Map(FlinkOptions.PATH.key -> BASE_PATH, FlinkOptions.TABLE_TYPE.key -> HoodieTableType.MERGE_ON_READ.name,
      FlinkOptions.PRECOMBINE_FIELD.key -> "ts")
    val builder: HoodiePipeline.Builder = HoodiePipeline.builder(targetTable)
      .column("id int")
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
