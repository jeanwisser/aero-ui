package controllers.tools

object MapHelper {
  def toMap(properties: String, splitChar: Char): Map[String, String] = {
    properties.split(splitChar).map(_.split('=')).map(s => s(0) -> s(1)).toMap
  }

  def getValue[T](properties: Map[String, String], key: String, f: String => T): T = {
    properties.get(key) match {
      case Some(value) => f(value)
      case None => throw new Exception(s"could not get $key from properties : $properties")
    }
  }
}
