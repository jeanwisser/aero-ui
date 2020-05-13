package models

import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import controllers.tools.ListHelper
import models.AerospikeContext._
import models.client.Aerospike

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class AerospikeContext(
    val client: Aerospike,
    val nodes: List[Node],
    val namespaces: Map[String, NamespaceInfo]
) {
  lazy val nodesInfo: List[NodeInfo]  = nodes.map(n => getNodeInformation(n))
  lazy val sets: Map[SetKey, SetInfo] = getSetsInformationClusterWide(nodes)

  def getNamespaceInformation(namespace: String): Either[String, NamespaceInfo] = {
    namespaces.get(namespace) match {
      case Some(namespaceInfo) => Right(namespaceInfo)
      case None                => Left(s"Namespace $namespace not found")
    }
  }

  def getSetInformation(namespace: String, set: String): Either[String, SetInfo] = {
    sets.get(SetKey(set, namespace)) match {
      case Some(setInfo) => Right(setInfo)
      case None          => Left(s"Set $set not found")
    }
  }

  def getNamespaceSets(namespace: String): Either[String, Map[SetKey, SetInfo]] = {
    val namespaceSets = sets.filter(s => s._1.namespace == namespace)
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
        val namespaces = getNamespacesInformationClusterWide(nodes)
        Right(new AerospikeContext(client, nodes, namespaces))
    }
  }

  def getNodes(client: Aerospike): List[Node] = {
    client.getNodes.map(_.toList).getOrElse(List.empty)
  }

  def getNodeInformation(node: Node): NodeInfo = {
    val details = Info.request(null, node)
    NodeInfo(node.getName, node.getHost, node.isActive, details.get("build"), details.get("statistics"))
  }

  def getSetsInformationClusterWide(nodes: List[Node]): Map[SetKey, SetInfo] = {
    nodes
      .flatMap(node => getSetsInformation(node))
      .groupBy(_.name)
      .map {
        case (name, sets) =>
          SetKey(name, sets.head.namespace) -> SetInfo(
            name,
            sets.head.namespace,
            sets.map(_.objectsCount).sum,
            sets.map(_.memoryUsedBytes).sum,
            sets.head.disableEviction,
            sets.head.enableXdr,
            sets.map(_.stopWritesCount).sum,
            sets.map(_.truncateLut).max,
            sets.map(_.tombstones).sum
          )
      }
  }

  def getNamespacesInformationClusterWide(nodes: List[Node]): Map[String, NamespaceInfo] = {
    nodes
      .flatMap(node => getNamespacesInformation(node))
      .groupBy(_.name)
      .map {
        case (name, namespaces) =>
          name -> NamespaceInfo(
            name,
            namespaces.map(_.masterObjects).sum,
            namespaces.head.storageEngine,
            namespaces.head.replicationFactor,
            namespaces.map(_.memoryUsedBytes).sum,
            ListHelper.sumListRec[Long](namespaces.map(_.diskUsedBytes), 0),
            namespaces.map(_.memoryTotalSize).sum,
            ListHelper.sumListRec[Long](namespaces.map(_.diskTotalSize), 0),
            namespaces.map(_.memoryFreePercent).sum / namespaces.size,
            ListHelper.sumListRec[Int](namespaces.map(_.diskFreePercent), 0).map(s => s / namespaces.size)
          )
      }
  }

  def getNamespacesInformation(node: Node): Seq[NamespaceInfo] = {
    Info
      .request(null, node, "namespaces")
      .split(';')
      .toSeq
      .map { namespace =>
        val namespaceProperties = Info.request(null, node, s"namespace/$namespace")
        NamespaceInfo(namespace, namespaceProperties)
      }
  }

  def getSetsInformation(node: Node): Seq[SetInfo] = {
    Info
      .request(null, node, s"sets")
      .split(';')
      .filter(!_.equals(""))
      .toSeq
      .map { setProperties =>
        SetInfo(setProperties)
      }
  }
}
