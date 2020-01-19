package core

object Logger {

  def debug(step: String, message: String): Unit = {
    log("DEBUG", step, message)
  }

  def info(step: String, message: String): Unit = {
    log("INFO", step, message)
  }

  def warn(step: String, message: String): Unit = {
    log("WARN", step, message)
  }

  def error(step: String, message: String): Unit = {
    log("ERROR", step, message)
  }

  def error(step: String, message: String, e: Exception): Unit = {
    log("ERROR", step, message + " - " + e)
  }

  private def log(level: String, step: String, message: String): Unit = {
    println("[%s] - %s --- %s".format(level, step, message))
  }
}