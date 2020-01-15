
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

object customFormInput extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[helper.FieldElements,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(elements: helper.FieldElements):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
"""),format.raw/*3.1*/("""<div class="field is-horizontal """),_display_(/*3.34*/if(elements.hasErrors)/*3.56*/ {_display_(Seq[Any](format.raw/*3.58*/("""error""")))}),format.raw/*3.64*/("""">
    <div class="field-label is-normal">
        <label class="label">"""),_display_(/*5.31*/elements/*5.39*/.label),format.raw/*5.45*/("""</label>
    </div>

    <div class="field-body">
        <p class="control">
            """),_display_(/*10.14*/elements/*10.22*/.input),format.raw/*10.28*/("""
            """),format.raw/*11.13*/("""<span class="errors">"""),_display_(/*11.35*/elements/*11.43*/.errors.mkString(", ")),format.raw/*11.65*/("""</span>
        </p>
    </div>
</div>
"""))
      }
    }
  }

  def render(elements:helper.FieldElements): play.twirl.api.HtmlFormat.Appendable = apply(elements)

  def f:((helper.FieldElements) => play.twirl.api.HtmlFormat.Appendable) = (elements) => apply(elements)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2020-01-15T23:39:35.522243
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/customFormInput.scala.html
                  HASH: 7cf71bba362318434276cdbbd193499a769e3d12
                  MATRIX: 753->1|879->34|906->35|965->68|995->90|1034->92|1070->98|1169->171|1185->179|1211->185|1329->276|1346->284|1373->290|1414->303|1463->325|1480->333|1523->355
                  LINES: 21->1|26->2|27->3|27->3|27->3|27->3|27->3|29->5|29->5|29->5|34->10|34->10|34->10|35->11|35->11|35->11|35->11
                  -- GENERATED --
              */
          