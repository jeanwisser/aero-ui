package controllers
import play.api.data.Forms._
import play.api.data.Form

object ConnexionForm {
  case class Data(host: String, port: Int)

  val connexionForm: Form[Data] = Form(
    mapping(
      "Host" -> nonEmptyText,
      "Port" -> number(min = 0, max = 65535)
    )(Data.apply)(Data.unapply)
  )
}
