package utils

import org.scalatest.FlatSpec
import utils.Translator.{French, English}

/**
  * Created by Nikolay Cherkezishvili on 07/03/2016
  */
class Translator$Test extends FlatSpec {

  it should "extract translation" in {
    Translator.translate(French, English, "courant")
  }

}
