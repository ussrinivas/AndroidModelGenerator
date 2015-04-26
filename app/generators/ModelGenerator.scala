package generators

import models.Database
import models.ActionResult
import models.Models
import utils.CodeGenUtils
import models.Fields
import play.api.Play
import play.api.Play.current

class ModelGenerator(database: Database) {

  private def objectInfo(m: Models) = (m.name.toLowerCase + "Info")

  private def listName(m: Models) = (m.name.toLowerCase + "List")

  private def convertToFieldObjs(fieldsList: List[Fields]): String = {
    fieldsList.map { f =>
      "  " + CodeGenUtils.convertToLowerCamelCase(f.name) + ": " + f.fieldType
    } mkString ("\n", ", \n", "")
  }

  private def getConvertedValues(m: Models): String = {
    m.fieldsList.map { f =>
      "      c.get" + f.fieldType + "(c.getColumnIndex(" + CodeGenUtils.convertToUpperCamelCase(m.name) + "Colomns." + f.name.toUpperCase + "))"
    } mkString ("\n", ",\n", "")    
  }

  private def convertToContentValue(m: Models): String = {
    m.fieldsList.map { f =>
      "    values.put(" + CodeGenUtils.convertToUpperCamelCase(m.name) + "Colomns." + f.name.toUpperCase + ", " + objectInfo(m) + "." + CodeGenUtils.convertToLowerCamelCase(f.name) + ")"
    } mkString ("", "\n", "")
  }

  private def getModel(m: Models): ActionResult = {
    var content = CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/model.template"))

    content = content.replace(CodeGenUtils.packageTemplate, database.packageName)
    content = content.replace(CodeGenUtils.classTemplate, CodeGenUtils.convertToUpperCamelCase(m.name))
    content = content.replace(CodeGenUtils.objectTemplate, objectInfo(m))
    content = content.replace(CodeGenUtils.listNameTemplate, listName(m))
    content = content.replace(CodeGenUtils.databaseTemplate, CodeGenUtils.convertToUpperCamelCase(database.name))
    content = content.replace(CodeGenUtils.fieldObjects, convertToFieldObjs(m.fieldsList))
    content = content.replace(CodeGenUtils.convertedValues, getConvertedValues(m))
    content = content.replace(CodeGenUtils.contentValues, convertToContentValue(m))
    content = content.replace(CodeGenUtils.queryCondition, CodeGenUtils.convertToUpperCamelCase(m.name) + "Colomns." + m.fieldsList(0).name.toUpperCase + " + \" = \" + " + m.name.toLowerCase + "Info." + m.name.toLowerCase + "Id")

    ActionResult(CodeGenUtils.convertToUpperCamelCase(m.name) + ".scala", content)
  }

  def generate: List[ActionResult] = {
    database.models.map { m =>
      getModel(m)
    }
  }
}