package utils

import java.net.URL

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities, RemoteWebDriver}

import scala.collection.mutable.ListBuffer
import scala.xml.{Elem, XML, Node}
import utils.TranslatorObjects._

object Translator {

  def readPageByHtmlUnit(from: Language, to: Language, word: String): Elem = {
    val client = new WebClient(BrowserVersion.CHROME)
    client.getOptions.setJavaScriptEnabled(false)
    val url: String = s"http://en.bab.la/dictionary/${from.name}-${to.name}/$word"
    val page = client.getPage(url): HtmlPage
    val rawXml = page.asXml().replaceAll("(?s)//<!\\[CDATA\\[.*?\\]\\]>", "").replaceAll("//\\]\\]>", "").lines.fold("") { (x, y) => x.trim + "\n" + y.trim }
    XML.loadString(rawXml)
  }

  def translate(from: Language, to: Language, word: String): Seq[Translation] = {
    val xml = readPageByHtmlUnit(from, to, word)
    val found =
      (xml \\ "div")
        .filter {
          resultNode =>
            ((resultNode \ "@class").text contains "result-wrapper") &&
              ((resultNode \\ "div").filter(resultLeftNode => (resultLeftNode \ "@class").text contains "result-left") \\ "strong").nonEmpty
        }.theSeq

    found.map { n => processSegment(from, to, n) }
  }

  def processSegment(from: Language, to: Language, node: Node): Translation = {
    val leftResults = (node \\ "div").filter(n => (n \ "@class").text contains "result-left")
    val rightResults = (node \\ "div").filter(n => (n \ "@class").text contains "result-right")

    def removeExtraSpaces(word: String) = word.replaceAll("[\n ]+", " ").replaceAll("' ", "'").replaceAll("’ ", "’").replaceAll(" ,", ",").replaceAll(" \\.", ".").trim

    val examples = ListBuffer[Example]()
    for (i <- 1 until leftResults.length) {
      val leftResult = removeExtraSpaces(leftResults(i).text)
      val rightResult = removeExtraSpaces(rightResults(i).text)
      examples += Example(leftResult, rightResult)
    }

    val dirtyWord: String = removeExtraSpaces(leftResults(0).text)
    val dirtyTranslation: String = removeExtraSpaces(rightResults(0).text.trim)

    def formatWord(rawWord: String): (String, Option[String], Option[WordType], Option[Gender]) = {
      val wordRegex = """(?U)([\w \-]+) ?.*""".r
      val adjectiveRegex = """.*\{ adj\. \}.*""".r
      val maleAdjectiveRegex = """.*\{ adj\. m \}.*""".r
      val femaleAdjectiveRegex = """.*\{ adj\. f \}.*""".r
      val maleNounRegex = """.*\{ m \}.*""".r
      val femaleNounRegex = """.*\{ f \}.*""".r
      val nounRegex = """.*\{ noun \}.*""".r
      val verbRegex = """.*\{ vb \}.*""".r
      val verbTransitiveRegex = """.*\{ v\.t\. \}.*""".r
      val pronRegex = """.*\{ ?pron\. ?\}.*""".r

      val wordRegex(word) = rawWord
      val (wordType, gender) = rawWord match {
        case adjectiveRegex() => (Some(Adjective), None)
        case maleAdjectiveRegex() => (Some(Adjective), Some(Masculine))
        case femaleAdjectiveRegex() => (Some(Adjective), Some(Feminine))
        case maleNounRegex() => (Some(Noun), Some(Masculine))
        case femaleNounRegex() => (Some(Noun), Some(Feminine))
        case nounRegex() => (Some(Noun), None)
        case pronRegex() => (Some(Pronoun), None)
        case verbRegex() => (Some(Verb), None)
        case verbTransitiveRegex() => (Some(VerbTransitive), None)
        case _ => (None, None)
      }
      (word, None, wordType, gender)
    }

    val (cleanWord, wordDescription, wordType, gender) = formatWord(dirtyWord)
    val (cleanTranslation, translationDescription, translationWordType, translationGender) = formatWord(dirtyTranslation)
    Translation(Word(cleanWord, wordDescription, from, wordType, gender), Word(cleanTranslation, translationDescription, to, translationWordType, translationGender), examples.toList)
  }

}
