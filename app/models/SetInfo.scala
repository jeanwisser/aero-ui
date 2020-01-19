package models

import core.MapHelper

final case class SetInfo(name: String, properties: Map[String, String])

object SetInfo {
  def apply(name: String, properties: String): SetInfo = {
    SetInfo(name, MapHelper.toMap(properties))
  }
}
