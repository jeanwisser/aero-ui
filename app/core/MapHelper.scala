package core

object MapHelper {
  def toMap(properties: String, splitChar: Char): Map[String, String] = {
    properties.split(splitChar).map(_.split('=')).map(s => s(0) -> s(1)).toMap
  }
}
