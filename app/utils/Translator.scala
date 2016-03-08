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

  def readPageByWebClient(from: Language, to: Language, word: String): Elem = {
    val capabilities = DesiredCapabilities.chrome()
    capabilities.setJavascriptEnabled(false)
    val driver = new RemoteWebDriver(new URL("http://192.168.99.100:4444/wd/hub"), capabilities)
//    val driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities)

    try {
      driver.get(s"http://en.bab.la/dictionary/french-english/$word")

      val source = driver.getPageSource
      XML.loadString(source)
    } finally {
      driver.quit()
    }
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

    def removeExtraSpaces(word: String) = word.replaceAll("[\n ]+", " ").replaceAll("' ", "'").replaceAll("’ ", "’").replaceAll(" ,", ",").trim

    val examples = ListBuffer[Example]()
    for (i <- 1 until leftResults.length) {
      val leftResult = removeExtraSpaces(leftResults(i).text)
      val rightResult = removeExtraSpaces(rightResults(i).text)
      examples += Example(leftResult, rightResult)
    }

    val dirtyWord: String = removeExtraSpaces(leftResults(0).text)
    val dirtyTranslation: String = removeExtraSpaces(rightResults(0).text.trim)

    val adjectiveRegex = """([a-zA-Z\- ]+) \{ adj\. \} ?(.*)""".r
    val maleAdjectiveRegex = """([a-zA-Z\- ]+) \{ adj\. m \} ?(.*)""".r
    val femaleAdjectiveRegex = """([a-zA-Z\- ]+) \{ adj\. f \} ?(.*)""".r
    val maleNounRegex = """([a-zA-Z\- ]+) \{ m \} ?(.*)""".r
    val femaleNounRegex = """([a-zA-Z\- ]+) \{ f \} ?(.*)""".r
    val nounRegex = """([a-zA-Z\- ]+) \{ noun \} ?(.*)""".r
    val verbRegex = """([.\- ]+) (\[.*\]) \{ vb \} ?(.*)""".r
    val onlyWordRegex = """([a-zA-Z\- ]+) .*""".r


    def formatWord(rawWord: String): (String, Option[String], Option[WordType], Option[Gender]) = {
//      case adjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), None)
//      case maleAdjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), Some(Male))
//      case femaleAdjectiveRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Adjective), Some(Female))
//      case maleNounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), Some(Male))
//      case femaleNounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), Some(Female))
//      case nounRegex(extractedWord, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Noun), None)
//      case verbRegex(extractedWord, _, description) => (extractedWord, if (description.isEmpty) None else Some(description), Some(Verb), None)
//      case onlyWordRegex(extractedWord) => (extractedWord, None, None, None)
//      case _ => (word, None, None, None)
      val wordRegex = """([\\w \-]).*""".r
      val wordRegex(word) = rawWord

    }

    val (cleanWord, wordDescription, wordType, gender) = formatWord(dirtyWord)
    val (cleanTranslation, translationDescription, translationWordType, translationGender) = formatWord(dirtyTranslation)
    Translation(Word(cleanWord, wordDescription, from, wordType, gender), Word(cleanTranslation, translationDescription, to, translationWordType, translationGender), examples.toList)
  }

}
