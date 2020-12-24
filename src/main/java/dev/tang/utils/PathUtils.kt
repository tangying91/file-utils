package dev.tang.utils

import java.io.File

object PathUtils {

    val rootPath: String = File("").absolutePath

    val rootName = rootPath.substringAfterLast(File.separator)

    val loggerPath = rootPath + File.separator + "error"

    val loggerEmailPath = rootPath + File.separator + "归档结果.txt"

    const val resultFile = "file-utils-result.xls"
}