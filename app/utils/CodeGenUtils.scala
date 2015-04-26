package utils

import java.io.InputStream

object CodeGenUtils {
  val packageTemplate = "${packageName}"
  val databaseTemplate = "${databaseName}"
  val importsTemplate = "${imports}"
  val createTablesTemplate = "${createTables}"
  val tableDetailsTemplate = "${tableDetails}"
  val executeQueryTemplate = "${executeQuery}"
  val contractTemplate = "${contract}"
  val traitsTemplate = "${colomnTraits}"
  val pathVariables = "${pathVariables}"
  val detailsTables = "${detailsTables}"
  val modelTemplate = "${modelName}"
  val fieldsTemplate = "${fields}"
  val classTemplate = "${className}"
  val objectTemplate = "${objectName}"
  val listNameTemplate = "${listName}"
  val fieldObjects = "${fieldObjects}"
  val convertedValues = "${convertedValues}"
  val contentValues = "${contentValues}"
  val queryCondition = "${queryCondition}"
  val uriNumbering = "${uriNumbering}"
  val matcherAddUri = "${matcherAddUri}"
  val caseContentType = "${caseContentType}"
  val insertQuery = "${insertQuery}"
  val caseQuery = "${caseQuery}"

  private def toCamelCase(s: String): String = {    
    val parts = s.split("_");
    if (parts.size > 1)
      parts(0) + toProperCase(parts(1));
    else
      s.toLowerCase
  }

  private def toUpperCamelCase(s: String): String = {
    val parts = s.split("_");
    var content = "";
    parts.foreach(a => content += toProperCase(a))
    content
  }

  private def toProperCase(s: String): String = {
    s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
  }

  def convertToLowerCamelCase(content: String): String = {
    toCamelCase(content)
  }

  def convertToUpperCamelCase(content: String): String = {
    toUpperCamelCase(content)
  }

  def readTemplate(fileName: String): String = {
    scala.io.Source.fromFile(fileName).mkString
  }

  def readTemplate(stream: InputStream) = {
    scala.io.Source.fromInputStream(stream).mkString
  }
}
 