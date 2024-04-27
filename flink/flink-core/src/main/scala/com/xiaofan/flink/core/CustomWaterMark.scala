package com.xiaofan.flink.core

import com.xiaofan.utils.DateUtils
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner
import org.apache.flink.streaming.api.windowing.triggers.{EventTimeTrigger, Trigger}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import java.time.Duration
import java.util
import java.util.Collections
import scala.collection.JavaConverters._

/**
 * @description 自定义窗口大小(实现同一topic数据可以不同周期计算)
 *              背景:数据为直播间抓取数据，爬虫维护了一个账号规则简单讲 大号采集周期时间短，账号级别约第采集周期约长从而
 *              实现高质量的数据抓取，实时系统需要在一套程序适应不同的周期（采集周期=计算周期）
 * @author twan
 * @date 2023-08-30 21:44:33
 * @version 1.0
 */
object CustomWindowSize {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8081)
    /**
     * 赵四,room_1,3,10,2021-01-03 10:00:00
     * 刘能,room_2,3,40,2021-01-03 10:00:00
     * 大脑袋,room_2,10,2,2021-01-03 10:00:00
     * 赵四,room_1,3,20,2021-01-03 10:03:00
     * 刘能,room_2,3,30,2021-01-03 10:03:00
     * 赵四,room_1,3,18,2021-01-03 10:06:00
     * 刘能,room_2,3,18,2021-01-03 10:06:00
     * 老谢,room_2,3,17,2021-01-03 10:06:00
     * 刘能,room_2,3,16,2021-01-03 10:09:00
     * 老谢,room_2,3,22,2021-01-03 10:09:00
     * 大脑袋,room_2,10,5,2021-01-03 10:10:00
     * 赵四,room_1,3,50,2021-01-03 10:12:00
     * 刘能,room_2,3,80,2021-01-03 10:12:00
     * 赵四,room_1,3,40,2021-01-03 10:27:00
     * 刘能,room_2,3,50,2021-01-03 10:27:00
     * 赵四,room_1,3,70,2021-01-03 10:30:00
     * 刘能,room_2,3,30,2021-01-03 10:30:00
     * 大脑袋,room_2,10,20,2021-01-03 10:30:00
     * 小号 0,3,6,9,12,15
     * 大号 0,5,10,15
     */
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.setParallelism(1)
    val eventDataStream: DataStream[UserEvent] = env.socketTextStream("cdh1", 9999).map(t => {
      val strings: Array[String] = t.split(",")
      UserEvent(strings(0), strings(1), strings(2).toInt, strings(3).toInt,strings(4))
    }).assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(new SerializableTimestampAssigner[UserEvent] {
      override def extractTimestamp(element: UserEvent, recordTimestamp: Long) = {
        DateUtils.convertDateStr2Long(element.eventTime, DateUtils.DATE_TIME_PATTERN).toLong
      }
    }))
    val resultDataStream: DataStream[(String, Long)] = eventDataStream.keyBy(_.roomId)
      //.window(TumblingEventTimeWindows.of(Time.seconds(3))) //固定窗口
      .window(new CustomWindowAssigner())
      .process(new ProcessWindowFunction[UserEvent, Tuple2[String, Long], String, TimeWindow]() {
        lazy val sumReadNum: MapState[String, Long] = getRuntimeContext.getMapState(new MapStateDescriptor[String, Long]("sumReadNum", classOf[String], classOf[Long]))
        override def process(key: String, context: Context, elements: Iterable[UserEvent], out: Collector[(String, Long)]): Unit = {
          elements.foreach(event => {
            if (sumReadNum.contains(event.name)) sumReadNum.put(event.name, sumReadNum.get(event.name) + event.goodsCount) else sumReadNum.put(event.name, event.goodsCount)
          })
          val elementTimeMap: Map[String, String] = elements.groupBy(_.name).map(t => t._1 -> t._2.map(t1 => t1.eventTime + "->" + t1.goodsCount).mkString(","))
          println("window start:%s,end:%s rows:%s".format(DateUtils.convertMilliTimeToString(context.window.getStart), DateUtils.convertMilliTimeToString(context.window.getEnd), elementTimeMap.mkString(",")))
          out.collect(elements.head.roomId, sumReadNum.values().iterator().asScala.sum)
        }
      })
    resultDataStream.print("dataStream>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}

case class UserEvent(name: String, roomId: String, windSize: Int, goodsCount: Int, eventTime: String)

class CustomWindowAssigner extends WindowAssigner[Object, TimeWindow] {
  override def assignWindows(element: Object, timestamp: Long, context: WindowAssigner.WindowAssignerContext): util.Collection[TimeWindow] = { // 根据数据字段的值来决定窗口的大小
    val windowSize = element.asInstanceOf[UserEvent].windSize * 1000 * 60
    val startTime: Long = timestamp - (timestamp % windowSize)
    Collections.singletonList(new TimeWindow(startTime, startTime + windowSize))
  }

  override def toString = "CustomWindowAssigner"

  override def isEventTime = true

  override def getDefaultTrigger(streamExecutionEnvironment: environment.StreamExecutionEnvironment): Trigger[Object, TimeWindow] = EventTimeTrigger.create()

  override def getWindowSerializer(executionConfig: ExecutionConfig): TypeSerializer[TimeWindow] = new TimeWindow.Serializer
}