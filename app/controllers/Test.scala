package controllers

import client.Aerospike
import client.Aerospike.getDefaultPolicy
import com.aerospike.client.{AerospikeClient, AerospikeException, Host}

import scala.util.{Failure, Success, Try}

object Test {
  def main(args: Array[String]): Unit = {


    val res = null

    println(res match {
      case null => None
      case v => Some(v)
    })

  }

}
