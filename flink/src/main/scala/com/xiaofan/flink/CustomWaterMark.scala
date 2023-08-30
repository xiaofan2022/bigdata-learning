package com.xiaofan.flink

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala._

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
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.setParallelism(1)
    val eventDataStream: DataStream[UserEvent] = env.socketTextStream("hadoop101", 9999).map(t => {
      val strings: Array[String] = t.split(",")
      UserEvent(strings(0), strings(1).toInt, strings(2).toInt)
    })
    eventDataStream.print("dataStream>>")
    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}
case class UserEvent(name:String ,readCount:Int,eventTime:Int)