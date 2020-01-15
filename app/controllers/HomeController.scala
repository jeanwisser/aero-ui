package controllers

import core.ClusterOverview
import javax.inject._
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  import ConnexionForm._

  private val postUrl = routes.HomeController.createForm()

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(form, postUrl))
  }

  def createForm(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    form.bindFromRequest.fold(formWithErrors => {
      println(formWithErrors)
      // binding failure, you retrieve the form containing errors:
      BadRequest(views.html.index(formWithErrors, postUrl))
    }, data => {
      /* binding success, you get the actual value. */
      Redirect(routes.HomeController.cluster("ok", 10))
    })
  }

  def cluster(host: String, port: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    ClusterOverview.getNodes(host, port).map { nodes =>
      val nodesInfo = nodes.map(node => ClusterOverview.getNodeInformation(node)).toSet
      Ok(views.html.cluster(host, port, nodesInfo, ClusterOverview.getNamespacesInformation(nodes).values.toList))
    }
  }

  def namespace(host: String, port: Int, name: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    ClusterOverview.getNodes(host, port).map { nodes =>
      val namespacesInfo = ClusterOverview.getNamespacesInformation(nodes)
      Ok(views.html.namespace(host, port, namespacesInfo.values.toList, namespacesInfo(name)))
    }
  }
}
