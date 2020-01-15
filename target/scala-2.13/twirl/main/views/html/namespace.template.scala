
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

object namespace extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[List[NodeInfo],List[NamespaceInfo],play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(nodes: List[NodeInfo], namespaces: List[NamespaceInfo]):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*2.2*/main("front", namespaces)/*2.27*/ {_display_(Seq[Any](format.raw/*2.29*/("""
"""),format.raw/*3.1*/("""<section class="hero welcome is-small">
    <div class="columns">
        <div class="column is-12">

            <div class="hero-body is-info">
                <div class="container">
                    <h1 class="subtitle is-4">
                        Cluster overview
                    </h1>
                </div>
            </div>

        </div>
    </div>
</section>
<section class="cluster-info">
    <div class="columns">
        <div class="column is-12">
            <div class="card events-card">
                <header class="card-header">
                    <p class="card-header-title">
                        Nodes
                    </p>
                </header>
                <div class="card-table">
                    <div class="content">
                        <table class="table is-fullwidth is-striped">
                            <tr>
                                <th>#</th>
                                <th>ID</th>
                                <th>Host</th>
                                <th>Port</th>
                                <th>Version</th>
                                <th>Active</th>
                            </tr>
                            <tbody>
                            """),_display_(/*39.30*/for((node, index) <- nodes.zipWithIndex) yield /*39.70*/ {_display_(Seq[Any](format.raw/*39.72*/("""
                            """),format.raw/*40.29*/("""<tr>
                                <td>"""),_display_(/*41.38*/{index+1}),format.raw/*41.47*/("""</td>
                                <td>"""),_display_(/*42.38*/node/*42.42*/.name),format.raw/*42.47*/("""</td>
                                <td>"""),_display_(/*43.38*/node/*43.42*/.host.name),format.raw/*43.52*/("""</td>
                                <td>"""),_display_(/*44.38*/node/*44.42*/.host.port),format.raw/*44.52*/("""</td>
                                <td>"""),_display_(/*45.38*/node/*45.42*/.version),format.raw/*45.50*/("""</td>
                                <td> <span class="tag is-success">"""),_display_(/*46.68*/node/*46.72*/.isActive),format.raw/*46.81*/("""</span></td>
                                <td class="level-right"><a class="show-config button is-small" href="#">Show config</a></td>
                            </tr>
                            """)))}),format.raw/*49.30*/("""
                            """),format.raw/*50.29*/("""</tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="card events-card">
                <header class="card-header">
                    <p class="card-header-title">
                        Namespaces
                    </p>
                </header>
                <div class="card-table">
                    <div class="content">
                        <table class="table is-fullwidth is-striped">
                            <tr>
                                <th>Name</th>
                                <th>Storage engine</th>
                                <th>Records</th>
                                <th>Replication factor</th>
                            </tr>
                            <tbody>
                            """),_display_(/*71.30*/for((namespace, index) <- namespaces.zipWithIndex) yield /*71.80*/ {_display_(Seq[Any](format.raw/*71.82*/("""
                            """),format.raw/*72.29*/("""<tr>
                                <td>"""),_display_(/*73.38*/namespace/*73.47*/.name),format.raw/*73.52*/("""</td>
                                <td>"""),_display_(/*74.38*/namespace/*74.47*/.getStorageEngine),format.raw/*74.64*/("""</td>
                                <td>"""),_display_(/*75.38*/namespace/*75.47*/.getObjects),format.raw/*75.58*/("""</td>
                                <td>"""),_display_(/*76.38*/namespace/*76.47*/.getReplicationFactor),format.raw/*76.68*/("""</td>
                                <td class="level-right"><a class="show-config button is-small" href="#">Open</a></td>
                            </tr>
                            """)))}),format.raw/*79.30*/("""
                            """),format.raw/*80.29*/("""</tbody>
                        </table>
                    </div>
                </div>
                <footer class="card-footer">
                    <a href="#" class="card-footer-item">View All</a>
                </footer>
            </div>
        </div>
    </div>
</section>
""")))}))
      }
    }
  }

  def render(nodes:List[NodeInfo],namespaces:List[NamespaceInfo]): play.twirl.api.HtmlFormat.Appendable = apply(nodes,namespaces)

  def f:((List[NodeInfo],List[NamespaceInfo]) => play.twirl.api.HtmlFormat.Appendable) = (nodes,namespaces) => apply(nodes,namespaces)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2019-12-22T21:01:08.836610
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/namespace.scala.html
                  HASH: 103bd49f9a688db77033f400419999a83e1abd70
                  MATRIX: 761->1|911->59|944->84|983->86|1010->87|2289->1339|2345->1379|2385->1381|2442->1410|2511->1452|2541->1461|2611->1504|2624->1508|2650->1513|2720->1556|2733->1560|2764->1570|2834->1613|2847->1617|2878->1627|2948->1670|2961->1674|2990->1682|3090->1755|3103->1759|3133->1768|3365->1969|3422->1998|4280->2829|4346->2879|4386->2881|4443->2910|4512->2952|4530->2961|4556->2966|4626->3009|4644->3018|4682->3035|4752->3078|4770->3087|4802->3098|4872->3141|4890->3150|4932->3171|5150->3358|5207->3387
                  LINES: 21->1|26->2|26->2|26->2|27->3|63->39|63->39|63->39|64->40|65->41|65->41|66->42|66->42|66->42|67->43|67->43|67->43|68->44|68->44|68->44|69->45|69->45|69->45|70->46|70->46|70->46|73->49|74->50|95->71|95->71|95->71|96->72|97->73|97->73|97->73|98->74|98->74|98->74|99->75|99->75|99->75|100->76|100->76|100->76|103->79|104->80
                  -- GENERATED --
              */
          