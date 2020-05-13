package controllers

import controllers.QueryForm.queryForm
import javax.inject.Inject
import models.{AerospikeContext, SetInfo, SetKey}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class NamespaceController @Inject() (messagesAction: MessagesActionBuilder, components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  final case class NamespacePageContext(context: AerospikeContext, setsInfo: Map[SetKey, SetInfo], selectedSet: SetInfo)

  def namespace(host: String, port: Int, namespaceName: String): Action[AnyContent] =
    messagesAction { implicit request: MessagesRequest[AnyContent] =>
      getNamespacePage(host, port, namespaceName, None)
    }

  def namespaceWithSet(host: String, port: Int, namespaceName: String, setName: String): Action[AnyContent] =
    messagesAction { implicit request: MessagesRequest[AnyContent] =>
      getNamespacePage(host, port, namespaceName, Some(setName))
    }

  def getNamespacePage(host: String, port: Int, namespaceName: String, setName: Option[String])(
      implicit request: MessagesRequest[AnyContent]
  ): Result = {
    getNamespacePageContext(host, port, namespaceName, setName) match {
      case Left(failureMessage) =>
        Redirect(routes.ClusterController.cluster(host, port)).flashing("exception" -> failureMessage)
      case Right(NamespacePageContext(context, setsInfo, selectedSet)) =>
        Ok(
          views.html
            .namespace(
              host,
              port,
              context.namespaces.values.toList,
              setsInfo.values.toList,
              namespaceName,
              selectedSet,
              queryForm,
              None
            )
        )
    }
  }

  def handleQueryForm(host: String, port: Int, namespaceName: String, setName: String): Action[AnyContent] = messagesAction.async {
    implicit request: MessagesRequest[AnyContent] =>
      queryForm.bindFromRequest.fold(
        _ => {
          Future(Redirect(routes.NamespaceController.namespaceWithSet(host, port, namespaceName, setName)))
        },
        data => {
          getNamespacePageContext(host, port, namespaceName, Some(setName)) match {
            case Left(failureMessage) =>
              Future(Redirect(routes.ClusterController.cluster(host, port)).flashing("exception" -> failureMessage))
            case Right(NamespacePageContext(context, setsInfo, selectedSet)) =>
              val record = context.getRecord(namespaceName, selectedSet.name, data.key)
              record.map {
                case Left(failureMessage) =>
                  Redirect(routes.NamespaceController.namespaceWithSet(host, port, namespaceName, setName))
                    .flashing("exception" -> failureMessage)
                case Right(record) =>
                  Ok(
                    views.html.namespace(
                      host,
                      port,
                      context.namespaces.values.toList,
                      setsInfo.values.toList,
                      namespaceName,
                      selectedSet,
                      queryForm,
                      Some(record)
                    )
                  )
              }
          }
        }
      )
  }

  def deleteRecord(host: String, port: Int, namespace: String, set: String, key: String): Action[AnyContent] = messagesAction.async {
    implicit request: MessagesRequest[AnyContent] =>
      AerospikeContext(host, port) match {
        case Left(failureMessage) =>
          Future(Redirect(routes.ClusterController.cluster(host, port)).flashing("exception" -> failureMessage))
        case Right(context) =>
          context.deleteRecord(namespace, set, key).map {
            case Left(failureMessage) =>
              Redirect(routes.NamespaceController.namespaceWithSet(host, port, namespace, set)).flashing("exception" -> failureMessage)
            case Right(_) =>
              Redirect(routes.NamespaceController.namespaceWithSet(host, port, namespace, set))
                .flashing("message" -> s"Record with PK = $key was successfully deleted from set $set")
          }
      }
  }

  private def getNamespacePageContext(
      host: String,
      port: Int,
      namespace: String,
      set: Option[String]
  ): Either[String, NamespacePageContext] = {
    for {
      context           <- AerospikeContext(host, port)
      selectedNamespace <- context.getNamespaceInformation(namespace)
      setsInfo          <- context.getNamespaceSets(namespace)
      selectedSet       <- context.getSetInformation(namespace, set.getOrElse(setsInfo.keys.head.set))
    } yield NamespacePageContext(context, setsInfo, selectedSet)
  }
}
