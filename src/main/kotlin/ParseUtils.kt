import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.Locale

/**
 * Filename: ParseUtils
 * Author: GG
 * Date: 2023/8/28 0028 17:49
 * Description:
 */
object ParseUtils {

    private lateinit var valuesXml: Element

    @Throws
    suspend fun excel2Xml(excelPath: String, xmlFilesPath: String, logCallback: ((String) -> Unit)? = null) {

        // 读取 Excel 文件
        val file = File(excelPath)
        // Place definition above class declaration to make field static

        val inputStream = FileInputStream(file)
        val workbook = WorkbookFactory.create(inputStream)

        val directory = File(xmlFilesPath)
        val subdirectories = listSubdirectories(directory)

        val xmlMap = mutableMapOf<File, Element>()
        val documentMap = mutableMapOf<File, Document>()

        val reader = SAXBuilder()
        for (subdirectory in subdirectories) {
            val stringsXMl = File(subdirectory, "strings.xml")
            if (!stringsXMl.exists()) continue
            val document = reader.build(stringsXMl)
            val root: Element = document.rootElement
            xmlMap[subdirectory] = root
            documentMap[subdirectory] = document
            if (subdirectory.name == "values") {
                valuesXml = root
            }
        }

        // 读取第一个工作表
        val sheet: Sheet = workbook.getSheetAt(0)

        val map = mutableMapOf<String, Int>()
        var enIndex = -1

        if (::valuesXml.isInitialized) {
            var tempStr = ""
            var valuesIndex: Int = -1
            var element: Element? = null
            var enValue: Cell
            sheet.forEachIndexed { rowIndex, row ->
                if (rowIndex == 0) {
                    row.forEachIndexed { index, cell ->
//                    println("$index----" + cell.stringCellValue)
                        map[cell.stringCellValue] = index
                        if (cell.stringCellValue.equals("values-en")) {
                            enIndex = index
                        }
                    }
                } else {
                    enValue = row.getCell(enIndex)

                    tempStr = enValue.stringCellValue.trim().replace("'", "\\'")
                    if (tempStr.isEmpty())
                        return@forEachIndexed
                    valuesXml.children.forEachIndexed eachIndex@{ i, e ->
                        if (e.text == tempStr) {
                            valuesIndex = i
                            element = e
//                            println("$valuesIndex---->${element?.text}")
                            return@eachIndex
                        }
                    }

                    if (element == null) {
                        element = Element("string")
                        element?.let {
                            it.text = tempStr
                            val name = getElementName(row, valuesIndex, tempStr)
                            it.setAttribute("name", name)
//                            logInfoFlow.emit("${name}----->${it.text}" + "\n")
//                            println(name)
                            valuesXml.addContent(it)
                        }
                    }

                    xmlMap.forEach { (file, xmlElement) ->
                        map[file.name]?.let {
                            val cell = row.getCell(it)
                            val value = cell.stringCellValue.replace("'", "\\'")
                            val name = element?.getAttributeValue("name")
                            if (xmlElement.children.firstOrNull { item -> item.getAttributeValue("name") == name } == null) {
                                val newElement = Element("string")
                                newElement.text = value

                                newElement.setAttribute("name", name)
                                xmlElement.addContent(newElement)
                            }
                        }
                    }
                }
            }

        }
        xmlMap.forEach { (parentFile, xmlElement) ->
            // 将更改保存回 XML 文件
            val xmlFile = File(parentFile, "strings.xml")
            if (xmlFile.exists()) {
                // 保存修改后的 XML 文件
                val outputter = XMLOutputter(Format.getPrettyFormat())
                outputter.output(xmlElement, FileWriter(xmlFile))
                println("${parentFile.name}-->保存完成")
                logCallback?.invoke("${parentFile.name}-->保存完成")
            }
        }
        // 关闭工作簿和输入流
        workbook.close()
        inputStream.close()
    }

    private fun getElementName(row: Row, valuesIndex: Int, tempStr: String): String {
        val id = row.getCell(0).stringCellValue
        if (id.isNotEmpty()) {
            return id
        }
        if (valuesIndex != -1)
            return valuesXml.children.getOrNull(valuesIndex)?.text ?: addNameAttribute(
                tempStr.replace(Regex("[^a-zA-Z0-9_]"), "_").lowercase(Locale.getDefault()),
                valuesXml.children
            )
        return ""
    }

    private fun addNameAttribute(name: String, elements: List<Element>): String {
        if (elements.firstOrNull { it.getAttributeValue("name") == name } == null) {
            return name
        } else {
            return addNameAttribute(name + "_other", elements)
        }
    }

    private fun listSubdirectories(directory: File): List<File> {
        val subdirectories = mutableListOf<File>()

        // 获取文件夹中的所有文件和文件夹
        val files = directory.listFiles() ?: return emptyList()

        // 遍历所有文件和文件夹
        for (file in files) {
            if (file.isDirectory) {
                // 如果是文件夹，将其添加到返回列表中，并递归获取其子文件夹
                subdirectories.add(file)
                subdirectories.addAll(listSubdirectories(file))
            }
        }

        return subdirectories
    }

}