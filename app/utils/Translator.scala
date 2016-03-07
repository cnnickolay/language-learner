package utils

import java.net.URL

import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities, RemoteWebDriver}

import scala.collection.mutable.ListBuffer
import scala.xml.{Node, XML}
import utils.TranslatorObjects._

object Translator {

  def translate(from: Language, to: Language, word: String): Seq[Translation] = {

    val capabilities = DesiredCapabilities.chrome()
    capabilities.setJavascriptEnabled(false)
    val driver = new RemoteWebDriver(new URL("http://192.168.99.100:4444/wd/hub"), capabilities)

    try {
      driver.get(s"http://en.bab.la/dictionary/french-english/$word")

      val source = driver.getPageSource
      val xml = XML.loadString(source)

      val found =
        (xml \\ "div")
          .filter {
            resultNode =>
              ((resultNode \ "@class").text contains "result-wrapper") &&
                ((resultNode \\ "div").filter(resultLeftNode => (resultLeftNode \ "@class").text contains "result-left") \\ "strong").text == word
          }.theSeq

      found.map { n => processSegment(from, to, n) }
    } finally {
      driver.quit()
    }
  }

  def processSegment(from: Language, to: Language, node: Node): Translation = {
    val leftResults = (node \\ "div").filter(n => (n \ "@class").text contains "result-left")
    val rightResults = (node \\ "div").filter(n => (n \ "@class").text contains "result-right")


    val examples = ListBuffer[Example]()
    for (i <- 1 until leftResults.length) {
      val leftResult = leftResults(i).text
      val rightResult = rightResults(i).text
      examples += Example(leftResult, rightResult)
    }

    val dirtyWord: String = leftResults(0).text.trim
    val dirtyTranslation: String = rightResults(0).text.trim

    val adjectiveRegex = """([a-zA-Z\-]+) \{adj\.\} ?(.*)""".r
    val maleAdjectiveRegex = """([a-zA-Z\-]+) \{adj\. m\} ?(.*)""".r
    val femaleAdjectiveRegex = """([a-zA-Z\-]+) \{adj\. f\} ?(.*)""".r
    val maleNounRegex = """([a-zA-Z\-]+) \{m\} ?(.*)""".r
    val femaleNounRegex = """([a-zA-Z\-]+) \{f\} ?(.*)""".r
    val nounRegex = """([a-zA-Z\-]+) \{noun\} ?(.*)""".r
    val onlyWordRegex = """([a-zA-Z\-]+) .*""".r

    def formatWord(word: String): (String, Option[String], Option[WordType], Option[Gender]) = word match {
      case adjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), None)
      case maleAdjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), Some(Male))
      case femaleAdjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), Some(Female))
      case maleNounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), Some(Male))
      case femaleNounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), Some(Female))
      case nounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), None)
      case onlyWordRegex(extractedWord) => (extractedWord, None, None, None)
      case _ => (word, None, None, None)
    }

    val (cleanWord, wordDescription, wordType, gender) = formatWord(dirtyWord)
    val (cleanTranslation, translationDescription, translationWordType, translationGender) = formatWord(dirtyTranslation)
    Translation(Word(cleanWord, wordDescription, from, wordType, gender), Word(cleanTranslation, translationDescription, to, translationWordType, translationGender), examples.toList)
  }

}
