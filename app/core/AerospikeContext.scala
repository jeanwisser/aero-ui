package core

import client.Aerospike
import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import models.AerospikeRecord
import models.NamespaceInfo
import models.NodeInfo
import models.SeedNode
import models.SetInfo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

final case class AerospikeContext(
    client: Aerospike,
    nodes: List[NodeInfo],
    namespaces: Map[String, NamespaceInfo],
    sets: Map[String, SetInfo]
) {

  def getNamespaceInformation(namespace: String): Either[String, NamespaceInfo] = {
    namespaces.get(namespace) match {
      case Some(namespaceInfo) => Right(namespaceInfo)
      case None                => Left(s"Namespace $namespace not found")
    }
  }

  def getSetInformation(set: String): Either[String, SetInfo] = {
    sets.get(set) match {
      case Some(setInfo) => Right(setInfo)
      case None          => Left(s"Set $set not found")
    }
  }

  def getNamespaceSets(namespace: String): Either[String, Map[String, SetInfo]] = {
    val namespaceSets = sets.filter(s => s._2.namespace == namespace)
    if (namespaceSets.isEmpty) {
      Left(s"Namespace $namespace does contains any data")
    } else {
      Right(namespaceSets)
    }
  }

  def getRecord(namespace: String, set: String, key: String): Future[Either[String, AerospikeRecord]] = {
    client.get(namespace, set, key).map {
      case Success(optRecord) =>
        optRecord match {
          case Some(record) => Right(AerospikeRecord(key, record))
          case None         => Left(s"Could not find key $key in set $set")
        }
      case Failure(e) => Left(s"Error querying namespace $namespace, set $set with key $key: $e")
    }
  }
}

object AerospikeContext {
  def apply(host: String, port: Int): Try[AerospikeContext] = {
    Aerospike(SeedNode(host, port)).map { client =>
      val nodes      = getNodes(client)
      val namespaces = getNamespacesInformation(nodes.head)
      val sets       = namespaces.flatMap(n => getSetsInformation(nodes.head, n._1))
      new AerospikeContext(client, nodes.map(n => getNodeInformation(n)), namespaces, sets)
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
        NamespaceInfo(namespace, Info.request(null, node, s"namespace/$namespace"))
      }
      .map(n => (n.name, n))
      .toMap
  }

  def getSetsInformation(node: Node, namespace: String): Map[String, SetInfo] = {
    Info
      .request(null, node, s"sets/$namespace")
      .split(';')
      .filter(!_.equals(""))
      .map { set =>
        SetInfo(namespace, set)
      }
      .map(n => (n.name, n))
      .toMap
  }
}
