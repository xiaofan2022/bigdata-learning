package com.xiaofan.hudi

import com.xiaofan.flink.utils.FlinkUtils

/**
 * @author: twan
 * @date: 2023/9/13 10:51
 * @description:
 */
object SampleSinkHive {

  def main(args: Array[String]): Unit = {
    FlinkUtils.getSampleStreamTableEnvironment("", 111)
  }

}
