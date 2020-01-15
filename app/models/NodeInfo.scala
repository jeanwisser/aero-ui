package models

import com.aerospike.client.Host
import core.MapHelper

final case class NodeInfo(name: String, host: Host, isActive: Boolean, version: String, statistics: Map[String, String])

object NodeInfo {
  def apply(name: String, host: Host, isActive: Boolean, version: String, statistics: String): NodeInfo =
    NodeInfo(name, host, isActive, version, MapHelper.toMap(statistics))
}