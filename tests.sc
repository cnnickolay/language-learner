import utils.TranslatorObjects._

import scala.util.matching.Regex

val s = """adtrd { pron. }"""

def formatWord(rawWord: String): (String, Option[String], Option[WordType], Option[Gender]) = {
  val wordRegex = """(?U)([\w \-]+) ?.*""".r
  val adjectiveRegex = """.*\{ adj\. \}.*""".r
  val maleAdjectiveRegex = """.*\{ adj\. m \}.*""".r
  val femaleAdjectiveRegex = """.*\{ adj\. f \}.*""".r
  val maleNounRegex = """.*\{ m \}.*""".r
  val femaleNounRegex = """.*\{ f \}.*""".r
  val nounRegex = """.*\{ noun \}.*""".r
  val verbRegex = """.*\{ vb \}.*""".r
  val pronRegex = """.*\{ pron\. \}.*""".r

  val wordRegex(word) = rawWord
  val (wordType, gender) = rawWord match {
    case adjectiveRegex() => (Some(Adjective), None)
    case maleAdjectiveRegex() => (Some(Adjective), Some(Masculine))
    case femaleAdjectiveRegex() => (Some(Adjective), Some(Feminine))
    case maleNounRegex() => (Some(Noun), Some(Masculine))
    case femaleNounRegex() => (Some(Noun), Some(Feminine))
    case nounRegex() => (Some(Noun), None)
    case verbRegex() => (Some(Verb), None)
    case pronRegex() => (Some(Pronoun), None)
    case _ => (None, None)
  }
  (word, None, wordType, gender)
}

formatWord(s)