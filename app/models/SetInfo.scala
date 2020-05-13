package models

import controllers.tools.ReadableHelper
import controllers.tools.MapHelper

final case class SetInfo(
    name: String,
    namespace: String,
    objectsCount: Long,
    memoryUsedBytes: Long,
    disableEviction: Boolean,
    enableXdr: String,
    stopWritesCount: Long,
    truncateLut: Long,
    tombstones: Long
) {
  def getMemoryUsedBytesH: String = ReadableHelper.toHumanReadableBytes(memoryUsedBytes)
  def getObjectsCountH: String    = ReadableHelper.toHumanReadableNumber(objectsCount)
}

object SetInfo {
  def apply(properties: String): SetInfo = {
    val propertiesMap               = MapHelper.toMap(properties, ':')
    def getName: String             = MapHelper.getValue(propertiesMap, "set", _.toString)
    def getNamespaceName: String    = MapHelper.getValue(propertiesMap, "ns", _.toString)
    def getObjects: Long            = MapHelper.getValue(propertiesMap, "objects", _.toLong)
    def getMemoryUsedBytes: Long    = MapHelper.getValue(propertiesMap, "memory_data_bytes", _.toLong)
    def getDisableEviction: Boolean = MapHelper.getValue(propertiesMap, "disable-eviction", _.toBoolean)
    def getEnableXdr: String        = MapHelper.getValue(propertiesMap, "set-enable-xdr", _.toString)
    def getStopWritesCount: Long    = MapHelper.getValue(propertiesMap, "stop-writes-count", _.toLong)
    def getTruncateLut: Long        = MapHelper.getValue(propertiesMap, "truncate_lut", _.toLong)
    def getTombstones: Long         = MapHelper.getValue(propertiesMap, "tombstones", _.toLong)

    SetInfo(
      getName,
      getNamespaceName,
      getObjects,
      getMemoryUsedBytes,
      getDisableEviction,
      getEnableXdr,
      getStopWritesCount,
      getTruncateLut,
      getTombstones
    )
  }
}
