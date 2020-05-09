package models.client

import com.aerospike.client._
import com.aerospike.client.cluster.ClusterStats
import com.aerospike.client.cluster.Node
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.client.policy.InfoPolicy
import com.aerospike.client.policy.QueryPolicy
import com.aerospike.client.policy.WritePolicy
import models.SeedNode

import scala.collection.mutable
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class Aerospike(connexion: AerospikeClient) {
  def get(ns: String, set: String, key: String): Future[Try[Option[Record]]] = Future {
    val qPolicy = new QueryPolicy()
    Try(connexion.get(qPolicy, new Key(ns, set, key))).map(Option(_))
  }
  def getNodes: Try[Array[Node]] = Try(connexion.getNodes)
  def close(): Unit              = connexion.close()
}

object Aerospike {
  private val connectionPool = new mutable.HashMap[String, Aerospike]

  def apply(seedNode: SeedNode): Try[Aerospike] = {
    val aerospikeHost = new Host(seedNode.host, seedNode.port)
    if (!connectionPool.contains(aerospikeHost.toString)) {
      Try(new AerospikeClient(getDefaultPolicy, aerospikeHost)) match {
        case Success(connexion) if connexion.isConnected =>
          connectionPool.addOne((aerospikeHost.toString, new Aerospike(connexion)))
          Success(connectionPool(aerospikeHost.toString))
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
    cPolicy.timeout = 10000
    cPolicy.maxConnsPerNode = 1
    cPolicy
  }

  def deleteConnectionFromPool(seedNode: SeedNode): Unit = {
    val aerospikeHost = new Host(seedNode.host, seedNode.port)
    if (connectionPool.contains(aerospikeHost.toString)) {
      connectionPool.get(aerospikeHost.toString).foreach { connection =>
        connection.close()
      }
      connectionPool.remove(aerospikeHost.toString)
    }
  }
}
