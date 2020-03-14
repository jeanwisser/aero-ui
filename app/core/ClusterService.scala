package core

import client.Aerospike
import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import models.{NamespaceInfo, NodeInfo, SetInfo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object ClusterService {

  def getNamespacesInformation(node: Node): Map[String, NamespaceInfo] = {
    val namespaces = Info.request(null, node, "namespaces").split(';')
    namespaces
      .map { namespace =>
        NamespaceInfo(namespace, Info.request(null, node, s"namespace/$namespace"))
      }
      .map(n => (n.name, n))
      .toMap
  }

  def getSetsInformation(node: Node, namespace: String): Try[Map[String, SetInfo]] = {
    val sets = Info.request(null, node, s"sets/$namespace").split(';')
    Try(
      sets
        .map(SetInfo(_))
        .map(n => n.name -> n)
        .toMap)
  }

  def getNodeInformation(node: Node): NodeInfo = {
    val details = Info.request(null, node)
    NodeInfo(node.getName, node.getHost, node.isActive, details.get("version"), details.get("statistics"))
  }

  def getNodes(client: Aerospike): Future[List[Node]] = {
    client.getNodes.map(_.map(_.toList).getOrElse(List.empty))
  }
}