package controllers

import javax.inject.Inject
import models.AerospikeContext
import play.api.mvc._

import scala.concurrent.ExecutionContext

class ClusterController @Inject() (messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def cluster(host: String, port: Int): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    AerospikeContext(host, port) match {
      case Left(failureMessage) =>
        Redirect(routes.ClusterController.cluster(host, port)).flashing("exception" -> failureMessage)
      case Right(context) =>
        Ok(views.html.cluster(host, port, context.nodesInfo.toSet, context.namespaces.values.toList))
    }
  }
}
