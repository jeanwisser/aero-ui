package models

import core.{Logger, MapHelper}

final case class NamespaceInfo(name: String,
                               objectsCount: Long,
                               storageEngine: String,
                               replicationFactor: Int,
                               memoryUsedBytes: Long,
                               diskUsedBytes: Long,
                               MemoryTotalSize: Long,
                               DiskTotalSize: Long,
                               memoryFreePercent: Int,
                               diskFreePercent: Int)

object NamespaceInfo {
  def apply(name: String, properties: String): NamespaceInfo = {
    val propertiesMap = MapHelper.toMap(properties, ';')

    def getObjects: Long = getValue(propertiesMap, "objects", _.toLong)
    def getStorageEngine: String = getValue(propertiesMap, "storage-engine", _.toString)
    def getReplicationFactor: Int = getValue(propertiesMap, "replication-factor", _.toInt)
    def getMemoryUsedBytes: Long = getValue(propertiesMap, "memory_used_bytes", _.toLong)
    def getDiskUsedBytes: Long = getValue(propertiesMap, "device_used_bytes", _.toLong)
    def getMemoryTotalSize: Long = getValue(propertiesMap, "memory-size", _.toLong)
    def getDiskTotalSize: Long = getValue(propertiesMap, "device_total_bytes", _.toLong)
    def getMemoryFreePercent: Int = getValue(propertiesMap, "memory_free_pct", _.toInt)
    def getDiskFreePercent: Int = getValue(propertiesMap, "device_available_pct", _.toInt)

    NamespaceInfo(name,
                   getObjects,
                   getStorageEngine,
                   getReplicationFactor,
                   getMemoryUsedBytes,
                   getDiskUsedBytes,
                   getMemoryTotalSize,
                   getDiskTotalSize,
                   getMemoryFreePercent,
                   getDiskFreePercent)
  }

  private def getValue[T](properties: Map[String, String], key: String, f: String => T): T = {
    properties.get(key) match {
      case Some(value) => f(value)
      case None => throw new Exception(s"could not get $key from properties : $properties")
    }
  }
}
