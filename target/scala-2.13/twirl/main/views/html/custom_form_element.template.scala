
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

object custom_form_element extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[helper.FieldElements,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(elements: helper.FieldElements):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
"""),format.raw/*3.1*/("""<div class=""""),_display_(/*3.14*/if(elements.hasErrors)/*3.36*/ {_display_(Seq[Any](format.raw/*3.38*/("""error""")))}),format.raw/*3.44*/("""">
    <label for=""""),_display_(/*4.18*/elements/*4.26*/.id),format.raw/*4.29*/("""">"""),_display_(/*4.32*/elements/*4.40*/.label),format.raw/*4.46*/("""</label>
    <div class="input">
        """),_display_(/*6.10*/elements/*6.18*/.input),format.raw/*6.24*/("""
        """),format.raw/*7.9*/("""<span class="errors">"""),_display_(/*7.31*/elements/*7.39*/.errors.mkString(", ")),format.raw/*7.61*/("""</span>
        <span class="help">"""),_display_(/*8.29*/elements/*8.37*/.infos.mkString(", ")),format.raw/*8.58*/("""</span>
    </div>
</div>"""))
      }
    }
  }

  def render(elements:helper.FieldElements): play.twirl.api.HtmlFormat.Appendable = apply(elements)

  def f:((helper.FieldElements) => play.twirl.api.HtmlFormat.Appendable) = (elements) => apply(elements)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2020-01-14T22:54:59.849628
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/custom_form_element.scala.html
                  HASH: 97f997ae2cffb8ba6c0cba90801941ecd74d34fd
                  MATRIX: 757->1|883->34|910->35|949->48|979->70|1018->72|1054->78|1100->98|1116->106|1139->109|1168->112|1184->120|1210->126|1278->168|1294->176|1320->182|1355->191|1403->213|1419->221|1461->243|1523->279|1539->287|1580->308
                  LINES: 21->1|26->2|27->3|27->3|27->3|27->3|27->3|28->4|28->4|28->4|28->4|28->4|28->4|30->6|30->6|30->6|31->7|31->7|31->7|31->7|32->8|32->8|32->8
                  -- GENERATED --
              */
          