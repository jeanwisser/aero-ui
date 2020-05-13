package controllers.tools

object ReadableHelper {
  def toHumanReadableBytes(bytes: Long): String = {
    if (-1024 < bytes && bytes < 1024) return s"$bytes B"
    val ci = Array("k", "M", "G", "T", "P")

    @scala.annotation.tailrec
    def loop(i: Int, b: Double): (Int, Double) = {
      if (b <= -999_950 || b >= 999_950) loop(i + 1, b / 1024)
      else (i, b)
    }

    val (index, result) = loop(0, bytes)
    val stringValue = if ((result / 1024) % 1 == 0) f"${result / 1024}%1.0f" else f"${result / 1024}%1.2f"
    s"$stringValue ${ci(index)}B"
  }

  def toHumanReadableNumber(number: Long): String = {
    val formatter = java.text.NumberFormat.getIntegerInstance
    formatter.format(number)
  }
}
