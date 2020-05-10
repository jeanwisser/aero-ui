package controllers

import controllers.ConnexionForm.{Data, _}
import javax.inject._
import models.SeedNode
import models.client.Aerospike
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
@Singleton
class ConnexionController @Inject() (messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def index(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(connexionForm.fill(Data("127.0.0.1", 3000))))
  }

  def handleConnexionForm(): Action[AnyContent] = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    connexionForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(views.html.index(formWithErrors)))
      },
      data => {
        Aerospike(SeedNode(data.host, data.port)) match {
          case Failure(exception) => Future(Redirect(routes.ConnexionController.index()).flashing("exception" -> exception.getMessage))
          case Success(_)         => Future(Redirect(routes.ClusterController.cluster(data.host, data.port)))
        }
      }
    )
  }

  def closeConnection(host: String, port: Int): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Aerospike.deleteConnectionFromPool(SeedNode(host, port))
    Redirect(routes.ConnexionController.index()).flashing("message" -> s"Closed connection to host $host")
  }
}
