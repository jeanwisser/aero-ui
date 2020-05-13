package controllers.tools

object ListHelper {
  @annotation.tailrec
  def sumListRec[T](list: List[Option[T]], acc: T)(implicit ev: Numeric[T]): Option[T] = {
    list match {
      case head :: tail =>
        head match {
          case Some(value) => sumListRec(tail, ev.plus(acc, value))
          case None        => None
        }
      case Nil => Some(acc)
    }
  }
}
