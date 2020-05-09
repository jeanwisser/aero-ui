package models

import models.client.Aerospike
import com.aerospike.client.Info
import com.aerospike.client.cluster.Node

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

final case class AerospikeContext(
    client: Aerospike,
    nodes: List[NodeInfo],
    namespaces: Map[String, NamespaceInfo]
) {

  def getNamespaceInformation(namespace: String): Either[String, NamespaceInfo] = {
    namespaces.get(namespace) match {
      case Some(namespaceInfo) => Right(namespaceInfo)
      case None                => Left(s"Namespace $namespace not found")
    }
  }

  def getRecord(namespace: String, set: String, key: String): Future[Either[String, AerospikeRecord]] = {
    client.get(namespace, set, key).map {
      case Success(optRecord) =>
        optRecord match {
          case Some(record) => Right(AerospikeRecord(Some(key), record))
          case None         => Left(s"Could not find key $key in set $set")
        }
      case Failure(e) => Left(s"Error querying namespace $namespace, set $set with key $key: $e")
    }
  }

  def getSetPreview(namespace: String, set: String): Future[Either[String, Iterable[AerospikeRecord]]] = {
    client.scan(namespace, set, 10).map {
      case Success(records) =>
        Right(records.map {
          case (key, record) => AerospikeRecord(key, record)
        })
      case Failure(e) => Left(s"Error scanning namespace $namespace, set $set: $e")
    }
  }
}

object AerospikeContext {
  def apply(host: String, port: Int): Try[AerospikeContext] = {
    Aerospike(SeedNode(host, port)).map { client =>
      val nodes      = getNodes(client)
      val namespaces = getNamespacesInformation(nodes.head)
      new AerospikeContext(client, nodes.map(n => getNodeInformation(n)), namespaces)
    }
  }

  def getNodes(client: Aerospike): List[Node] = {
    client.getNodes.map(_.toList).getOrElse(List.empty)
  }

  def getNodeInformation(node: Node): NodeInfo = {
    val details = Info.request(null, node)
    NodeInfo(node.getName, node.getHost, node.isActive, details.get("build"), details.get("statistics"))
  }

  def getNamespacesInformation(node: Node): Map[String, NamespaceInfo] = {
    Info
      .request(null, node, "namespaces")
      .split(';')
      .map { namespace =>
        val namespaceProperties = Info.request(null, node, s"namespace/$namespace")
        val sets                = getSetsInformation(node, namespace)
        NamespaceInfo(namespace, namespaceProperties, sets)
      }
      .map(n => (n.name, n))
      .toMap
  }

  def getSetsInformation(node: Node, namespace: String): Map[String, SetInfo] = {
    Info
      .request(null, node, s"sets/$namespace")
      .split(';')
      .filter(!_.equals(""))
      .map { setProperties =>
        SetInfo(setProperties)
      }
      .map(n => (n.name, n))
      .toMap
  }
}
