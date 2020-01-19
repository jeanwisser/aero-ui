package controllers

import client.Aerospike
import core.ClusterOverview
import javax.inject._
import play.api.mvc._
import ConnexionForm._
import models.SeedNode

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {
  private val postUrl = routes.HomeController.createForm()

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(exception: Option[String]): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(form, postUrl, exception))
  }

  def createForm(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    form.bindFromRequest.fold(formWithErrors => {
      println(formWithErrors)
      BadRequest(views.html.index(formWithErrors, postUrl, None))
    }, data => {
      Aerospike(SeedNode(data.host, data.port)) match {
        case Success(_) => Redirect(routes.HomeController.cluster(data.host, data.port))
        case Failure(exception) => Ok(views.html.index(form, postUrl, Some(exception.getMessage)))
      }
    })
  }

  def cluster(host: String, port: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Aerospike(SeedNode(host, port)) match {
      case Success(client) =>
        ClusterOverview.getNodes(client).map { nodes =>
          val nodesInfo = nodes.map(node => ClusterOverview.getNodeInformation(node)).toSet
          val namespacesInfo = ClusterOverview.getNamespacesInformation(nodes(0))
          Ok(views.html.cluster(host, port, nodesInfo, namespacesInfo))
        }
      case Failure(exception) => index(Some(exception.getMessage))(request)
    }
  }

  def namespace(host: String, port: Int, namespaceName: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      Aerospike(SeedNode(host, port)) match {
        case Success(client) =>
          ClusterOverview.getNodes(client).map { nodes =>
            val namespacesInfo = ClusterOverview.getNamespacesInformation(nodes(0))
            val setsInfo = ClusterOverview.getSetsInformation(nodes(0), namespaceName).values.toList
            Ok(views.html.namespace(host, port, namespacesInfo, setsInfo, namespaceName))
          }
        case Failure(exception) => index(Some(exception.getMessage))(request)
      }
  }
}
