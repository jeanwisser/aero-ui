// @GENERATOR:play-routes-compiler
// @SOURCE:/home/jean/IdeaProjects/aeroUI/conf/routes
// @DATE:Sun Dec 15 20:55:05 CET 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
