
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

object front extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(param: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*2.2*/main("front")/*2.15*/ {_display_(Seq[Any](format.raw/*2.17*/("""
"""),format.raw/*3.1*/("""<section id="content">
    <div class="wrapper doc">
        <article>
            <h1>AeroUI """),_display_(/*6.25*/param),format.raw/*6.30*/("""!</h1>
        </article>
        <aside>
            """),_display_(/*9.14*/commonSidebar()),format.raw/*9.29*/("""
        """),format.raw/*10.9*/("""</aside>
    </div>
</section>
""")))}))
      }
    }
  }

  def render(param:String): play.twirl.api.HtmlFormat.Appendable = apply(param)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (param) => apply(param)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2019-12-15T20:17:48.634583
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/front.scala.html
                  HASH: ac374cc13dbb55cebc0d85e7528f48c0fc5478af
                  MATRIX: 729->1|838->18|859->31|898->33|925->34|1046->129|1071->134|1152->189|1187->204|1223->213
                  LINES: 21->1|26->2|26->2|26->2|27->3|30->6|30->6|33->9|33->9|34->10
                  -- GENERATED --
              */
          