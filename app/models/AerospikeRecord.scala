package models

import com.aerospike.client.Record
import controllers.tools.ReadableHelper

import scala.jdk.CollectionConverters._

case class AerospikeRecord (key: String, bins: Map[String, AnyRef], generation: Int, expiration: Int){
  def getExpirationH: String = ReadableHelper.toHumanReadableDate(expiration.toLong * 1000)
}

object AerospikeRecord{
  def apply(key: String, record: Record): AerospikeRecord = {
    AerospikeRecord(key, record.bins.asScala.toMap, record.generation, record.expiration)
  }
}
