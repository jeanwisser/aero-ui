package models

final case class ClusterPage(nodeInfos: List[NodeInfo], namespaces: Map[String, NamespaceInfo])

object ClusterPage {
  def apply(host: String, port: Int): Either[String, ClusterPage] = {
    for {
      context    <- AerospikeContext(host, port)
      namespaces <- context.getNamespacesInformationClusterWide
    } yield ClusterPage(context.nodes, namespaces)
  }
}
