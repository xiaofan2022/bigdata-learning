package com.xiaofan.flink.utils

import com.xiaofan.flink.bean.Student901
import org.apache.commons.lang3.SystemUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction

import java.time.Duration

/**
 * @description ${description}
 * @author twan
 * @date 2023-08-30 21:51:15
 * @version 1.0
 */
object FlinkUtils {

  def getStreamEnvironment(
                            pathSuffix: String = "",
                            checkPointDuration: Duration = Duration.ofMinutes(1),
                            isLocalEnv: Boolean = true): StreamExecutionEnvironment = {
    var env: StreamExecutionEnvironment = null
    if (isLocalEnv) {
      val configuration = new Configuration()
      configuration.setInteger("rest.port", 8081)
      env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
      //env.setParallelism(1)
    } else {
      env = StreamExecutionEnvironment.getExecutionEnvironment
    }
    env.enableCheckpointing(checkPointDuration.toMillis)
    //最小间隔
    //env.getCheckpointConfig.setMinPauseBetweenCheckpoints(Duration.ofMinutes(5).toMillis)
    //超时时间
    //checkpoint允许的最大连续失败次数
    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(3)
    if (!isLocalEnv) {
      env.getCheckpointConfig.setCheckpointStorage(
        new Path("hdfs://nameservice1/data/flink/checkpoint/" + pathSuffix))
    } else if (SystemUtils.IS_OS_WINDOWS) {
      env.getCheckpointConfig.setCheckpointStorage(
        new Path("file:///{}/{}".format(getClass.getResource("").getPath, pathSuffix)))
    } else {
      env.getCheckpointConfig.setCheckpointStorage(
        new Path("file:///home/zgx/data/flink/check_point/" + pathSuffix))
    }
    env
  }

  def main(args: Array[String]): Unit = {
    getCustomSource { () =>
      List(new Student901())
    }
  }

  /**
   * 数据生成函数
   *
   * @param dataGenerator
   * @tparam Out
   * @return
   */
  def getCustomSource[Out](
      dataGenerator: () => List[Out],
      millis: Long = Duration.ofMinutes(1).toMillis): SourceFunction[Out] = {
    new SourceFunction[Out]() {
      var flag = true

      override def run(context: SourceFunction.SourceContext[Out]) = {
        while (flag) {
          dataGenerator().foreach(context.collect(_))
          Thread.sleep(millis)
        }
      }

      override def cancel() = flag = false
    }
  }

}
