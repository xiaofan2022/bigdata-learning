package com.xiaofan.flink

object ScalaStyleTest {

  def main(args: Array[String]): Unit = {}

  def getMessage(message: String): Unit = {
    val sql =
      """
        |select * from 
        |student
        |""".stripMargin
    this.getClass.getClassLoader.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent.getParent
    println("atest")
    if (true) println("test") else println("aaa")
    val foo = false
    val bar = true
    var baz = null
    if (foo && bar) {
      baz
    }
  }

}
