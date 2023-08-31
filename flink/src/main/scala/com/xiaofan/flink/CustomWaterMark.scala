package com.xiaofan.flink

import com.sun.tools.javac.util.List.{collector, convert}
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkGenerator, WatermarkOutput, WatermarkStrategy}
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import scala.collection.JavaConverters._
import java.time.Duration
import scala.sys.env

/**
 * @description ${description}
 * @author twan
 * @date 2023-08-30 21:44:33
 * @version 1.0
 */
object CustomWaterMark {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    /**
     小张,room_1,1,10,1000
     小张,room_1,1,5,1000
     小李,room_2,1,10,1500
     */
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.setParallelism(1)
    val eventDataStream: DataStream[UserEvent] = env.socketTextStream("cdh1", 9999).map(t => {
      val strings: Array[String] = t.split(",")
      UserEvent(strings(0),strings(1), strings(2).toInt, strings(3).toInt,strings(4).toInt)
    }).assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(new SerializableTimestampAssigner[UserEvent] {
      override def extractTimestamp(element: UserEvent, recordTimestamp: Long) = {
        element.eventTime
      }
    }))
    val resultDataStream: DataStream[(String, Long)] = eventDataStream.keyBy(_.roomId)
      .window(TumblingEventTimeWindows.of(Time.seconds(3))) //固定窗口
      .process(new ProcessWindowFunction[UserEvent, Tuple2[String, Long], String, TimeWindow]() {
        lazy val sumReadNum: MapState[String, Long] = getRuntimeContext.getMapState(new MapStateDescriptor[String, Long]("sumReadNum", classOf[String], classOf[Long]))
        override def process(key: String, context: Context, elements: Iterable[UserEvent], out: Collector[(String, Long)]): Unit = {
          elements.foreach(event => {
            if (sumReadNum.contains(event.name)) sumReadNum.put(event.name, sumReadNum.get(event.name) + event.clickCount) else sumReadNum.put(event.name, event.clickCount)
          })
          println("window start:%d,end:%d".format(context.window.getStart,context.window.getEnd))
          out.collect(elements.head.roomId, sumReadNum.values().iterator().asScala.sum)
        }
      })
    resultDataStream.print("dataStream>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
case class UserEvent(name:String,roomId:String ,windSize:Int,clickCount:Int,eventTime:Int)

class PunctuatedAssigner extends WatermarkGenerator[UserEvent] {
  override def onEvent(event: UserEvent, l: Long, output: WatermarkOutput): Unit = {

  }

  override def onPeriodicEmit(output: WatermarkOutput): Unit = {
    output.emitWatermark(new Watermark(System.currentTimeMillis - maxTimeLag))
  }
}