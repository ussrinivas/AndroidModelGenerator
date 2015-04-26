package generators

import models.Database
import utils.CodeGenUtils
import models.Models
import models.ActionResult
import play.api.Play
import play.api.Play.current


class ContractGenerator(database: Database) {

  val contractName = CodeGenUtils.convertToUpperCamelCase(database.name)

  private def generateImports(m: Models): String = {
    "import " + database.packageName + ".provider." + "Helpers." + CodeGenUtils.convertToUpperCamelCase(m.name) + "DetailsTable\n"
  }

  private def generateTableTrait: String = {
    database.models.map { m =>
      "    val " + m.name.toUpperCase + " = \"" + m.name.toLowerCase + "\""
    } mkString ("\n")
  }
  
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

  private def generatePathVariables(m: Models): String = {
    "  val PATH_" + m.name.toUpperCase + "_DETAILS = \"" + m.name.toLowerCase + "_details\"" + "\n"
  }

  private def generateDetailsClass(m: Models): String = {

    val contentType = "vnd." + contractName.toLowerCase + "." + m.name.toLowerCase

    var content = "\n" + CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/details.template"))
    content = content.replace(CodeGenUtils.modelTemplate, CodeGenUtils.convertToUpperCamelCase(m.name))
    content = content.replace("${modelNameUpper}", m.name.toUpperCase)
    content = content.replace("${modelNameLower}", m.name.toLowerCase)
    content = content.replace(CodeGenUtils.databaseTemplate, CodeGenUtils.convertToUpperCamelCase(database.name))
    content = content.replace("${contentType}", contentType)
    content = content.replace("${projection}", m.fieldsList.map { f => CodeGenUtils.convertToUpperCamelCase(m.name) + "Colomns." + f.name.toUpperCase }.mkString("", ", ", ""))
    content
  }

  private def generate(f: (Models) => String): String = {
    var content = ""
    database.models.map { m => (content += f(m)) }
    content
  }

  def generate: ActionResult = { 		
    var content = CodeGenUtils.readTemplate(Play.classloader.getResourceAsStream("templates/contract.template"))
    content = content.replace(CodeGenUtils.packageTemplate, database.packageName)
    content = content.replace(CodeGenUtils.contractTemplate, CodeGenUtils.convertToUpperCamelCase(database.name))
    content = content.replace(CodeGenUtils.traitsTemplate, generate(generateTraits))
    content = content.replace(CodeGenUtils.pathVariables, generate(generatePathVariables))
    content = content.replace(CodeGenUtils.detailsTables, generate(generateDetailsClass))
    content = content.replace(CodeGenUtils.tableDetailsTemplate, generateTableTrait)
    new ActionResult(CodeGenUtils.convertToUpperCamelCase(database.name) + "Contract.scala", content)
  }

}