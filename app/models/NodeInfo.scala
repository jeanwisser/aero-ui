package models

import com.aerospike.client.Host
import com.aerospike.client.cluster.Node
import controllers.tools.MapHelper

final case class NodeInfo(
    name: String,
    host: Host,
    isActive: Boolean,
    build: String,
    clusterSize: Int,
    clientConnections: Long,
    migratePartitionsRemaining: Long,
    namespaces: Set[String],
    aerospikeNode: Node
)

object NodeInfo {
  def apply(name: String, host: Host, isActive: Boolean, build: String, statistics: String, namespaces: String, node: Node): NodeInfo = {
    val propertiesMap                       = MapHelper.toMap(statistics, ';')
    def getClusterSize: Int                 = MapHelper.getValue(propertiesMap, "cluster_size", _.toInt)
    def getClientConnections: Long          = MapHelper.getValue(propertiesMap, "client_connections", _.toLong)
    def getMigratePartitionsRemaining: Long = MapHelper.getValue(propertiesMap, "migrate_partitions_remaining", _.toLong)
    NodeInfo(
      name,
      host,
      isActive,
      build,
      getClusterSize,
      getClientConnections,
      getMigratePartitionsRemaining,
      namespaces.split(";").toSet,
      node
    )
  }
}
