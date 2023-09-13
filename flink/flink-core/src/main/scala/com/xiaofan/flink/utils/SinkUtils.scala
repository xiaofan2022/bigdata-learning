package com.xiaofan.flink.utils

import com.google.common.base.CaseFormat
import com.xiaofan.utils.BeanUtils
import org.apache.flink.connector.jdbc.{
  JdbcConnectionOptions,
  JdbcExecutionOptions,
  JdbcSink,
  JdbcStatementBuilder
}
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.slf4j.{Logger, LoggerFactory}

import java.sql.PreparedStatement
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.reflect.runtime.universe.runtimeMirror

object SinkUtils {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val jdbcDriverName: String = "com.mysql.jdbc.Driver"

  def getJDBCSink[T](
      sql: String,
      dataBaseName: String,
      jdbcStatementBuilder: JdbcStatementBuilder[T],
      batchSize: Int = 200): SinkFunction[T] = {
    val jdbcConnectionOptionsBuilder = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
      .withUrl("jdbc:mysql://hadoop101:3306/%s?useUnicode=true&characterEncoding=UTF-8".format(
        dataBaseName))
      .withUsername("root")
      .withPassword("123456")
    JdbcSink.sink(
      sql,
      jdbcStatementBuilder,
      JdbcExecutionOptions.builder().withBatchSize(batchSize).build(),
      jdbcConnectionOptionsBuilder.withDriverName(jdbcDriverName).build())
  }

  def main(args: Array[String]): Unit = {
    val student: Student = Student(1, "萧仲昂", 12)
    val goodsInfoLatestSql =
      """
        |insert INTO student ( id, name, age)
        |VALUES
        |	(?,?,?)
        |""".stripMargin
    createStatement(goodsInfoLatestSql, student, null)
  }

  def createStatement(sql: String, obj: AnyRef, statement: PreparedStatement): Unit = {
    val isCaseClass: Boolean =
      runtimeMirror(obj.getClass.getClassLoader).classSymbol(obj.getClass).isCaseClass
    var valueMap: Map[String, Any] = null
    if (isCaseClass) {
      valueMap = ReflectUtils.getCaseClassFieldValues(obj)
    } else {
      valueMap = BeanUtils
        .getMethods(obj, "get")
        .asScala
        .map(t => t._1.toLowerCase -> t._2.invoke(obj))
        .toMap
    }
    val newSql: String = sql.replace("`", "").replace("\\s+", "")
    val strings: Array[String] = newSql
      .substring(newSql.indexOf("(") + 1, newSql.indexOf(")"))
      .split(",")
      .map(t => CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, t.trim).toLowerCase)
    strings.zipWithIndex.foreach(t => {
      val index: Int = t._2 + 1
      if (valueMap.contains(t._1)) {
        val value: Any = valueMap.get(t._1).get
        value match {
          case x: java.lang.Long => {
            if (value != null) {
              statement.setLong(index, value.asInstanceOf[java.lang.Long])
            } else {
              statement.setLong(index, 0L)
            }
          }
          case y: java.lang.String =>
            statement.setString(index, value.asInstanceOf[java.lang.String])
          case z: java.lang.Integer => {
            if (value != null) {
              statement.setInt(index, value.asInstanceOf[java.lang.Integer])
            } else {
              statement.setInt(index, 0)
            }
          }
          case b: java.math.BigDecimal =>
            statement.setBigDecimal(index, value.asInstanceOf[java.math.BigDecimal])
        }
      } else {
        logger.info("filed not found {}", t._1)
      }
    })
  }

}
