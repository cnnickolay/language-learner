package controllers

import com.google.inject.Inject
import model.Model.{Subtitle, SubtitlesSrtRaw}
import model.SubtitleDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import utils.SrtParser

import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode


class SubtitleController @Inject()(subtitleDao: SubtitleDao) extends Controller {

  def getAll(mediaGroupId: Long, mediaId: Long) = Action.async {
    for {
      subtitles <- subtitleDao.all(mediaId)
    } yield Ok(Json.toJson(subtitles))
  }

  def byId(mediaGroupId: Long, mediaId: Long, subtitleId: Long) = Action.async {
    for {
      subtitle <- subtitleDao.byId(mediaId, subtitleId)
    } yield Ok(Json.toJson(subtitle))
  }

  def update(mediaGroupId: Long, mediaId: Long, subtitleId: Long) = Action.async { request =>
    request.body.asJson.map(json =>
      json.validate[Subtitle] match {
        case JsSuccess(subtitle, _) =>
          subtitleDao.update(mediaId, subtitleId, subtitle)
            .flatMap(_ => Future(Ok(s"Subtitle $subtitleId of media $mediaId updated")))
        case JsError(e) => Future(BadRequest("Unable to parse json"))
      }
    ).getOrElse(Future(BadRequest("Unable to process request")))
  }

  def create(mediaGroupId: Long, mediaId: Long) = Action.async { request =>
    request.body.asJson.map { json =>
      json.validate[Subtitle] match {
        case JsSuccess(subtitle, _) =>
          subtitleDao.totalSubtitles(mediaId)
            .flatMap {
              totalSubs => subtitleDao.create(subtitle)
            }
            .flatMap { _ =>
              Future { Ok("new subtitle added")}
            }
        case JsError(e) => Future(BadRequest("Unable to parse json"))
      }

    }.getOrElse(Future(BadRequest("Unable to process request")))
  }

  def delete(mediaGroupId: Long, mediaId: Long, subtitleId: Long) = Action.async {
    for {
      _ <- subtitleDao.delete(mediaId, subtitleId)
    } yield Ok(s"Subtitle $subtitleId of media $mediaId deleted")
  }

  def uploadSrt(mediaGroupId: Long, mediaId: Long) =
    Action.async(parse.maxLength(512 * 1024, parser = parse.json(512 * 1024))) { request =>
      Future {
        request.body match {
          case Left(_) => BadRequest("File is too big")
          case Right(json) =>
            json.validate[SubtitlesSrtRaw] match {
              case JsSuccess(srt, _) =>
                val text = SrtParser.parseText(srt.srt)
                text.foreach(textBlock =>
                  subtitleDao.create(Subtitle(id = None, offset = Some(textBlock.timeFrom.setScale(1, RoundingMode.DOWN)), textBlock.text, Some(mediaId)))
                )
                Ok(text.toString())
              case JsError(_) => BadRequest("Unable to parse json")
            }
        }
      }
    }
}
