package controllers.tools

object BytesHelper {
  def toHumanReadableBytes(bytes: Long): String = {
    if (-1000 < bytes && bytes < 1000) return s"$bytes B"
    val ci = Array("k", "M", "G", "T", "P")

    @scala.annotation.tailrec
    def loop(i: Int, b: Double): (Int, Double) = {
      if (b <= -999_950 || b >= 999_950) loop(i + 1, b / 1000)
      else (i, b)
    }

    val (index, result) = loop(0, bytes)
    f"${result / 1000}%1.2f ${ci(index)}B"
  }
}
