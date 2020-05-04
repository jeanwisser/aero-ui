package controllers

import client.Aerospike
import core.ClusterService
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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
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
          Future(Redirect(routes.HomeController.cluster(data.host, data.port)))
        })
      }
    )
  }

  def cluster(host: String, port: Int): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    redirectIfError(Aerospike(SeedNode(host, port)).map { client =>
      val nodes = ClusterService.getNodes(client)
      val nodesInfo = nodes.map(n => ClusterService.getNodeInformation(n))
      val namespacesInfo = ClusterService.getNamespacesInformation(nodes.head).values.toList
      Ok(views.html.cluster(host, port, nodesInfo.toSet, namespacesInfo))
    })
  }

  def namespace(host: String, port: Int, namespaceName: String, setName: Option[String]): Action[AnyContent] =
    messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
      getNamespacePageResult(host, port, namespaceName, setName, None)
    }

  def handleQueryForm(host: String, port: Int, namespaceName: String, setName: String): Action[AnyContent] = messagesAction.async {
    implicit request: MessagesRequest[AnyContent] =>
      queryForm.bindFromRequest.fold(
        _ => {
          Future(Redirect(routes.HomeController.namespace(host, port, namespaceName, Some(setName))))
        },
        data => {
          getNamespacePageResult(host, port, namespaceName, Some(setName), Some(data.key))
        }
      )
  }

  def getNamespacePageResult(host: String, port: Int, namespaceName: String, setName: Option[String], keyToQuery: Option[String])(implicit request: MessagesRequest[AnyContent]): Future[Result] = {
    redirectIfError(Aerospike(SeedNode(host, port)).map{ client =>
      val node = ClusterService.getNodes(client).head
      val namespacesInfo = ClusterService.getNamespacesInformation(node)
      if (!namespacesInfo.contains(namespaceName))
        return Future(Redirect(routes.HomeController.cluster(host, port)).flashing("exception" -> s"Namespace $namespaceName does not exist"))
      val setsContext = for {
        sets <- ClusterService.getSetsInformation(node, namespaceName)
        set <- ClusterService.getSetInformation(sets, setName.getOrElse(sets.keys.head))
      } yield (sets, set)
      setsContext match {
        case Left(failureMsg) =>
          Future(Redirect(routes.HomeController.cluster(host, port)).flashing("exception" -> failureMsg))
        case Right((sets, selectedSet)) =>
          keyToQuery match {
            case Some(key) =>
              val record = ClusterService.getRecord(client, namespaceName, selectedSet.name, key)
              record.map {
                case Left(failureMsg) => Redirect(routes.HomeController.namespace(host, port, namespaceName, setName)).flashing("exception" -> failureMsg)
                case Right(record) =>
                  Ok(views.html.namespace(host, port, namespacesInfo.values.toList, sets.values.toList, namespaceName, selectedSet, queryForm, Some(record)))
              }
            case None =>
              Future(Ok(
                views.html
                  .namespace(host, port, namespacesInfo.values.toList, sets.values.toList, namespaceName, selectedSet, queryForm, None)))
          }
      }
    })
  }

  def redirectIfError(result: Try[Future[Result]])(implicit messagesRequestHeader: MessagesRequestHeader): Future[Result] = {
    result match {
      case Failure(exception) => Future(Redirect(routes.HomeController.index()).flashing("exception" -> exception.getMessage))
      case Success(success) => success
    }
  }

  def redirectIfError(result: Try[Result])(implicit messagesRequestHeader: MessagesRequestHeader): Result = {
    result match {
      case Failure(exception) => Redirect(routes.HomeController.index()).flashing("exception" -> exception.getMessage)
      case Success(success) => success
    }
  }

}
