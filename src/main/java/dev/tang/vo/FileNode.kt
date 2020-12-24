package dev.tang.vo

class FileNode(var column: Int = 0, var name: String = "") {
    var directory: Boolean = true
    var path: String = ""
    val children = mutableListOf<FileNode>()
}