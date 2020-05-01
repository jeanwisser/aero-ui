package controllers

import client.Aerospike
import core.ClusterService
import javax.inject._
import play.api.mvc._
import ConnexionForm._
import models.{AerospikeRecord, NamespaceInfo, SeedNode, SetInfo}
import QueryForm._
import com.aerospike.client.Record

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

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(connexionForm))
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

  def cluster(host: String, port: Int): Action[AnyContent] = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    redirectIfError(Aerospike(SeedNode(host, port)).map { client =>
      ClusterService.getNodes(client).map { nodes =>
        val nodesInfo = nodes.map(node => ClusterService.getNodeInformation(node)).toSet
        val namespacesInfo = ClusterService.getNamespacesInformation(nodes.head).values.toList
        Ok(views.html.cluster(host, port, nodesInfo, namespacesInfo))
      }
    })
  }

  def namespace(host: String, port: Int, namespaceName: String, setName: Option[String]): Action[AnyContent] =
    messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
      redirectIfError(Aerospike(SeedNode(host, port)).map { client =>
        ClusterService.getNodes(client).map {
          nodes =>
            val namespacesInfo = ClusterService.getNamespacesInformation(nodes.head)
            if (namespacesInfo.contains(namespaceName)) {
              ClusterService.getSetsInformation(nodes.head, namespaceName) match {
                case Success(sets) =>
                  if (setName.forall(s => sets.contains(s))) {
                    val selectedSet = setName.map(sets(_)).getOrElse(sets.values.head)
                    Ok(
                      views.html
                        .namespace(host, port, namespacesInfo.values.toList, sets.values.toList, namespaceName, selectedSet, queryForm, None)
                    )
                  } else {
                    Redirect(routes.HomeController.cluster(host, port))
                      .flashing("exception" -> s"Namespace $namespaceName does contains a set named $setName")
                  }
                case Failure(e) =>
                  Redirect(routes.HomeController.cluster(host, port))
                    .flashing("exception" -> s"Namespace $namespaceName does contains any data: $e")
              }
            } else {
              Redirect(routes.HomeController.cluster(host, port)).flashing("exception" -> s"Namespace $namespaceName does not exist")
            }
        }
      })
    }

  def handleQueryForm(host: String, port: Int, namespaceName: String, setName: String): Action[AnyContent] = messagesAction.async {
    implicit request: MessagesRequest[AnyContent] =>
      queryForm.bindFromRequest.fold(
        _ => {
          Future(Redirect(routes.HomeController.namespace(host, port, namespaceName, Some(setName))))
        },
        data => {
          redirectIfError(Aerospike(SeedNode(host, port)).map { client =>
            client.get(namespaceName, setName, data.key).map {
              case Success(optRecord) =>
                optRecord match {
                  case Some(record) =>
                    Redirect(routes.HomeController.namespace(host, port, namespaceName, Some(setName)))
                  case None => Redirect(routes.HomeController.namespace(host, port, namespaceName, Some(setName)))
                }
              case Failure(_) => Redirect(routes.HomeController.namespace(host, port, namespaceName, Some(setName)))
            }
          })
        }
      )
  }

  def redirectIfError(result: Try[Future[Result]])(implicit messagesRequestHeader: MessagesRequestHeader): Future[Result] = {
    result match {
      case Failure(exception) => Future(Redirect(routes.HomeController.index()).flashing("exception" -> exception.getMessage))
      case Success(success) => success
    }
  }

}
