package models

import controllers.tools.ReadableHelper
import controllers.tools.MapHelper

import scala.util.Try

final case class NamespaceInfo(
    name: String,
    masterObjects: Long,
    storageEngine: String,
    replicationFactor: Int,
    memoryUsedBytes: Long,
    diskUsedBytes: Option[Long],
    memoryTotalSize: Long,
    diskTotalSize: Option[Long],
    memoryFreePercent: Int,
    diskFreePercent: Option[Int]
) {

  def getMemoryUsedBytesH: String         = ReadableHelper.toHumanReadableBytes(memoryUsedBytes)
  def getMemoryTotalSizeH: String         = ReadableHelper.toHumanReadableBytes(memoryTotalSize)
  def getDiskUsedBytesH: Option[String]   = diskUsedBytes.map(d => ReadableHelper.toHumanReadableBytes(d))
  def getDiskTotalSizeH: Option[String]   = diskTotalSize.map(d => ReadableHelper.toHumanReadableBytes(d))
  def getMemoryUsagePercentage: Int       = 100 - memoryFreePercent
  def getDiskUsagePercentage: Option[Int] = diskFreePercent.map(d => 100 - d)
  def isUsingDisk: Boolean                = diskUsedBytes.isDefined && diskTotalSize.isDefined && diskFreePercent.isDefined
  def getMasterObjectsCountH: String      = ReadableHelper.toHumanReadableNumber(masterObjects)
  def getReplicasObjectsCountH: String    = ReadableHelper.toHumanReadableNumber(Math.max(masterObjects * (replicationFactor - 1), 0))
}

object NamespaceInfo {
  def apply(name: String, properties: String): NamespaceInfo = {
    val propertiesMap                   = MapHelper.toMap(properties, ';')
    def getMasterObjects: Long          = MapHelper.getValue(propertiesMap, "master_objects", _.toLong)
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
      getMasterObjects,
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
