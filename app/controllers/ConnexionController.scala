package controllers

import models.client.Aerospike
import javax.inject._
import play.api.mvc._
import ConnexionForm.{Data, _}
import models.{AerospikeRecord, NamespaceInfo, NodeInfo, SeedNode, SetInfo}
import QueryForm._
import com.aerospike.client.Record
import play.api.data.Form

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try


@Singleton
class ConnexionController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
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
        redirectIfError(Aerospike(SeedNode(data.host, data.port)).map { _ =>
          Future(Redirect(routes.ClusterController.cluster(data.host, data.port)))
        })
      }
    )
  }

  def redirectIfError(result: Try[Future[Result]])(implicit messagesRequestHeader: MessagesRequestHeader): Future[Result] = {
    result match {
      case Failure(exception) => Future(Redirect(routes.ConnexionController.index()).flashing("exception" -> exception.getMessage))
      case Success(success) => success
    }
  }
}
