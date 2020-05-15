package controllers

import javax.inject.Inject
import models.ClusterPage
import play.api.mvc._

import scala.concurrent.ExecutionContext

class ClusterController @Inject() (messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def cluster(host: String, port: Int): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    ClusterPage(host, port) match {
      case Left(failureMessage) =>
        Redirect(routes.ConnexionController.index()).flashing("exception" -> failureMessage)
      case Right(context) =>
        Ok(views.html.cluster(host, port, context.nodeInfos.toSet, context.namespaces.values.toList))
    }
  }
}
