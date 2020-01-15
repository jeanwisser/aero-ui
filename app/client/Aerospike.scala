package client

import com.aerospike.client._
import com.aerospike.client.cluster.{ClusterStats, Node}
import com.aerospike.client.policy.{ClientPolicy, InfoPolicy, QueryPolicy, WritePolicy}
import scala.util.{Failure, Success, Try}

class AerospikeContext(connexion: AerospikeClient) {
  val client: AerospikeClient = connexion

  def get(ns: String, set: String, key: String): Option[Record] = {
    val qPolicy = new QueryPolicy()
    Try(client.get(qPolicy, new Key(ns, set, key))) match {
      case Success(record) => Option(record)
      case Failure(e) => throw new AerospikeException(s"Could not get record in Aerospike: ${e.getMessage}")
    }
  }

  def getNodes: Option[Array[Node]] = {
    Try(client.getNodes) match {
      case Success(nodes) => Option(nodes)
      case Failure(e) => throw new AerospikeException(s"Could not get nodes: ${e.getMessage}")
    }
  }

  def getClusterStats: Option[ClusterStats] = {
    Try(client.getClusterStats) match {
      case Success(stats) => Option(stats)
      case Failure(e) => throw new AerospikeException(s"Could not get cluster statistics: ${e.getMessage}")
    }
  }
}

object AerospikeContext {
  def apply(host: String, port: Int): AerospikeContext = {
    Try(new AerospikeClient(getDefaultPolicy, new Host(host, port))) match {
      case Success(connexion) if connexion.isConnected => new AerospikeContext(connexion)
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