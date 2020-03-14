package models

import core.{Logger, MapHelper}

final case class SetInfo(name: String, objectsCount: Long, memoryUsedBytes: Long){
}

object SetInfo {
  def apply(properties: String): SetInfo = {
    val propertiesMap = MapHelper.toMap(properties, ':')
    def getName: String = getValue(propertiesMap, "set", _.toString)
    def getObjects: Long = getValue(propertiesMap, "objects", _.toLong)
    def getMemoryUsedBytes: Long = getValue(propertiesMap, "memory_data_bytes", _.toLong)
    SetInfo(getName, getObjects, getMemoryUsedBytes)
  }

  private def getValue[T](properties: Map[String, String], key: String, f: String => T): T = {
    properties.get(key) match {
      case Some(value) => f(value)
      case None => throw new Exception(s"could not get $key from properties : $properties")
    }
  }
}
