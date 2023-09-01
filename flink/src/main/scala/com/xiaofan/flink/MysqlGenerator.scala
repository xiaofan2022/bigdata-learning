package com.xiaofan.flink

import com.xiaofan.flink.utils.{CommonUtils, SinkUtils}
import com.xiaofan.utils.RandomNameUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.jdbc.JdbcStatementBuilder
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}

import java.sql.PreparedStatement
import java.time.Duration
import java.util.Random

/**
 * @author: twan
 * @date: 2023/9/1 11:11
 * @description:
 */
object MysqlGenerator {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.setInteger("rest.port", 8080)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    env.enableCheckpointing(Duration.ofSeconds(10).toMillis)
    //最小间隔
    //env.getCheckpointConfig.setMinPauseBetweenCheckpoints(Duration.ofMinutes(5).toMillis)
    //超时时间
    env.getCheckpointConfig.setCheckpointTimeout(Duration.ofMinutes(10).toMillis)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)

    env.getCheckpointConfig.setCheckpointStorage(new Path("file://" + CommonUtils.getCurrentCKPath() + "/cdctest"))

    env.setParallelism(1)
    val sourceDataStream: DataStream[Student] = env.addSource(new SourceFunction[Student]() {
      var flag = true

      override def run(context: SourceFunction.SourceContext[Student]) = {
        var index = 1
        val random = new Random()
        while (flag) {
          context.collect(Student(index, RandomNameUtils.fullName(), random.nextInt(100)))
          index = index + 1
          Thread.sleep(1000L) // 1s生成1个数据

        }
      }

      override def cancel() = flag = false
    })
    val sql =
      """INSERT INTO `test`.`student`(`id`, `name`, `age`) VALUES (?, ?, ?)
        |
        |""".stripMargin
    sourceDataStream.addSink(SinkUtils.getJDBCSink(sql, "test",
      new JdbcStatementBuilder[Student]() {
        override def accept(preparedStatement: PreparedStatement, u: Student) = {
          SinkUtils.createStatement(sql, u, preparedStatement)
        }
      }))

    //sourceDataStream.addSink()
    /*    sourceDataStream.addSink()*/

    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

}


case class Student(id: Int, name: String, age: Int)
