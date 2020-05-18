package models.client

import java.util

import com.aerospike.client._
import com.aerospike.client.cluster.Node
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.client.policy.InfoPolicy
import com.aerospike.client.policy.QueryPolicy
import com.aerospike.client.policy.WritePolicy
import models.SeedNode
import Aerospike._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.jdk.CollectionConverters._

class Aerospike(val connexion: AerospikeClient) {

  def get(ns: String, set: String, key: String): Future[Try[Option[Record]]] = Future {
    val qPolicy = new QueryPolicy()
    qPolicy.setTimeout(3000)
    Try(connexion.get(qPolicy, new Key(ns, set, key))).map(Option(_))
  }

  def delete(ns: String, set: String, key: String): Future[Try[Boolean]] = Future {
    val wPolicy = new WritePolicy()
    wPolicy.setTimeout(3000)
    Try(connexion.delete(wPolicy, new Key(ns, set, key)))
  }

  def getInfo(node: Node, parameter: String): String = {
    Info.request(getInfoPolicy, node, parameter)
  }

  def getInfo(node: Node, parameters: List[String]): Map[String, String] = {
    val connection = node.getConnection(getInfoPolicy.timeout)
    Try(Info.request(connection, parameters.asJava)) match {
      case Success(result) =>
        node.putConnection(connection)
        result.asScala.toMap
      case Failure(exception) =>
        node.closeConnection(connection)
        throw exception
    }
  }

  def getNodes: Try[Array[Node]] = Try(connexion.getNodes)
  def close(): Unit              = connexion.close()
}

object Aerospike {
  private val connectionPool = new mutable.HashMap[String, Aerospike]

  def apply(seedNode: SeedNode): Try[Aerospike] = {
    val aerospikeHost = new Host(seedNode.host, seedNode.port)
    if (!connectionPool.contains(aerospikeHost.toString)) {
      tryCreateNewClient(aerospikeHost)
    } else {
      Success(connectionPool(aerospikeHost.toString))
    }
  }

  def tryCreateNewClient(aerospikeHost: Host): Try[Aerospike] = {
    Try(new AerospikeClient(getDefaultPolicy, aerospikeHost)) match {
      case Success(connexion) if connexion.isConnected =>
        connectionPool.addOne((aerospikeHost.toString, new Aerospike(connexion)))
        Success(connectionPool(aerospikeHost.toString))
      case Success(connexion) if !connexion.isConnected =>
        Failure(new AerospikeException(s"Could not connect to Aerospike : ${connexion.getClusterStats}"))
      case Failure(e) => Failure(new AerospikeException(s"Could not connect to Aerospike : ${e}"))
    }
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

  private def getDefaultPolicy: ClientPolicy = {
    val cPolicy = new ClientPolicy()
    cPolicy.timeout = 10000
    cPolicy.maxConnsPerNode = 50
    cPolicy
  }

  private def getInfoPolicy: InfoPolicy = {
    val iPolicy = new InfoPolicy()
    iPolicy.timeout = 3000
    iPolicy
  }
}
