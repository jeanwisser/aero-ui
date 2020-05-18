package models

import com.aerospike.client.cluster.Node
import controllers.tools.ListHelper
import models.client.Aerospike

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AerospikeContext(
    val client: Aerospike,
    val nodes: List[NodeInfo]
) {

  def getNamespacesInformationClusterWide: Either[String, Map[String, NamespaceInfo]] = {
    Try(
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
    ) match {
      case Failure(exception)  => Left(exception.toString)
      case Success(namespaces) => Right(namespaces)
    }
  }

  def getNamespaceSets(namespace: String): Either[String, Map[String, SetInfo]] = {
    Try(
      nodes
        .flatMap(node => getSetsInformation(namespace, node))
        .groupBy(_.name)
        .map {
          case (name, sets) =>
            name -> SetInfo(
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
    ) match {
      case Failure(exception)            => Left(exception.toString)
      case Success(sets) if sets.isEmpty => Left(s"Namespace $namespace does contains any data")
      case Success(sets)                 => Right(sets)
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

  private def getNamespacesInformation(node: NodeInfo): Set[NamespaceInfo] = {
    node.namespaces
      .map { namespace =>
        val namespaceProperties = client.getInfo(node.aerospikeNode, s"namespace/$namespace")
        NamespaceInfo(namespace, namespaceProperties)
      }
  }

  private def getSetsInformation(namespace: String, node: NodeInfo): Seq[SetInfo] = {
    client.getInfo(node.aerospikeNode, s"sets/$namespace")
      .split(';')
      .filter(!_.equals(""))
      .toSeq
      .map { setProperties =>
        SetInfo(setProperties)
      }
  }
}

object AerospikeContext {
  def apply(host: String, port: Int): Either[String, AerospikeContext] = {
    val context = for {
      client    <- Aerospike(SeedNode(host, port))
      nodes     <- client.getNodes.map(_.toList)
      nodeInfos <- getNodesInformation(client, nodes)
    } yield new AerospikeContext(client, nodeInfos)
    context match {
      case Failure(exception) => Left(exception.toString)
      case Success(context)   => Right(context)
    }
  }

  private def getNodesInformation(client: Aerospike, nodes: List[Node]): Try[List[NodeInfo]] = {
    Try(nodes.map { node =>
      val details = client.getInfo(node, List("build", "statistics", "namespaces"))
      NodeInfo(node.getName, node.getHost, node.isActive, details("build"), details("statistics"), details("namespaces"), node)
    })
  }
}
