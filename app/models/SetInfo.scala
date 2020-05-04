package models

import core.Logger
import core.MapHelper

import scala.util.Try
import core.Extensions._

final case class SetInfo(
    name: String,
    objectsCount: Long,
    memoryUsedBytes: Long,
    diskUsedBytes: Option[Long],
    disableEviction: Boolean,
    enableXdr: String,
    stopWritesCount: Long,
    truncateLut: Long,
    tombstones: Long
){
  def getMemoryUsedBytesH = memoryUsedBytes.toHumanReadableBytes
  def getDiskUsedBytesH = diskUsedBytes.map(d => d.toHumanReadableBytes)
}

object SetInfo {
  def apply(properties: String): SetInfo = {
    val propertiesMap                  = MapHelper.toMap(properties, ':')
    def getName: String                = MapHelper.getValue(propertiesMap, "set", _.toString)
    def getObjects: Long               = MapHelper.getValue(propertiesMap, "objects", _.toLong)
    def getMemoryUsedBytes: Long       = MapHelper.getValue(propertiesMap, "memory_data_bytes", _.toLong)
    def getDiskUsedBytes: Option[Long] = Try(MapHelper.getValue(propertiesMap, "disk_data_bytes", _.toLong)).toOption
    def getDisableEviction: Boolean    = MapHelper.getValue(propertiesMap, "disable-eviction", _.toBoolean)
    def getEnableXdr: String           = MapHelper.getValue(propertiesMap, "set-enable-xdr", _.toString)
    def getStopWritesCount: Long       = MapHelper.getValue(propertiesMap, "stop-writes-count", _.toLong)
    def getTruncateLut: Long           = MapHelper.getValue(propertiesMap, "truncate_lut", _.toLong)
    def getTombstones: Long            = MapHelper.getValue(propertiesMap, "tombstones", _.toLong)
    SetInfo(
      getName,
      getObjects,
      getMemoryUsedBytes,
      getDiskUsedBytes,
      getDisableEviction,
      getEnableXdr,
      getStopWritesCount,
      getTruncateLut,
      getTombstones
    )
  }
}
