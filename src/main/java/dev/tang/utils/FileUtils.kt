package dev.tang.utils

import dev.tang.vo.FileNode
import dev.tang.vo.FilePath
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 文件处理工具
 */
object FileUtils {
    /**
     * Make file directory
     *
     * @param dir
     * @return
     */
    fun mkdir(dir: String?): String? {
        var created = false
        val dirFile = File(dir)
        if (!dirFile.exists()) {
            if (dirFile.mkdirs()) {
                created = true
            } else {
            }
        } else {
            created = true
        }
        return if (created) {
            dir
        } else {
            ""
        }
    }

    /**
     * Write file
     *
     * @param filePath
     * @param content
     * @param append
     */
    fun writeFile(filePath: String?, content: String?, append: Boolean) {
        try {
            val sb = StringBuffer()
            val bw = BufferedWriter(FileWriter(filePath, append))
            sb.append(content)
            sb.append("\r\n")
            bw.write(sb.toString())
            bw.flush()
            bw.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    /**
     * Write bytes
     *
     * @param filePath
     */
    fun writeFile(filePath: String, bytes: ByteArray?) {
        try {
            val file = File(filePath)
            val out = FileOutputStream(file)
            out.write(bytes)
            out.flush()
            out.close()
        } catch (ioe: IOException) {
        }
    }

    fun writeFile(filePath: String, inputStream: InputStream) {
        val file = File(filePath)

        var bos: BufferedOutputStream? = null
        var bis: BufferedInputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(file))
            bis = BufferedInputStream(inputStream)
            var c: Int
            while (bis.read().also { c = it } != -1) {
                bos.write(c)
                bos.flush()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw Exception("文件保存失败!")
        } finally {
            bos!!.close()
            bis!!.close()
        }
    }

    /**
     * Delete all files
     *
     * @param path
     * @return
     */
    fun deleteFile(path: String): Boolean {
        var success = true
        val file = File(path)
        if (!file.exists()) {
            success = false
        }
        if (file.isDirectory) {
            success = false
        }
        success = success and file.delete()
        return success
    }

    /**
     * Delete all files
     *
     * @param path
     * @return
     */
    fun deleteAllFile(path: String?): Boolean {
        var success = true
        val file = File(path)
        if (!file.exists()) {
            success = false
        }
        if (!file.isDirectory) {
            success = false
        }
        if (success) {
            val tempList = file.list()
            var temp: File? = null
            for (i in tempList.indices) {
                temp = if (path!!.endsWith(File.separator)) {
                    File(path + tempList[i])
                } else {
                    File(path + File.separator + tempList[i])
                }
                if (temp.isFile) {
                    success = success and temp.delete()
                }
            }
        }
        return success
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath    :压缩后存放路径
     * @param fileName       :压缩后文件的名称
     * @param suffix         :自定义压缩文件后缀名
     * @return
     */
    fun fileToZip(sourceFilePath: String, zipFilePath: String, fileName: String, suffix: String): Boolean {
        var flag = false
        val sourceFile = File(sourceFilePath)
        var fis: FileInputStream? = null
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        var zos: ZipOutputStream? = null
        if (!sourceFile.exists()) {
            println("待压缩的文件目录：" + sourceFilePath + "不存在.")
            sourceFile.mkdir() // 新建目录
        }
        try {
            val zipFile = File("$zipFilePath/$fileName.$suffix")
            if (zipFile.exists()) {
                println(zipFilePath + "目录下存在名字为:" + fileName + "." + suffix + "打包文件.")
            } else {
                val sourceFiles = sourceFile.listFiles()
                if (null == sourceFiles || sourceFiles.size < 1) {
                    println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.")
                } else {
                    fos = FileOutputStream(zipFile)
                    zos = ZipOutputStream(BufferedOutputStream(fos))
                    val bufs = ByteArray(1024 * 10)
                    for (i in sourceFiles.indices) { //创建ZIP实体，并添加进压缩包
                        val zipEntry = ZipEntry(sourceFiles[i].name)
                        zos.putNextEntry(zipEntry)
                        //读取待压缩的文件并写进压缩包里
                        fis = FileInputStream(sourceFiles[i])
                        bis = BufferedInputStream(fis, 1024 * 10)
                        var read = 0
                        while (bis.read(bufs, 0, 1024 * 10).also { read = it } != -1) {
                            zos.write(bufs, 0, read)
                        }
                    }
                    flag = true
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            throw RuntimeException(e)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        } finally { //关闭流
            try {
                bis?.close()
                zos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
        return flag
    }

    /**
     * 列出目录下所有文件
     */
    fun listFile(parent: FileNode, path: String): FileNode {
        File(path).listFiles()?.forEach { file ->
            // 过滤自身
            if (!file.name.contains("file-utils")) {
                val child = FileNode(parent.column + 1)
                child.name = file.name
                child.path = file.path.replace(PathUtils.rootPath, "")
                child.directory = file.isDirectory
                parent.children.add(child)

                if (file.isDirectory) {
                    listFile(child, file.path)
                }
            }
        }
        return parent
    }

    /**
     * 列出目录下所有文件
     */
    fun listFile2(path: String): List<FilePath> {
        val list = mutableListOf<FilePath>()
        File(path).listFiles()?.forEach { file ->
            if (file.isDirectory) {
                list.addAll(listFile2(file.path))
            } else {
                // 过滤自身
                if (!file.name.contains("file-utils")) {
                    val fn = FilePath()
                    fn.name = file.name
                    fn.path = PathUtils.rootName + file.path.replace(PathUtils.rootPath, "")
                    fn.directory1 = fn.path.split(File.separator).getOrElse(1) { "" }
                    fn.directory2 = fn.path.split(File.separator).getOrElse(2) { "" }

                    if (fn.directory1 == fn.name) {
                        fn.directory1 = ""
                    }
                    if (fn.directory2 == fn.name) {
                        fn.directory2 = ""
                    }
                    list.add(fn)
                }
            }
        }
        return list
    }
}