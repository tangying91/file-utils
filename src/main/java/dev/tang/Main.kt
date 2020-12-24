package dev.tang

import dev.tang.app.impl.*
import dev.tang.utils.FileUtils
import dev.tang.utils.PathUtils

/**
 * 主入口
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        FileUtils.deleteFile(PathUtils.loggerEmailPath)
        EmailLogic.read()
    }
}