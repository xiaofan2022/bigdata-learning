package com.xiaofan.utils

/**
 * @author: twan
 * @date: 2023/9/1 17:30
 * @description:
 */
object CommonUtils {

  def main(args: Array[String]): Unit = {
    println(getCurrentModelPath())
    println(getCurrentCKPath())
  }

  def getCurrentCKPath(): String = {
    getCurrentModelPath() + "/checkpoint"
  }

  def getCurrentModelPath() = {
    val path: String = this.getClass.getClassLoader.getResource("./").getPath
    path.substring(0, path.indexOf("target") - 1)
  }

  def getCurrentModelPath(classLoader: ClassLoader) = {
    val path: String = classLoader.getResource("./").getPath
    path.substring(0, path.indexOf("target") - 1)

  }


}
