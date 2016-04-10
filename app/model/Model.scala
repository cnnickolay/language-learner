package model

object Model {

  case class Course(name: String, languageId: Int)
  case class Lesson(id: Option[Long], courseId: Long)

  case class LessonChunk(id: Option[Long], lessonId: Long)

  case class Exercise()

  // Lesson chunk of type Dialog
  case class Dialog(id: Option[Long], name: String, languageId: Int)
  case class Speech(id: Option[Long], speaker: Option[String], pos: Int)
  case class Phrase(id: Option[Long], text: String, pos: Int)
  case class Word(id: Option[Long], word: String, pos: Int)
  // think about exercises over dialogs by hiding some words


  // Lesson chunk of typ Grammar
  case class Grammar(id: Option[Long])
}
