package dev.tang.app.impl

import dev.tang.app.AppForJoan
import dev.tang.utils.FileUtils
import dev.tang.utils.PathUtils
import dev.tang.vo.FileNode
import jxl.Workbook
import jxl.write.WritableSheet
import java.io.File

/**
 * 找出所有文件名称
 * 特殊处理：第一层只找出文件夹，文件不显示
 * 其他：按层级递归显示
 */
object AppForJoan2 : AppForJoan() {

    override fun execute() {
        super.execute()

        // 写入目标Excel文件
        val filePath = PathUtils.rootPath + File.separator + PathUtils.resultFile
        val workbook = Workbook.createWorkbook(File(filePath))

        // 第一层是年份文件夹，转化成分页
        var sheetIndex = 0
        File(PathUtils.rootPath).listFiles()?.forEach { file ->
            if (file.isDirectory) {
                cellRow = 0

                // 创建分页
                val sheet = workbook.createSheet(file.name, sheetIndex)

                // 写入分页数据
                val fn = FileUtils.listFile(FileNode(name = File(file.path).name), file.path)
                writeRow(fn, sheet)

                // 分页设置自动列宽
                autoColumnSize(sheet)

                // 分页递增
                sheetIndex++
            }
        }

        try {
            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            workbook.close()
        }
    }

    private fun writeRow(fileNode: FileNode, sheet: WritableSheet) {
        cellRow++

        if (fileNode.directory) {
            writeCell(sheet, cellRow, fileNode.column, fileNode.name, fileDirectoryFormat)

            cellRow--
            fileNode.children.forEach {
                writeRow(it, sheet)
            }
        } else {
            writeCell(sheet, cellRow, fileNode.column, fileNode.name)
            writeCell(sheet, cellRow, fileNode.column + 1, fileNode.path)
        }
    }
}