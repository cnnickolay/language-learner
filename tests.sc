import utils.Translator.{Male, Noun, Female}

val maleNounRegex = """.*\{m\}""".r
val femaleNounRegex = """.*\{f\}""".r

val (wordType, gender) = "word {f}" match {
  case maleNounRegex() => (Some(Noun), Some(Male))
  case femaleNounRegex() => (Some(Noun), Some(Female))
  case _ => (None, None)
}
