package models.client

import com.aerospike.client._
import com.aerospike.client.cluster.{ClusterStats, Node}
import com.aerospike.client.policy.{ClientPolicy, InfoPolicy, QueryPolicy, WritePolicy}
import models.SeedNode

import scala.collection.mutable
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class Aerospike(connexion: AerospikeClient) {
  val client: AerospikeClient = connexion

  def get(ns: String, set: String, key: String): Future[Try[Option[Record]]] = Future {
    val qPolicy = new QueryPolicy()
    Try(client.get(qPolicy, new Key(ns, set, key))).map(Option(_))
  }

  def getNodes: Try[Array[Node]] = Try(client.getNodes)

  def getClusterStats: Try[ClusterStats] = Try(client.getClusterStats)
}

object Aerospike {
  private val connectionPool = new mutable.HashMap[String, Aerospike]

  def apply(seedNode: SeedNode): Try[Aerospike] = {
    val aerospikeHost = new Host(seedNode.host, seedNode.port)
    if (!connectionPool.contains(aerospikeHost.toString)) {
      Try(new AerospikeClient(getDefaultPolicy, aerospikeHost)) match {
        case Success(connexion) if connexion.isConnected =>
          connectionPool.addOne((aerospikeHost.toString, new Aerospike(connexion))); Success(connectionPool(aerospikeHost.toString))
        case Success(connexion) if !connexion.isConnected =>
          Failure(new AerospikeException(s"Could not connect to Aerospike : ${connexion.getClusterStats}"))
        case Failure(e) => Failure(new AerospikeException(s"Could not connect to Aerospike : ${e}"))
      }
    } else {
      Success(connectionPool(aerospikeHost.toString))
    }
  }

  def getDefaultPolicy: ClientPolicy = {
    val cPolicy = new ClientPolicy()
    cPolicy.timeout = 60000
    cPolicy.maxConnsPerNode = 10000
    cPolicy
  }
}
