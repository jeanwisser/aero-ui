package controllers

import core.AerospikeContext
import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

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
