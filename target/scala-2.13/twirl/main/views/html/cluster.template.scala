
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

object cluster extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[Set[NodeInfo],List[NamespaceInfo],play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(nodes: Set[NodeInfo], namespaces: List[NamespaceInfo]):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*2.2*/main("cluster", namespaces)/*2.29*/ {_display_(Seq[Any](format.raw/*2.31*/("""
"""),format.raw/*3.1*/("""<section class="hero welcome is-small">
<div class="columns">
<div class="column is-12">

            <div class="container">
                <h1 class="is-info is-size-5 is-4">
                    CLUSTER OVERVIEW
                </h1>
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
                            """),_display_(/*36.30*/for((node, index) <- nodes.zipWithIndex) yield /*36.70*/ {_display_(Seq[Any](format.raw/*36.72*/("""
                            """),format.raw/*37.29*/("""<tr>
                                <td>"""),_display_(/*38.38*/{index+1}),format.raw/*38.47*/("""</td>
                                <td>"""),_display_(/*39.38*/node/*39.42*/.name),format.raw/*39.47*/("""</td>
                                <td>"""),_display_(/*40.38*/node/*40.42*/.host.name),format.raw/*40.52*/("""</td>
                                <td>"""),_display_(/*41.38*/node/*41.42*/.host.port),format.raw/*41.52*/("""</td>
                                <td>"""),_display_(/*42.38*/node/*42.42*/.version),format.raw/*42.50*/("""</td>
                               <td> <span class="tag is-success">"""),_display_(/*43.67*/node/*43.71*/.isActive),format.raw/*43.80*/("""</span></td>
                                <td class="level-right"><a class="show-config button is-small" href="#">Show config</a></td>
                            </tr>
                            """)))}),format.raw/*46.30*/("""
                            """),format.raw/*47.29*/("""</tbody>
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
                            """),_display_(/*68.30*/for((namespace, index) <- namespaces.zipWithIndex) yield /*68.80*/ {_display_(Seq[Any](format.raw/*68.82*/("""
                            """),format.raw/*69.29*/("""<tr>
                                <td>"""),_display_(/*70.38*/namespace/*70.47*/.name),format.raw/*70.52*/("""</td>
                                <td>"""),_display_(/*71.38*/namespace/*71.47*/.getStorageEngine),format.raw/*71.64*/("""</td>
                                <td>"""),_display_(/*72.38*/namespace/*72.47*/.getObjects),format.raw/*72.58*/("""</td>
                                <td>"""),_display_(/*73.38*/namespace/*73.47*/.getReplicationFactor),format.raw/*73.68*/("""</td>
                                <td class="level-right"><a class="show-config button is-small" href="#">Open</a></td>
                            </tr>
                            """)))}),format.raw/*76.30*/("""
                            """),format.raw/*77.29*/("""</tbody>
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

  def render(nodes:Set[NodeInfo],namespaces:List[NamespaceInfo]): play.twirl.api.HtmlFormat.Appendable = apply(nodes,namespaces)

  def f:((Set[NodeInfo],List[NamespaceInfo]) => play.twirl.api.HtmlFormat.Appendable) = (nodes,namespaces) => apply(nodes,namespaces)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2020-01-09T21:40:17.470388
                  SOURCE: /home/jean/IdeaProjects/aeroUI/app/views/cluster.scala.html
                  HASH: c3f730372770b23d556bae365dc6ac1c661e0154
                  MATRIX: 758->1|907->58|942->85|981->87|1008->88|2188->1241|2244->1281|2284->1283|2341->1312|2410->1354|2440->1363|2510->1406|2523->1410|2549->1415|2619->1458|2632->1462|2663->1472|2733->1515|2746->1519|2777->1529|2847->1572|2860->1576|2889->1584|2988->1656|3001->1660|3031->1669|3263->1870|3320->1899|4178->2730|4244->2780|4284->2782|4341->2811|4410->2853|4428->2862|4454->2867|4524->2910|4542->2919|4580->2936|4650->2979|4668->2988|4700->2999|4770->3042|4788->3051|4830->3072|5048->3259|5105->3288
                  LINES: 21->1|26->2|26->2|26->2|27->3|60->36|60->36|60->36|61->37|62->38|62->38|63->39|63->39|63->39|64->40|64->40|64->40|65->41|65->41|65->41|66->42|66->42|66->42|67->43|67->43|67->43|70->46|71->47|92->68|92->68|92->68|93->69|94->70|94->70|94->70|95->71|95->71|95->71|96->72|96->72|96->72|97->73|97->73|97->73|100->76|101->77
                  -- GENERATED --
              */
          