package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class Course(id: Option[Long], name: String, targetLanguageId: Long, presentingLanguageId: Long)

class CourseDao @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all(): Future[Seq[Course]] = db.run(Courses.result)
  def byId(id: Long): Future[Option[Course]] = db.run(Courses.filter(_.id === id).result.headOption)
  def insert(course: Course) = db.run(Courses += course)
  def delete(id: Long) = db.run(Courses.filter(_.id === id).delete)
  def update(id: Long, course: Course) = db.run(Courses.filter(_.id === id).update(course))

  class CourseTable(tag: Tag) extends Table[Course](tag, "role") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def targetLanguageId = column[Long]("target_language_id")
    def presentingLanguageId = column[Long]("presenting_language_id")

    def * = (id.?, name, targetLanguageId, presentingLanguageId) <> (Course.tupled, Course.unapply)
  }

  val Courses = TableQuery[CourseTable]
}
