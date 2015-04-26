package models

import play.api.libs.json.Json

case class Fields(name: String, fieldType: String)

case class Models(name: String, idName: String, number: Int, fieldIndex: Int, fieldsList: List[Fields])

case class Database(name: String, packageName: String, models: List[Models])

case class ActionResult(name: String, code: String)

case class UserResponse(var results: List[ActionResult]) {
  def add(r: ActionResult) {
      results ::= r
  }
}

object ModelImplicits {
  implicit val fieldsJson = Json.format[Fields]
  implicit val modelsJson = Json.format[Models]
  implicit val databaseJson = Json.format[Database]
  implicit val actionResultJson = Json.format[ActionResult]
  implicit val userResponseJson = Json.format[UserResponse]
}
