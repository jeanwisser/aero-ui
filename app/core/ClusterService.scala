package core

import client.Aerospike
import com.aerospike.client.Info
import com.aerospike.client.cluster.Node
import controllers.routes
import models.{AerospikeRecord, NamespaceInfo, NodeInfo, SetInfo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

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

  def getSetsInformation(node: Node, namespace: String): Either[String, Map[String, SetInfo]] = {
    Try(Info.request(null, node, s"sets/$namespace").split(';')) match {
      case Success(sets) => Right(sets.map(SetInfo(_)).map(s => s.name -> s).toMap)
      case Failure(e) => Left(s"Namespace $namespace does contains any data: $e")
    }
  }

  def getSetInformation(sets: Map[String, SetInfo], set: String): Either[String, SetInfo] = {
    sets.get(set) match {
        case Some(setInfo) => Right(setInfo)
        case None => Left(s"Set $set not found")
      }
  }

  def getNodeInformation(node: Node): NodeInfo = {
    val details = Info.request(null, node)
    NodeInfo(node.getName, node.getHost, node.isActive, details.get("version"), details.get("statistics"))
  }

  def getNodes(client: Aerospike): List[Node] = {
    client.getNodes.map(_.toList).getOrElse(List.empty)
  }

  def getRecord(client: Aerospike, namespace: String, set: String, key: String): Future[Either[String, AerospikeRecord]] = {
    client.get(namespace, set, key).map {
      case Success(optRecord) =>
        optRecord match {
          case Some(record) => Right(AerospikeRecord(key, record))
          case None => Left(s"Could not find key $key in set $set")
        }
      case Failure(e) => Left(s"Error querying namespace $namespace, set $set with key $key: $e")
    }
  }
}
