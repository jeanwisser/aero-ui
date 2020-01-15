package core

import client.Aerospike
import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import models.{NamespaceInfo, NodeInfo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ClusterOverview {

  def getNamespacesInformation(nodes: Iterable[Node]): Map[String, NamespaceInfo] = {
    val namespacesInfo = nodes.flatMap { node =>
      val namespaces = Info.request(null, node, "namespaces").split(';')
      namespaces.map { namespace =>
        NamespaceInfo(namespace, Info.request(null, node, s"namespace/$namespace"))
      }
    }
    (namespacesInfo.toList :+ NamespaceInfo("ok", "ok=0")).map(n => n.name -> n).toMap
  }

  def getNodeInformation(node: Node): NodeInfo = {
    val details = Info.request(null, node)
    NodeInfo(node.getName, node.getHost, node.isActive, details.get("version"), details.get("statistics"))
  }

  def getNodes(seedNodeHost: String, seedNodePort: Int): Future[List[Node]] = {
    val client = Aerospike(seedNodeHost, seedNodePort)
    client.getNodes.map(_.map(_.toList).getOrElse(List.empty))
  }
}
