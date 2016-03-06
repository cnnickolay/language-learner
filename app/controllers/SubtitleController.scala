package controllers

import java.io.File

import com.google.inject.Inject
import model.Model.{SubtitlesSrtRaw, Subtitle}
import model.SubtitleDao
import play.api.libs.Files.TemporaryFile
import play.api.libs.json._
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Result, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.SrtParser
import utils.SrtParser.TextBlock

import scala.concurrent.Future


class SubtitleController @Inject()(subtitleDao: SubtitleDao) extends Controller {

  def getAll(id: Long) = Action.async {
    for {
      subtitles <- subtitleDao.all(id)
    } yield Ok(Json.toJson(subtitles))
  }

  def byId(mediaId: Long, subtitleId: Long) = Action.async {
    for {
      subtitle <- subtitleDao.byId(mediaId, subtitleId)
    } yield Ok(Json.toJson(subtitle))
  }

  def update(mediaId: Long, subtitleId: Long) = Action.async { request =>
    request.body.asJson.map(json =>
      json.validate[Subtitle] match {
        case JsSuccess(subtitle, _) =>
          subtitleDao.update(mediaId, subtitleId, subtitle)
            .flatMap(_ => Future(Ok(s"Subtitle $subtitleId of media $mediaId updated")))
        case JsError(e) => Future(BadRequest("Unable to parse json"))
      }
    ).getOrElse(Future(BadRequest("Unable to process request")))
  }

  def create(mediaId: Long) = Action.async { request =>
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

  def delete(mediaId: Long, subtitleId: Long) = Action.async {
    for {
      _ <- subtitleDao.delete(mediaId, subtitleId)
    } yield Ok(s"Subtitle $subtitleId of media $mediaId deleted")
  }

  def uploadSrt(mediaId: Long) = Action.async(parse.maxLength(512 * 1024, parser = parse.json(512 * 1024))) { request =>
    Future {
      request.body match {
        case Left(_) => BadRequest("File is too big")
        case Right(json) =>
          json.validate[SubtitlesSrtRaw] match {
            case JsSuccess(srt, _) =>
              val text = SrtParser.parseText(srt.srt)
              text.foreach(textBlock =>
                subtitleDao.create(Subtitle(id = None, offset = Some(textBlock.timeFrom), textBlock.text, mediaId))
              )
              Ok(text.toString())
            case JsError(_) => BadRequest("Unable to parse json")
          }
      }
    }
  }
}
