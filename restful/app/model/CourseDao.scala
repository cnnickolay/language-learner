package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class Course(id: Option[Long], name: String, targetLanguageId: Int, presentingLanguageId: Int)

class CourseDao @Inject() (val dbConfigProvider: DatabaseConfigProvider, val languageDao: LanguageDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import languageDao.Languages

  def all(): Future[Seq[Course]] = db.run(Courses.result)
  def byId(id: Long): Future[Option[Course]] = db.run(Courses.filter(_.id === id).result.headOption)
  def byName(name: String): Future[Option[Course]] = db.run(Courses.filter(_.name === name).result.headOption)
  def insert(course: Course) = db.run(Courses += course)
  def delete(id: Long) = db.run(Courses.filter(_.id === id).delete)
  def update(id: Long, course: Course) = db.run(Courses.filter(_.id === id).update(course))

  class CourseTable(tag: Tag) extends Table[Course](tag, "course") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def targetLanguageId = column[Int]("target_language_id")
    def presentingLanguageId = column[Int]("presenting_language_id")

    def targetLanguage = foreignKey("course_target_language_id_fkey", targetLanguageId, Languages)(_.id)
    def presentingLanguage = foreignKey("course_presenting_language_id_fkey", presentingLanguageId, Languages)(_.id)

    def * = (id.?, name, targetLanguageId, presentingLanguageId) <> (Course.tupled, Course.unapply)
  }

  val Courses = TableQuery[CourseTable]
}
