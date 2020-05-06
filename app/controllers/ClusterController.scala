package controllers

import client.Aerospike
import core.AerospikeContext
import javax.inject.Inject
import models.SeedNode
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.MessagesActionBuilder
import play.api.mvc.MessagesRequest
import play.api.mvc.MessagesRequestHeader
import play.api.mvc.Result

import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class ClusterController @Inject() (messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def cluster(host: String, port: Int): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    redirectIfConnexionError(AerospikeContext(host, port).map { context =>
      Ok(views.html.cluster(host, port, context.nodes.toSet, context.namespaces.values.toList))
    })
  }

  def redirectIfConnexionError(result: Try[Result])(implicit messagesRequestHeader: MessagesRequestHeader): Result = {
    result match {
      case Failure(exception) => Redirect(routes.ConnexionController.index()).flashing("exception" -> exception.getMessage)
      case Success(success)   => success
    }
  }
}
