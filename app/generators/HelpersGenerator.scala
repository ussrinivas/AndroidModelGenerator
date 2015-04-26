package generators

import models.Database
import models.ActionResult
import utils.CodeGenUtils
import models.Models
import play.api.Play
import play.api.Play.current

object HelpersGenerator {
  var database: Database = null

  private def generateTraits(m: Models): String = {
    var content = "\n" + CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/traits.template"))
    var variables = ""
    content = content.replace(CodeGenUtils.modelTemplate, CodeGenUtils.convertToUpperCamelCase(m.name))
    m.fieldsList.map { f =>
      variables += "\n    val " + f.name.toUpperCase + " = \"" + f.name + "\""
    }
    content = content.replace(CodeGenUtils.fieldsTemplate, variables)
    content
  }

  def generate(d: Database): ActionResult = {
    database = d
    new ActionResult("Helpers.scala", "package " + d.packageName + ".provider.Helpers\n" + d.models.map { m => generateTraits(m) }.mkString)
  }
}