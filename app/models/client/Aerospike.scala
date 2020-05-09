package models.client

import com.aerospike.client._
import com.aerospike.client.cluster.ClusterStats
import com.aerospike.client.cluster.Node
import com.aerospike.client.policy.{ClientPolicy, InfoPolicy, Priority, QueryPolicy, ScanPolicy, WritePolicy}
import models.SeedNode

import scala.collection.mutable
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class Aerospike(connexion: AerospikeClient) {
  val client: AerospikeClient = connexion

  def put(ns: String, set: String, key: String, value: String): Future[Try[Unit]] = Future {
    val wPolicy = new WritePolicy()
    val bin = new Bin("", value)
    Try(client.put(wPolicy, new Key(ns, set, key), bin))
  }

  def get(ns: String, set: String, key: String): Future[Try[Option[Record]]] = Future {
    val qPolicy = new QueryPolicy()
    Try(connexion.get(qPolicy, new Key(ns, set, key))).map(Option(_))
  }

  def scan(ns: String, set: String, limit: Int): Seq[Record] = {
    val sPolicy = new ScanPolicy
    sPolicy.concurrentNodes = true
    sPolicy.priority = Priority.LOW
    sPolicy.includeBinData = false
    sPolicy.scanPercent = 1

    val records = new mutable.ListBuffer[Record]
    val callback = new ScanCallback {
      override def scanCallback(key: Key, record: Record): Unit = {
        if(records.size < limit){
          records.addOne(record)
        }
      }
    }

    client.scanAll(sPolicy, ns, set, callback)
    records.toList
  }



  def getNodes: Try[Array[Node]]         = Try(connexion.getNodes)
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

  def getDefaultPolicy: ClientPolicy = {
    val cPolicy = new ClientPolicy()
    cPolicy.timeout = 10000
    cPolicy.maxConnsPerNode = 50
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
