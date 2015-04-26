package generators

import models.ActionResult
import models.Database
import utils.CodeGenUtils
import play.api.Play
import play.api.Play.current

object ProviderGenerator {

  var database: Database = null

  def generateUriNumbering: String = {
    var number = 0
    var base = 0
    database.models.map { m =>
      base += 100
      number = base
      "    val " + m.name.toUpperCase + " = " + number + "\n" +
        "    val " + m.fieldsList(0).name.toUpperCase + " = " + (number + 1) + "\n"
    } mkString ("\n")
  }

  def generateMatcherAddUri: String = {
    database.models.map { m =>
      "    matcher.addURI(authority, \"" + m.name.toLowerCase + "\", " + m.name.toUpperCase + ")\n" +
        "    matcher.addURI(authority, \"" + m.fieldsList(0).name.toLowerCase + "\", " + m.fieldsList(0).name.toUpperCase + ")\n"
    } mkString ("\n")
  }

  def generateCaseContentType: String = {
    var content = database.models.map { m =>
      "    case UriValues." + m.name.toUpperCase + " => " + CodeGenUtils.convertToUpperCamelCase(m.name) + "DetailsTable.CONTENT_TYPE\n" +
        "    case UriValues." + m.fieldsList(0).name.toUpperCase + " => " + CodeGenUtils.convertToUpperCamelCase(m.name) + "DetailsTable.CONTENT_ITEM_TYPE\n"
    } mkString ("\n")
    content += "\n    case _ => throw new UnsupportedOperationException(\"Unknown uri: \" + uri)"
    content
  }

  def generateInsertQuery: String = {
    var content = database.models.map { m =>
      val modelName = CodeGenUtils.convertToUpperCamelCase(m.name)
      "    case UriValues." + m.name.toUpperCase + " => {\n" +
        "      db.insertOrThrow(Tables." + m.name.toUpperCase + ", null, values)\n" +
        "      getContext().getContentResolver().notifyChange(uri, null)\n" +
        "      " + modelName + "DetailsTable.build" + modelName + "DetailsUri(values.getAsString(" + modelName + "Colomns." + m.fieldsList(0).name.toUpperCase + "))\n    }\n"
    } mkString ("\n")
    content += "\n    case _ => throw new UnsupportedOperationException(\"Unknown uri: \" + uri)"
    content
  }

  def generateCaseQuery: String = {
    var content = database.models.map { m =>
      val modelName = CodeGenUtils.convertToUpperCamelCase(m.name)
      "    case UriValues." + m.fieldsList(0).name.toUpperCase + " => {\n" +
        "      builder.where(" + modelName + "Colomns." + m.fieldsList(0).name.toUpperCase + "+ \"=?\",\n" +
        "        " + modelName + "DetailsTable.get" + CodeGenUtils.convertToUpperCamelCase(m.fieldsList(0).name) + "(uri))\n" +
        "        " + "builder.table(Tables." + modelName.toUpperCase + ")\n" +
        "    }\n" +
        "    case UriValues." + modelName.toUpperCase + " => {\n" +
        "      builder.table(Tables." + modelName.toUpperCase + ")\n" +
        "    }\n"
    } mkString ("\n")
    content += "\n    case _ => throw new UnsupportedOperationException(\"Unknown uri: \" + uri)"
    content
  }

  def generate(d: Database): ActionResult = {
    database = d

    var content = CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/provider.template"))

    content = content.replace(CodeGenUtils.packageTemplate, d.packageName)
    content = content.replace(CodeGenUtils.databaseTemplate, CodeGenUtils.convertToUpperCamelCase(d.name))
    content = content.replace(CodeGenUtils.packageTemplate, d.packageName)
    content = content.replace(CodeGenUtils.uriNumbering, generateUriNumbering)
    content = content.replace(CodeGenUtils.matcherAddUri, generateMatcherAddUri)
    content = content.replace(CodeGenUtils.caseContentType, generateCaseContentType)
    content = content.replace(CodeGenUtils.insertQuery, generateInsertQuery)
    content = content.replace(CodeGenUtils.caseQuery, generateCaseQuery)

    ActionResult(CodeGenUtils.convertToUpperCamelCase(d.name) + "Provider.scala", content)
  }
}