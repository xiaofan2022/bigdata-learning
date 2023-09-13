package com.xiaofan.flink.utils

import scala.reflect.runtime.universe.{TermSymbol, runtimeMirror}

/**
 * @author: twan
 * @date: 2023/9/1 14:17
 * @description:
 */
object ReflectUtils {

  // 使用反射获取属性值
  def getCaseClassFieldValues(obj: Any): Map[String, Any] = {
    val mirror = runtimeMirror(obj.getClass.getClassLoader)
    val instanceMirror = mirror.reflect(obj)
    val members = instanceMirror.symbol.typeSignature.members.collect {
      case m: TermSymbol if m.isVal || m.isVar =>
        m.name.toString.trim -> instanceMirror.reflectField(m)
    }.toMap

    members.map { case (fieldName, fieldMirror) =>
      (fieldName, fieldMirror.get)
    }
  }

}
