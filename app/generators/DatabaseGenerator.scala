package generators

import models.Database
import models.ActionResult
import utils.CodeGenUtils
import models.Fields
import play.api.Play
import play.api.Play.current

object DatabaseGenerator {
  var database: Database = null

  private def mapTypes(t: String): String = {
    t match {
      case "String" => "text"
      case "Int" => "integer"
      case "Long" => "long"
      case _ => "text"
    }
  }

  private def generateImports: String = {
    database.models.map { m =>
      "import " + database.packageName + ".provider." + "Helpers." + CodeGenUtils.convertToUpperCamelCase(m.name) + "DetailsTable"      
    } mkString ("\n")
  }

  private def generateTableTrait: String = {
    database.models.map { m =>
      "    val " + m.name.toUpperCase + " = \"" + m.name.toLowerCase + "\""
    } mkString ("\n")
  }

  private def generateDatabaseQuery: String = {
    database.models.map { m =>
      "    db.execSQL(Tables." + m.name.toUpperCase + ")"
    } mkString ("\n")
  }

  private def mapFields(fieldsList: List[Fields], modelName: String): String = {
    fieldsList.map { f =>
      "     " + modelName + "Colomns." + f.name.toUpperCase + " + \" " + mapTypes(f.fieldType) 
    } mkString ("\n", ", \" +\n", ")\"")
  }

  private def generateCreateTables: String = {

    database.models.map { m =>
      val modelName = CodeGenUtils.convertToUpperCamelCase(m.name)
      "  val CREATE_" + m.name.toUpperCase + " = \"CREATE TABLE \" +\n" +
        "     Tables." + m.name.toUpperCase + " + \" (\" + " + "BaseColumns._ID +\n" +
        "     \" integer NOT NULL PRIMARY KEY AUTOINCREMENT, \" +" + mapFields(m.fieldsList, modelName)
    } mkString ("\n\n")
  }

  def generate(d: Database): ActionResult = {
    database = d
    var content = CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/database.template"))

    content = content.replace(CodeGenUtils.packageTemplate, database.packageName)
    content = content.replace(CodeGenUtils.databaseTemplate, CodeGenUtils.convertToUpperCamelCase(database.name))
//    content = content.replace(CodeGenUtils.importsTemplate, generateImports)
    content = content.replace(CodeGenUtils.tableDetailsTemplate, generateTableTrait)
    content = content.replace(CodeGenUtils.executeQueryTemplate, generateDatabaseQuery)
    content = content.replace(CodeGenUtils.createTablesTemplate, generateCreateTables)

    ActionResult(CodeGenUtils.convertToUpperCamelCase(database.name) + "Database.scala", content)
  }
}