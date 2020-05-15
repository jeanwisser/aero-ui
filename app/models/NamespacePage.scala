package models

final case class NamespacePage(
    context: AerospikeContext,
    namespaces: Map[String, NamespaceInfo],
    setsInfo: Map[String, SetInfo],
    selectedSet: SetInfo
)

object NamespacePage {
  def apply(
      host: String,
      port: Int,
      namespace: String,
      set: Option[String]
  ): Either[String, NamespacePage] = {
    for {
      context     <- AerospikeContext(host, port)
      namespaces  <- context.getNamespacesInformationClusterWide
      _           <- getCurrentNamespace(namespaces, namespace)
      setsInfo    <- context.getNamespaceSets(namespace)
      selectedSet <- getCurrentSet(setsInfo, set.getOrElse(setsInfo.keys.head))
    } yield NamespacePage(context, namespaces, setsInfo, selectedSet)
  }

  private def getCurrentSet(setsInfo: Map[String, SetInfo], set: String): Either[String, SetInfo] = {
    setsInfo.get(set) match {
      case None      => Left(s"Set $set was not found")
      case Some(set) => Right(set)
    }
  }

  private def getCurrentNamespace(namespaces: Map[String, NamespaceInfo], namespace: String): Either[String, NamespaceInfo] = {
    namespaces.get(namespace) match {
      case None      => Left(s"Namespace $namespace was not found")
      case Some(set) => Right(set)
    }
  }
}
