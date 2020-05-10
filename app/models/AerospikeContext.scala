package models

import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import models.client.Aerospike

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class AerospikeContext(
    val client: Aerospike,
    val nodes: List[NodeInfo],
    val namespaces: Map[String, NamespaceInfo]
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
          case Some(record) => Right(AerospikeRecord(key, record))
          case None         => Left(s"Could not find key $key in set $set")
        }
      case Failure(exception) => Left(s"Error querying set $set with key $key: $exception")
    }
  }

  def deleteRecord(namespace: String, set: String, key: String): Future[Either[String, Unit]] = {
    client.delete(namespace, set, key).map {
      case Success(result) =>
        if (result) Right(()) else Left(s"Could not find record with key $key in set $set")
      case Failure(exception) => Left(s"An error occurred while deleting record with key $key: $exception")
    }
  }
}

object AerospikeContext {
  def apply(host: String, port: Int): Either[String, AerospikeContext] = {
    Aerospike(SeedNode(host, port)) match {
      case Failure(exception) => Left(exception.getMessage)
      case Success(client) =>
        val nodes      = getNodes(client)
        val namespaces = getNamespacesInformation(nodes.head)
        Right(new AerospikeContext(client, nodes.map(n => getNodeInformation(n)), namespaces))
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
