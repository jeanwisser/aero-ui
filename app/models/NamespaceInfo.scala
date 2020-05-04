package models

import core.MapHelper
import scala.util.Try
import core.Extensions._

final case class NamespaceInfo(
    name: String,
    objectsCount: Long,
    storageEngine: String,
    replicationFactor: Int,
    memoryUsedBytes: Long,
    diskUsedBytes: Option[Long],
    memoryTotalSize: Long,
    diskTotalSize: Option[Long],
    memoryFreePercent: Int,
    diskFreePercent: Option[Int]
){

  def getMemoryUsedBytesH = memoryUsedBytes.toHumanReadableBytes
  def getMemoryTotalSizeH = memoryTotalSize.toHumanReadableBytes
  def getDiskUsedBytesH = diskUsedBytes.map(d => d.toHumanReadableBytes)
  def getDiskTotalSizeH = diskTotalSize.map(d => d.toHumanReadableBytes)
  def getMemoryUsagePercentage = 100 - memoryFreePercent
  def getDiskUsagePercentage = diskFreePercent.map(d => 100 - d)
  def isUsingDisk = diskUsedBytes.isDefined && diskTotalSize.isDefined && diskFreePercent.isDefined
}

object NamespaceInfo {
  def apply(name: String, properties: String): NamespaceInfo = {
    val propertiesMap = MapHelper.toMap(properties, ';')

    def getObjects: Long                = MapHelper.getValue(propertiesMap, "objects", _.toLong)
    def getStorageEngine: String        = MapHelper.getValue(propertiesMap, "storage-engine", _.toString)
    def getReplicationFactor: Int       = MapHelper.getValue(propertiesMap, "replication-factor", _.toInt)
    def getMemoryUsedBytes: Long        = MapHelper.getValue(propertiesMap, "memory_used_bytes", _.toLong)
    def getDiskUsedBytes: Option[Long]  = Try(MapHelper.getValue(propertiesMap, "device_used_bytes", _.toLong)).toOption
    def getMemoryTotalSize: Long        = MapHelper.getValue(propertiesMap, "memory-size", _.toLong)
    def getDiskTotalSize: Option[Long]  = Try(MapHelper.getValue(propertiesMap, "device_total_bytes", _.toLong)).toOption
    def getMemoryFreePercent: Int       = MapHelper.getValue(propertiesMap, "memory_free_pct", _.toInt)
    def getDiskFreePercent: Option[Int] = Try(MapHelper.getValue(propertiesMap, "device_available_pct", _.toInt)).toOption

    NamespaceInfo(
      name,
      getObjects,
      getStorageEngine,
      getReplicationFactor,
      getMemoryUsedBytes,
      getDiskUsedBytes,
      getMemoryTotalSize,
      getDiskTotalSize,
      getMemoryFreePercent,
      getDiskFreePercent
    )
  }
}
