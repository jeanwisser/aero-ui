package controllers
import play.api.data.Form
import play.api.data.Forms._

object QueryForm {
  case class Data(key: String)

  val queryForm = Form(
    mapping(
      "Key" -> nonEmptyText,
    )(Data.apply)(Data.unapply)
  )
}
