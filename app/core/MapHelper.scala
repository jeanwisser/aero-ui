package core

object MapHelper {
  def toMap(properties: String): Map[String, String] = {
    properties.split(';').map(_.split('=')).map(s => s(0) -> s(1)).toMap
  }
}
