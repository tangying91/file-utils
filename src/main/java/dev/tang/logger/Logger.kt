package dev.tang.logger

import dev.tang.utils.FileUtils
import dev.tang.utils.PathUtils
import dev.tang.utils.TimeUtils

/**
 * 日志显示器
 */
class Logger(private val clazz: Class<*>) {

    fun warn(content: String) {
        val loggerPath = PathUtils.loggerPath
        FileUtils.writeFile(loggerPath, "[Warn] " + TimeUtils.currentTime + " [" + clazz.name + "] " + content, true)
    }

    fun warn(c: String, vararg args: Any) {
        var content = c
        for (i in 0 until args.size) {
            content = content.replace("{$i}", args[i].toString())
        }
        val loggerPath = PathUtils.loggerPath
        content = "[Warn] " + TimeUtils.currentTime + " [" + clazz.name + "] " + content
        FileUtils.writeFile(loggerPath, content, true)
    }

    fun error(c: String, vararg args: Any) {
        var content = c
        for (i in 0 until args.size) {
            content = content.replace("{$i}", args[i].toString())
        }
        val loggerPath = PathUtils.loggerPath
        content = "[Error] " + TimeUtils.currentTime + " [" + clazz.name + "] " + content
        FileUtils.writeFile(loggerPath, content, true)
    }
}