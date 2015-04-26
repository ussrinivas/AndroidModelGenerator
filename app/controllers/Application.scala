package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._
import models.ModelImplicits._

import generators.DatabaseGenerator
import generators.HelpersGenerator
import generators.ProviderGenerator

trait Responses {
  self: Controller =>

  def okRes(msg: String) = Json.obj("status" -> 1, "msg" -> msg)

  def badRes(err: String) = Json.obj("status" -> 0, "err" -> err)

  def bad(err: String) =
  BadRequest(err)

  def futureBad(err: String) = Future.successful {
    BadRequest(badRes(err))
  }

  def justOk(msg: String) =
  Ok(okRes(msg))

  def futureOk(msg: String) = Future.successful {
    Ok(okRes(msg))
  }
}

object Application extends Controller with Responses {

  def index = Action {
    Ok(views.html.index("Android Model Generator"))
  }

  def receiveModelContent = Action.async(parse.json) { implicit request =>
    try {
      val database = request.body.as[models.Database]
      Future.successful {
        val response = UserResponse(List(
          DatabaseGenerator.generate(database), 
          ProviderGenerator.generate(database), 
          HelpersGenerator.generate(database)))
        Ok(Json.toJson(response))
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        futureBad("Failed to parse the received data")
    }
  }

}
