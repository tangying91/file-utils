package dev.tang.app

import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableSheet

open class AppForJoan {

    /**
     * Excel 单元格渲染
     * 文件目录，黄色，虚线框标注
     */
    val fileDirectoryFormat = WritableCellFormat()

    /**
     * 记录当前单元行
     */
    var cellRow = 0

    /**
     * 实现自适应动态列宽
     */
    val columnWidthMap = mutableMapOf<Int, Int>()

    /**
     * 对外执行入口
     */
    open fun execute() {
        // 每次执行都会重新初始化
        cellRow = 0
        columnWidthMap.clear()
    }

    /**
     * 写入一个单元格
     */
    fun writeCell(sheet: WritableSheet, row: Int, column: Int, content: String, wcf: WritableCellFormat = WritableCellFormat()) {
        sheet.addCell(Label(column, row, content, wcf))
        columnWidthMap[column] = kotlin.math.max(content.length + 10, columnWidthMap.getOrElse(column) { 0 })
    }

    /**
     * 分页设置自动列宽
     */
    fun autoColumnSize(sheet: WritableSheet) {
        columnWidthMap.forEach {
            sheet.setColumnView(it.key, it.value)
        }

        // 清空
        columnWidthMap.clear()
    }

    init {
        fileDirectoryFormat.setBackground(Colour.YELLOW)
        fileDirectoryFormat.setBorder(Border.ALL, BorderLineStyle.DASH_DOT)
    }
}