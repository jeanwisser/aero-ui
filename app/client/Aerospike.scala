package client

import com.aerospike.client._
import com.aerospike.client.cluster.{ClusterStats, Node}
import com.aerospike.client.policy.{ClientPolicy, InfoPolicy, QueryPolicy, WritePolicy}

import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Aerospike(connexion: AerospikeClient) {
  val client: AerospikeClient = connexion

  def get(ns: String, set: String, key: String): Future[Option[Record]] = Future {
    val qPolicy = new QueryPolicy()
    Try(client.get(qPolicy, new Key(ns, set, key))) match {
      case Success(record) => Option(record)
      case Failure(e) => throw new AerospikeException(s"Could not get record in Aerospike: ${e.getMessage}")
    }
  }

  def throwExce: Array[Node] = {
    val res: Array[Node] = Array.empty
    throw new Exception("bug")
    res
  }

  def getNodes: Future[Option[Array[Node]]] = Future {
    Try(client.getNodes) match {
      case Success(nodes) => Option(nodes)
      case Failure(e) => throw new AerospikeException(s"Could not get nodes: ${e.getMessage}")
    }
  }

  def getClusterStats: Future[Option[ClusterStats]] = Future {
    Try(client.getClusterStats) match {
      case Success(stats) => Option(stats)
      case Failure(e) => throw new AerospikeException(s"Could not get cluster statistics: ${e.getMessage}")
    }
  }
}

object Aerospike {
  def apply(host: String, port: Int): Aerospike = {
    Try(new AerospikeClient(getDefaultPolicy, new Host(host, port))) match {
      case Success(connexion) if connexion.isConnected => new Aerospike(connexion)
      case Success(connexion) if !connexion.isConnected => throw new AerospikeException(s"Could not connect to Aerospike : ${connexion.getClusterStats}")
      case Failure(e) => throw new AerospikeException(s"Could not connect to Aerospike : ${e.getMessage}")
    }
  }

  def getDefaultPolicy: ClientPolicy = {
    val cPolicy = new ClientPolicy()
    cPolicy.timeout = 60000
    cPolicy.maxConnsPerNode = 10000
    cPolicy
  }
}