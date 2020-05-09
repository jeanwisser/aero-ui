package models

import controllers.tools.ReadableHelper
import controllers.tools.MapHelper

import scala.util.Try

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
    diskFreePercent: Option[Int],
    sets: Map[String, SetInfo]
) {

  def getMemoryUsedBytesH: String         = ReadableHelper.toHumanReadableBytes(memoryUsedBytes)
  def getMemoryTotalSizeH: String         = ReadableHelper.toHumanReadableBytes(memoryTotalSize)
  def getDiskUsedBytesH: Option[String]   = diskUsedBytes.map(d => ReadableHelper.toHumanReadableBytes(d))
  def getDiskTotalSizeH: Option[String]   = diskTotalSize.map(d => ReadableHelper.toHumanReadableBytes(d))
  def getMemoryUsagePercentage: Int       = 100 - memoryFreePercent
  def getDiskUsagePercentage: Option[Int] = diskFreePercent.map(d => 100 - d)
  def isUsingDisk: Boolean                = diskUsedBytes.isDefined && diskTotalSize.isDefined && diskFreePercent.isDefined
  def getObjectsCountH: String            = ReadableHelper.toHumanReadableNumber(objectsCount)

  def getSetInformation(set: String): Either[String, SetInfo] = {
    sets.get(set) match {
      case Some(setInfo) => Right(setInfo)
      case None          => Left(s"Set $set not found")
    }
  }

  def getNamespaceSets: Either[String, Map[String, SetInfo]] = {
    if (sets.isEmpty) {
      Left(s"Namespace $name does contains any data")
    } else {
      Right(sets)
    }
  }
}

object NamespaceInfo {
  def apply(name: String, properties: String, sets: Map[String, SetInfo]): NamespaceInfo = {
    val propertiesMap                   = MapHelper.toMap(properties, ';')
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
      getDiskFreePercent,
      sets
    )
  }
}
