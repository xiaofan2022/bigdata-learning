package com.xiaofan.flink

import com.xiaofan.flink.bean.Student901
import com.xiaofan.flink.utils.{CommonUtils, FlinkUtils}
import com.xiaofan.utils.{DateUtils, JdbcUtil}
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.async.{AsyncRetryPredicate, AsyncRetryStrategy, ResultFuture, RichAsyncFunction}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.scala.{AsyncDataStream, DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.util.retryable.RetryPredicates
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.time.Duration
import java.util.concurrent.{ExecutorService, TimeUnit}
import java.util.function.Predicate
import java.{util => ju}
import scala.collection.JavaConverters._

/**
 * @author:
 * twan
 * @date:
 * 2023/9/1 10:11
 * @description:
 */
object AsyncMysqlSource {

  val jdbcUrl =
    "jdbc:mysql://hadoop101:3306?useSSL=false&allowPublicKeyRetrieval=true"

  val username = "root"
  val password = "123456"
  val driverName = "com.mysql.jdbc.Driver"

  def main(args: Array[String]): Unit = {

    val jdbcUtil = new JdbcUtil("test")
    val resultList: ju.List[ju.Map[String, AnyRef]] =
      jdbcUtil.query("select count(*) counts from test.student ")
    val counts: Int = resultList.get(0).values().asScala.head.toString.toInt
    //val counts=1000
    val ckPath = "file://%s/cdctest".format(CommonUtils.getCurrentCKPath())
    val env: StreamExecutionEnvironment =
      FlinkUtils.getStreamEnvironment(ckPath, Duration.ofMinutes(10))
    val sourceDataStream: DataStream[Student901] = env
      .addSource(
        FlinkUtils.getCustomSource[Student901](() =>
          1.to(counts)
            .map(index => {
              val student90 = new Student901(index, "", 0)
              student90.setEventTime(DateUtils.getCurrentTimeStamp)
              student90
            })
            .toList))
      .assignTimestampsAndWatermarks(WatermarkStrategy
        .forBoundedOutOfOrderness(Duration.ofSeconds(0))
        .withTimestampAssigner(new SerializableTimestampAssigner[Student901] {
          override def extractTimestamp(t: Student901, l: Long) =
            t.getEventTime * 1000
        }))

    val resultDataStream: DataStream[Student901] = AsyncDataStream.unorderedWaitWithRetry(
      sourceDataStream,
      new AsyncDatabaseRequest(),
      1000L,
      TimeUnit.SECONDS,
      100,
      createFixedRetryStrategy[Student901](3, 1000L))
    /* val resultDataStream: DataStream[Student901] =
       AsyncDataStream.unorderedWait(
         sourceDataStream,
         new AsyncDatabaseRequest(),
         1L,
         TimeUnit.SECONDS)*/
    // println(new MysqlClient().queryStudent(23))
    // /resultDataStream.print("result>>>")
    resultDataStream
      .keyBy(new KeySelector[Student901, String] {
        override def getKey(in: Student901) = {
          "test"
        }
      })
      .window(TumblingEventTimeWindows.of(Time.minutes(1)))
      .allowedLateness(Time.seconds(30)) //ÔÊÐí³Ùµ½30s
      .process(new ProcessWindowFunction[Student901, String, String, TimeWindow] {
        override def process(
            key: String,
            context: Context,
            elements: Iterable[Student901],
            out: Collector[String]): Unit = {
          out.collect("count:%d,sum:%d".format(elements.size, elements.map(_.getAge.toLong).sum))
        }
      })
      /*    .aggregate(new AggregateFunction[Student901, Tuple3[Long,Long, Long], Tuple3[Long,Long, Long]] {
            override def createAccumulator() = (0,0, 0)

            override def add(in: Student901, acc: (Long,Long, Long)) = {
              (acc._1 + in.getAge,acc._2+1, acc._3 + in.getName.hashCode)
            }

            override def getResult(acc: (Long, Long,Long)) = (acc._1,acc._2, acc._3)

            override def merge(acc: (Long,Long, Long), acc1: (Long,Long, Long)) =
              (acc._1 + acc1._1,acc._2+1, acc._3 + acc1._3)
          })*/
      .print("sum>>>>")

    env.execute(this.getClass.getSimpleName.dropRight(1))
  }

  /**
   * 这里的AsyncRetryStrategy 是scala包下的区别java版本根据flink源码中使用
   *
   * @param maxAttempts
   * @param fixedDelayMs
   * @tparam OUT
   * @return
   */
  private def createFixedRetryStrategy[OUT](
      maxAttempts: Int,
      fixedDelayMs: Long): AsyncRetryStrategy[OUT] = {
    new AsyncRetryStrategy[OUT] {

      override def canRetry(currentAttempts: Int): Boolean = {
        currentAttempts <= maxAttempts
      }

      override def getBackoffTimeMillis(currentAttempts: Int): Long = fixedDelayMs

      override def getRetryPredicate(): AsyncRetryPredicate[OUT] = {
        new AsyncRetryPredicate[OUT] {
          override def resultPredicate: Option[Predicate[ju.Collection[OUT]]] = {
            Option(new Predicate[ju.Collection[OUT]] {
              override def test(t: ju.Collection[OUT]): Boolean = {
                //println("teteeeeeeeeee")
                t.isEmpty
              }
            })
          }

          override def exceptionPredicate: Option[Predicate[Throwable]] = Some(
            RetryPredicates.HAS_EXCEPTION_PREDICATE)
        }
      }
    }
  }

  class AsyncDatabaseRequest extends RichAsyncFunction[Student901, Student901] {

    val client: MysqlClient = null
    val executorService: ExecutorService = null
    val logger: Logger = LoggerFactory.getLogger(classOf[AsyncDatabaseRequest])
    var conn: Connection = null
    var ps: PreparedStatement = null

    override def open(parameters: Configuration): Unit = {
      import java.sql.DriverManager
      logger.info("async function for hbase java open ...")
      super.open(parameters)
      Class.forName(driverName)
      conn = DriverManager.getConnection(jdbcUrl, username, password)
      ps = conn.prepareStatement("select * from test.student where id = ?")
    }

    override def close(): Unit = {
      super.close()
      ps.close()
      conn.close()
    }

    override def asyncInvoke(
        student: Student901,
        resultFuture: ResultFuture[Student901]): Unit = {
      ps.setInt(1, student.getId)
      val resultSet: ResultSet = ps.executeQuery()
      if (resultSet.next())
        resultFuture.complete(
          List(new Student901(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3))))
    }

    override def timeout(input: Student901, resultFuture: ResultFuture[Student901]): Unit = {
      println("input{} timeout".format(input))
      super.timeout(input, resultFuture)
    }

  }

  class MysqlClient {
    Class.forName("com.mysql.jdbc.Driver")

    val conn: Connection =
      DriverManager.getConnection(jdbcUrl, username, password)

    val ps: PreparedStatement =
      conn.prepareStatement("select *  from test.student where id = ?")

    def queryStudent(id: Int): Student901 = {
      var student: Student901 = null
      ps.setInt(1, id)
      val resultSet: ResultSet = ps.executeQuery()
      while (resultSet.next()) {
        student = new Student901(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3))
      }
      student
    }

  }

}
