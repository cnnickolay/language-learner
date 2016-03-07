import java.util

import com.gargoylesoftware.htmlunit.html.{HtmlElement, HtmlPage}
import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}

import scala.collection.JavaConverters._
import scala.xml.XML

val w = new WebClient(BrowserVersion.CHROME)
w.getOptions.setJavaScriptEnabled(false)
w.getOptions.setCssEnabled(false)

val page = w.getPage("http://en.bab.la/dictionary/french-english/nuit"): HtmlPage
val xml = XML.loadString(page.asXml())
val div = xml \ "div"
println(div)