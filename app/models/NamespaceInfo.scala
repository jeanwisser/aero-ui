package models

import controllers.tools.{BytesHelper, MapHelper}

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

  def getMemoryUsedBytesH      = BytesHelper.toHumanReadableBytes(memoryUsedBytes)
  def getMemoryTotalSizeH      = BytesHelper.toHumanReadableBytes(memoryTotalSize)
  def getDiskUsedBytesH        = diskUsedBytes.map(d => BytesHelper.toHumanReadableBytes(d))
  def getDiskTotalSizeH        = diskTotalSize.map(d => BytesHelper.toHumanReadableBytes(d))
  def getMemoryUsagePercentage = 100 - memoryFreePercent
  def getDiskUsagePercentage   = diskFreePercent.map(d => 100 - d)
  def isUsingDisk              = diskUsedBytes.isDefined && diskTotalSize.isDefined && diskFreePercent.isDefined

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
