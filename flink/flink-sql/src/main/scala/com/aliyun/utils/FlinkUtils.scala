package com.aliyun.utils

import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.hudi.table.catalog.HoodieCatalog
import org.slf4j.{Logger, LoggerFactory}

import java.io.IOException
import java.net.ServerSocket
import java.time.Duration

/**
 * @description ${description}
 * @author twan
 * @date 2023-08-30 21:51:15
 * @version 1.0
 */
object FlinkUtils {
  private val logger: Logger = LoggerFactory.getLogger(FlinkUtils.getClass)


  def getStreamEnvironment(
                            pathSuffix: String = "",
                            checkPointDuration: Duration = Duration.ofMinutes(1),
                            isLocalEnv: Boolean = true): StreamExecutionEnvironment = {
    var env: StreamExecutionEnvironment = null
    if (isLocalEnv) {
      val configuration = new Configuration()
      val port: Int = findAvailablePort(8080, 9000)
      logger.warn("port:{}", port)
      configuration.setInteger("rest.port", port)
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
    if (System.getProperty("os.name").toLowerCase.contains("windows")) {
      env.getCheckpointConfig.setCheckpointStorage(
        new Path("file:///D://develop/check_point/" + pathSuffix))
    } else {
      env.getCheckpointConfig.setCheckpointStorage(
        new Path("hdfs://hadoop101:9000/data/flink/checkpoint/" + pathSuffix))
    }
    env
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

  def findAvailablePort(startPort: Int, endPort: Int): Int = {
    for (port <- startPort to endPort) {
      try {
        val serverSocket = new ServerSocket(port)
        // 如果ServerSocket能成功创建，说明端口没有被占用
        serverSocket.close() // 立即关闭以避免不必要的资源占用
        return port
      } catch {
        case e: IOException =>
        // 端口被占用，或者其它IO错误，继续尝试下一个端口
      }
    }
    // 如果没有找到空闲端口，返回-1或者抛出一个异常
    -1
  }

  def getHudiCatalog(hudiCataLogName: String = "hudi_catalog"): HoodieCatalog = {
    val configuration = new Configuration()
    configuration.setString("type", "hudi")
    configuration.setString("mode", "hms")
    configuration.setString("default-database", "default")
    configuration.setString("hive.conf.dir", "D://develop//data//hive_conf")
    configuration.setString("catalog.path", "/hudi_catalog")
    new HoodieCatalog(hudiCataLogName, configuration)
  }

}
