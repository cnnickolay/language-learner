package utils

import scala.collection.mutable.ListBuffer
import scala.io.{Source, BufferedSource}
import scala.util.matching.Regex

/**
  * Rewrite this object
  */
object SrtParser {

  case class TextBlock(number: Int, timeFrom: BigDecimal, timeTo: BigDecimal, text: String)

  private val timeRegex = "(\\d\\d):(\\d\\d):(\\d\\d),(\\d\\d\\d) \\-\\-> (\\d\\d):(\\d\\d):(\\d\\d),(\\d\\d\\d)".r
  private val timeRegexMinutes = "(\\d\\d):(\\d\\d) \\-\\-> (\\d\\d):(\\d\\d)".r
  private val numberRegex = "(\\d+)".r
  private val emptyLineRegex = "()".r

  def parseText(text: String): List[TextBlock] = {
    var textBlock = new TextBlock(0, 0, 0, "")
    var listBuffer = new ListBuffer[TextBlock]

    for (line <- text.lines) {
      if (line.isEmpty) {
        listBuffer += textBlock
        textBlock = new TextBlock(0, 0, 0, "")
      } else {
        textBlock = lineToTextBlock(line, textBlock)
      }
    }

    listBuffer.toList
  }

  private def lineToTextBlock(line: String, textBlock: TextBlock): TextBlock = line match {
    case timeRegex(h1, m1, s1, ms1, h2, m2, s2, ms2) =>
      val fromTimeInt = h1.toInt * 3600000 + m1.toInt * 60000 + s1.toInt * 1000 + ms1.toInt
      val toTimeInt = h2.toInt * 3600000 + m2.toInt * 60000 + s2.toInt * 1000 + ms2.toInt
      val fromTime = fromTimeInt.toDouble / 1000
      val toTime = toTimeInt.toDouble / 1000
      textBlock.copy(timeFrom = fromTime, timeTo = toTime)
    case timeRegexMinutes(m1, s1, m2, s2) =>
      val fromTimeInt = m1.toInt * 60000 + s1.toInt * 1000
      val toTimeInt = m2.toInt * 60000 + s2.toInt * 1000
      val fromTime = fromTimeInt.toDouble / 1000
      val toTime = toTimeInt.toDouble / 1000
      textBlock.copy(timeFrom = fromTime, timeTo = toTime)
    case numberRegex(num) => textBlock.copy(number = num.toInt)
    case _ => textBlock.copy(text = textBlock.text + " " + line)
  }

  private def oneOrNumber(s: String): Int = {
    val num = s.toInt
    if (num == 0) {
      1
    } else {
      num
    }
  }

}
