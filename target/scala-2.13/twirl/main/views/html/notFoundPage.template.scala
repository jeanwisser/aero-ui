
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

object notFoundPage extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(path: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*2.2*/main("notFoundPage")/*2.22*/ {_display_(Seq[Any](format.raw/*2.24*/("""
"""),format.raw/*3.1*/("""<section id="content">
    <div class="wrapper doc">
        <article>
            <h1>404 Page not found: """),_display_(/*6.38*/path),format.raw/*6.42*/("""</h1>
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

  def render(path:String): play.twirl.api.HtmlFormat.Appendable = apply(path)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (path) => apply(path)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2019-12-15T20:39:26.395324
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/notFoundPage.scala.html
                  HASH: 98803d3d4a789fd0b07801407a1b61617bec5074
                  MATRIX: 736->1|844->17|872->37|911->39|938->40|1072->148|1096->152|1176->206|1211->221|1247->230
                  LINES: 21->1|26->2|26->2|26->2|27->3|30->6|30->6|33->9|33->9|34->10
                  -- GENERATED --
              */
          