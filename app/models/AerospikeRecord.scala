package models

import com.aerospike.client.Record
import scala.jdk.CollectionConverters._

case class AerospikeRecord (key: String, bins: Map[String, AnyRef], generation: Int, expiration: Int)

object AerospikeRecord{
  def apply(key: String, record: Record): AerospikeRecord = {
    AerospikeRecord(key, record.bins.asScala.toMap, record.generation, record.expiration)
  }
}
