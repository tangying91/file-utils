package dev.tang.app.impl

import dev.tang.app.AppForJoan
import dev.tang.utils.FileUtils
import dev.tang.utils.PathUtils
import dev.tang.vo.FilePath
import jxl.Workbook
import jxl.write.WritableSheet
import java.io.File

/**
 * 文档名称整理
 * 需求：一级目录，二级目录，文件名，文件相对路径
 */
object AppForJoan1 : AppForJoan() {

    /**
     * 找出所有项目文档
     */
    override fun execute() {
        super.execute()

        // 写入目标Excel文件
        val filePath = PathUtils.rootPath + File.separator + PathUtils.resultFile
        val workbook = Workbook.createWorkbook(File(filePath))
        val sheet = workbook.createSheet(PathUtils.rootName, 0)

        // 先写入有层级的数据
        val nodes = FileUtils.listFile2(PathUtils.rootPath)
        nodes.filter { it.directory1 != "" }.forEach {
            writeRow(sheet, it)
        }

        // 再写入无层级的数据
        nodes.filter { it.directory1 == "" }.forEach {
            writeRow(sheet, it)
        }

        // 自动设置列宽
        autoColumnSize(sheet)

        // 写入和关闭
        try {
            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            workbook.close()
        }
    }

    /**
     * 写入一行数据
     */
    private fun writeRow(sheet: WritableSheet, fileNode: FilePath) {
        cellRow++
        writeCell(sheet, cellRow, 0, fileNode.directory1, fileDirectoryFormat)
        writeCell(sheet, cellRow, 1, fileNode.directory2, fileDirectoryFormat)
        writeCell(sheet, cellRow, 2, fileNode.name)
        writeCell(sheet, cellRow, 3, fileNode.path)
    }
}