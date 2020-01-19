package models

import core.{Logger, MapHelper}

final case class SetInfo(name: String, properties: Map[String, String]){
  private def getValue[T](key: String, f: String => Option[T]): Option[T] = {
    properties.get(key).flatMap(v => f(v)) match {
      case Some(value) => Some(value)
      case None =>
        Logger.warn("InfoProperties.getValue", s"could not get $key from properties for set: $name")
        None
    }
  }
}

object SetInfo {
  def apply(name: String, properties: String): SetInfo = {
    SetInfo(name, MapHelper.toMap(properties))
  }
}
