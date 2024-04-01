package com.aliyun.test

import org.slf4j.LoggerFactory

/**
 * @description ${description}
 * @author twan
 * @date 2024-03-29 23:24:05
 * @version 1.0
 */
object LogTest {
  val LOG = LoggerFactory.getLogger(LogTest.getClass) // 这个类填自己的类名


  def main(args: Array[String]): Unit = {
    LOG.info("test")
  }

}
