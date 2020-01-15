package models

import core.{Logger, MapHelper}

final case class NamespaceInfo(name: String, properties: Map[String, String]) {
  def getObjects: Option[Long] = getValue("objects", _.toLongOption)
  def getStorageEngine: Option[String] = getValue("storage-engine", x => Option(x.toString))
  def getReplicationFactor: Option[Int] = getValue("replication-factor", _.toIntOption)
  def getMemoryUsedBytes: Option[Long] = getValue("memory_used_bytes", _.toLongOption)
  def getDiskUsedBytes: Option[Long] = getValue("disk_used_bytes", _.toLongOption)
  def getMemoryTotalSize: Option[Long] = getValue("memory-size", _.toLongOption)
  def getDiskTotalSize: Option[Long] = getValue("device_total_bytes", _.toLongOption)
  def getMemoryFreePercent: Option[Int] = getValue("memory_free_pct", _.toIntOption)
  def getDiskFreePercent: Option[Int] = getValue("device_available_pct", _.toIntOption)

  private def getValue[T](key: String, f: String => Option[T]): Option[T] = {
    properties.get(key).flatMap(v => f(v)) match {
      case Some(value) => Some(value)
      case None =>
        Logger.warn("InfoProperties.getValue", s"could not get $key from properties for namespace: $name")
        None
    }
  }
}

object NamespaceInfo {
  def apply(name: String, properties: String): NamespaceInfo = {
    NamespaceInfo(name, MapHelper.toMap(properties))
  }
}
