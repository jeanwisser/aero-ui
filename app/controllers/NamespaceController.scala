package controllers

import controllers.QueryForm.queryForm
import javax.inject.Inject
import models.AerospikeContext
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class NamespaceController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(components) {

  def namespace(host: String, port: Int, namespaceName: String, setName: Option[String]): Action[AnyContent] =
    messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
      getNamespacePageResult(host, port, namespaceName, setName, None)
    }

  def handleQueryForm(host: String, port: Int, namespaceName: String, setName: String): Action[AnyContent] = messagesAction.async {
    implicit request: MessagesRequest[AnyContent] =>
      queryForm.bindFromRequest.fold(
        _ => {
          Future(Redirect(routes.NamespaceController.namespace(host, port, namespaceName, Some(setName))))
        },
        data => {
          getNamespacePageResult(host, port, namespaceName, Some(setName), Some(data.key))
        }
      )
  }

  def getNamespacePageResult(host: String, port: Int, namespaceName: String, setName: Option[String], keyToQuery: Option[String])(implicit request: MessagesRequest[AnyContent]): Future[Result] = {
    redirectIfError(AerospikeContext(host, port).map{ context =>
      val setsContext = for {
        selectedNamespace <- context.getNamespaceInformation(namespaceName)
        setsInfo <- selectedNamespace.getNamespaceSets
        set <-  selectedNamespace.getSetInformation(setName.getOrElse(setsInfo.keys.head))
      } yield (setsInfo, set)

      setsContext match {
        case Left(failureMsg) =>
          Future(Redirect(routes.ClusterController.cluster(host, port)).flashing("exception" -> failureMsg))
        case Right((sets, selecetdSet)) =>
          keyToQuery match {
            case Some(key) =>
              val record = context.getRecord(namespaceName, selecetdSet.name, key)
              record.map {
                case Left(failureMsg) => Redirect(routes.NamespaceController.namespace(host, port, namespaceName, setName)).flashing("exception" -> failureMsg)
                case Right(record) =>
                  Ok(views.html.namespace(host, port, context.namespaces.values.toList, sets.values.toList, namespaceName, selecetdSet, queryForm, Some(record)))
              }
            case None =>
              Future(Ok(
                views.html
                  .namespace(host, port, context.namespaces.values.toList, sets.values.toList, namespaceName, selecetdSet, queryForm, None)))
          }
      }
    })
  }

  def redirectIfError(result: Try[Future[Result]])(implicit messagesRequestHeader: MessagesRequestHeader): Future[Result] = {
    result match {
      case Failure(exception) => Future(Redirect(routes.ConnexionController.index()).flashing("exception" -> exception.getMessage))
      case Success(success) => success
    }
  }

}
